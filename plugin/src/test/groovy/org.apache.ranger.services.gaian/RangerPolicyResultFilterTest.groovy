package org.apache.ranger.services.gaian

import spock.lang.*
import org.apache.ranger.services.gaian.RangerPolicyResultFilter

class RangerPolicyResultFilterTest extends spock.lang.Specification {
    def "check SetForwardingNode always returns true"() {
        given:
        def rangerPolicyResultFilter = new RangerPolicyResultFilter()

        expect: "verify we get true regardless of input"
        result == rangerPolicyResultFilter.setForwardingNode(inputString)

        where:
        inputString | result
        "banana"    | true
        null        | true
    }

    def "check setUserCredentials always returns true"() {
        given:
        def rangerPolicyResultFilter = new RangerPolicyResultFilter()

        expect: "verify we get true regardless of input"
        result == rangerPolicyResultFilter.setUserCredentials(inputString)

        where:
        inputString | result
        "banana"    | true
        null        | true
    }
}
