@POOrders @PO @ALL @pending
Feature: PO Orders Tests

  Scenario: Create an Order with all the prerequisites(happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I successfully add products with codes "14126,14136,14139,14141,14143" to the basket
    And I set the payment details with
    """
      {"method": "CARD", "token": "123456"}
    """
    And the response status is 200
    When I set voice and data with
    """
      {"diallingNumber": "01786821518", "postcode":"EH8 8BA"}
     """
    And the response status is 200
    And I update the customer with
    """
      {
        "profileId": "jovi",
        "identity": {
          "title": "mister",
          "firstName": "Joviano",
          "lastName": "Dias"
        },
        "email": "jovez10@gmail.com",
        "mobile": "929929292"
      }
    """
    And the response status is 200
    And I add an address with the body
    """
      {
        "purposes": [
            "BILLING",
            "INSTALLATION",
            "DELIVERY"
        ],
        "houseName": "Barn Cottage, Wiske House",
        "subBuildingName": "Barn Cottage",
        "street": "Front Street",
        "locality": "Appleton Wiske",
        "town": "NORTHALLERTON",
        "county": "North Yorkshire",
        "dpSuffix": "2N",
        "postcode": "DL6 2AA",
        "countryCode": "GBR"
      }
    """
    And the response status is 201
    And I create an order with the body
    """
      {}
    """
    And the response status is 201


  Scenario: Create an Order with missing products, payment details, voice and set data and address.
    Given I target the PO environment
    When I create an interaction reference
    And I create an order with the body
    """
      {}
    """
    And the json contains this item
      | errors[0].code                 | errors[0].description                                      |
      | sis.interaction.invalid.status | The Interaction status must be [VALID] to create an order. |

  Scenario: Create an Order with missing payment details , voice and set data and address.
    Given I target the PO environment
    When I create an interaction reference
    And I successfully add products with codes "14126,14136,14139,14141,14143" to the basket

    And I create an order with the body
    """
      {}
    """
    And the json contains this item
      | errors[0].code                 | errors[0].description                                      |
      | sis.interaction.invalid.status | The Interaction status must be [VALID] to create an order. |

  Scenario: Create an Order with missing voice and set data and address.
    Given I target the PO environment
    When I create an interaction reference
    And I successfully add products with codes "14126,14136,14139,14141,14143" to the basket
    And I set the payment details with
    """
      {"method": "CARD", "token": "123456"}
    """
    And the response status is 200

    And I create an order with the body
    """
      {}
    """
    And the json contains this item
      | errors[0].code                 | errors[0].description                                      |
      | sis.interaction.invalid.status | The Interaction status must be [VALID] to create an order. |


  Scenario: Create an Order with missing address.
    Given I target the PO environment
    When I create an interaction reference
    And I successfully add products with codes "14126,14136,14139,14141,14143" to the basket
    And I set the payment details with
    """
      {"method": "CARD", "token": "123456"}
    """
    And the response status is 200

    When I set voice and data with
    """
      {"diallingNumber": "01786821518", "postcode":"EH8 8BA"}
     """
    And the response status is 200

    And I create an order with the body
    """
      {}
    """
    And the json contains this item
      | errors[0].code                 | errors[0].description                                      |
      | sis.interaction.invalid.status | The Interaction status must be [VALID] to create an order. |


   Scenario: Create an Order with invalid interaction.
    Given I target the PO environment
    When I create an order with invalid interaction with body
    """
        {}
    """

    And the response status is 400
    And the json contains this item
      | errors[0].code | errors[0].description   |
      | invalid_po_ref | PO Reference is invalid |


  Scenario: Submitting an Order twice
    Given I target the PO environment
    When I create an interaction reference
    And I successfully add products with codes "14126,14136,14139,14141,14143" to the basket
    And I set the payment details with
    """
      {"method": "CARD", "token": "123456"}
    """
    And the response status is 200
    When I set voice and data with
    """
      {"diallingNumber": "01786821518", "postcode":"EH8 8BA"}
     """
    And the response status is 200
    And I update the customer with
    """
      {
        "profileId": "jovi",
        "identity": {
          "title": "mister",
          "firstName": "Joviano",
          "lastName": "Dias"
        },
        "email": "jovez10@gmail.com",
        "mobile": "929929292"
      }
    """
    And the response status is 200
    And I add an address with the body
    """
      {
        "purposes": [
            "BILLING",
            "INSTALLATION",
            "DELIVERY"
        ],
        "houseName": "Barn Cottage, Wiske House",
        "subBuildingName": "Barn Cottage",
        "street": "Front Street",
        "locality": "Appleton Wiske",
        "town": "NORTHALLERTON",
        "county": "North Yorkshire",
        "dpSuffix": "2N",
        "postcode": "DL6 2AA",
        "countryCode": "GBR"
      }
    """
    And the response status is 201
    And I create an order with the body
    """
      {}
    """
    And the response status is 201
    And I create an order with the body
    """
      {}
    """
    And the response status is <>