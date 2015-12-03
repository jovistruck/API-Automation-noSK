package com.nowtv.pav.test.steps;

public class ExpectedPerformance {
    Integer minimumNumberOfRequests;
    String requestName;
    Double percentile95th;
    Double mean;
    Double tps;
    Double maxFailurePercentage;

    public String failedRequestName() {
        return requestName + "-failed";
    }
}
