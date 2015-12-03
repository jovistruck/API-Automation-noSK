package com.nowtv.pav.test.steps;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.JsonPath;
import com.nowtv.pav.test.rest.HttpContext;
import org.springframework.http.HttpMethod;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.nowtv.pav.test.rest.HttpContext.httpContext;

public class GenericMethods {

    public static int CALLED = 0;

    protected static final String VM_URL = System.getProperty("VM_URL", "https://vouchers-nowtv-stage.sky.com/");
    protected static final String PO_URL = System.getProperty("PO_URL", "http://development-po.cf.dev-paas.bskyb.com");
    protected static final String BO_URL = System.getProperty("BO_URL", "http://development-bo.cf.dev-paas.bskyb.com");
    protected static final String SO_URL = System.getProperty("SO_URL", "http://development-so.cf.dev-paas.bskyb.com");
    protected static final String CBS_URL = System.getProperty("CBS_URL", "http://localhost:8081");

    protected static String interactionReference;
    protected static String firstProductReference;
    protected static String firstOfferReference;
    protected static String lastProductReference;
    protected static String lastOfferReference;
    protected static Multimap<String, String> CBSServicesMap = ArrayListMultimap.create();

    protected static void targetVm() {
        httpContext().integrationHost = VM_URL;
    }

    protected static void targetCBS() {
        httpContext().integrationHost = CBS_URL;
    }

    protected static void targetPO() {
        httpContext().integrationHost = PO_URL;
    }

    protected static void targetBO() {
        httpContext().integrationHost = BO_URL;
    }

    protected static void targetSO() {
        httpContext().integrationHost = SO_URL;
    }

    protected static void targetCBSService(String serviceName) {

        List<String> serviceDetails = (List<String>) CBSServicesMap.get(serviceName);
        String serviceUrl = serviceDetails.get(0);

        httpContext().integrationHost = serviceUrl;
        httpContext().resetHeaders();

        if (serviceDetails.size() > 1) {
            String serviceUserName = serviceDetails.get(1);
            String servicePassword = serviceDetails.get(2);
            httpContext().useBasicAuthorisation(serviceUserName, servicePassword);
        }
    }

    protected static void targetVmHttps() {
        httpContext().integrationHost = VM_URL.replace("http:", "https:");
    }

    protected static void mapCBSServicesForPO() {

        targetPO();
        setAuthHeadersForAdminApp();
        String jsonOfServices = httpContext().jsonPath.get("VCAP_SERVICES");


        System.out.println(jsonOfServices);

        List<String> serviceNames = JsonPath.read(jsonOfServices, "$.user-provided[*].name");
        List<String> serviceURLs = JsonPath.read(jsonOfServices, "$.user-provided[*].credentials.url");
        List<String> serviceUsername = JsonPath.read(jsonOfServices, "$.user-provided[*].credentials.username");
        serviceUsername.addAll(JsonPath.read(jsonOfServices, "$.user-provided[*].credentials.clientName"));
        List<String> servicePassword = JsonPath.read(jsonOfServices, "$.user-provided[*].credentials.password");
        servicePassword.addAll(JsonPath.read(jsonOfServices, "$.user-provided[*].credentials.secret"));

        for (int i = 0; i < serviceNames.size(); i++) {
            CBSServicesMap.put(serviceNames.get(i), serviceURLs.get(i));
            CBSServicesMap.put(serviceNames.get(i), serviceUsername.get(i));
            CBSServicesMap.put(serviceNames.get(i), servicePassword.get(i));

        }
    }

    protected static void mapCBSServicesForBO() {

        targetBO();
        setAuthHeadersForAdminApp();
        String jsonOfServices = httpContext().jsonPath.get("VCAP_SERVICES");

        httpContext().attachToCucumberReport(CBSServicesMap.toString());

        List<String> serviceNames = JsonPath.read(jsonOfServices, "$.user-provided[*].name");

        List<String> serviceURLs = JsonPath.read(jsonOfServices, "$.user-provided[*].credentials.url");
        httpContext().attachToCucumberReport(serviceNames.toString());
        httpContext().attachToCucumberReport(serviceURLs.toString());
        httpContext().attachToCucumberReport(String.valueOf(serviceNames.size()));
        httpContext().attachToCucumberReport(String.valueOf(serviceURLs.size()));

        httpContext().attachToCucumberReport(CBSServicesMap.toString());

        for (int i = 0; i < serviceNames.size(); i++) {
            httpContext().attachToCucumberReport("I AM IN");
            CBSServicesMap.put(serviceNames.get(i), serviceURLs.get(i));
        }
        httpContext().attachToCucumberReport(CBSServicesMap.toString());

    }


    protected static void mapCBSServicesForSO() {

        targetSO();
        setAuthHeadersForAdminApp();
        String jsonOfServices = httpContext().jsonPath.get("VCAP_SERVICES");

        List<String> serviceNames = JsonPath.read(jsonOfServices, "$.user-provided[*].name");
        List<String> serviceURLs = JsonPath.read(jsonOfServices, "$.user-provided[*].credentials.url");
        List<String> serviceUsername = JsonPath.read(jsonOfServices, "$.user-provided[*].credentials.username");
        serviceUsername.addAll(JsonPath.read(jsonOfServices, "$.user-provided[*].credentials.clientName"));
        List<String> servicePassword = JsonPath.read(jsonOfServices, "$.user-provided[*].credentials.password");
        servicePassword.addAll(JsonPath.read(jsonOfServices, "$.user-provided[*].credentials.secret"));

        for (int i = 0; i < serviceNames.size(); i++) {
            CBSServicesMap.put(serviceNames.get(i), serviceURLs.get(i));
            CBSServicesMap.put(serviceNames.get(i), serviceUsername.get(i));
            CBSServicesMap.put(serviceNames.get(i), servicePassword.get(i));
        }

    }

    protected static void printCBSServiceMap() {
        Set keySet = CBSServicesMap.keySet();
        Iterator keyIterator = keySet.iterator();

        while (keyIterator.hasNext()) {
            Object key = keyIterator.next();
            httpContext().attachToCucumberReport("Key: " + key);

            Collection values = (Collection) CBSServicesMap.get((String) key);
            Iterator valuesIterator = values.iterator();
            while (valuesIterator.hasNext()) {
                httpContext().attachToCucumberReport("Value: " + valuesIterator.next());
            }
        }
    }

    private static void setAuthHeadersForAdminApp() {
        httpContext().resetHeaders();
        httpContext().useBasicAuthorisation("admin", "p4v4dm1n");
        httpContext().httpMethodJsonOut(HttpMethod.GET, "/admin/env", null);
    }

    protected static void cucumberReportWriteEnable(Boolean writeEnable) {
        HttpContext.cucumberReportWriteEnable = writeEnable;
    }

}