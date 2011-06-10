package com.goldin.plugins.teamcity.messenger.test.infra

import org.gcontracts.annotations.Ensures
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification


/**
 * Super class for all tests
 */
@ContextConfiguration( locations = 'classpath:/build-server-plugin-messenger-test.xml' )
class BaseSpecification extends Specification
{

    @Ensures({ result })
    String text ( String fileName )
    {
        def    url = this.getClass().getResource( "/$fileName" )
        assert url, "Resource \"/$fileName\" not found"
        url.text
    }
}
