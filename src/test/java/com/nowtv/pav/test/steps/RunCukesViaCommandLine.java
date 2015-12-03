package com.nowtv.pav.test.steps;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(strict = true, plugin = {"pretty", "json:build/reports/cucumber.json", "html:build/reports/"}, features = "classpath:features")
public class RunCukesViaCommandLine {
}
