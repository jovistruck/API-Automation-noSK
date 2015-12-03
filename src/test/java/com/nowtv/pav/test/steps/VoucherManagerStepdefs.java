package com.nowtv.pav.test.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.nowtv.pav.test.performance.ResponseConsumer;
import com.nowtv.pav.test.performance.ResponseStats;
import com.nowtv.pav.test.rest.HttpContext;
import com.nowtv.pav.test.utils.DelayedAssert;
import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.generators.core.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.jayway.awaitility.Awaitility.await;
import static com.nowtv.pav.test.TestContext.testContext;
import static com.nowtv.pav.test.rest.HttpContext.httpContext;
import static com.nowtv.pav.test.steps.RestStepDefinitions.*;
import static io.generators.core.Generators.nDigitPositiveInteger;
import static io.generators.core.MoreFunctions.toUpperCase;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.function.Function.identity;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.generate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.fail;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

public class VoucherManagerStepdefs extends GenericMethods {

    private static final String CSR_USER = "parkesD";
    private static final String LDAP_VALID_USER_QUERY = "?user=" + CSR_USER;
    private static final String LDAP_INVALID_USER_QUERY = "?user=BAD_USER";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HashFunction hashFunction = Hashing.murmur3_32();
    private static final BiFunction<Double, String, String> priceHasher = (price, currency) -> hashFunction.newHasher().putDouble(price).putString(currency, Charsets.UTF_8).hash().toString();
    private static Accounts accounts = loadAccounts();
    private static Generator<Long> randomAccountGenerator = new RandomPositiveLongGenerator(10_000_000, 99_999_999);
    private static Scenario scenario;
    private static List<Tuple<String, Optional<String>>> vouchers;
    private static List<Tuple<String, String>> ntVouchers;

    private static final String _24HOUR_TIME = "HH:mm:ss";

    private static String campaignType;

    private static LinkedTransferQueue<Long> activableAccounts;
    private static LinkedTransferQueue<Long> cancellableAccounts;

    private static LinkedTransferQueue<Tuple<String, Optional<String>>> pendingSUVs;
    private static LinkedTransferQueue<Tuple<String, Optional<String>>> activeSUVs;
    private static LinkedTransferQueue<Tuple<String, Optional<String>>> cancelledSUVs;
    private static LinkedTransferQueue<Tuple<String, Optional<String>>> redeemedSUVcodeAndAccountId;
    private static LinkedTransferQueue<Long> redeemedPromoCodeAccountIds;

    private static LinkedTransferQueue<Tuple<String, String>> activeSUNTVouchers;
    private static LinkedTransferQueue<Tuple<String, String>> cancelledSUNTVouchers;
    private static LinkedTransferQueue<Tuple<String, String>> redeemedSUNTcodeAndAccountId;

    private static List<Tuple<Integer, Generator<ResponseStats>>> actions;
    private static final ResponseConsumer statsConsumer = new ResponseConsumer();
    private static int perfTestDurationInSeconds;

    private static final Map<String, String> productOrOfferToPriceHash = new ImmutableMap.Builder<String, String>()
            .put("ENTERTAINMENT_SUBSCRIPTION_MONTH", priceHasher.apply(6.99, "GBP"))
            .put("SPORTS_PASS_DAY", priceHasher.apply(6.99, "GBP"))
            .put("B_002326.P_002325.OF_002978", priceHasher.apply(3.49, "GBP")) // 50% Discounted Sports Day Pass
            .put("B_002326.P_002325.OF_002976", priceHasher.apply(0.00, "GBP")) // Free Sports Day Pass
            .put("SPORTS_PASS_WEEK", priceHasher.apply(10.99, "GBP"))
            .put("B_002326.P_002325.OF_002984", priceHasher.apply(5.49, "GBP")) // 50% Discounted Sports Week Pass
            .put("B_002326.P_002325.OF_002983", priceHasher.apply(0.00, "GBP")) // Free Sports Week Pass
            .build();

    @Before
    public void start(Scenario scenario) {
        VoucherManagerStepdefs.scenario = scenario;
        actions = new ArrayList<>();

        activableAccounts = new LinkedTransferQueue<>();
        cancellableAccounts = new LinkedTransferQueue<>();

        testContext().voucherCode = null;
        httpContext().resetStandardRestTemplate();
        httpContext().resetResponseVariables();
        httpContext().resetHeaders();
    }

    public Collector<Long,LinkedTransferQueue<Long>,LinkedTransferQueue<Long>> transferQueueCollector() {
        return new Collector<Long, LinkedTransferQueue<Long>, LinkedTransferQueue<Long>>() {
            @Override
            public Supplier<LinkedTransferQueue<Long>> supplier() {
                return LinkedTransferQueue::new;
            }

            @Override
            public BiConsumer<LinkedTransferQueue<Long>, Long> accumulator() {
                return LinkedTransferQueue::add;
            }

            @Override
            public BinaryOperator<LinkedTransferQueue<Long>> combiner() {
                return (r,l) -> {r.addAll(l);return r;};
            }

            @Override
            public Function<LinkedTransferQueue<Long>, LinkedTransferQueue<Long>> finisher() {
                return identity();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(IDENTITY_FINISH);
            }
        };
    }

    @Given("an active (PROMO_CODE|SINGLE_USE_TRANSFERABLE|SINGLE_USE_NON_TRANSFERABLE|GIFTING) campaign with offer id (.*)$")
    public void createCampaignByType(String campaignType, String offerId) {
        VoucherManagerStepdefs.campaignType = campaignType;
        targetVm();
        testContext().priceHash = productOrOfferToPriceHash.get(offerId);
        createCampaign(campaignType, "END2END-" + Generators.alphabetic(30).next(), null, null, offerId);
        assertCreateUTFJsonResponse();
        extractIdFromResponse();
    }

