package com.nowtv.pav.test.steps.po;

import com.nowtv.pav.test.rest.HttpContext;
import com.nowtv.pav.test.steps.GenericMethods;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpMethod;

import static com.nowtv.pav.test.rest.HttpContext.httpContext;

public class ProductStepDefs extends GenericMethods {

    private Scenario scenario;

    @Before
    public void before(Scenario scenario) {

        this.scenario = scenario;
        httpContext().scenario = this.scenario;
    }

    @And("^I add (products|offers) with code to the basket$")
    public void I_add_to_the_basket(String typeOfItem, String requestBody) {

        httpContext().httpMethodJsonOut(HttpMethod.PUT, String.format("/interactions/%s/basket", interactionReference), requestBody);

        if (typeOfItem.equals("products")) {
            firstProductReference = httpContext().jsonPath.get("basket.products[0].reference").toString();
            lastProductReference = httpContext().jsonPath.get("basket.products[-1].reference").toString();
        } else if (typeOfItem.equals("offers")) {
            firstOfferReference = httpContext().jsonPath.get("basket.offers[0].reference").toString();
            lastOfferReference = httpContext().jsonPath.get("basket.offers[-1].reference").toString();
        }
    }

    @And("^I add (products|offers) to the basket$")
    public void I_add_products_offers(String typeOfItem, String requestBody) {
        I_add_to_the_basket(typeOfItem, requestBody);
    }

    @And("^I successfully add (products|offers) with codes \"([^\"]*)\" to the basket$")
    public void I_add_products_offers_to_the_basket(String typeOfItem, String codes) {

        String requestBody;
        for (String code : codes.split(",")) {
            requestBody = "{\n" +
                    "                \"add\":{\n" +
                    "                \""+typeOfItem+"\":[{\n" +
                    "                    \"code\":\""+code+"\", \"action\":\"PROVIDE\"\n" +
                    "                }]}\n" +
                    "            }\n" +
                    "            ";
            I_add_to_the_basket(typeOfItem, requestBody);
            Assertions.assertThat(HttpContext.httpContext().responseCode).isEqualTo(201);
        }
    }

    @And("^I remove the (first|last) added (products|offers) from the basket$")
    public void I_remove_first_added_product_offer_from_the_basket(String firstOrLast, String typeOfItem) {

        String referenceType;

        if (typeOfItem.equals("products")) {
            if (firstOrLast.equals("first")) {
                referenceType = firstProductReference;
            } else {
                referenceType = lastProductReference;
            }
        } else {
            if (firstOrLast.equals("first")) {
                referenceType = firstOfferReference;
            } else {
                referenceType = lastOfferReference;
            }
        }

        String requestBody = String.format("{\"%s\":{\"%s\":[{\"basketRef\":\"%s\"}]}}", "remove", typeOfItem, referenceType);
        httpContext().httpMethodJsonOut(HttpMethod.PUT, String.format("/interactions/%s/basket", interactionReference), requestBody);
    }

    @And("^I remove the (products|offers) with (product|offers) reference \"(.*)\" from the basket$")
    public void I_remove_product_offer_with_interaction_reference(String typeOfItem, String typeOfItems, String reference, String requestBody) {

        httpContext().httpMethodJsonOut(HttpMethod.PUT, String.format("/interactions/%s/basket", interactionReference), requestBody);
        //Uses only the body. Rest of params for simplicity in reading step.
    }

    @And("^I get the lead time for the interaction$")
    public void I_get_the_lead_time_for_the_interaction() throws Throwable {

        httpContext().httpMethodJsonOut(HttpMethod.GET, String.format("/interactions/%s/lead-time", interactionReference), null);
    }
}