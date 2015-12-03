@PORootLinks @ALL
Feature: Root links

  Scenario: Root links validation for PO
    Given I target the PO environment
    When I get from "/"
    Then the response status is 200
    And the response body matches the template "po-root-links-schema"
#TODO: add tests


  Scenario: Root links validation for BO
    Given I target the BO environment
    When I get from "/"
    Then the response status is 200
    And the response body matches the template "bo-root-links-schema"

   Scenario: Root links validation for SO
    Given I target the SO environment
    When I get from "/"
    Then the response status is 200
    And the response body matches the template "so-root-links-schema"