package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesPersistency
import com.goldin.plugins.teamcity.messenger.api.Message


/**
 * {@link MessagesPersistency} implementation
 */
class MessagesPersistencyImpl implements MessagesPersistency
{
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
