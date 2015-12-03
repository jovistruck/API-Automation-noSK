package com.nowtv.pav.test.steps.po;

import com.nowtv.pav.test.rest.HttpContext;
import com.nowtv.pav.test.steps.GenericMethods;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpMethod;

import static com.nowtv.pav.test.rest.HttpContext.httpContext;

public class InteractionStepDefs extends GenericMethods {

    private Scenario scenario;

    @Before
    public void before(Scenario scenario) {

        this.scenario = scenario;
        httpContext().scenario=this.scenario;
        interactionReference = null; // reset
    }

    @When("^I get the available catalogue$")
    public void i_get_the_available_catalogue() {
        httpContext().httpMethodJsonOut(HttpMethod.GET, String.format("/interactions/%s/catalogue/products", interactionReference), null);
    }

    @When("^I create an interaction reference$")
    public void i_create_an_interaction_reference() {
        httpContext().httpMethodJsonOut(HttpMethod.POST, "/interactions", null);
        Assertions.assertThat(HttpContext.httpContext().responseCode).isEqualTo(201);
        interactionReference = httpContext().jsonPath.getString("reference");
        System.out.println("creating interaction ref: " + interactionReference);
    }

    @When("^I successfully create an interaction reference with country \"(GBR)\" and interest source (\\d+)$")
    public void i_successfully_create_an_interaction_reference_with_country_and_interest_source(String countryCode, String interestSource) {
        //not yet implemented in PO

        httpContext().httpMethodJsonOut(HttpMethod.POST, "/interactions", String.format("{\"country\":\"%s\",\"interestSource\":%s}",countryCode,interestSource));
        Assertions.assertThat(HttpContext.httpContext().responseCode).isEqualTo(201);
        interactionReference = httpContext().jsonPath.getString("reference");
        System.out.println("creating interaction ref: " + interactionReference);
    }

    @And("^the interaction reference is returned$")
    public void the_interaction_reference_is_returned() {
        Assertions.assertThat(httpContext().jsonPath.get("reference").toString()).isEqualToIgnoringCase(interactionReference);
    }

    @And("^I get the interaction$")
    public void I_get_the_interaction() throws Throwable {
        httpContext().httpMethodJsonOut(HttpMethod.GET, String.format("/interactions/%s", interactionReference), null);
    }
}
