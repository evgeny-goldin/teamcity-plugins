package com.goldin.plugins.teamcity.messenger.test.tests

import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore

/**
 * {@link MessagesBean} test
 */
class MessagesBeanTest extends BaseSpecification
{
    @Autowired
    final MessagesBean messagesBean


    @Ignore
    def "testing sending Message to user"() {

        when:
        def m1        = messageNoId( Urgency.INFO, false, [], [ 'someUser' ] )
        def messageId = messagesBean.sendMessage( m1 )
        def m2        = messagesBean.getMessages( 'someUser' )

        then:
        m1.id < 0
        messageId > 0
        m2.id        == messageId
        m1.timestamp == m2.timestamp
        m1.message   == m2.message
    }
}
