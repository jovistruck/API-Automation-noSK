@PO @POInteractions @ALL @wip
Feature: PO Interactions Tests

  Scenario: Get Voice and Data Capabilities (Happy Path)
    Given I target the PO environment
    When I create an interaction reference
    When I set voice and data with
     """
        { "diallingNumber" : "1234", "postcode" : "NG250NJ" }
     """
    Then the response status is 200
    Then the response body matches the template "voice-and-data-schema"