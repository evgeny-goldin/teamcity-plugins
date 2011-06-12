package com.goldin.plugins.teamcity.messenger.test.tests

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification
import spock.lang.FailsWith


/**
 * {@link Message} tests
 */
class MessageText extends BaseSpecification
{

    @FailsWith( AssertionError )
    def "testing creating message without recipients"() {
        expect:
        messageNoId( Urgency.INFO, false, [], [] )
    }


    @FailsWith( AssertionError )
    def "testing creating illegal message with empty text"() {
        expect:
        new Message( 'someone', Urgency.INFO, '', -1, true, [], [] )
    }
}
