package com.nowtv.pav.test.steps.po;

import com.nowtv.pav.test.steps.GenericMethods;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import org.springframework.http.HttpMethod;

import static com.nowtv.pav.test.rest.HttpContext.httpContext;

public class PaymentStepDefs extends GenericMethods {

    private Scenario scenario;

    @Before
    public void before(Scenario scenario) {

        this.scenario = scenario;
        httpContext().scenario=this.scenario;
        interactionReference = null; // reset
    }

    @When("^I set the payment details with$")
    public void i_set_the_payment_method_with(String body) {
        httpContext().httpMethodJsonOut(HttpMethod.PUT, String.format("/interactions/%s/payment-method", interactionReference), body);
    }

}
