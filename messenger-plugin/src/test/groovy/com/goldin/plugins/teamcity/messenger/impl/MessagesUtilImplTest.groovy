package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesUtil
import spock.lang.Specification

/**
 * {@link MessagesUtilImpl} tests
 */
class MessagesUtilImplTest extends Specification
{
    final MessagesUtil util = new MessagesUtilImpl()


    def "testing HTML escaping"() {

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
}
