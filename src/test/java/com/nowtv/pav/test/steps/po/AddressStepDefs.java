package com.nowtv.pav.test.steps.po;

import com.jayway.restassured.path.json.JsonPath;
import com.nowtv.pav.test.steps.GenericMethods;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpMethod;

import static com.nowtv.pav.test.rest.HttpContext.httpContext;

public class AddressStepDefs extends GenericMethods {

    private Scenario scenario;

    @Before
    public void before(Scenario scenario) {

        this.scenario = scenario;
        httpContext().scenario=this.scenario;
    }

    @And("^I add an address with the request body as$")
    public void I_add_an_address_with_body(String requestBody) {
        postAddress(requestBody);
    }

    @And("^I add an address with name \"([^\"]*)\" with purposes? \"([^\"]*)\"$")
    public void I_add_an_address(String addressName, String purposes) throws Throwable {
        String[] addressPurposes = purposes.split(",");
        String addressPurp = "";
        for (String purp : addressPurposes) {
            addressPurp += ",\"" + purp.toUpperCase() + "\"";
        }
        addressPurp = addressPurp.substring(1);
        String requestBody = createAddressRequestBody(addressName, addressPurp);
        postAddress(requestBody);
    }

    @And("^I add an address with the body$")
    public void I_add_an_address_with_the_body(String addressBody) throws Throwable {
        postAddress(addressBody);
    }

    @And("^I add an address with name \"([^\"]*)\"$")
    public void I_add_an_address_with_no_purpose(String addressName) throws Throwable {
        String requestBody = createAddressRequestBody(addressName, "");
        postAddress(requestBody);
    }

    @And("^the single address references match \"([^\"]*)\"$")
    public void the_address_references_match(String addressTypes) throws Throwable {
        JsonPath jsonPath = httpContext().jsonPath;
        String singleAddressReference = jsonPath.get("addresses.reference").toString();

        for (String addr : addressTypes.split(",")) {
            Assertions.assertThat(singleAddressReference.equalsIgnoreCase(jsonPath.get("keyAddresses." + addr + "AddressRef")));
            Assertions.assertThat(singleAddressReference.equalsIgnoreCase(jsonPath.get("orderAddresses." + addr + "Address.reference")));
        }
    }

    private void postAddress(String address) {
        httpContext().httpMethodJsonOut(HttpMethod.POST, String.format("/interactions/%s/addresses", interactionReference), address);
    }

    private String createAddressRequestBody(String addressName, String addressPurposes) {
        return "   {\n" +
                " \"name\": \"" + addressName + "\",\n" +
                " \"purposes\" : [ " + addressPurposes + " ],\n" +
                " \"houseNumber\" : \"1\",\n" +
                " \"houseName\" : \"Athena Court\",\n" +
                " \"street\" : \"Grant Way\",\n" +
                " \"locality\" : \"Syon Lane\",\n" +
                " \"town\" : \"IsleWorth\",\n" +
                " \"county\" : \"Middlesex\",\n" +
                " \"postcode\" : \"TW7 5DQ\",\n" +
                "  \"countryCode\" : \"GBR\"\n" +
                "    }";
    }

}
