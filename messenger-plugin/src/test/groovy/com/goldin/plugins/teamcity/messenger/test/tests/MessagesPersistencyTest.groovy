package com.goldin.plugins.teamcity.messenger.test.tests

import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification
import org.springframework.beans.factory.annotation.Autowired

/**
 * Messages persietncy machanism tests.
 */
class MessagesPersistencyTest extends BaseSpecification
{
    @Autowired
    final MessagesBean messagesBean


    def "test sending single message"() {

        when:
        assert messagesFile.size() == 0
        messagesBean.sendMessage( messageNoId( Urgency.INFO, true ))
        sleep( 100 )


        then:
        messagesBean.allMessages.size() == 1
        messagesFile.size()             == 272
    }
}