    @And("^a single use voucher$")
    public void a_single_use_voucher() {
        targetVm();
        Object campaignId = getLastCreatedObjectId();
        httpContext().httpMethodJsonOut(HttpMethod.POST, "/campaign/" + campaignId + "/voucher-batch/casenumber/" + nDigitPositiveInteger(8).next(), null);
        assertCreateUTFJsonResponse();
        testContext().voucherCode = httpContext().jsonPath.getString("code");
    }

    @And("^a promo code voucher$")
    public void a_promo_code_voucher() {
        targetVm();
        Object campaignId = getLastCreatedObjectId();
        testContext().promoCode = FluentGenerator.from(new RandomAlphanumericGenerator(10)).transform(toUpperCase()).next();

        httpContext().httpMethodJsonOut(HttpMethod.POST, "/voucher/promocode", "{\"code\":\"" + testContext().promoCode + "\", \"state\":\"ACTIVE\", \"campaign\":{\"id\":" + campaignId + "}}");
        assertCreateUTFJsonResponse();
    }

    @Then("^the response status is (\\d+) with message \"([^\"]*)\" and error code (\\d+)$")
    public void the_response_status_is_with_message_and_error_code(int httpStatus, String errorMessage, String errorCode) {
        assertThat(httpContext().responseCode).isEqualTo(httpStatus);
        assertOnErrorResponse(errorCode, errorMessage);
    }

    @Then("^the response status is (\\d+) with message contains \"([^\"]*)\" and error code (\\d+)$")
    public void the_response_status_is_with_message_contains_and_error_code(int httpStatus, String errorMessage, String errorCode) {
        assertThat(httpContext().responseCode).isEqualTo(httpStatus);
        assertOnErrorMessageContains(errorCode, errorMessage);
    }

    @When("^I ask for the voucher details with a valid CSR ldap user$")
    public void I_successfully_get_the_voucher_details_with_valid_CSR_ldap_user() {
        extractVoucherCodeFromResponse();
        requestVoucherDetails(LDAP_VALID_USER_QUERY);
    }

    @When("^I ask for the voucher details without a valid CSR ldap user$")
    public void I_ask_for_the_voucher_details_without_a_valid_CSR_ldap_user() {
        requestVoucherDetails(LDAP_INVALID_USER_QUERY);
    }

    @Then("^I am returned the details of the voucher$")
    public void I_am_returned_the_details_of_the_voucher_with_the_details(DataTable voucherDetails) {
        assertOkUTFJsonResponse();
        validateCucumberDataAgainstJson(voucherDetails);
    }

    @Then("^I am returned the new voucher details$")
    public void I_am_returned_the_new_voucher_details(DataTable voucherDetails) {
        assertCreateUTFJsonResponse();
        validateCucumberDataAgainstJson(voucherDetails);
    }

    @Then("^I am returned the details of the campaign$")
    public void I_am_returned_the_details_of_the_campaign(DataTable campaignDetails) {
        assertOkUTFJsonResponse();
        validateCucumberDataAgainstJson(campaignDetails);
    }

    @When("^I ask for the campaign details with a valid CSR ldap user$")
    public void I_successfully_ask_for_the_campaign_details_with_valid_CSR_ldap_user() {
        requestCampaignDetails(LDAP_VALID_USER_QUERY);
    }

    @When("^I ask for the campaign details without a valid CSR ldap user$")
    public void I_ask_for_the_campaign_details_without_valid_CSR_ldap_user() {
        requestCampaignDetails(LDAP_INVALID_USER_QUERY);
    }

    @Then("^I receive an authentication error message \"([^\"]*)\"$")
    public void I_receive_an_authentication_error_message(String errorMessage) {
        assertOnErrorResponse(errorMessage);
    }

    @When("^I ask to cancel a voucher with a valid CSR ldap user$")
    public void I_ask_to_cancel_a_voucher_with_an_valid_user() {
        requestCancelVoucher(LDAP_VALID_USER_QUERY);
    }

    @When("^I ask to cancel a voucher with an invalid CSR ldap user$")
    public void I_ask_to_cancel_a_voucher_with_an_invalid_user() {
        requestCancelVoucher(LDAP_INVALID_USER_QUERY);
    }

    @Then("^I get an ok message$")
    public void I_get_an_ok_message() {
        assertOkUTFJsonResponse();
    }

    @When("^I ask to create an additional voucher for an account with a valid CSR user$")
    public void I_ask_to_create_an_additional_voucher_for_an_account_with_a_valid_CSR_user() {
        requestCreateAdditionalVoucher(LDAP_VALID_USER_QUERY);
    }

    @And("^I am authorised as salesforce for VM$")
    public void i_am_authorised_as_salesforce() {
        targetVm();
        AuthStepDefinitions.iAmAuthorisedWithOauthUser("EqVFvw5HU2", "rmQYgGZ4xFyHmXKReQn8FhBz8gmR8a29mGXtYz775vE34eJTFG");
    }

    @And("^I am authorised as admin for VM")
    public void i_am_authorised_as_admin_for_VM() {
        targetVm();
        httpContext().useBasicAuthorisation("admin", "pass");
    }

    @And("^I am authorised as platform for VM")
    public void i_am_authorised_as_platform_for_VM() {
        targetVm();
        AuthStepDefinitions.iAmAuthorisedWithOauthUser("r4VgvE5sMW", "hBYn8FvERGggmR8z754xF34eQrmeQJFG79mGXtZTa28YzmXKyH");
    }

    @And("^I am authorised as ppm for VM")
    public void i_am_authorised_as_ppm_for_VM() {
        targetVm();
        AuthStepDefinitions.iAmAuthorisedWithOauthUser("ucAVXsvQnj", "yatzKYYy6L8PCzb9CHnjNYgc5NttTkFhkXtUMg4HD2n7caREuP");
    }

