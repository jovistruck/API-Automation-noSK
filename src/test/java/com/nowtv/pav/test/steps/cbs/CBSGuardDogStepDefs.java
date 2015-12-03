package com.nowtv.pav.test.steps.cbs;

import com.nowtv.pav.test.steps.GenericMethods;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;

import static com.nowtv.pav.test.rest.HttpContext.httpContext;

public class CBSGuardDogStepDefs extends GenericMethods{

    private Scenario scenario;

    @Before
    public void before(cucumber.api.Scenario scenario) {
        this.scenario = scenario;
        httpContext().scenario = this.scenario;
        try{
            cucumberReportWriteEnable(false);
            mapCBSServicesForPO();
            mapCBSServicesForBO();
            mapCBSServicesForSO();
        }finally {
            cucumberReportWriteEnable(true);
        }
    }

    @Given("^I target the CBS service \"([^\"]*)\"$")
    public void I_target_the_CBS_service(String CBSService) throws Throwable {
        /*
            Key: sal-order-development-cloud
            Key: cbs-billing-development-cloud
            Key: lws-address-development-cloud
            Key: ces-catalogue-development-cloud
            Key: cbs-interaction-development-cloud
            Key: umv
        */
        //printCBSServiceMap();
        targetCBSService(CBSService);
    }




}
