package com.goldin.plugins.teamcity.messenger.test.infra

import com.goldin.plugins.teamcity.messenger.api.MessagesPersistency
import com.goldin.plugins.teamcity.messenger.api.Message


/**
 * Test {@link MessagesPersistency} implementation
 */
class MessagesPersistencyImpl implements MessagesPersistency
{
    private List<Message> messages

    @Override
    void persist ( List<Message> messages ) { this.messages = messages }

    @Override
    List<Message> restore () { messages }
}