    @And("^I provide invalid oauth credentials$")
    public void I_provide_invalid_oauth_credentials() {
        AuthStepDefinitions.iAmAuthorisedWithOauthUser("BAD_KEY", "BAD_SECRET");
    }

    @Given("^I have the campaign name$")
    public void I_have_the_campaign_name() {
        testContext().campaignName = httpContext().jsonPath.get("name");
    }

    @When("^I ask for all campaigns that match by name with a valid CSR ldap user$")
    public void I_ask_for_all_campaigns_that_match_by_name() {
        targetVmHttps();
        findAllCampaignsByName(LDAP_VALID_USER_QUERY);
    }


    @When("^I ask for all campaigns that match by name without a valid CSR ldap user$")
    public void I_ask_for_all_campaigns_that_match_by_name_without_a_valid_CSR_ldap_user() {
        targetVmHttps();
        findAllCampaignsByName(LDAP_INVALID_USER_QUERY);
    }

    @And("^I see the list of campaigns that match that name$")
    public void I_see_the_list_of_campaigns_that_match_that_name() {
        List<Map<?, ?>> campaigns = httpContext().jsonPath.getList(".");
        assertThat(campaigns.size()).isGreaterThan(0);
        campaigns.stream().forEach(campaignInfo -> assertThat(campaignInfo.get("name")).isEqualTo(testContext().campaignName));
    }

    @Given("^the campaign has a promo code$")
    public void the_campaign_has_a_promo_code() {
        targetVm();
        testContext().promoCode = Generators.alphabetic(10).next().toUpperCase();
        extractIdFromResponse();
        String body = "{\"code\":\"" + testContext().promoCode + "\",\"state\":\"ACTIVE\", \"campaign\":{\"id\":" + testContext().campaignId + "}}";
        httpContext().httpMethodJsonOut(HttpMethod.POST, "/voucher/promocode", body);
        assertCreateUTFJsonResponse();
    }

    @When("^I ask for the promo codes for a campaign with a valid CSR ldap user$")
    public void I_ask_for_the_promo_codes_for_a_campaign_with_a_valid_CSR_ldap_user() {
        targetVmHttps();
        getPromoCodesForCampaign(LDAP_VALID_USER_QUERY);
    }

    @When("^I ask for the promo codes for a campaign with an invalid CSR ldap user$")
    public void I_ask_for_the_promo_codes_for_a_campaign_with_an_invalid_CSR_ldap_user() {
        targetVmHttps();
        getPromoCodesForCampaign(LDAP_INVALID_USER_QUERY);
    }

    @Then("^I see the created promo code in the campaign$")
    public void I_see_the_created_promo_code_in_the_campaign() {
        List<Map<?, ?>> promoCodes = httpContext().jsonPath.get(".");
        assertThat(promoCodes.size()).isEqualTo(1);
        promoCodes.stream().forEach(promo -> assertThat(promo.get("code")).isEqualTo(testContext().promoCode));
    }

