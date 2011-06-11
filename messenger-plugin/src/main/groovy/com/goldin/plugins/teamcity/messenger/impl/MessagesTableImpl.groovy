package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.MessagesTable
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

/**
 * {@link MessagesTable} implementation
 */
class MessagesTableImpl implements MessagesTable
{
    final MessagesConfiguration configuration


    @Requires({ configuration })
    MessagesTableImpl ( MessagesConfiguration configuration )
    {
        this.configuration = configuration
    }


    @Override
    @Requires({ message && ( message.id < 0 )})
    @Ensures({ result })
    Message addMessage ( Message message )
    {
        null
    }

    
    @Override
    @Requires({ messageId > 0 })
    @Ensures({ result && ( result.id == messageId ) })
    Message deleteMessage ( long messageId )
    {
        null
    }


    @Override
    @Requires({ ( messageId > 0 ) && username })
    @Ensures({ result && ( result.id == messageId ) })
    Message deleteMessage ( long messageId, String username )
    {
        null
    }

    
    @Override
    @Ensures({ result != null })
    List<Message> getAllMessages ()
    {
        []
    }

    
    @Override
    @Requires({ m && ( m.id > 0 ) })
    boolean containsMessage ( Message m )
    {
        false
    }

    
    @Override
    void persist ()
    {

    }

    @Override
    void restore ()
    {

    }
}
