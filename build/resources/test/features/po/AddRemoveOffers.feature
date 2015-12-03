@POOffers @PO @ALL

Feature: PO Add Remove Offers

  Scenario: Add an offer to the basket (happy path)
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

  Scenario Outline: Add multiple offers to the basket (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I add products with code to the basket
      """
        {"add":{"products":[{"code":"14122","action":"PROVIDE"}]}}
      """
    And I add <itemType> with code to the basket
      """
        {"add":{"<itemType>":[{"code":"<code>","action":"PROVIDE"}]}}
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
    And the response body matches the template "add-products-offers"
    Examples:
      | itemType | code  |
      | offers   | 76270 |
      | offers   | 76270 |
      | offers   | 76270 |
      #need more offers

  Scenario Outline: Add multiple products and offers to the basket (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I add products with code to the basket
      """
        {"add":{"products":[{"code":"14122","action":"PROVIDE"}]}}
      """
    And I add products with code to the basket
          """
        {"add":{"products":[{"code":"14123","action":"PROVIDE"}]}}
      """
    And I add products with code to the basket
      """
        {"add":{"products":[{"code":"14122","action":"PROVIDE"}]}}
      """
    And I add <itemType> with code to the basket
      """
        {"add":{"<itemType>":[{"code":"<code>","action":"PROVIDE"}]}}
      """
    Then the response status is 201
    And these json path values are not null
      | reference |
    And the interaction reference is returned
    And the json "basket.offers" list contains
      | code   |
      | <code> |
    And these json path values are not null
      | basket.offers[0].reference |
    And these json path values are not null
      | basket.products[0].reference |
    Examples:
      | itemType | code  |
      | offers   | 76270 |
      | offers   | 76270 |
      | offers   | 76270 |
      #need more offers

  Scenario: Remove an offer from the basket (happy path)
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
    And I remove the last added offers from the basket
    And the response status is 201
    Then these json path values are equal to
      | path          | value |
      | basket.offers | []    |
    And the interaction reference is returned

  Scenario: Add an offer to the basket after removing a product (sad path)
    Given I target the PO environment
    When I create an interaction reference
    And I add products with code to the basket
      """
        {"add":{"products":[{"code":"14122","action":"PROVIDE"}]}}
      """
    And I add products with code to the basket
      """
        {"add":{"products":[{"code":"14123","action":"PROVIDE"}]}}
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
    And I remove the first added products from the basket
    And I add offers with code to the basket
      """
        {"add":{"offers":[{"code":"76270","action":"PROVIDE"}]}}
      """
    And the response status is 201
    Then the json "basket.offers" list contains
      | code  |
      | 76270 |

  Scenario Outline: Remove a non existent offer from the basket (sad path)
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
    And I remove the offers with offers reference "<reference>" from the basket
      """
        {"remove":{"offers":[{"basketRef":"<reference>"}]}}
      """
    Then the response status is 422
    And the json contains this item
      | errors[0].code            | errors[0].description                                                                              |
      | sis.basket.item.not.found | The requested resource could not be found [Basket offer reference [<expectedReference>] not found] |
    Examples:
      | reference | expectedReference |
      | invalid   | invalid           |
      | 28378273  | 28378273          |
      | £££       | Â£Â£Â£            |