    @When("^platform (activate|deactivate)s the voucher$")
    public void platform_activates_the_voucher(String voucherAction) {
        targetVm();
        httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + testContext().voucherCode + "/" + voucherAction, null);
        assertOkUTFJsonResponse();
    }

    @Then("^the voucher is (\\w+)$")
    public void the_voucher_is_state(String voucherStatus) {
        assertThat(httpContext().jsonPath.getString("state")).isEqualTo(voucherStatus);
    }

    @And("^I ask for the voucher details$")
    public void I_ask_for_the_voucher_details() {
        targetVm();
        requestPlatformVoucherDetails();
    }

    @And("^a voucher batch of size (\\d+)$")
    public void a_voucher_batch_of_size(int batchSize) {
        targetVm();
        extractIdFromResponse();
        ResponseStats responseStats = new ResponseStats("VM-generateBatch");
        httpContext().httpMethodJsonOut(HttpMethod.POST, "/campaign/" + testContext().campaignId + "/voucher-batch?size=" + batchSize, null);
        testContext().voucherBatchId = getLastCreatedObjectId();
        await().pollDelay(5, SECONDS).pollInterval(3, SECONDS).atMost(10, MINUTES).until(() -> {
            httpContext().httpMethodJsonOut(HttpMethod.GET, "/campaign/" + testContext().campaignId + "/voucher-batch", null);
            return httpContext().jsonPath.get("[0].state").equals("READY");
        });
        statsConsumer.accept(responseStats.succeed());
        HttpContext httpContext = httpContext();
        httpContext.httpMethodJsonOut(HttpMethod.GET, "/campaign/" + testContext().campaignId + "/voucher-batch/" + testContext().voucherBatchId + "/export", null);
        vouchers = checkNotNull(httpContext.csv, "Failed to retrieve vouchers from batch %s", testContext().voucherBatchId)
                .subList(1, httpContext().csv.size())
                .stream()
                .map(l -> new Tuple<String, Optional<String>>(l.get(1), empty()))
                .collect(Collectors.toList());

        if (campaignType.equals("GIFTING")) {
            activeSUVs = new LinkedTransferQueue<>();
            pendingSUVs = new LinkedTransferQueue<>(vouchers);
        } else {
            activeSUVs = new LinkedTransferQueue<>(vouchers);
            pendingSUVs = new LinkedTransferQueue<>();
        }
        cancelledSUVs = new LinkedTransferQueue<>();
        redeemedSUVcodeAndAccountId = new LinkedTransferQueue<>();
        redeemedPromoCodeAccountIds = new LinkedTransferQueue<>();
    }

    @And("^a non-transferable voucher batch of size (\\d+)$")
    public void a_non_transferable_voucher_batch_of_size(int batchSize) throws IOException {
        List<String> accounts = IntStream.range(0, batchSize).mapToObj(n -> VoucherManagerStepdefs.accounts.getRandomAccount()).collect(toList());


        targetVm();
        extractIdFromResponse();
        ResponseStats responseStats = new ResponseStats("VM-generateNTBatch");
        uploadCsv(testContext().campaignId, accounts);

        testContext().voucherBatchId = getLastCreatedObjectId();
        await().pollDelay(5, SECONDS).pollInterval(3, SECONDS).atMost(10, MINUTES).until(() -> {
            httpContext().httpMethodJsonOut(HttpMethod.GET, "/campaign/" + testContext().campaignId + "/voucher-batch", null);
            return httpContext().jsonPath.get("[0].state").equals("READY");
        });
        statsConsumer.accept(responseStats.succeed());
        HttpContext httpContext = httpContext();
        httpContext.httpMethodJsonOut(HttpMethod.GET, "/campaign/" + testContext().campaignId + "/voucher-batch/" + testContext().voucherBatchId + "/export", null);

        ntVouchers = checkNotNull(httpContext.csv, "Failed to retrieve vouchers from batch %s", testContext().voucherBatchId)
                .subList(1, httpContext().csv.size()).stream().map(l -> new Tuple<String, String>(l.get(1), l.get(3))).collect(toList());

        activeSUNTVouchers = new LinkedTransferQueue<>(ntVouchers);
        cancelledSUNTVouchers = new LinkedTransferQueue<>();
        redeemedSUNTcodeAndAccountId = new LinkedTransferQueue<>();
    }

    private void uploadCsv(long campaignId, List<String> accounts) throws IOException {
        Path csv = Files.createTempFile("account-import", ".csv");
        csv.toFile().deleteOnExit();
        Files.write(csv, singletonList("\"Account Ids\""));

        Files.write(csv, accounts, APPEND);

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new UrlResource(csv.toAbsolutePath().toUri()));

        httpContext().httpMethodJsonOut(POST, "/campaign/" + campaignId + "/voucher-batch/upload", parts, MULTIPART_FORM_DATA_VALUE + "; charset=utf-8");
    }

    @And("^I cancel the voucher batch$")
    public void cancel_voucher_batch() {
        targetVm();
        httpContext().httpMethodJsonOut(HttpMethod.POST, "/campaign/" + testContext().campaignId + "/voucher-batch/" + testContext().voucherBatchId + "/cancel", null);
    }

    @Then("^(\\d+) vouchers are cancelled from the batch$")
    public void vouchers_are_cancelled_from_the_batch(int numberOfVouchers) {
        targetVm();
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/campaign/" + testContext().campaignId + "/voucher-batch/" + testContext().voucherBatchId + "/export", null);
        long count = httpContext().csv.subList(1, httpContext().csv.size()).stream()
                .map(l -> l.get(2))
                .filter("CANCELLED"::equals)
                .count();
        assertThat(count).isEqualTo(numberOfVouchers);
    }

    @And("^there will be (\\d+)% random SUT voucher retrievals$")
    public void there_will_be_random_voucher_retrievals(int percentage) {
        actions.add(Tuple.tuple(percentage, () -> {
            targetVm();
            httpContext().useBasicAuthorisation("admin", "pass");
            ResponseStats responseStats = new ResponseStats("VM-getSUTVoucherByCode");
            Tuple<String, Optional<String>> tuple = getActiveSUV();
            httpContext().httpMethodJsonOut(HttpMethod.GET, "/voucher/" + tuple.getFirst(), null);
            addActiveSUV(tuple);
            return requestWasSuccessful() ? responseStats.succeed() : responseStats.fail();
        }));
    }

    @And("^there will be (\\d+)% random of Promo Code voucher retrievals$")
    public void there_will_be_random_of_promo_code_voucher_retrievals(int percentage) {
        actions.add(Tuple.tuple(percentage, () -> {
            targetVm();
            httpContext().useBasicAuthorisation("admin", "pass");
            ResponseStats responseStats = new ResponseStats("VM-getPromoCodeVoucherByCode");
            httpContext().httpMethodJsonOut(HttpMethod.GET, "/voucher/" + testContext().promoCode, null);
            return requestWasSuccessful() ? responseStats.succeed() : responseStats.fail();
        }));
    }

    @When("^I run streamed performance tests for (\\d+) minutes$")
    public void I_run_streamed_performance_tests_for_minutes(int minutes) {
        int sum = actions.stream().mapToInt(Tuple::getFirst).sum();
        checkArgument(sum == 100, "Performance percentages do not sum to 100 but to %s", sum);

        perfTestDurationInSeconds = 60 * minutes;
        Supplier<Generator<ResponseStats>> actionSupplier = createdGenerators(actions.stream().sorted((a, b) -> b.getFirst().compareTo(a.getFirst())).collect(toList()));
        DateTime end = now().plusMinutes(minutes);
        logToCucumberReport("---Performance Tests  scheduled from: " + now().toString(_24HOUR_TIME) + " to: " + end.toString(_24HOUR_TIME));
        generate(actionSupplier).parallel().map(Generator::next).peek(statsConsumer::accept).allMatch(x -> end.isAfterNow());
    }

    @When("^I cancel all outstanding (.*) products$")
    public void cancel_all_outstanding_products(String productId) {
        activableAccounts.forEach(accountId -> cancelProductForAccount(productId, accountId));
        cancellableAccounts.forEach(accountId -> cancelProductForAccount(productId, accountId));
    }

    @Given("^(\\d+)% of requests will be to activate a gifting voucher$")
    public void of_requests_will_be_to_activate_a_gifting_voucher(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Tuple<String, Optional<String>> tuple = getPendingSUV();
            if (tuple == null) return new ResponseStats("VM-deactivateGiftingVoucher-wait").fail();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-activateGiftingVoucher");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + tuple.getFirst() + "/activate", null);
            if (requestWasSuccessful()) {
                addActiveSUV(tuple);
                return responseStats.succeed();
            }
            return responseStats.fail();
        }));
    }

    @Given("^(\\d+)% of requests will be to deactivate a gifting voucher$")
    public void of_requests_will_be_to_deactivate_a_gifting_voucher(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Tuple<String, Optional<String>> tuple = getActiveSUV();
            if (tuple == null) return new ResponseStats("VM-deactivateGiftingVoucher-wait").fail();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-deactivateGiftingVoucher");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + tuple.getFirst() + "/deactivate", null);
            if (requestWasSuccessful()) {
                addPendingSUV(tuple);
                return responseStats.succeed();
            }
            return responseStats.fail();
        }));
    }

    @Given("^(\\d+)% of requests will be to check if a SUV can be redeemed$")
    public void of_requests_will_be_to_check_if_a_SUV_can_be_redeemed(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Long accountId = randomAccountGenerator.next();
            Tuple<String, Optional<String>> tuple = getActiveSUV();
            if (tuple == null) return new ResponseStats("VM-dryRunRedeemSUV-wait").fail();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-dryRunRedeemSUV");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + tuple.getFirst() + "/redeem/" + accountId + "?dryRun=true", null);
            return requestWasSuccessful() ? responseStats.succeed() : responseStats.fail();
        }));
    }

    @Given("^(\\d+)% of requests will be to redeem a SUV$")
    public void of_requests_will_be_to_redeem_a_SUV(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            String accountId = randomAccountGenerator.next().toString();
            Tuple<String, Optional<String>> tuple = getActiveSUV();
            if (tuple == null) return new ResponseStats("VM-redeemSUV-wait").fail();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-redeemSUV");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + tuple.getFirst() + "/redeem/" + accountId, null);
            if (requestWasSuccessful()) {
                addRedeemedSUVcodeAndAccountId(new Tuple<>(tuple.getFirst(), of(accountId)));
                return responseStats.succeed();
            }
            return responseStats.fail();
        }));
    }

    @Given("^(\\d+)% of requests will be to reverse a redeemed SUV$")
    public void of_requests_will_be_to_reverse_a_redeemed_SUV(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Tuple<String, Optional<String>> tuple = getRedeemedSUVcodeAndAccountId();
            if (tuple == null) return new ResponseStats("VM-reverseRedeemSUV-wait").fail();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-reverseRedeemSUV");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + tuple.getFirst() + "/reverse/" + tuple.getSecond().get(), null);
            if (requestWasSuccessful()) {
                addActiveSUV(tuple);
                return responseStats.succeed();
            }
            return responseStats.fail();
        }));
    }

    @Given("^(\\d+)% of requests will be to cancel an active SUV$")
    public void of_requests_will_be_to_cancel_an_active_SUV(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Tuple<String, Optional<String>> tuple = getActiveSUV();
            if (tuple == null) return new ResponseStats("VM-cancelActiveSUV-wait").fail();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-cancelActiveSUV");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + tuple.getFirst() + "/cancel", null);
            if (requestWasSuccessful()) {
                addCancelledSUV(tuple);
                return responseStats.succeed();
            }
            return responseStats.fail();
        }));
    }

    @Given("^(\\d+)% of requests will be to activate a cancelled SUV$")
    public void of_requests_will_be_to_activate_a_cancelled_SUV(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Tuple<String, Optional<String>> tuple = getCancelledSUV();
            if (tuple == null) return new ResponseStats("VM-activateCancelledSUV-wait").fail();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-activateCancelledSUV");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + tuple.getFirst() + "/activate", null);
            if (requestWasSuccessful()) {
                addActiveSUV(tuple);
                return responseStats.succeed();
            }
            return responseStats.fail();
        }));
    }

    @Given("^(\\d+)% of requests will be to check if a promo code can be redeemed$")
    public void of_requests_will_be_to_check_if_a_promo_code_can_be_redeemed(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Long accountId = randomAccountGenerator.next();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-dryRunRedeemPromoCode");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + testContext().promoCode + "/redeem/" + accountId + "?dryRun=true", null);
            return requestWasSuccessful() ? responseStats.succeed() : responseStats.fail();
        }));
    }

    @Given("^(\\d+)% of requests will be to redeem a promo code$")
    public void of_requests_will_be_to_redeem_a_promo_code(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Long accountId = randomAccountGenerator.next();
            // TODO: if multiple promo code redemptions per account are allowed then remove 2nd part in if statement
            if (redeemedPromoCodeAccountIds.contains(accountId))
                return new ResponseStats("VM-redeemPromoCode-wait").fail();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-redeemPromoCode");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + testContext().promoCode + "/redeem/" + accountId, null);
            if (requestWasSuccessful()) {
                addRedeemedPromoCodeAccountId(accountId);
                return responseStats.succeed();
            }
            return responseStats.fail();
        }));
    }

    @Given("^(\\d+)% of requests will be to reverse a redeemed promo code$")
    public void of_requests_will_be_to_reverse_a_redeemed_promo_code(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Long accountId = getRedeemedPromoCodeAccountId();
            if (accountId == null) return new ResponseStats("VM-reverseRedeemPromoCode-wait").fail();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-reverseRedeemPromoCode");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + testContext().promoCode + "/reverse/" + accountId, null);
            return requestWasSuccessful() ? responseStats.succeed() : responseStats.fail();
        }));
    }

    @When("all redeemed promo codes are reversed$")
    public void all_redeemed_promo_codes_are_reversed() {
        for (Long accountId : redeemedPromoCodeAccountIds) {
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + testContext().promoCode + "/reverse/" + accountId, null);
        }
    }

    @Then("^after the tests finish the results satisfy following stats")
    public void after_the_tests_finish_the_results_satisfy_following_stats(List<ExpectedPerformance> expectedPerformances) {
        Map<String, DescriptiveStatistics> performanceTestResults = statsConsumer.getStatsGroupedByRequestGroupAndResult();
        StringBuffer loggerResultsTable = new StringBuffer();
        StringBuffer loggerResultsCSV = new StringBuffer();

        double totalRequests = 0;
        double totalBatchGenerateTime = 0;

        loggerResultsTable.append("******** Performance results *****<br>")
                .append("---- Summary " + now().toString(_24HOUR_TIME) + "  ----<br>");

        performanceTestResults.forEach((k, v) -> loggerResultsTable.append("<br>Request:" + k + " N:" + v.getN()));

        loggerResultsTable.append("<br>---- Details ----")
                .append("<br><br><table><tr><td><b>Request</b></td><td><b>No of Requests</b><td><b>Mean for 95% of Requests(ms)</b></td><td><b>Max Acceptable Mean for 95% of Requests(ms)</b></td><td><b>Mean for all requests(ms)</b></td><td><b>Max Acceptable Mean for all Requests(ms)</b></td><td><b>Request Failure %</b></td><td><b>Max Allowed Request Failure %</b></td><td><b>Successful Requests Per Second</b></td>");

        DelayedAssert delayedAssert = new DelayedAssert();

        boolean failAtEnd = false;
        for (ExpectedPerformance expectedPerformance : expectedPerformances) {
            DescriptiveStatistics statistics = performanceTestResults.get(expectedPerformance.requestName);
            loggerResultsTable.append("<tr><td>" + expectedPerformance.requestName + "</td>");
            loggerResultsCSV.append(expectedPerformance.requestName);

            if (statistics == null) {
                loggerResultsTable.append("<td colspan=7><b>ONLY ERRORS reported for this test -> </b> " + expectedPerformance.requestName + "</td>");
                loggerResultsCSV.append(expectedPerformance.requestName + ",0");
                failAtEnd = true;
                continue;
            }

            long n = statistics.getN();
            if (expectedPerformance.minimumNumberOfRequests != null) {
                loggerResultsTable.append("<td>" + n + "</td>");
                delayedAssert.accept(() -> assertThat(n).describedAs("Expected %s requests to be executed at least %s times but number of executions is %s", expectedPerformance.requestName, expectedPerformance.minimumNumberOfRequests, n).isGreaterThanOrEqualTo(expectedPerformance.minimumNumberOfRequests));
            }

            if (expectedPerformance.tps != null) {
                double tps = statistics.getN() / (double) perfTestDurationInSeconds;
                loggerResultsTable.append("<td>" + tps + "</td>");
                delayedAssert.accept(() -> assertThat(tps).describedAs("Expected %s requests to have TPS >= %s but was %s . Executed %s requests in %s minutes", expectedPerformance.requestName, expectedPerformance.tps, tps, statistics.getN(), perfTestDurationInSeconds / 60).isGreaterThanOrEqualTo(expectedPerformance.tps));
            }

            if (expectedPerformance.percentile95th != null) {
                double percentile = statistics.getPercentile(95);
                loggerResultsTable.append("<td>" + Integer.toString((int) percentile) + "</td><td>" + expectedPerformance.percentile95th + "</td>");
                delayedAssert.accept(() -> assertThat(percentile).describedAs("Expected %s requests to have 95th percentile <= %s but was %s", expectedPerformance.requestName, expectedPerformance.percentile95th, percentile).isLessThanOrEqualTo(expectedPerformance.percentile95th));
            }
            if (expectedPerformance.mean != null) {
                double mean = statistics.getMean();
                loggerResultsTable.append("<td>" + Integer.toString((int) mean) + "</td><td>" + expectedPerformance.mean + "</td>");
                delayedAssert.accept(() -> assertThat(mean).describedAs("Expected %s requests to have mean <= %s but was %s", expectedPerformance.requestName, expectedPerformance.mean, mean).isLessThanOrEqualTo(expectedPerformance.mean));
            }

            if (expectedPerformance.maxFailurePercentage != null) {
                DescriptiveStatistics failedStats = performanceTestResults.get(expectedPerformance.failedRequestName());
                long failedN = failedStats != null ? failedStats.getN() : 0;
                double totalN = n + failedN;
                double failedPercentage = (double) failedN / totalN * 100;
                loggerResultsTable.append("<td>" + (double) Math.round(failedPercentage * 100000) / 100000 + "</td><td>" + expectedPerformance.maxFailurePercentage + "</td>");
                delayedAssert.accept(() -> assertThat(failedPercentage).describedAs("Expected %s requests to have failure percentage <= %s but was %s", expectedPerformance.requestName, expectedPerformance.maxFailurePercentage, failedPercentage).isLessThanOrEqualTo(expectedPerformance.maxFailurePercentage));
            }

            if (expectedPerformance.mean != null) {
                if (expectedPerformance.requestName.contains("generate")) {
                    loggerResultsTable.append("<td>" + "NA" + "</td>");
                    loggerResultsCSV.append("," + "0");
                    totalBatchGenerateTime += statistics.getMean();//deduct batch creation time for TPS calculation
                } else {
                    double currentRequestTypeTPS = (1000 / statistics.getMean());
                    totalRequests = totalRequests + n;
                    loggerResultsTable.append("<td>" + (double) Math.round(currentRequestTypeTPS * 1000) / 1000 + "</td>");
                    loggerResultsCSV.append("," + (double) Math.round(currentRequestTypeTPS * 1000) / 1000);
                }
            }

            loggerResultsTable.append("</tr>");
            loggerResultsCSV.setLength(0);
        }

        double totalTimeWithoutBatchGeneration = perfTestDurationInSeconds - totalBatchGenerateTime / 1000;
        loggerResultsTable.append("<td><b>Total requests per second:</b> " + (double) Math.round(totalRequests / totalTimeWithoutBatchGeneration));
        loggerResultsTable.append("</table>");

        logToCucumberReport(loggerResultsTable.toString());
        logToCucumberReport("Total requests per second: " + (double) Math.round(totalRequests / totalTimeWithoutBatchGeneration));


        delayedAssert.execute();
        if (failAtEnd) fail("ONLY ERRORS reported for at least one test");
    }

    private String getLastCreatedObjectId() {
        return httpContext().jsonPath.getString("id");
    }

    private static void cancelProductForAccount(String productId, long accountId) {
        httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/product/" + productId + "/account/" + accountId + "/cancelled", null);
    }


    private void getPromoCodesForCampaign(String ldapUser) {
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/salesforce/campaign/" + testContext().campaignId + "/promo-codes" + ldapUser, null);
        System.out.println(httpContext().responseBody);
    }

    private void findAllCampaignsByName(String ldapQuery) {
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/salesforce/campaign" + ldapQuery + "&name=" + testContext().campaignName, null);
    }

    private void requestCreateAdditionalVoucher(String ldapUserQuery) {
        targetVmHttps();
        Integer accountNumber = nDigitPositiveInteger(8).next();
        httpContext().httpMethodJsonOut(HttpMethod.POST, "/salesforce/campaign/" + testContext().campaignId + "/voucher-batch/casenumber/" + accountNumber + ldapUserQuery, null);
    }

    private void requestVoucherDetails(String ldapUserQuery) {
        targetVmHttps();
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/salesforce/voucher/" + testContext().voucherCode + ldapUserQuery, null);
    }

    private void requestPlatformVoucherDetails() {
        targetVm();
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/signed/voucher/" + testContext().voucherCode, null);
    }

    private void requestCampaignDetails(String ldapUserQuery) {
        targetVmHttps();
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/salesforce/campaign/" + testContext().campaignId + ldapUserQuery, null);
    }

    private void requestCancelVoucher(String ldapUserQuery) {
        extractVoucherCodeFromResponse();
        targetVmHttps();
        httpContext().httpMethodJsonOut(HttpMethod.POST, "/salesforce/voucher/" + testContext().voucherCode + "/cancel" + ldapUserQuery, null);
    }

    private static void extractIdFromResponse() {
        testContext().campaignId = httpContext().jsonPath.getLong("id");
    }

    private static void extractVoucherCodeFromResponse() {
        if (testContext().voucherCode == null) {
            testContext().voucherCode = httpContext().jsonPath.get("code");
        }
    }

    private void validateCucumberDataAgainstJson(DataTable dataTable) {
        List<String> headers = dataTable.topCells();
        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            for (String path : headers) {
                assertProperty(path, row.get(path));
            }
        }
    }

    public static void createCampaign(String type, String name, String start, String end, String offerId) {
        StringBuilder jsonOut = new StringBuilder();
        jsonOut.append("{\"type\":\"").append(type).append("\",")
                .append("\"name\":\"").append(name).append("\",")
                .append("\"prefix\":\"NOW\",\"cancelled\":false,\"pomOfferId\":\"" + offerId + "\"");

        if (start != null) {
            jsonOut.append(",\"start\":\"").append(start).append("\"");
        }
        if (end != null) {
            jsonOut.append(",\"end\":\"").append(end).append("\"");
        }
        jsonOut.append("}");

        httpContext().httpMethodJsonOut(HttpMethod.POST, "/campaign", jsonOut.toString());
    }


    private static void logToCucumberReport(String s) {
        scenario.write("    " + s + "           ");
    }

    private Supplier<Generator<ResponseStats>> createdGenerators(List<Tuple<Integer, Generator<ResponseStats>>> actions) {
        return () -> createGeneratorsInternal(actions, 100);
    }

    private Tuple<String, Optional<String>> getPendingSUV() {
        try {
            return pendingSUVs.poll(5, SECONDS);
        } catch (InterruptedException e) {
            throw propagate(e);
        }
    }

    private void addPendingSUV(Tuple<String, Optional<String>> tuple) {
        pendingSUVs.offer(tuple);
    }

    private Tuple<String, Optional<String>> getActiveSUV() {
        try {
            return activeSUVs.poll(5, SECONDS);
        } catch (InterruptedException e) {
            throw propagate(e);
        }
    }

    private Tuple<String, String> getActiveSUNT() {
        try {
            return activeSUNTVouchers.poll(5, SECONDS);
        } catch (InterruptedException e) {
            throw propagate(e);
        }
    }

    private void addActiveSUV(Tuple<String, Optional<String>> tuple) {
        activeSUVs.offer(tuple);
    }

    private void addActiveSUNT(Tuple<String, String> voucherCodeAndAccountId) {
        activeSUNTVouchers.offer(voucherCodeAndAccountId);
    }

    private Tuple<String, Optional<String>> getCancelledSUV() {
        try {
            return cancelledSUVs.poll(5, SECONDS);
        } catch (InterruptedException e) {
            throw propagate(e);
        }
    }

    private Tuple<String, String> getCancelledSUNT() {
        try {
            return cancelledSUNTVouchers.poll(5, SECONDS);
        } catch (InterruptedException e) {
            throw propagate(e);
        }
    }

    private void addCancelledSUV(Tuple<String, Optional<String>> tuple) {
        cancelledSUVs.offer(tuple);
    }

    private void addCancelledSUNT(Tuple<String, String> tuple) {
        cancelledSUNTVouchers.offer(tuple);
    }

    private Tuple<String, Optional<String>> getRedeemedSUVcodeAndAccountId() {
        try {
            return redeemedSUVcodeAndAccountId.poll(5, SECONDS);
        } catch (InterruptedException e) {
            throw propagate(e);
        }
    }

    private Tuple<String, String> getRedeemedSUNTcodeAndAccountId() {
        try {
            return redeemedSUNTcodeAndAccountId.poll(5, SECONDS);
        } catch (InterruptedException e) {
            throw propagate(e);
        }
    }

    private void addRedeemedSUVcodeAndAccountId(Tuple<String, Optional<String>> tuple) {
        redeemedSUVcodeAndAccountId.offer(tuple);
    }

    private void addRedeemedSUNTcodeAndAccountId(Tuple<String, String> voucherCodeAccountId) {
        redeemedSUNTcodeAndAccountId.offer(voucherCodeAccountId);
    }

    private Long getRedeemedPromoCodeAccountId() {
        try {
            return redeemedPromoCodeAccountIds.poll(5, SECONDS);
        } catch (InterruptedException e) {
            throw propagate(e);
        }
    }

    private void addRedeemedPromoCodeAccountId(Long accountId) {
        redeemedPromoCodeAccountIds.offer(accountId);
    }

    private boolean requestWasSuccessful() {
        return httpContext().responseCode == 200;
    }

    private Generator<ResponseStats> createGeneratorsInternal(List<Tuple<Integer, Generator<ResponseStats>>> actions, double percentageLeft) {
        if (percentageLeft <= 0 && actions.isEmpty()) {
            throw new IllegalArgumentException("There are actions left but percentage is <= 0");
        } else if (actions.size() == 1) {
            return actions.get(0).getSecond();
        }

        Integer actionPercentage = actions.get(0).getFirst();
        int adjustedPercentage = (int) (actionPercentage / percentageLeft * 100);
        Generator<ResponseStats> action = actions.get(0).getSecond();
        return new BiasedGenerator<>(adjustedPercentage, action, createGeneratorsInternal(actions.subList(1, actions.size()), percentageLeft - actionPercentage));
    }

    @And("^(\\d+)% of requests will be to check if a SUNT can be redeemed$")
    public void _of_requests_will_be_to_check_if_a_SUNT_can_be_redeemed(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Tuple<String, String> tuple = getActiveSUNT();
            if (tuple == null) return new ResponseStats("VM-dryRunRedeemSUNT-wait").fail();
            String voucherCode = tuple.getFirst();
            String accountId = tuple.getSecond();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-dryRunRedeemSUNT");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + voucherCode + "/redeem/" + accountId + "?dryRun=true", null);
            return requestWasSuccessful() ? responseStats.succeed() : responseStats.fail();
        }));
    }

    @And("^(\\d+)% of requests will be to redeem a SUNT voucher$")
    public void _of_requests_will_be_to_redeem_a_SUNT_voucher(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Tuple<String, String> tuple = getActiveSUNT();
            if (tuple == null) return new ResponseStats("VM-redeemSUNT-wait").fail();
            String voucherCode = tuple.getFirst();
            String accountId = tuple.getSecond();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-redeemSUNT");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + voucherCode + "/redeem/" + accountId, null);
            if (requestWasSuccessful()) {
                addRedeemedSUNTcodeAndAccountId(tuple);
                return responseStats.succeed();
            }
            return responseStats.fail();
        }));
    }

    @And("^(\\d+)% of requests will be to reverse a redeemed SUNT$")
    public void _of_requests_will_be_to_reverse_a_redeemed_SUNT(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Tuple<String, String> tuple = getRedeemedSUNTcodeAndAccountId();
            if (tuple == null) return new ResponseStats("VM-reverseRedeemSUNT-wait").fail();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-reverseRedeemSUNT");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + tuple.getFirst() + "/reverse/" + tuple.getSecond(), null);
            if (requestWasSuccessful()) {
                addActiveSUNT(tuple);
                return responseStats.succeed();
            }
            return responseStats.fail();
        }));
    }

    @And("^(\\d+)% of requests will be to cancel an active SUNT$")
    public void _of_requests_will_be_to_cancel_an_active_SUNT(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Tuple<String, String> tuple = getActiveSUNT();
            if (tuple == null) return new ResponseStats("VM-cancelActiveSUNT-wait").fail();
            String voucherCode = tuple.getFirst();

            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-cancelActiveSUNT");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + voucherCode + "/cancel", null);
            if (requestWasSuccessful()) {
                addCancelledSUNT(tuple);
                return responseStats.succeed();
            }
            return responseStats.fail();
        }));
    }

    @And("^(\\d+)% of requests will be to activate a cancelled SUNT$")
    public void _of_requests_will_be_to_activate_a_cancelled_SUNT(int percent) {
        actions.add(Tuple.tuple(percent, () -> {
            Tuple<String, String> tuple = getCancelledSUNT();
            if (tuple == null) return new ResponseStats("VM-activateCancelledSUNT-wait").fail();
            String voucherCode = tuple.getFirst();
            i_am_authorised_as_platform_for_VM();
            ResponseStats responseStats = new ResponseStats("VM-activateCancelledSUNT");
            httpContext().httpMethodJsonOut(HttpMethod.POST, "/signed/voucher/" + voucherCode + "/activate", null);
            if (requestWasSuccessful()) {
                addActiveSUNT(tuple);
                return responseStats.succeed();
            }
            return responseStats.fail();
        }));
    }

    private static Accounts loadAccounts() {
        try (InputStream accountStream = VoucherManagerStepdefs.class.getResourceAsStream("/accountIds.json")) {
            return objectMapper.readValue(accountStream, Accounts.class);
        } catch (IOException e) {
            throw propagate(e);
        }
    }


    @And("I get another random account")
    public static long pickAccountId() {
        if (accounts == null) {
            loadAccounts();
        }
        return Long.parseLong(accounts.getRandomAccount());
    }

}
