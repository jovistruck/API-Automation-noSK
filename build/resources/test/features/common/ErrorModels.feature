@ErrorModel @ALL
Feature: Error Models

  @PO
  Scenario: Error model checks for PO
    Given I target the PO environment
    When I get from "/addresses?countryCode=tll&searchAll=sas"
    Then the response status is 422
    And these json path values are not null
      | errors[0].code        |
      | errors[0].description |

  @BO
  Scenario: Error model checks for BO
    Given I target the BO environment
    And I get from "/bills?numberOfBills=DD"
    Then the response status is 400
    And these json path values are not null
      | errors[0].code        |
      | errors[0].description |

  @SO
  Scenario: Error model checks for SO
    Given I target the SO environment
    And I get from "/orders"
    Then the response status is 400
    And these json path values are not null
      | errors[0].code        |
      | errors[0].description |