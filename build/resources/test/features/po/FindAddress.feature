@POFindAddress @PO @ALL

Feature: PO Find Address

  Scenario: Find an address (happy path)
    Given I target the PO environment
    When I get from "/addresses?countryCode=GBR&searchAll=1,RH15+0DH"
    Then the response status is 200
    And these json path values are equal to
      | path                              | value                                   |
      | data[0].summary                   | 1 Oak Hall Park, BURGESS HILL, RH15 0DH |
      | data[0].addressFields.houseNumber | 1                                       |

  Scenario: Find an address (happy path)
    Given I target the PO environment
    When I get from "/addresses?countryCode=GBR&searchAll=1 Stable Cottages, Shepton Road, Oakhill, RADSTOCK, BA3 5HT"
    Then the response status is 200
    And these json path values are not null
      | data                                    |
      | data[0].summary                         |
      | data[0].addressFields.houseNumber       |
      | data[0].addressFields.houseName         |
      | data[0].addressFields.subBuildingNumber |
      | data[0].addressFields.street            |
      | data[0].addressFields.locality          |
      | data[0].addressFields.town              |
      | data[0].addressFields.dpSuffix          |
      | data[0].addressFields.postcode          |
      | data[0].addressFields.countryCode       |

  Scenario: Find an address json schema check (happy path)
    Given I target the PO environment
    When I get from "/addresses?countryCode=GBR&searchAll=1 Stable Cottages, Shepton Road, Oakhill, RADSTOCK, BA3 5HT"
    And the response status is 200
    Then the response body matches the template "find-address"

  Scenario Outline: Find addresses parameter mixing (happy path)
    Given I target the PO environment
    When I get from "<request>"
    And the response status is 200
    Then the json contains this item
      | data[0].summary | data[0].addressFields.houseNumber |
      | <dataSummary>   | <dataHouseNumber>                 |
    And the response body matches the template "find-address"
    Examples:
      | request                                                                                          | dataSummary                                                 | dataHouseNumber |
      | /addresses?countryCode=GBR&searchAll=1,RH15+0DH                                                  | 1 Oak Hall Park, BURGESS HILL, RH15 0DH                     | 1               |
      | /addresses?countryCode=GBR&searchAll=Oak Hill Burgess                                            | 54 Oakhill Road, Old Swan, LIVERPOOL, L13 5UF               | 54              |
      | /addresses?countryCode=GBR&searchAll=2 Stable Cottages, Shepton Road, Oakhill, RADSTOCK, BA3 5HT | 2 Stable Cottages, Shepton Road, Oakhill, RADSTOCK, BA3 5HT | 2               |
#These may be way too flaky with hardset data. Will remove if data changes a lot

  Scenario Outline: Find addresses with parameter mixing (sad path)
    Given I target the PO environment
    When I get from "<request>"
    Then the response status is <responseCode>
    And the json contains this item
      | errors[0].code | errors[0].description |
      | <errorCode>    | <errorMessage>        |
    Examples:
      | request                                           | errorCode          | errorMessage                                                                         | responseCode |
      | /addresses                                        | service.badRequest | The request is not valid [Required String parameter 'countryCode' is not present]    | 422          |
      | /addresses?countryCode=23323&searchAll=1,RH15+0DH | service.badRequest | The request is not valid [No enum constant com.bskyb.common.model.CountryCode.23323] | 422          |
      | /addresses?countryCode=BPO&searchAll=1,RH15+0DH   | service.badRequest | The request is not valid [No enum constant com.bskyb.common.model.CountryCode.BPO]   | 422          |
      | /addresses?countryCode=&searchAll=1,RH15+0DH      | service.badRequest | The request is not valid [No enum constant com.bskyb.common.model.CountryCode.]      | 422          |
      | /addresses?countryCode=£R£&searchAll=1,RH15+0DH   | service.badRequest | The request is not valid [No enum constant com.bskyb.common.model.CountryCode.Â£RÂ£] | 422          |
      | /addresses?searchAll=1,RH15+0DH                   | service.badRequest | The request is not valid [Required String parameter 'countryCode' is not present]    | 422          |
      | /addresses?countryCode=GBR                        | service.badRequest | The request is not valid [Required String parameter 'searchAll' is not present]      | 422          |