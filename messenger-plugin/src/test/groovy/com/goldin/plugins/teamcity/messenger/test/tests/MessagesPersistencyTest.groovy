package com.goldin.plugins.teamcity.messenger.test.tests

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification

/**
 * Messages persietncy machanism tests.
 */
class MessagesPersistencyTest extends BaseSpecification
{

    def "test sending single message"() {

        given:
        assert messagesFile.size()            == 0
        assert messagesTable.numberOfMessages == 0

        when:
        messagesBean.sendMessage( messageNoId( Urgency.INFO, true ))
        sleep( 100 )

        then:
        messagesBean.allMessages.size() == 1
        messagesFile.size()              > 200
    }


    def "test sending multiple messages"() {

        given:
        assert messagesFile.size()            == 0
        assert messagesTable.numberOfMessages == 0
        def n = 1000

        when:
        n.times{ messagesBean.sendMessage( messageNoId( Urgency.INFO, true )) }
        def sentMessages = messagesBean.allMessages
        sleep( 500 )

        MessagesBean restoredMessagesBean = ( MessagesBean ) springContext.getBean( 'messages-bean', MessagesBean )
        def receivedMessages = restoredMessagesBean.allMessages

        then:
        sentMessages.size()     == n
        receivedMessages.size() == n
        messagesFile.size()      > 0
        sentMessages.every { Message m -> receivedMessages.contains( m ) }
    }
}
