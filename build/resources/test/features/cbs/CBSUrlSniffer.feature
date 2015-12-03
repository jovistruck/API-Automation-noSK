@CBSURLSniffer @pending
Feature: CBS Sniffer

  Scenario: CBS Create Prospect Interaction
    Given I target the CBS service "cbs-interaction-development-cloud"
    And I post to "/cbs-sales-interaction-service/v1/interactions/prospect" with
    """
      {
        "country" : "GBR",
        "interestSource" : "123456"
      }
    """
    Then the response body matches the template "cbs-create-prospect-interaction"
#
#  Scenario: CBS Create Prospect Interaction
#    Given I target the CBS service "cbs-interaction-development-cloud"
#    And I post to "/cbs-sales-interaction-service/v1/interactions/prospect" with
#    """
#      {
#        "country" : "GBR",
#        "interestSource" : "123456"
#      }
#    """
#    Then the response body matches the template "cbs-create-prospect-interaction"
