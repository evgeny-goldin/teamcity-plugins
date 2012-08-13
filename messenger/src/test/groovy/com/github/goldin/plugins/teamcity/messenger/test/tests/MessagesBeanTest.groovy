package com.github.goldin.plugins.teamcity.messenger.test.tests

import com.github.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.github.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.github.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification
import spock.lang.FailsWith


/**
 * {@link MessagesBean} test
 */
@SuppressWarnings([ 'JUnitPublicNonTestMethod' ])
class MessagesBeanTest extends BaseSpecification
{

    def "testing sending message to user"() {

        when:
        def m1        = messageNoId( Urgency.INFO, false, [], [ 'someUser' ] )
        def messageId = messagesBean.sendMessage( m1 )
        def m2        = messagesBean.getMessagesForUser( 'someUser' ).first()

        then:
        m1.id     < 0
        messageId > 0
        m2.id        == messageId
        m1.timestamp == m2.timestamp
        m1.text      == m2.text
    }


    def "testing deleting message sent to user"() {

        when:
        def m1        = messageNoId( Urgency.INFO, false, [], [ 'someUser' ] )
        def messageId = messagesBean.sendMessage( m1 )
        def messages1 = messagesBean.getMessagesForUser( 'someUser' )

        messagesBean.deleteMessageByUser( messageId, 'someUser' )
        def messages2 = messagesBean.getMessagesForUser( 'someUser' )

        then:
        ! messages1.isEmpty()
        messages1.first().with{ ( timestamp == m1.timestamp ) && ( text == m1.text ) }
        messages2.isEmpty()
        ! messagesTable.containsMessage( messageId )
    }


    def "testing deleting message sent to multiple users"() {

        when:
        def messageId = messagesBean.sendMessage( messageNoId( Urgency.INFO, false, [], [ 'someUser', 'someOtherUser' ] ))
        messagesBean.deleteMessageByUser( messageId, 'someUser' )
        messagesBean.deleteMessageByUser( messageId, 'someOtherUser' )

        then:
        ! messagesBean.getMessagesForUser( 'someUser' )
        ! messagesBean.getMessagesForUser( 'someOtherUser' )
        ! messagesTable.containsMessage( messageId )
    }


    def "testing deleting message sent to multiple groups and users"() {

        when:
        def messageId = messagesBean.sendMessage( messageNoId( Urgency.INFO, false, [ 'someGroup' ], [ 'someUser', 'someOtherUser' ] ))
        messagesBean.deleteMessageByUser( messageId, 'someUser' )
        messagesBean.deleteMessageByUser( messageId, 'someOtherUser' )

        then:
        ! messagesBean.getMessagesForUser( 'someUser' )
        ! messagesBean.getMessagesForUser( 'someOtherUser' )
        messagesTable.containsMessage( messageId )
    }


    def "testing deleting message"() {

        when:
        def m1        = messageNoId( Urgency.INFO, false, [], [ 'someUser' ] )
        def messageId = messagesBean.sendMessage( m1 )
        def messages1 = messagesBean.getMessagesForUser( 'someUser' )

        messagesBean.deleteMessage( messageId )
        def messages2 = messagesBean.getMessagesForUser( 'someUser' )

        then:
        ! messages1.isEmpty()
        messages1.first().with{ ( timestamp == m1.timestamp ) && ( text == m1.text ) }
        messages2.isEmpty()
        ! messagesTable.containsMessage( messageId )
    }


    @FailsWith ( AssertionError )
    def "testing deleting message with illegal id"() {

        expect:
        messagesBean.sendMessage( messageNoId( Urgency.INFO, false, [], [ 'someUser' ] ))
        messagesBean.sendMessage( messageNoId( Urgency.INFO, false, [], [ 'someUser' ] ))
        messagesBean.deleteMessage( messageId )

        where:
        messageId | dummy
        -1        | ''
         1        | ''
         10001    | ''
         10002    | ''
    }
}
