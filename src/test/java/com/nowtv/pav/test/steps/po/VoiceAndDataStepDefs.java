package com.nowtv.pav.test.steps.po;

import com.nowtv.pav.test.steps.GenericMethods;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import org.springframework.http.HttpMethod;

import static com.nowtv.pav.test.rest.HttpContext.httpContext;

public class VoiceAndDataStepDefs extends GenericMethods {

    private Scenario scenario;

    @Before
    public void before(Scenario scenario) {

        this.scenario = scenario;
        httpContext().scenario=this.scenario;
        interactionReference = null; // reset
    }

    @When("^I set voice and data with$")
    public void i_get_voice_and_data_with(String body) {
        httpContext().httpMethodJsonOut(HttpMethod.PUT, String.format("/interactions/%s/voice-and-data/target", interactionReference), body);
    }
}
