@BOBillingBillDetails @BOBilling @BO @ALL
Feature: BO Bill Details

  Scenario: BO Bill Details (happy path)
    Given I target the BO environment
    When I view bill details for a bill of the type "Line-Movies-Broadband"
    And the response status is 200
    Then the response body matches the template "bill-details"

  Scenario: Per product itemized charge calculation
    Given I target the BO environment
    When I view bill details for a bill of the type "tv talk and broadband charges"
    And the response status is 200
    Then the bill total for the bundle is computed correctly

  Scenario: Aggregated multiple product total
    Given I target the BO environment
    When I view bill details for a bill of the type "tv talk and broadband charges"
    And the response status is 200
    Then the total subscription charges are computed correctly

  Scenario: Per product prorated charge calculation
    Given I target the BO environment
    When I view bill details for a bill of the type "1 prorated bill"
    And the response status is 200
    Then the bill total for prorated charges is computed correctly

 @pending
  Scenario: Prorated charge date should be one month before the billing date
    Given I target the BO environment
    When I view bill details for a bill of the type "1 prorated bill"
    And the response status is 200
    Then the date for prorated charges is computed correctly