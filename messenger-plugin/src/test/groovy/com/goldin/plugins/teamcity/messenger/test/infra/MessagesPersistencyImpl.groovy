package com.goldin.plugins.teamcity.messenger.test.infra

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.goldin.plugins.teamcity.messenger.api.MessagesPersistency
import com.goldin.plugins.teamcity.messenger.api.MessagesUtil
import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration

/**
 * Test {@link MessagesPersistency} implementation
 */
class MessagesPersistencyImpl implements MessagesPersistency
{
    private Map data


    MessagesPersistencyImpl ( MessagesContext context, MessagesConfiguration config, MessagesUtil util )
    {
        data = [ messageId : 1002,
                 messages  : [ new Message( 1001, context, config, util, new Message( 'me', Urgency.INFO, 'text' )).messagePersistencyData ]]
    }


    @Override
    void save ( Map data ) { this.data = data }


    @Override
    Map restore () { data }
}
