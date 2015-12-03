@POGetProductCatalogue @PO @ALL

Feature: PO Get Product Catalogue

  Scenario: Get Product Catalogue (happy path with json schema check)
    Given I target the PO environment
    When I get from "/catalogue/products"
    Then the response status is 200
    And the response body matches the template "product-catalogue-schema"


  ## Tests for caching to be reset at midnight (5 minutes?)

  ##Q to tomas
  ## Does this JSON look okay
  ## How is the caching implemented. 60 minutes ..
  ## How will the caching be tested.