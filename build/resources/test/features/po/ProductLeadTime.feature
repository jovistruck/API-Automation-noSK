@POProductLeadTime @PO @ALL @pending

Feature: PO Lead Time

  Scenario: Get Lead Time for an Interaction (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And  I get the lead time for the interaction
    Then the response status is 200