package com.nowtv.pav.test.steps.po;

import com.nowtv.pav.test.steps.GenericMethods;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import org.springframework.http.HttpMethod;

import static com.nowtv.pav.test.rest.HttpContext.httpContext;

public class OrdersStepDefs extends GenericMethods {

    private Scenario scenario;

    @Before
    public void before(Scenario scenario) {

        this.scenario = scenario;
        httpContext().scenario=this.scenario;
    }

    @And("^I create an order with the body$")
    public void I_create_an_order_with_body(String body) throws Throwable {
        httpContext().httpMethodJsonOut(HttpMethod.POST, String.format("/interactions/%s/order", interactionReference), body);
    }

    @When("^I create an order with invalid interaction with body$")
    public void I_create_an_order_with_invalid_interaction(String body) throws Throwable {
        httpContext().httpMethodJsonOut(HttpMethod.POST, String.format("/interactions/%s/order", interactionReference + "INVALID"), body);
    }
}