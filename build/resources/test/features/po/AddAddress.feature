@POAddAddress @PO @ALL

Feature: PO Add Address

  Scenario: Add one address (base sanity)
    Given I target the PO environment
    When I create an interaction reference
    And I add an address with name "my purpose" with purposes "DELIVERY,BILLING,INSTALLATION"
    Then the response status is 201
    And these json path values are not null
      | reference |
    And the interaction reference is returned
    And the response body matches the template "add-address-all-purposes"
    And the single address references match "delivery,billing,installation"
    And these json path values are equal to
      | path                                           | value      |
      | addresses[0].name                              | my purpose |
      | addresses[0].houseNumber                       | 1          |
      | orderAddresses.billingAddress.name             | my purpose |
      | orderAddresses.billingAddress.houseNumber      | 1          |
      | orderAddresses.deliveryAddress.name            | my purpose |
      | orderAddresses.deliveryAddress.houseNumber     | 1          |
      | orderAddresses.installationAddress.name        | my purpose |
      | orderAddresses.installationAddress.houseNumber | 1          |


  Scenario: Add one address without INSTALLATION purpose
    Given I target the PO environment
    When I create an interaction reference
    And I add an address with name "my purpose" with purposes "DELIVERY,BILLING"
    Then the response status is 201
    And these json path values are not null
      | reference |
    And the interaction reference is returned
    And the response body matches the template "add-address-delivery-billing"
    And the single address references match "delivery,billing"
    And these json path values are equal to
      | path                                       | value      |
      | addresses[0].name                          | my purpose |
      | addresses[0].houseNumber                   | 1          |
      | orderAddresses.billingAddress.name         | my purpose |
      | orderAddresses.billingAddress.houseNumber  | 1          |
      | orderAddresses.deliveryAddress.name        | my purpose |
      | orderAddresses.deliveryAddress.houseNumber | 1          |
    And these json path values are null
      | path                               |
      | orderAddresses.installationAddress |


  Scenario Outline: Add an address with each of the purpose types
    Given I target the PO environment
    When I create an interaction reference
    And I add an address with name "my purpose" with purpose "<purposeType>"
    Then the response status is 201
    And the response body matches the template "<schemaName>"
    And the single address references match "<purposeType>"
    And these json path values are equal to
      | path                                            | value      |
      | addresses[0].name                               | my purpose |
      | addresses[0].houseNumber                        | 1          |
      | orderAddresses.<purposeType>Address.name        | my purpose |
      | orderAddresses.<purposeType>Address.houseNumber | 1          |
    Examples:
      | purposeType  | schemaName               |
      | delivery     | add-address-delivery     |
      | billing      | add-address-billing      |
      | installation | add-address-installation |

  Scenario: Add one address with no purpose
    Given I target the PO environment
    When I create an interaction reference
    And I add an address with name "no purpose"
    Then the response status is 201
    And these json path values are not null
      | reference |
    And the interaction reference is returned
    And the response body matches the template "add-address"
    And these json path values are equal to
      | path                     | value      |
      | addresses[0].name        | no purpose |
      | addresses[0].houseNumber | 1          |
    And these json path values are null
      | path                                |
      | keyAddresses.billingAddressRef      |
      | keyAddresses.deliveryAddressRef     |
      | keyAddresses.installationAddressRef |
    And these json path values are equal to
      | path           | value |
      | orderAddresses | {}    |

  Scenario: Add one address with invalid purpose
    Given I target the PO environment
    When I create an interaction reference
    And I add an address with name "my purpose" with purposes "invalid"
    Then the response status is 400
    And the json contains this item
      | errors[0].code |
      | invalid_json   |
    And these json path values are not null
      | errors[0].description |

  Scenario Outline: Add an address with postcode validations
    Given I target the PO environment
    When I create an interaction reference
    And I add an address with the request body as
    """
    {
        "houseName": "Athena Court",
        "town": "IsleWorth",
        "purposes": [
            "DELIVERY"
        ],
        "street": "Grant Way",
        "countryCode": "GBR",
        "name": "my purpose",
        "houseNumber": "1",
        "locality": "Syon Lane",
        "county": "Middlesex",
        "postcode": "<postCode>"
    }
    """
    Then the response status is <response>
    Examples:
      | postCode | response |
      | TW7 5DQ  | 201      |
      | tw7 5dq  | 201      |
      | Tw7 5dQ  | 201      |
      | Tw75dQ   | 201      |
      | TW75DQ   | 201      |
      | tetetet  | 422      |
      | ""       | 400      |

#  https://lwssis{env}.gss.bskyb.com/cbs-sales-interaction-service/v1/interactions/{interaction-ref}/addresses

