package com.nowtv.pav.test;

import com.nowtv.pav.test.steps.RunCukesViaCommandLine;
import org.junit.runner.JUnitCore;

public class CucumberTestMain {
	
	public CucumberTestMain() {
		JUnitCore junit = new JUnitCore();
		junit.run(RunCukesViaCommandLine.class);
	}
	
	public static void main(String[] args) {
		new CucumberTestMain();
	}
	
}
