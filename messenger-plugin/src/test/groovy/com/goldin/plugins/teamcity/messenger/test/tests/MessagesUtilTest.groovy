package com.goldin.plugins.teamcity.messenger.test.tests

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.api.MessagesUtil
import com.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification

/**
 * {@link MessagesUtil} tests
 */
class MessagesUtilTest extends BaseSpecification
{
    def "testing HTML escaping with variables"() {

        expect:
        util.htmlEscape( input ) == output

        where:
        input      | output
        ' '        | ' '
        'aa'       | 'aa'
        'html'     | 'html'
        '<html'    | '&lt;html'
        '<html>'   | '&lt;html&gt;'
        '<&html>'  | '&lt;&amp;html&gt;'
        '<&"html>' | '&lt;&amp;&quot;html&gt;'
        '<script>' | '&lt;script&gt;'
    }


    def "testing HTML escaping with files"() {

        expect:
        util.htmlEscape( text( input )) == text( output )

        where:
        input                       | output
        'htmlEscape-input.html.txt' | 'htmlEscape-output.html.txt'
    }



    def "testing Messages sorting by urgency"() {

        when:
        def m1 = messageWithId( Urgency.INFO,     false, [], [ 'username' ] )
        def m2 = messageWithId( Urgency.CRITICAL, false, [], [ 'username' ] )
        def m3 = messageWithId( Urgency.WARNING,  false, [], [ 'username' ] )

        def tests = 0
        [ m1, m2, m3 ].eachPermutation{
            List<Message> messages ->
            tests++
            assert util.sort( messages, 'username' ) == [ m2, m3, m1 ]
        }
        
        then:
        tests == 6
    }


    def "testing Messages sorting by urgency and sent to 'All'"() {

        when:
        def m1 = messageWithId( Urgency.INFO,     true,  [], [ 'username' ] )
        def m2 = messageWithId( Urgency.CRITICAL, true,  [], [ 'username' ] )
        def m3 = messageWithId( Urgency.CRITICAL, false, [], [ 'username' ] )
        def m4 = messageWithId( Urgency.INFO,     false, [], [ 'username' ] )

        def tests = 0
        [ m1, m2, m3, m4 ].eachPermutation{
            List<Message> messages ->
            tests++
            assert util.sort( messages, 'username' ) == [ m3, m2, m4, m1 ]
        }

        then:
        tests == 24
    }


    def "testing Messages sorting by sending to users"() {

        when:
        def m1 = messageWithId( Urgency.CRITICAL, true,  [], [] )
        def m2 = messageWithId( Urgency.CRITICAL, true,  [], [ 'username' ] )
        def m3 = messageWithId( Urgency.CRITICAL, false, [], [ 'username' ] )

        def tests = 0
        [ m1, m2, m3 ].eachPermutation{
            List<Message> messages ->
            tests++
            assert util.sort( messages, 'username' ) == [ m3, m2, m1 ]
        }

        then:
        tests == 6
    }


    def "testing Messages sorting by sending to groups"() {

        when:
        def m1 = messageWithId( Urgency.INFO,    true,  [ 'testGroup'  ],              [ 'otherUser' ] )
        def m2 = messageWithId( Urgency.WARNING, false, [ 'otherGroup' ],              [ 'username', 'otherUser' ] )
        def m3 = messageWithId( Urgency.WARNING, true,  [ 'testGroup', 'otherGroup' ], []  )
        def m4 = messageWithId( Urgency.WARNING, false, [ 'testGroup' ],               [ 'username' ]  )

        def tests = 0
        [ m1, m2, m3, m4 ].eachPermutation{
            List<Message> messages ->
            tests++
            assert util.sort( messages, 'username' ) == [ m4, m2, m3, m1 ]
        }

        then:
        tests == 24
    }
}
