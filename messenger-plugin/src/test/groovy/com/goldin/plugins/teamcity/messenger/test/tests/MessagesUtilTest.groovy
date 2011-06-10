package com.goldin.plugins.teamcity.messenger.test.tests

import com.goldin.plugins.teamcity.messenger.api.MessagesUtil
import com.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.api.MessagesContext

/**
 * {@link MessagesUtil} tests
 */
class MessagesUtilTest extends BaseSpecification
{
    @Autowired
    final MessagesUtil util

    @Autowired
    final MessagesContext context


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
        def m1 = new Message( 1, context, util, new Message( 'me', Urgency.INFO,     'INFO     Message', -1, false, [], [ 'username' ] ))
        def m2 = new Message( 2, context, util, new Message( 'me', Urgency.CRITICAL, 'CRITICAL Message', -1, false, [], [ 'username' ] ))
        def m3 = new Message( 3, context, util, new Message( 'me', Urgency.WARNING,  'WARNING  Message', -1, false, [], [ 'username' ] ))

        then:
        def permutations = [ m1, m2, m3 ].eachPermutation{
            List<Message> messages ->
            assert util.sort( messages, 'username' ) == [ m2, m3, m1 ]
        }
    }
}
