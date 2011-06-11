package com.goldin.plugins.teamcity.messenger.test.infra

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.goldin.plugins.teamcity.messenger.api.MessagesUtil
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Super class for all tests
 */
@ContextConfiguration( locations = 'classpath:/build-server-plugin-messenger-test.xml' )
class BaseSpecification extends Specification
{
    @Autowired
    final MessagesUtil    util

    @Autowired
    final MessagesContext context

    private counter = 1


    @Requires({ fileName })
    @Ensures({ result })
    String text ( String fileName )
    {
        def    url = this.getClass().getResource( "/$fileName" )
        assert url, "Resource \"/$fileName\" not found"
        url.text
    }


//    @Requires({ urgency && ( sendToGroups != null ) && ( sendToUsers != null ) })
    @Ensures({ result.id > 0 })
    Message messageWithId ( Urgency urgency = Urgency.INFO, boolean sendToAll = true, List<String> sendToGroups = [], List<String> sendToUsers = [] )
    {
        new Message( counter++, context, util, messageNoId( urgency, sendToAll, sendToGroups, sendToUsers ))
    }

    
//    @Requires({ urgency && ( sendToGroups != null ) && ( sendToUsers != null ) })
    @Ensures({ result.id == -1 })
    Message messageNoId ( Urgency urgency = Urgency.INFO, boolean sendToAll = true, List<String> sendToGroups = [], List<String> sendToUsers = [] )
    {
        new Message( 'me', urgency, "[$urgency] message", -1, sendToAll, sendToGroups, sendToUsers )
    }
}
