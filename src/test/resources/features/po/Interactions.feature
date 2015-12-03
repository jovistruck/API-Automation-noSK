@POInteractions @PO @ALL
Feature: PO Interactions Tests

  Scenario: Post interactions
    Given I target the PO environment
    When I post to "/interactions"
    Then the response status is 201
    And the response body matches the template "create-interaction"
#country defaults to GBR

  Scenario: Get interaction
    Given I target the PO environment
    When I create an interaction reference
    And I get the interaction
    Then the response status is 200
    And the response body matches the template "get-interaction"