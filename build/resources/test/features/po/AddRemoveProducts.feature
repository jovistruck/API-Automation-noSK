@POProducts @PO @ALL

Feature: PO Add Remove Products

  Scenario Outline: Add a product to the basket (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I add <itemType> with code to the basket
      """
        {"add":{"<itemType>":[{"code":"<code>","action":"PROVIDE"}]}}
      """
    Then the response status is 201
    And these json path values are not null
      | reference |
    And the interaction reference is returned
    And the json "basket.products" list contains
      | code   |
      | <code> |
    And these json path values are not null
      | basket.products[0].reference |
    And the response body matches the template "add-products-offers"
    Examples:
      | itemType | code  |
      | products | 14122 |
      | products | 14123 |
      | products | 14139 |
      | products | 14136 |


  @POProductsRemove
  Scenario: Remove a product from the basket (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I add products with code to the basket
      """
        {"add":{"products":[{"code":"14122","action":"PROVIDE"}]}}
      """
    And I remove the first added products from the basket
    Then the response status is 201
    And these json path values are equal to
      | path            | value |
      | basket.offers   | []    |
      | basket.products | []    |
    And these json path values are not null
      | reference |
    And the interaction reference is returned

  Scenario: Add a product to the basket after removing an offer (sad path)
    Given I target the PO environment
    When I create an interaction reference
    And I add products with code to the basket
      """
        {"add":{"products":[{"code":"14122","action":"PROVIDE"}]}}
      """
    And I add offers with code to the basket
      """
        {"add":{"offers":[{"code":"76270","action":"PROVIDE"}]}}
      """
    Then the response status is 201
    And these json path values are not null
      | reference |
    And the interaction reference is returned
    And the json "basket.offers" list contains
      | code  |
      | 76270 |
    And these json path values are not null
      | basket.offers[0].reference |
    And I remove the first added offers from the basket
    And I add products with code to the basket
      """
        {"add":{"products":[{"code":"14123","action":"PROVIDE"}]}}
      """
    And the response status is 201
    Then these json path values are not null
      | basket.products[0].reference |

  @pending @pendingOnCBS @SAL-582
  Scenario: Remove a product from the basket after adding an offer (sad path)
    Given I target the PO environment
    When I create an interaction reference
    And I add products with code to the basket
      """
        {"add":{"products":[{"code":"14122","action":"PROVIDE"}]}}
      """
    And I add offers with code to the basket
      """
        {"add":{"offers":[{"code":"76270","action":"PROVIDE"}]}}
      """
    Then the response status is 201
    And I remove the first added products from the basket
    Then the response status is 201

  @pending @pendingOnCBS @SAL-582
  Scenario: Add a product and offer then remove product and add an offer (sad path)
    Given I target the PO environment
    When I create an interaction reference
    And I add products with code to the basket
      """
        {"add":{"products":[{"code":"14122","action":"PROVIDE"}]}}
      """
    And I add offers with code to the basket
      """
        {"add":{"offers":[{"code":"76270","action":"PROVIDE"}]}}
      """
    Then the response status is 201
    And I remove the first added products from the basket
    And these json path values are equal to
      | path   | value |
      | basket | {}    |
    And I add offers with code to the basket
    Then the response status is 201

  Scenario Outline: Remove a non existent product from the basket (sad path)
    Given I target the PO environment
    When I create an interaction reference
    And I add products with code to the basket
      """
        {"add":{"products":[{"code":"14122","action":"PROVIDE"}]}}
      """
    And I remove the products with product reference "<reference>" from the basket
      """
        {"remove":{"products":[{"basketRef":"<reference>"}]}}
      """
    Then the response status is 422
    And the json contains this item
      | errors[0].code            | errors[0].description                                                                                |
      | sis.basket.item.not.found | The requested resource could not be found [Basket product reference [<expectedReference>] not found] |
    Examples:
      | reference | expectedReference |
      | invalid   | invalid           |
      | 28378273  | 28378273          |
      | £££       | Â£Â£Â£          |