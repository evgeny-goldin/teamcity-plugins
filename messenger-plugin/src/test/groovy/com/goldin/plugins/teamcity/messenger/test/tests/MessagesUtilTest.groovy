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


    def "testing positive intersect()"() {

        expect:
        permutations( s1, s2 ) { List l1, List l2 ->
            assert util.intersect( l1, l1 ) && util.intersect( l2, l2 )
            assert util.intersect( l1, l2 ) && util.intersect( l2, l1 )
        }

        where:
        s1                | s2
        [1, 2, 3]  | [3, 4, 5]
        [1]        | [3, 4, 5, 1, 6]
        [0, 0, 0]  | [3, 4, 5, 0, 6]
        ['a', 'b'] | ['c', 'b', 'd']
        ['q', 'b'] | ['c', 'b', 'a']
        ['!',  ''] | ['!', '@', '' ]
    }


    def "testing negative intersect()"() {

        expect:
        ! ( util.intersect( [], [] ) || util.intersect( [1], [] ) || util.intersect( [], [2] ))
        
        permutations( s1, s2 ) { List l1, List l2 ->
            assert ! ( util.intersect( l1, l2 ) || util.intersect( l2, l1 ))
        }

        where:
        s1         | s2
        [1, 2, 3]  | [6, 4, 5]
        [1]        | [3, 4, 5, 2, 6]
        [0, 0, 0]  | [3, 4, 5, 8, 6]
        ['a', 'b'] | ['c', 'x', 'd']
        ['q', 'b'] | ['c', 'z', 'a']
        ['#',  ''] | ['!', '@', 'c']
    }


    def "testing Messages sorting by urgency"() {

        when:
        def m1 = messageWithId( Urgency.INFO,     false, [], [ 'username' ] )
        def m2 = messageWithId( Urgency.CRITICAL, false, [], [ 'username' ] )
        def m3 = messageWithId( Urgency.WARNING,  false, [], [ 'username' ] )

        then:
        6 == permutations([ m1, m2, m3 ]) {
            List<Message> messages -> assert util.sort( messages, 'username' ) == [ m2, m3, m1 ]
        }
    }


    def "testing Messages sorting by urgency and sent to 'All'"() {

        when:
        def m1 = messageWithId( Urgency.INFO,     true,  [], [ 'username' ] )
        def m2 = messageWithId( Urgency.CRITICAL, true,  [], [ 'username' ] )
        def m3 = messageWithId( Urgency.CRITICAL, false, [], [ 'username' ] )
        def m4 = messageWithId( Urgency.INFO,     false, [], [ 'username' ] )

        then:
        24 == permutations([ m1, m2, m3, m4 ]) {
            List<Message> messages -> assert util.sort( messages, 'username' ) == [ m3, m2, m4, m1 ]
        }
    }


    def "testing Messages sorting by sending to users"() {

        when:
        def m1 = messageWithId( Urgency.CRITICAL, true,  [], [] )
        def m2 = messageWithId( Urgency.CRITICAL, true,  [], [ 'username' ] )
        def m3 = messageWithId( Urgency.CRITICAL, false, [], [ 'username' ] )

        then:
        6 == permutations([ m1, m2, m3 ]) {
            List<Message> messages -> assert util.sort( messages, 'username' ) == [ m3, m2, m1 ]
        }
    }


    def "testing Messages sorting by sending to groups"() {

        when:
        def m1 = messageWithId( Urgency.INFO,    true,  [ 'testGroup'  ],              [ 'otherUser' ] )
        def m2 = messageWithId( Urgency.WARNING, false, [ 'otherGroup' ],              [ 'username', 'otherUser' ] )
        def m3 = messageWithId( Urgency.WARNING, true,  [ 'testGroup', 'otherGroup' ], []  )
        def m4 = messageWithId( Urgency.WARNING, false, [ 'testGroup' ],               [ 'username' ]  )

        then:
        24 == permutations([ m1, m2, m3, m4 ]) {
            List<Message> messages -> assert util.sort( messages, 'username' ) == [ m4, m2, m3, m1 ]
        }
    }
}
