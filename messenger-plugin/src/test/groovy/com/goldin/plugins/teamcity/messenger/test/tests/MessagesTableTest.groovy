package com.goldin.plugins.teamcity.messenger.test.tests

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.api.MessagesTable
import com.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification


/**
 * {@link MessagesTable} test
 */
class MessagesTableTest extends BaseSpecification
{
    private List<Message> newMessages() {
        List<Message> messages = []
        random.nextInt( 100 ).times { messages <<  messageNoId( Urgency.INFO, true, [], [] ) }
        messages
    }


    def "test adding new message"() {

        when:
        Message m1 = messageNoId()
        Message m2 = messagesTable.addMessage( m1 )
        Message m3 = messagesTable.allMessages.first()

        then:
        m1.id < 0
        m2.id > 0
        m2.is( m3 )
    }


    def "test adding new messages"() {

        when:
        List<Message> messagesNew      = newMessages()
        List<Message> messagesSent     = messagesNew.collect  { messagesTable.addMessage( it    )}
        List<Message> messagesReceived = messagesSent.collect { messagesTable.getMessage( it.id )}
        List<Message> messagesAll      = messagesTable.allMessages

        then:
        messagesNew.every      { it.id < 0 }
        messagesSent.every     { it.id > 0 }
        messagesReceived.every { it.id > 0 }
        messagesNew.size() == messagesSent.size()
        messagesSent       == messagesReceived
        messagesSent.containsAll( messagesAll )
        messagesAll.containsAll( messagesSent )
    }


    def "test removing new message"() {

        when:
        Message m1 = messageNoId( Urgency.INFO, true, [], [] )
        Message m2 = messagesTable.addMessage( m1 )
        Message m3 = messagesTable.allMessages.first()
        Message m4 = messagesTable.deleteMessage( m2.id ).first()

        then:
        m1.id < 0
        m2.id > 0
        m2.is( m3 )
        m3.is( m4 )
        messagesTable.numberOfMessages == 0
    }


    def "test removing new messages"() {

        when:
        List<Message> messagesNew     = newMessages()
        List<Message> messagesSent    = messagesNew.collect  { messagesTable.addMessage( it )}
        List<Message> messagesAll     = messagesTable.allMessages
        List<Message> messagesRemoved = messagesTable.deleteMessage( messagesSent*.id as long[] )

        then:
        messagesNew.every     { it.id < 0 }
        messagesSent.every    { it.id > 0 }
        messagesRemoved.every { it.id > 0 }
        messagesNew.size()     == messagesSent.size()
        messagesSent.containsAll( messagesAll )
        messagesAll.containsAll( messagesSent )
        messagesSent           == messagesRemoved
        messagesTable.numberOfMessages == 0
    }
}
