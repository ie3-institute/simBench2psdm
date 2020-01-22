package edu.ie3

import spock.lang.Specification

class EmptyTest extends Specification {
    def "empty test should be successful"() {
        when:
        int a = 1
        int b = 1

        then:
        a == b
    }
}
