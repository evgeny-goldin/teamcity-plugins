package com.goldin.plugins.teamcity.messenger.test.tests

import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.impl.MessagesBeanImpl
import org.springframework.beans.factory.annotation.Autowired
import com.goldin.plugins.teamcity.messenger.test.infra.BaseSpecification

/**
 * {@link MessagesBeanImpl} test
 */
class MessagesBeanTest extends BaseSpecification
{
    @Autowired
    MessagesBean messagesBean

    
}
