package com.goldin.plugins.teamcity.messenger.test.tests

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.MessagesTable
import com.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import org.junit.Before

/**
 * {@link MessagesTable} test
 */
class MessagesTableTest extends BaseSpecification
{
    @Autowired
    final MessagesTable table


    private List<Message> newMessages() {
        List<Message> messages = []
        random.nextInt( 100 ).times { messages <<  messageNoId( Urgency.INFO, true, [], [] ) }
        messages
    }


    @Before // Not required for Spock but this silents "JUnitPublicNonTestMethod" CodeNarc rule
    def setup() { table.deleteAllMessages() }

    
    def "test adding new message"() {

        when:
        Message m1 = messageNoId()
        Message m2 = table.addMessage( m1 )
        Message m3 = table.allMessages.first()

        then:
        m1.id < 0
        m2.id > 0
        m2.is( m3 )
    }


    def "test adding new messages"() {

        when:
        List<Message> messagesNew      = newMessages()
        List<Message> messagesSent     = messagesNew.collect  { table.addMessage( it    )}
        List<Message> messagesReceived = messagesSent.collect { table.getMessage( it.id )}
        List<Message> messagesAll      = table.allMessages

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
        Message m2 = table.addMessage( m1 )
        Message m3 = table.allMessages.first()
        Message m4 = table.deleteMessage( m2.id )

        then:
        m1.id < 0
        m2.id > 0
        m2.is( m3 )
        m3.is( m4 )
        table.numberOfMessages == 0
    }


    def "test removing new messages"() {

        when:
        List<Message> messagesNew     = newMessages()
        List<Message> messagesSent    = messagesNew.collect  { table.addMessage( it )}
        List<Message> messagesAll     = table.allMessages
        List<Message> messagesRemoved = messagesSent.collect { table.deleteMessage( it.id )}

        then:
        messagesNew.every     { it.id < 0 }
        messagesSent.every    { it.id > 0 }
        messagesRemoved.every { it.id > 0 }
        messagesNew.size()     == messagesSent.size()
        messagesSent.containsAll( messagesAll )
        messagesAll.containsAll( messagesSent )
        messagesSent           == messagesRemoved
        table.numberOfMessages == 0
    }
}
