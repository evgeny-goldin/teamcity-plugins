package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.api.Message


/**
 * {@link MessagesBean} implementation
 */
class MessagesBeanImpl implements MessagesBean
{
    
    @Override
    long sendMessage (Message message)
    {
        0
    }

    @Override
    List<Message> getMessagesForUser (String username)
    {
        []
    }

    @Override
    Message deleteMessage (long messageId)
    {
        null
    }

    @Override
    Message deleteMessage (long messageId, String username)
    {
        null
    }

}
