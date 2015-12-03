$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("features/bo/BillDetails.feature");
formatter.feature({
  "line": 2,
  "name": "BO Bill Details",
  "description": "",
  "id": "bo-bill-details",
  "keyword": "Feature",
  "tags": [
    {
      "line": 1,
      "name": "@BOBillingBillDetails"
    },
    {
      "line": 1,
      "name": "@BOBilling"
    },
    {
      "line": 1,
      "name": "@BO"
    },
    {
      "line": 1,
      "name": "@ALL"
    }
  ]
});
formatter.before({
  "duration": 106056158,
  "status": "passed"
});
formatter.before({
  "duration": 294580,
  "status": "passed"
});
formatter.before({
  "duration": 232499,
  "status": "passed"
});
formatter.before({
  "duration": 226400,
  "status": "passed"
});
formatter.before({
  "duration": 1131828482,
  "status": "passed"
});
formatter.before({
  "duration": 474175,
  "status": "passed"
});
formatter.before({
  "duration": 64494847,
  "status": "passed"
});
formatter.before({
  "duration": 377443,
  "status": "passed"
});
formatter.before({
  "duration": 321406,
  "status": "passed"
});
formatter.before({
  "duration": 284692,
  "status": "passed"
});
formatter.before({
  "duration": 345799,
  "status": "passed"
});
formatter.scenario({
  "line": 4,
  "name": "BO Bill Details (happy path)",
  "description": "",
  "id": "bo-bill-details;bo-bill-details-(happy-path)",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 5,
  "name": "I target the BO environment",
  "keyword": "Given "
});
formatter.step({
  "line": 6,
  "name": "I view bill details for a bill of the type \"Line-Movies-Broadband\"",
  "keyword": "When "
});
formatter.step({
  "line": 7,
  "name": "the response status is 200",
  "keyword": "And "
});
formatter.step({
  "line": 8,
  "name": "the response body matches the template \"bill-details\"",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "BO",
      "offset": 13
    }
  ],
  "location": "CommonStepDefs.I_target_the_environment(String)"
});
formatter.result({
  "duration": 68782842,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Line-Movies-Broadband",
      "offset": 44
    }
  ],
  "location": "BillingStepDefs.I_view_bill_details_for_a_bill_of_the_type(String)"
});
formatter.embedding("text/html", "\u003cdiv\u003e\u003cpre style\u003d\"margin-bottom: 0;\"\u003e\u003cb\u003erequest:\u003c/b\u003e\u003cbr\u003e\u003cbr\u003ehttp://development-bo.cf.dev-paas.bskyb.com/bills/722522002-1\u003cbr\u003e\u003cbr\u003eGET | {X-Billing-Account-Number\u003d[621175377046]}\u003c/pre\u003e\u003c/div\u003e\u003cbr\u003e");
formatter.embedding("text/html", "\u003cdiv\u003e\u003cpre style\u003d\"margin-bottom: 0;\"\u003e\u003cb\u003eresponse:\u003c/b\u003e\u003cbr\u003e\u003cbr\u003e200 | {Server\u003d[nginx], Date\u003d[Thu, 03 Dec 2015 16:42:27 GMT], Content-Type\u003d[application/json; charset\u003dUTF-8], Content-Length\u003d[459], Connection\u003d[keep-alive], Keep-Alive\u003d[timeout\u003d10], Access-Control-Allow-Credentials\u003d[true], Access-Control-Allow-Origin\u003d[null], X-Cf-Requestid\u003d[bce673ab-7d7a-4637-567d-9e25ab32219b]}\u003cbr\u003e\u003cbr\u003e{\n    \"total\": 99.99,\n    \"triplePlay\": true,\n    \"_links\": {\n        \"self\": {\n            \"href\": \"http://development-bo.cf.dev-paas.bskyb.com/bills/722522002-1\"\n        }\n    },\n    \"subscriptionCharges\": {\n        \"total\": 99.99,\n        \"bundle\": {\n            \"charges\": [\n                {\n                    \"amount\": 9.99,\n                    \"description\": \"Now TV Movies\"\n                },\n                {\n                    \"amount\": 40,\n                    \"description\": \"Now TV Broadband Unlimited\"\n                },\n                {\n                    \"amount\": 15,\n                    \"description\": \"Now TV Talk Evenings and Weekends Extra\"\n                },\n                {\n                    \"amount\": 35,\n                    \"description\": \"Now TV Line Rental\"\n                }\n            ],\n            \"total\": 99.99\n        }\n    },\n    \"from\": \"2015-10-23\",\n    \"to\": \"2015-11-22\"\n}\u003c/pre\u003e\u003c/div\u003e\u003cbr\u003e");
formatter.result({
  "duration": 209459901,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "200",
      "offset": 23
    }
  ],
  "location": "RestStepDefinitions.theResponseStatusIs(int)"
});
formatter.result({
  "duration": 37456855,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "bill-details",
      "offset": 40
    }
  ],
  "location": "CommonStepDefs.Response_body_matches_template(String)"
});
formatter.embedding("text/html", "[{,   \"$schema\": \"http://json-schema.org/draft-04/schema#\",,   \"id\": \"http://pav.net\",,   \"type\": \"object\",,   \"properties\": {,     \"total\": {,       \"id\": \"http://pav.net/total\",,       \"type\": \"number\",     },,     \"triplePlay\": {,       \"id\": \"http://pav.net/triplePlay\",,       \"type\": \"boolean\",     },,     \"_links\": {,       \"id\": \"http://pav.net/_links\",,       \"type\": \"object\",,       \"properties\": {,         \"self\": {,           \"id\": \"http://pav.net/_links/self\",,           \"type\": \"object\",,           \"properties\": {,             \"href\": {,               \"id\": \"http://pav.net/_links/self/href\",,               \"type\": \"string\",             },           },,           \"additionalProperties\": false,         },       },,       \"additionalProperties\": false,     },,     \"subscriptionCharges\": {,       \"id\": \"http://pav.net/subscriptionCharges\",,       \"type\": \"object\",,       \"properties\": {,         \"total\": {,           \"id\": \"http://pav.net/subscriptionCharges/total\",,           \"type\": \"number\",         },,         \"bundle\": {,           \"id\": \"http://pav.net/subscriptionCharges/bundle\",,           \"type\": \"object\",,           \"properties\": {,             \"charges\": {,               \"id\": \"http://pav.net/subscriptionCharges/bundle/charges\",,               \"type\": \"array\",,               \"items\": [,                 {,                   \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/0\",,                   \"type\": \"object\",,                   \"properties\": {,                     \"amount\": {,                       \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/0/amount\",,                       \"type\": \"number\",                     },,                     \"description\": {,                       \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/0/description\",,                       \"type\": \"string\",                     },                   },,                   \"additionalProperties\": false,                 },,                 {,                   \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/1\",,                   \"type\": \"object\",,                   \"properties\": {,                     \"amount\": {,                       \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/1/amount\",,                       \"type\": \"integer\",                     },,                     \"description\": {,                       \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/1/description\",,                       \"type\": \"string\",                     },                   },,                   \"additionalProperties\": false,                 },,                 {,                   \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/2\",,                   \"type\": \"object\",,                   \"properties\": {,                     \"amount\": {,                       \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/2/amount\",,                       \"type\": \"integer\",                     },,                     \"description\": {,                       \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/2/description\",,                       \"type\": \"string\",                     },                   },,                   \"additionalProperties\": false,                 },,                 {,                   \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/3\",,                   \"type\": \"object\",,                   \"properties\": {,                     \"amount\": {,                       \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/3/amount\",,                       \"type\": \"integer\",                     },,                     \"description\": {,                       \"id\": \"http://pav.net/subscriptionCharges/bundle/charges/3/description\",,                       \"type\": \"string\",                     },                   },,                   \"additionalProperties\": false,                 },               ],,               \"additionalItems\": false,             },,             \"total\": {,               \"id\": \"http://pav.net/subscriptionCharges/bundle/total\",,               \"type\": \"number\",             },           },,           \"additionalProperties\": false,         },       },,       \"additionalProperties\": false,     },,     \"from\": {,       \"id\": \"http://pav.net/from\",,       \"type\": \"string\",     },,     \"to\": {,       \"id\": \"http://pav.net/to\",,       \"type\": \"string\",     },   },,   \"additionalProperties\": false,,   \"required\": [,     \"total\",,     \"triplePlay\",,     \"_links\",,     \"subscriptionCharges\",,     \"from\",,     \"to\",   ], }]");
formatter.result({
  "duration": 225727760,
  "status": "passed"
});
formatter.before({
  "duration": 223935,
  "status": "passed"
});
formatter.before({
  "duration": 131269,
  "status": "passed"
});
formatter.before({
  "duration": 125967,
  "status": "passed"
});
formatter.before({
  "duration": 107318,
  "status": "passed"
});
formatter.before({
  "duration": 174819020,
  "status": "passed"
});
formatter.before({
  "duration": 221256,
  "status": "passed"
});
formatter.before({
  "duration": 3372283,
  "status": "passed"
});
formatter.before({
  "duration": 232394,
  "status": "passed"
});
formatter.before({
  "duration": 197335,
  "status": "passed"
});
formatter.before({
  "duration": 159011,
  "status": "passed"
});
formatter.before({
  "duration": 192359,
  "status": "passed"
});
formatter.scenario({
  "line": 10,
  "name": "Per product itemized charge calculation",
  "description": "",
  "id": "bo-bill-details;per-product-itemized-charge-calculation",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 11,
  "name": "I target the BO environment",
  "keyword": "Given "
});
formatter.step({
  "line": 12,
  "name": "I view bill details for a bill of the type \"tv talk and broadband charges\"",
  "keyword": "When "
});
formatter.step({
  "line": 13,
  "name": "the response status is 200",
  "keyword": "And "
});
formatter.step({
  "line": 14,
  "name": "the bill total for the bundle is computed correctly",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "BO",
      "offset": 13
    }
  ],
  "location": "CommonStepDefs.I_target_the_environment(String)"
});
formatter.result({
  "duration": 257124,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "tv talk and broadband charges",
      "offset": 44
    }
  ],
  "location": "BillingStepDefs.I_view_bill_details_for_a_bill_of_the_type(String)"
});
formatter.embedding("text/html", "\u003cdiv\u003e\u003cpre style\u003d\"margin-bottom: 0;\"\u003e\u003cb\u003erequest:\u003c/b\u003e\u003cbr\u003e\u003cbr\u003ehttp://development-bo.cf.dev-paas.bskyb.com/bills/722563002-1\u003cbr\u003e\u003cbr\u003eGET | {X-Billing-Account-Number\u003d[621175677593]}\u003c/pre\u003e\u003c/div\u003e\u003cbr\u003e");
formatter.embedding("text/html", "\u003cdiv\u003e\u003cpre style\u003d\"margin-bottom: 0;\"\u003e\u003cb\u003eresponse:\u003c/b\u003e\u003cbr\u003e\u003cbr\u003e200 | {Server\u003d[nginx], Date\u003d[Thu, 03 Dec 2015 16:42:27 GMT], Content-Type\u003d[application/json; charset\u003dUTF-8], Content-Length\u003d[549], Connection\u003d[keep-alive], Keep-Alive\u003d[timeout\u003d10], Access-Control-Allow-Credentials\u003d[true], Access-Control-Allow-Origin\u003d[null], X-Cf-Requestid\u003d[c94773ce-1140-4ab6-7ada-d281f2a07deb]}\u003cbr\u003e\u003cbr\u003e{\n    \"total\": -107.34,\n    \"triplePlay\": true,\n    \"_links\": {\n        \"self\": {\n            \"href\": \"http://development-bo.cf.dev-paas.bskyb.com/bills/722563002-1\"\n        }\n    },\n    \"subscriptionCharges\": {\n        \"total\": 98.98,\n        \"bundle\": {\n            \"charges\": [\n                {\n                    \"amount\": 6.99,\n                    \"description\": \"Now TV Entertainment\"\n                },\n                {\n                    \"amount\": 9.99,\n                    \"description\": \"Now TV Movies\"\n                },\n                {\n                    \"amount\": 40,\n                    \"description\": \"Now TV Broadband Unlimited\"\n                },\n                {\n                    \"amount\": 0,\n                    \"description\": \"Now TV Pay as you Talk\"\n                },\n                {\n                    \"amount\": 35,\n                    \"description\": \"Now TV Line Rental\"\n                },\n                {\n                    \"amount\": 7,\n                    \"description\": \"Anonymous Caller Reject\"\n                }\n            ],\n            \"total\": 98.98\n        }\n    },\n    \"from\": \"2017-03-06\",\n    \"to\": \"2017-04-05\"\n}\u003c/pre\u003e\u003c/div\u003e\u003cbr\u003e");
formatter.result({
  "duration": 65727599,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "200",
      "offset": 23
    }
  ],
  "location": "RestStepDefinitions.theResponseStatusIs(int)"
});
formatter.result({
  "duration": 342578,
  "status": "passed"
});
formatter.match({
  "location": "BillingStepDefs.the_bill_total_for_bundle_is_computed_correctly()"
});
formatter.result({
  "duration": 25096594,
  "status": "passed"
});
formatter.before({
  "duration": 270854,
  "status": "passed"
});
formatter.before({
  "duration": 256804,
  "status": "passed"
});
formatter.before({
  "duration": 163196,
  "status": "passed"
});
formatter.before({
  "duration": 145450,
  "status": "passed"
});
formatter.before({
  "duration": 212070817,
  "status": "passed"
});
formatter.before({
  "duration": 291632,
  "status": "passed"
});
formatter.before({
  "duration": 3257512,
  "status": "passed"
});
formatter.before({
  "duration": 134370,
  "status": "passed"
});
formatter.before({
  "duration": 82378,
  "status": "passed"
});
formatter.before({
  "duration": 80641,
  "status": "passed"
});
formatter.before({
  "duration": 79196,
  "status": "passed"
});
formatter.scenario({
  "line": 16,
  "name": "Aggregated multiple product total",
  "description": "",
  "id": "bo-bill-details;aggregated-multiple-product-total",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 17,
  "name": "I target the BO environment",
  "keyword": "Given "
});
formatter.step({
  "line": 18,
  "name": "I view bill details for a bill of the type \"tv talk and broadband charges\"",
  "keyword": "When "
});
formatter.step({
  "line": 19,
  "name": "the response status is 200",
  "keyword": "And "
});
formatter.step({
  "line": 20,
  "name": "the total subscription charges are computed correctly",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "BO",
      "offset": 13
    }
  ],
  "location": "CommonStepDefs.I_target_the_environment(String)"
});
formatter.result({
  "duration": 153151,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "tv talk and broadband charges",
      "offset": 44
    }
  ],
  "location": "BillingStepDefs.I_view_bill_details_for_a_bill_of_the_type(String)"
});
formatter.embedding("text/html", "\u003cdiv\u003e\u003cpre style\u003d\"margin-bottom: 0;\"\u003e\u003cb\u003erequest:\u003c/b\u003e\u003cbr\u003e\u003cbr\u003ehttp://development-bo.cf.dev-paas.bskyb.com/bills/722563002-1\u003cbr\u003e\u003cbr\u003eGET | {X-Billing-Account-Number\u003d[621175677593]}\u003c/pre\u003e\u003c/div\u003e\u003cbr\u003e");
formatter.embedding("text/html", "\u003cdiv\u003e\u003cpre style\u003d\"margin-bottom: 0;\"\u003e\u003cb\u003eresponse:\u003c/b\u003e\u003cbr\u003e\u003cbr\u003e200 | {Server\u003d[nginx], Date\u003d[Thu, 03 Dec 2015 16:42:28 GMT], Content-Type\u003d[application/json; charset\u003dUTF-8], Content-Length\u003d[549], Connection\u003d[keep-alive], Keep-Alive\u003d[timeout\u003d10], Access-Control-Allow-Credentials\u003d[true], Access-Control-Allow-Origin\u003d[null], X-Cf-Requestid\u003d[95d9f172-f3d8-43de-4e41-51290d9d1ca2]}\u003cbr\u003e\u003cbr\u003e{\n    \"total\": -107.34,\n    \"triplePlay\": true,\n    \"_links\": {\n        \"self\": {\n            \"href\": \"http://development-bo.cf.dev-paas.bskyb.com/bills/722563002-1\"\n        }\n    },\n    \"subscriptionCharges\": {\n        \"total\": 98.98,\n        \"bundle\": {\n            \"charges\": [\n                {\n                    \"amount\": 6.99,\n                    \"description\": \"Now TV Entertainment\"\n                },\n                {\n                    \"amount\": 9.99,\n                    \"description\": \"Now TV Movies\"\n                },\n                {\n                    \"amount\": 40,\n                    \"description\": \"Now TV Broadband Unlimited\"\n                },\n                {\n                    \"amount\": 0,\n                    \"description\": \"Now TV Pay as you Talk\"\n                },\n                {\n                    \"amount\": 35,\n                    \"description\": \"Now TV Line Rental\"\n                },\n                {\n                    \"amount\": 7,\n                    \"description\": \"Anonymous Caller Reject\"\n                }\n            ],\n            \"total\": 98.98\n        }\n    },\n    \"from\": \"2017-03-06\",\n    \"to\": \"2017-04-05\"\n}\u003c/pre\u003e\u003c/div\u003e\u003cbr\u003e");
formatter.result({
  "duration": 72204608,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "200",
      "offset": 23
    }
  ],
  "location": "RestStepDefinitions.theResponseStatusIs(int)"
});
formatter.result({
  "duration": 147920,
  "status": "passed"
});
formatter.match({
  "location": "BillingStepDefs.the_total_subscription_charges_are_computed_correctly()"
});
formatter.result({
  "duration": 16363518,
  "status": "passed"
});
formatter.before({
  "duration": 104386,
  "status": "passed"
});
formatter.before({
  "duration": 83628,
  "status": "passed"
});
formatter.before({
  "duration": 71863,
  "status": "passed"
});
formatter.before({
  "duration": 55619,
  "status": "passed"
});
formatter.before({
  "duration": 165118220,
  "status": "passed"
});
formatter.before({
  "duration": 171073,
  "status": "passed"
});
formatter.before({
  "duration": 3524627,
  "status": "passed"
});
formatter.before({
  "duration": 142975,
  "status": "passed"
});
formatter.before({
  "duration": 74231,
  "status": "passed"
});
formatter.before({
  "duration": 71980,
  "status": "passed"
});
formatter.before({
  "duration": 70087,
  "status": "passed"
});
formatter.scenario({
  "line": 22,
  "name": "Per product prorated charge calculation",
  "description": "",
  "id": "bo-bill-details;per-product-prorated-charge-calculation",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 23,
  "name": "I target the BO environment",
  "keyword": "Given "
});
formatter.step({
  "line": 24,
  "name": "I view bill details for a bill of the type \"1 prorated bill\"",
  "keyword": "When "
});
formatter.step({
  "line": 25,
  "name": "the response status is 200",
  "keyword": "And "
});
formatter.step({
  "line": 26,
  "name": "the bill total for prorated charges is computed correctly",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "BO",
      "offset": 13
    }
  ],
  "location": "CommonStepDefs.I_target_the_environment(String)"
});
formatter.result({
  "duration": 140893,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "1 prorated bill",
      "offset": 44
    }
  ],
  "location": "BillingStepDefs.I_view_bill_details_for_a_bill_of_the_type(String)"
});
formatter.embedding("text/html", "\u003cdiv\u003e\u003cpre style\u003d\"margin-bottom: 0;\"\u003e\u003cb\u003erequest:\u003c/b\u003e\u003cbr\u003e\u003cbr\u003ehttp://development-bo.cf.dev-paas.bskyb.com/bills/722573002-1\u003cbr\u003e\u003cbr\u003eGET | {X-Billing-Account-Number\u003d[621175378887]}\u003c/pre\u003e\u003c/div\u003e\u003cbr\u003e");
formatter.embedding("text/html", "\u003cdiv\u003e\u003cpre style\u003d\"margin-bottom: 0;\"\u003e\u003cb\u003eresponse:\u003c/b\u003e\u003cbr\u003e\u003cbr\u003e200 | {Server\u003d[nginx], Date\u003d[Thu, 03 Dec 2015 16:42:28 GMT], Content-Type\u003d[application/json; charset\u003dUTF-8], Content-Length\u003d[642], Connection\u003d[keep-alive], Keep-Alive\u003d[timeout\u003d10], Access-Control-Allow-Credentials\u003d[true], Access-Control-Allow-Origin\u003d[null], X-Cf-Requestid\u003d[5303f04e-cfd4-4a50-6ebf-d0af6d1ac39b]}\u003cbr\u003e\u003cbr\u003e{\n    \"total\": 115.98,\n    \"triplePlay\": true,\n    \"_links\": {\n        \"self\": {\n            \"href\": \"http://development-bo.cf.dev-paas.bskyb.com/bills/722573002-1\"\n        }\n    },\n    \"subscriptionCharges\": {\n        \"total\": 106.99,\n        \"bundle\": {\n            \"charges\": [\n                {\n                    \"amount\": 6.99,\n                    \"description\": \"Now TV Entertainment\"\n                },\n                {\n                    \"amount\": 40,\n                    \"description\": \"Now TV Broadband Unlimited\"\n                },\n                {\n                    \"amount\": 25,\n                    \"description\": \"Now TV Talk Anytime Extra\"\n                },\n                {\n                    \"amount\": 35,\n                    \"description\": \"Now TV Line Rental\"\n                }\n            ],\n            \"total\": 106.99\n        }\n    },\n    \"from\": \"2015-11-02\",\n    \"to\": \"2015-12-01\",\n    \"proratedCharges\": {\n        \"total\": 8.99,\n        \"periods\": [\n            {\n                \"total\": 8.99,\n                \"from\": \"2015-11-05\",\n                \"to\": \"2015-12-01\",\n                \"bundle\": {\n                    \"charges\": [\n                        {\n                            \"amount\": 8.99,\n                            \"description\": \"Now TV Movies\"\n                        }\n                    ]\n                }\n            }\n        ],\n        \"from\": \"2015-10-02\"\n    }\n}\u003c/pre\u003e\u003c/div\u003e\u003cbr\u003e");
formatter.result({
  "duration": 107456947,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "200",
      "offset": 23
    }
  ],
  "location": "RestStepDefinitions.theResponseStatusIs(int)"
});
formatter.result({
  "duration": 275722,
  "status": "passed"
});
formatter.match({
  "location": "BillingStepDefs.the_bill_total_for_prorated_charges_is_computed_correctly()"
});
formatter.result({
  "duration": 23712033,
  "status": "passed"
});
});