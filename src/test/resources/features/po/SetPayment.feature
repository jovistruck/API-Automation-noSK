@POSetPayment @PO @ALL

Feature: PO Set Payment

  Scenario Outline: Set payment method with card and existing (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I set the payment details with
      """
        {"method": "<paymentMethod>", "token" : "token"}
      """
    Then the response status is 200
    And these json path values are equal to
      | path                          | value           |
      | paymentMethods.payment.method | <paymentMethod> |
      | paymentMethods.payment.token  | token           |
      | paymentMethods.billing.method | <paymentMethod> |
      | paymentMethods.billing.token  | token           |
    Examples:
      | paymentMethod |
      | CARD          |
      | EXISTING      |
      | NONE          |

  Scenario: Set payment method as EXISTING without token (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I set the payment details with
      """
        {"method": "EXISTING"}
      """
    Then the response status is 200
    And these json path values are equal to
      | path                          | value    |
      | paymentMethods.payment.method | EXISTING |
      | paymentMethods.billing.method | EXISTING |
    And these json path values are null
      | paymentMethods.payment.token |
      | paymentMethods.billing.token |

  Scenario Outline: Set payment method json path (happy path)
    Given I target the PO environment
    When I create an interaction reference
    And I set the payment details with
      """
        {"method": "<paymentMethod>", "token" : "token"}
      """
    Then the response status is 200
    And the response body matches the template "payment-schema"
    Examples:
      | paymentMethod |
      | CARD          |
      | EXISTING      |
      | NONE          |

  Scenario: Set payment method as CARD without token (sad path)
    Given I target the PO environment
    When I create an interaction reference
    And I set the payment details with
      """
        {"method": "CARD"}
      """
    Then the response status is 422
    And the json contains this item
      | errors[0].code     | errors[0].description                                                       |
      | service.badRequest | The request is not valid [token is mandatory when payment method is 'CARD'] |

  Scenario Outline: Set payment method without token (sad path)
    Given I target the PO environment
    When I create an interaction reference
    And I set the payment details with
      """
        {"method": "<paymentMethod>"}
      """
    Then the response status is 200
    Examples:
      | paymentMethod |
      | EXISTING      |
      | NONE          |

  Scenario Outline: Set payment method with invalid values (sad path)
    Given I target the PO environment
    When I create an interaction reference
    And I set the payment details with
      """
        {"method": "<paymentMethod>", "token" : "token"}
      """
    Then the response status is 400
    And the json contains this item
      | errors[0].code |
      | invalid_json   |
    Examples:
      | paymentMethod |
      | HELLO         |
      | EXISTINGYO    |
      | £$$££         |

  Scenario: Set invalid payment method without token (sad path)
    Given I target the PO environment
    When I create an interaction reference
    And I set the payment details with
      """
        {"method": "Hello"}
      """
    Then the response status is 400
    And the json contains this item
      | errors[0].code |
      | invalid_json   |

  Scenario: Set payment method without body(sad path)
    Given I target the PO environment
    When I create an interaction reference
    And I set the payment details with
      """
      """
    Then the response status is 400