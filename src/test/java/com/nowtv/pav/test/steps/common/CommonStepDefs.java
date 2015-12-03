package com.nowtv.pav.test.steps.common;

import com.nowtv.pav.test.steps.GenericMethods;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.hamcrest.MatcherAssert;
import org.springframework.http.HttpMethod;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static com.nowtv.pav.test.rest.HttpContext.httpContext;
import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;


public class CommonStepDefs extends GenericMethods {

    @SuppressWarnings("unused")
    private Scenario scenario;

    @Before
    public void before(Scenario scenario) {

        this.scenario = scenario;
        httpContext().scenario = this.scenario;
    }

    private void writeToCucumberReport(String linkText, String bodyText) {

        String timeStamp = String.valueOf(currentTimeMillis());
        String writeHTMLStart = "<a onclick=\"attachment=document.getElementById('" + timeStamp + "'); attachment.style.display = (attachment.style.display == 'none' ? 'block' : 'none');return false\">" + linkText + "</a><div id=\"" + timeStamp + "\" style=\"max-width: 250px; display: none;\">";
        String writeHTMLEnd = "</div><br>";
        scenario.write(writeHTMLStart + bodyText + writeHTMLEnd);

    }


    @When("^I check VM status$")
    public void I_check_VM_status() {
        targetVm();
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/status", null);
    }

    @When("^I check CBS status$")
    public void I_check_CBS_status() {
        targetCBS();
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/status", null);
    }

    @When("^I check PO status$")
    public void I_check_PO_status() {
        targetPO();
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/status", null);
    }

    @When("^I check BO status$")

    public void I_check_BO_status() {
        targetBO();
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/status", null);
    }

    @When("^I check SO status$")
    public void I_check_SO_status() {
        targetSO();
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/status", null);
    }

    @Given("^I target the (VM|VMHTTPS|SO|PO|BO|CBS) environment$")
    public void I_target_the_environment(String Environment) {
        if (Environment.equalsIgnoreCase("VM")) {
            targetVm();
        } else if (Environment.equalsIgnoreCase("VMHTTPS")) {
            targetVmHttps();
        } else if (Environment.equalsIgnoreCase("SO")) {
            targetSO();
        } else if (Environment.equalsIgnoreCase("PO")) {
            targetPO();
        } else if (Environment.equalsIgnoreCase("BO")) {
            targetBO();
        } else if (Environment.equalsIgnoreCase("CBS")) {
            targetCBS();
        }
    }

    @Given("^I target the \"([^\"]*)\" CBS service$")
    public void I_hit_the_cbs_service_environment(String CBSService) {

    }

    @Given(("^I authenticate with user \"([^\"]*)\" and password \"([^\"]*)\"$"))
    public void iAuthenticateWith(String user, String password) {
        httpContext().useBasicAuthorisation(user, password);
    }

    @Then("^the response body matches the template \"([^\"]*)\"$")
    public void Response_body_matches_template(String jsonFile) throws IOException {
        File file = new File("src/test/resources/jsonSchemas", jsonFile + ".json");

        Path json_path = Paths.get(file.getAbsolutePath());
        Charset charset = Charset.forName("ISO-8859-1");
        List<String> lines = Files.readAllLines(json_path, charset);

        httpContext().writeEmbeddedHTMLStart("json schema:");
        httpContext().attachToCucumberReport(lines.toString());
        httpContext().writeEmbeddedHTMLEnd();

        MatcherAssert.assertThat(httpContext().responseBody, matchesJsonSchemaInClasspath("jsonSchemas/" + jsonFile + ".json"));

    }

    @Then("^the number of items in the json path \"([^\"]*)\" is \"([^\"]*)\"$")
    public void the_number_of_items_in_the_json_path_is(String path, int expectedItemsSize) throws Throwable {

        List<String> itemsList = httpContext().jsonPath.get(path);
        assertThat(itemsList.size()).isEqualTo(expectedItemsSize);
    }

}
