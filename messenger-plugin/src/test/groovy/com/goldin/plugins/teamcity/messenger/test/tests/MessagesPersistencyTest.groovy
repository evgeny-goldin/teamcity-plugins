package com.goldin.plugins.teamcity.messenger.test.tests

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.Before

/**
 * Messages persietncy machanism tests.
 */
class MessagesPersistencyTest extends BaseSpecification
{
    @Before void setup   () { messagesFile.write( '' ) }
    @After  void cleanup () { messagesFile.write( '' ) }


    def "test sending single message"() {
        when:
        def messageId = messagesBean.sendMessage( messageNoId( Urgency.INFO, true ))
        sleep( 1000 )

        then:
        messageId                                                    == 1001
        messagesBean.sendMessage( messageNoId( Urgency.INFO, true )) == 1002
        messagesBean.allMessages.size() == 2
        messagesFile.size()              > 200
    }


    def "test sending multiple messages"() {

        when:
        def n = 400

        n.times{ messagesBean.sendMessage( messageNoId( Urgency.INFO, true )) }
        def sentMessages = messagesBean.allMessages
        sleep( 1000 )

        reset()
        messagesBean.restore()
        def receivedMessages = messagesBean.allMessages

        then:
        messagesBean.sendMessage( messageNoId( Urgency.INFO, true )) == n + 1001
        messagesBean.allMessages.size()                              == n + 1

        messagesFile.size()      > 0
        sentMessages.size()     == n
        receivedMessages.size() == n

        sentMessages.every { Message m -> receivedMessages.contains( m ) }
        receivedMessages.every { Message m -> sentMessages.contains( m ) }
    }


    def "test sending multiple messages concurrently"() {

        ExecutorService pool = Executors.newFixedThreadPool( Runtime.runtime.availableProcessors() - 1 ?: 2 )
        def messagesMap      = new ConcurrentHashMap( n )

        n.times{ pool.submit({
            def message              = messageNoId( Urgency.INFO, true )
            def messageId            = messagesBean.sendMessage( message )
            messagesMap[ messageId ] = message

            if ( sleepTime > 0 ){ sleep( random.nextInt( sleepTime )) }
        } as Runnable )}

        pool.shutdown()
        pool.awaitTermination( 1, TimeUnit.HOURS )

        def sentMessages = messagesBean.allMessages
        sleep( 1000 )

        reset()
        messagesBean.restore()
        def receivedMessages = messagesBean.allMessages

        expect:
        messagesBean.sendMessage( messageNoId( Urgency.INFO, true )) == n + 1001
        messagesBean.allMessages.size()                              == n + 1

        messagesFile.size()      > 0
        sentMessages.size()     == n
        receivedMessages.size() == n

        sentMessages.every { Message m -> receivedMessages.contains( m ) }
        receivedMessages.every { Message m -> sentMessages.contains( m ) }

        where:
        n   | sleepTime
        100 | 0
        150 | 50
        200 | 100
    }

}
