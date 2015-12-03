@SOGetOrders @SO @ALL
Feature: SO Get Customer Orders

  Scenario: Get orders for customer
    Given I target the SO environment
    And I add http header "X-Party-Id" with value "14443779585353471616497"
    When I get from "/orders"
    Then the response status is 200
    Then the response body matches the template "get-order-schema"

  Scenario: Get orders for customer with invalid party id
    Given I target the SO environment
    And I add http header "X-Party-Id" with value " "
    When I get from "/orders"
    Then the response status is 422
    Then these json path values are equal to
      | path                  | value                                                                             |
      | errors[0].code        | service.badRequest                                                                |
      | errors[0].description | The request is not valid [Request should contain either partyId OR accountNumber] |
#Does not actually accept Account Numbers

  Scenario: Get orders for customer with header is not specified
    Given I target the SO environment
    When I get from "/orders"
    Then the response status is 400
    And these json path values are equal to
      | path                  | value                                                |
      | errors[0].code        | request.header                                       |
      | errors[0].description | Request is missing required HTTP header 'X-Party-Id' |

  Scenario Outline: Get orders for customer with invalid partyID
    Given I target the SO environment
    And I add http header "X-Party-Id" with value "<xPartyId>"
    When I get from "/orders"
    And the response status is 200
    Then these json path values are equal to
      | path | value |
      | data | []    |
    Examples:
      | xPartyId       |
      | abcdehfghtieri |
      | 12124324       |
#      | ££££££££       |
  #Remove post https://git.nowtv.bskyb.com/payments/service-orchestration/issues/7

