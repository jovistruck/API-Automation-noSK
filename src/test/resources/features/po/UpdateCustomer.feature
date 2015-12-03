@POUpdateCustomer @PO @ALL

Feature: PO Update Customer

  Scenario: Update Customer (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I update the customer with
      """
      {
        "profileId": "jovi",
        "identity": {
          "title": "mister",
          "firstName": "Joviano",
          "lastName": "Dias"
        },
        "email": "jovez10@gmail.com",
        "mobile": "929929292"
      }
      """
    Then the response status is 200
    And these json path values are equal to
      | path                        | value   |
      | customer.identity.title     | mister  |
      | customer.identity.firstName | Joviano |
      | customer.identity.lastName  | Dias    |
    #TODO: add email mobile after https://cbsjira.bskyb.com/browse/SAL-622 is fixed.

  Scenario: Update Customer response schema check (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I update the customer with
      """
      {
        "profileId": "jovi",
        "identity": {
          "title": "mister",
          "firstName": "Joviano",
          "lastName": "Dias"
        },
        "email": "jovez10@gmail.com",
        "mobile": "929929292"
      }
      """
    Then the response status is 200
    And the response body matches the template "update-customer-schema"

  #Field length validations is done by CBS, but we are validating it like a paranoid payments team
  Scenario Outline: Update Customer field length validations
    Given I target the PO environment
    When I create an interaction reference
    And I update the customer with
      """
      {
        "profileId": "jovi",
        "identity": {
          "title": <title>,
          "firstName": <firstName>,
          "lastName": <lastName>
        },
        "email": "jovez10@gmail.com",
        "mobile": <mobile>
      }
      """
    Then the response status is 422
#    Then these json path values are equal to
#      | path                  | value         |
#      | errors[0].code        | <code>        |
#      | errors[0].description | <description> |
    Examples:
      | title         | firstName                                   | lastName                                    | mobile              | code                        | description                                  |
      | "mrmrmrmrmry" | "jov"                                       | "dias"                                      | "233232323"         | sis.title.size.invalid      | Title must be 1 to 10 characters             |
      | "mr"          | "jovianoduijovianoduijovianoduijovianoduis" | "dias"                                      | "233232323"         | sis.first.name.size.invalid | First name must be 1 to 40 characters        |
      | "mr"          | "jov"                                       | "jovianoduijovianoduijovianoduijovianoduis" | "999999999"         | sis.last.name.size.invalid  | Last name must be 1 to 40 characters         |
      | "mr"          | "jov"                                       | "dias"                                      | "99999999999999999" | sis.mobile.length           | Mobile length must between 6 - 16 characters |
      | ""            | "jov"                                       | "dias"                                      | "233232323"         | sis.title.size.invalid      | Title is required                            |
      | "mr"          | ""                                          | "dias"                                      | "233232323"         | sis.first.name.size.invalid | First name must be 1 to 40 characters        |
      | "mr"          | "jov"                                       | ""                                          | "233232323"         | sis.last.name.size.invalid  | Last name must be 1 to 40 characters         |
      | "mr"          | "jov"                                       | "dias"                                      | "22323"             | sis.mobile.length           | Mobile length must between 6 - 16 characters |

#  Scenario Outline: Update Customer with missing fields
#    Given I target the PO environment
#    When I create an interaction reference
#    And I update the customer with
#      """
#      <body>
#      """
#    Then the response status is 400
#    Then these json path values are equal to
#      | path           | value        |
#      | errors[0].code | invalid_json |
#    Examples:
#      | body                                                                                                                                            |
#      | {  "identity": {  "title": "mister",  "firstName": "Joviano",  "lastName": "Dias"  },  "email": "jovez10@gmail.com",  "mobile": "929929292"}    |
#      | {  "profileId": "jovi",  "email": "jovez10@gmail.com",  "mobile": "929929292"}                                                                  |
#      | {  "profileId": "jovi",  "identity": {   "firstName": "Joviano",  "lastName": "Dias"  },  "email": "jovez10@gmail.com",  "mobile": "929929292"} |
#      | {  "profileId": "jovi",  "identity": {  "title": "mister",  "lastName": "Dias"  },  "email": "jovez10@gmail.com",  "mobile": "929929292"}       |
#      | {  "profileId": "jovi",  "identity": {  "title": "mister",  "firstName": "Joviano"  },  "email": "jovez10@gmail.com",  "mobile": "929929292"}   |
#      | {  "profileId": "jovi",  "identity": {  "firstName": "Joviano",  "lastName": "Dias"  },  "email": "jovez10@gmail.com",  "mobile": "929929292"}  |
#      | {  "profileId": "jovi",  "identity": {  "title": "mister",  "firstName": "Joviano",  "lastName": "Dias"  },  "mobile": "929929292"}             |
#      | {  "profileId": "jovi",  "identity": {  "title": "mister",  "firstName": "Joviano",  "lastName": "Dias"  },  "email": "jovez10@gmail.com"}      |
#      | {  "profileId": "jovi",  "email": "jovez10@gmail.com",  "mobile": "929929292"}                                                                  |


#JSON Schema tests

#Reference: https://confluence.bskyb.com/display/sercat/Update+Customer
