@BOBillingHistory @BOBilling @BO @ALL
Feature: BO Billing History

  Scenario: BO Bill History sanity test
    Given I target the BO environment
    And I add a valid billing account number header with 1 bill
    When I get from "/bills?numberOfBills=1"
    And the response status is 200
    And these json path values are equal to
      | path         | value |
      | acctCurrency | GBP   |
    And these json path values are not null
      | billSummaries |
    When I get from "/bills"
    And these json path values are equal to
      | path         | value |
      | acctCurrency | GBP   |
    And these json path values are not null
      | billSummaries |

  Scenario Outline: BO Bill History with numberOfBills (Happy path)
    Given I target the BO environment
    And I add a valid billing account number header with 18 bill
    When I get from "/bills?numberOfBills=<NoOfBills>"
    Then the number of items in the json path "billSummaries" is "<NoOfBills>"
    Examples:
      | NoOfBills |
      | 1         |
      | 3         |
      | 18        |

  Scenario Outline: BO Bill History with invalid account number
    Given I target the BO environment
    And I add a billing account number header "<accountNumber>"
    When I get from "/bills?numberOfBills=1"
    Then the response status is 422
    Then these json path values are equal to
      | path                  | value         |
      | errors[0].code        | <code>        |
      | errors[0].description | <description> |
    Examples:
      | accountNumber | code                      | description                                                                 |
      | 1             | request.account_not_found | Unable to find account with external_id=[1] and external_id_type=[1]        |
      | salkdmkm      | request.account_not_found | Unable to find account with external_id=[salkdmkm] and external_id_type=[1] |
#      | £££££         | request.account_not_found | Unable to find account with external_id=[£££££] and external_id_type=[£££££]       |

  Scenario: BO Bill History defaults to 18 bills with no parameter specified (Happy path)
    Given I target the BO environment
    And I add a valid billing account number header with 1 bill
    When I get from "/bills"
#TODO:    Then the number of items in the json path "billSummaries" is "18"

  Scenario Outline: BO Bill History validation errors
    Given I target the BO environment
    And I add a valid billing account number header with 1 bill
    When I get from "/bills?numberOfBills=<NoOfBills>"
    And the response status is <responseCode>
    Then these json path values are equal to
      | path                  | value         |
      | errors[0].code        | <code>        |
      | errors[0].description | <description> |
    Examples:
      | NoOfBills   | code              | description                                                                          | responseCode |
      | abc         | request.parameter | Invalid parameter 'numberOfBills': 'abc' is not a valid 32-bit integer value         | 400          |
      | $%          | request.parameter | Invalid parameter 'numberOfBills': '$%' is not a valid 32-bit integer value          | 400          |
      | -122        | request.parameter | Invalid parameter passed to service : n must be a positive number.                   | 422          |
      | 0           | request.parameter | Invalid parameter passed to service : n must be a positive number.                   | 422          |
      | 18781787871 | request.parameter | Invalid parameter 'numberOfBills': '18781787871' is not a valid 32-bit integer value | 400          |