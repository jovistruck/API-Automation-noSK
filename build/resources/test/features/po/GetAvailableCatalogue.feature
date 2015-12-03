@POGetAvailableProducts @PO @ALL

Feature: PO Get Available Products Catalogue

  Scenario: Get available products (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I get the available catalogue
    Then the response status is 200
    And these json path values are not null
      | products                       |
      | products[0].id                 |
      | products[0].name               |
      | products[0].category           |
      | products[1].id                 |
      | products[1].name               |
      | products[1].category           |


  Scenario: Get available products json schema check (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I get the available catalogue
    Then the response status is 200
    And the response body matches the template "product-catalogue-schema"
