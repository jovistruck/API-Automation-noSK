@STATUS @ALL
Feature: Status and Admin

  @pending
  Scenario: VM is UP
    When I check VM status
    Then the status is Up

  @PO
  Scenario: PO is UP
    When I check PO status
    Then the status is Up

  @BO
  Scenario: BO is UP
    When I check BO status
    Then the status is Up

  @SO
  Scenario: SO is UP
    When I check SO status
    Then the status is Up

  @PO
  Scenario: Admin/Info endpoint
    Given I target the PO environment
    And I authenticate with user "admin" and password "p4v4dm1n"
    When I get from "/admin/info"
    Then the response status is 200
    And these json path values are not null
      | build.name    |
      | build.version |

  @PO
  Scenario: Admin/Env endpoint
    Given I target the PO environment
    And I authenticate with user "admin" and password "p4v4dm1n"
    When I get from "/admin/env"
    Then the response status is 200
    And these json path values are not null
      | CF_STACK  |
      | PATH      |
      | TMPDIR    |
      | VCAP_ZONE |

  @SO
  Scenario: Admin/Info endpoint
    Given I target the SO environment
    And I authenticate with user "admin" and password "p4v4dm1n"
    When I get from "/admin/info"
    Then the response status is 200
    And these json path values are not null
      | build.name    |
      | build.version |

  @SO
  Scenario: Admin/Env endpoint
    Given I target the SO environment
    And I authenticate with user "admin" and password "p4v4dm1n"
    When I get from "/admin/env"
    Then the response status is 200
    And these json path values are not null
      | CF_STACK  |
      | PATH      |
      | TMPDIR    |
      | VCAP_ZONE |

  @BO
  Scenario: Admin/Info endpoint
    Given I target the BO environment
    And I authenticate with user "admin" and password "p4v4dm1n"
    When I get from "/admin/info"
    Then the response status is 200
    And these json path values are not null
      | build.name    |
      | build.version |

  @BO
  Scenario: Admin/Env endpoint
    Given I target the BO environment
    And I authenticate with user "admin" and password "p4v4dm1n"
    When I get from "/admin/env"
    Then the response status is 200
    And these json path values are not null
      | CF_STACK  |
      | PATH      |
      | TMPDIR    |
      | VCAP_ZONE |
