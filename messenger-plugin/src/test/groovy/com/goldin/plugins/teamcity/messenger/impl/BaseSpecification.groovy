package com.goldin.plugins.teamcity.messenger.impl

import spock.lang.Specification
import org.gcontracts.annotations.Ensures

/**
 * Super class for all tests
 */
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
