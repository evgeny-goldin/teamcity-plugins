package com.goldin.plugins.teamcity.messenger.test.tests

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.junit.Before

/**
 * Messages persietncy machanism tests.
 */
class MessagesPersistencyTest extends BaseSpecification
{
    @Before
    void setup   () { assert  messagesFile.with { ( ! isFile()) || delete() }}
    void cleanup () { assert  messagesFile.with { ( ! isFile()) || delete() }}


    def "test sending single message"() {
        when:
        assert messagesTable.numberOfMessages == 0

        def messageId = messagesBean.sendMessage( messageNoId( Urgency.INFO, true ))
        sleep( 100 )

        then:
        messageId                                                    == 1001
        messagesBean.sendMessage( messageNoId( Urgency.INFO, true )) == 1002
        messagesBean.allMessages.size() == 2
        messagesFile.size()              > 200
    }


    def "test sending multiple messages"() {

        when:
        assert messagesFile.size()            == 0
        assert messagesTable.numberOfMessages == 0

        def n = 1000

        n.times{ messagesBean.sendMessage( messageNoId( Urgency.INFO, true )) }
        def sentMessages = messagesBean.allMessages
        sleep( 500 )

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
        receivedMessages == sentMessages
    }


    def "test sending multiple messages concurrently"() {

        assert messagesFile.size()            == 0
        assert messagesTable.numberOfMessages == 0

        ExecutorService pool = Executors.newFixedThreadPool( Runtime.runtime.availableProcessors() - 1 ?: 2 )
        def messagesMap      = new ConcurrentHashMap( n )

        n.times{ pool.submit({
            def message              = messageNoId( Urgency.INFO, true )
            def messageId            = messagesBean.sendMessage( message )
            messagesMap[ messageId ] = message

            if ( sleepTime ){ sleep( random.nextInt( sleepTime )) }
        } as Runnable )}

        pool.shutdown()
        pool.awaitTermination( 1, TimeUnit.HOURS )

        def sentMessages = messagesBean.allMessages
        sleep( 500 )

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
        n    | sleepTime
        100  | 0
        500  | 50
        1000 | 100
    }

}
