package com.nowtv.pav.test.steps.po;

import com.nowtv.pav.test.steps.GenericMethods;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import org.springframework.http.HttpMethod;

import static com.nowtv.pav.test.rest.HttpContext.httpContext;

public class CustomerStepDefs extends GenericMethods {

    private Scenario scenario;

    @Before
    public void before(Scenario scenario) {

        this.scenario = scenario;
        httpContext().scenario=this.scenario;
        interactionReference = null; // reset
    }

    @And("^I update the customer with$")
    public void I_update_the_customer_with(String requestBody) throws Throwable {
        httpContext().httpMethodJsonOut(HttpMethod.PUT, String.format("/interactions/%s/customer", interactionReference), requestBody);
    }

}
