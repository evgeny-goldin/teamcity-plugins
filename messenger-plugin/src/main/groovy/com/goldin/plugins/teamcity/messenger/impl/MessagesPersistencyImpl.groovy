package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.goldin.plugins.teamcity.messenger.api.MessagesPersistency
import org.gcontracts.annotations.Requires

/**
 * {@link MessagesPersistency} implementation
 */
class MessagesPersistencyImpl implements MessagesPersistency
{

    final MessagesContext context

    @Requires({ context })
    MessagesPersistencyImpl ( MessagesContext context )
    {
        this.context = context
    }

    
    @Override
    void persist (List<Message> messages)
    {

    }

    @Override
    List<Message> restore ()
    {
        []
    }

}
