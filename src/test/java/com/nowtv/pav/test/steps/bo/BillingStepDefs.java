package com.nowtv.pav.test.steps.bo;

import com.nowtv.pav.test.steps.GenericMethods;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.http.HttpMethod;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import static com.nowtv.pav.test.rest.HttpContext.httpContext;
import static org.assertj.core.api.Assertions.assertThat;

public class BillingStepDefs extends GenericMethods {

    private static HashMap<String, String[]> typeOfBillWithBillingAccountNumber;

    static {
        typeOfBillWithBillingAccountNumber = new HashMap<>();

        //############# Define bill types here, [Bill Type. Account Number, BillNumber]
        typeOfBillWithBillingAccountNumber.put("1 bills", new String[]{"621175377046", ""});
        typeOfBillWithBillingAccountNumber.put("3 bills", new String[]{"621175677494", ""});
        typeOfBillWithBillingAccountNumber.put("18 bills", new String[]{"621175677593", ""});

        typeOfBillWithBillingAccountNumber.put("1 prorated bill", new String[]{"621175378887", "722573002-1"});
        typeOfBillWithBillingAccountNumber.put("tv talk and broadband charges", new String[]{"621175677593", "722563002-1"});
        typeOfBillWithBillingAccountNumber.put("Line-Movies-Broadband", new String[]{"621175377046", "722522002-1"});

    }

    private static String getBillingAccountNumberWithBills(String bills) {
        return typeOfBillWithBillingAccountNumber.get(bills + " bills")[0];
    }

    private static String getBillingIDOfType(String accountType) {
        return typeOfBillWithBillingAccountNumber.get(accountType)[1];
    }

    private static String getBillingAccountOfType(String accountType) {
        return typeOfBillWithBillingAccountNumber.get(accountType)[0];
    }

    private Scenario scenario;

    @Before
    public void before(Scenario scenario) {

        this.scenario = scenario;
        httpContext().scenario = this.scenario;
    }

    @Given("^I add a valid billing account number header with (.*) bills?$")
    public void i_add_a_valid_billing_account_number_header(String bills) {
        httpContext().requestHeaders.add("X-Billing-Account-Number", getBillingAccountNumberWithBills(bills));
    }

    @Given("^I add a billing account number header \"(.*)\"$")
    public void i_add_billing_account_number_header(String accountNumber) {
        httpContext().requestHeaders.add("X-Billing-Account-Number", accountNumber);
    }

    @When("^I view bill details for a bill of the type \"([^\"]*)\"$")
    public void I_view_bill_details_for_a_bill_of_the_type(String billingType) throws Throwable {

        httpContext().requestHeaders.add("X-Billing-Account-Number", getBillingAccountOfType(billingType));
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/bills/" + getBillingIDOfType(billingType), null);
    }

    @Then("^the bill total for the bundle is computed correctly$")
    public void the_bill_total_for_bundle_is_computed_correctly() {

        List<Object> itemsList = httpContext().jsonPath.get("subscriptionCharges.bundle.charges.amount");
        BigDecimal itemsTotal = BigDecimal.valueOf(0.0);

        for (Object obj : itemsList) {
            if (obj instanceof Integer) {
                itemsTotal = itemsTotal.add(BigDecimal.valueOf((Integer) obj));
            } else if (obj instanceof BigDecimal) {
                itemsTotal = itemsTotal.add((BigDecimal) obj);
            }
        }
        String responseTotal = httpContext().jsonPath.get("subscriptionCharges.bundle.total").toString();
        assertThat(itemsTotal.toString()).isEqualTo(responseTotal);
    }


    @Then("^the bill total for prorated charges is computed correctly$")
    public void the_bill_total_for_prorated_charges_is_computed_correctly() {

            List<Object> itemsList = httpContext().jsonPath.get("proratedCharges.periods[0].bundle.charges.amount");
            BigDecimal itemsTotal = BigDecimal.valueOf(0.0);

            for (Object obj : itemsList) {
                if (obj instanceof Integer) {
                    itemsTotal = itemsTotal.add(BigDecimal.valueOf((Integer) obj));
                } else if (obj instanceof BigDecimal) {
                    itemsTotal = itemsTotal.add((BigDecimal) obj);
                }
            }

            String responseTotal =  httpContext().jsonPath.get("proratedCharges.periods[0].total").toString();
            assertThat(itemsTotal.toString()).isEqualTo(responseTotal);
    }

    @Then("^the total subscription charges are computed correctly$")
    public void the_total_subscription_charges_are_computed_correctly() throws Throwable {

        BigDecimal bundleTotal = httpContext().jsonPath.get("subscriptionCharges.bundle.total");
        BigDecimal subscriptionsTotal = httpContext().jsonPath.get("subscriptionCharges.total");
        assertThat(bundleTotal).isEqualTo(subscriptionsTotal);

    }
}
