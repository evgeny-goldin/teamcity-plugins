package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

/**
 * Users table
 */
interface UsersTable
{
    @Requires({ messages != null })
    void init ( List<Message> messages )


    @Requires({ username })
    @Ensures({ result != null })
    List<Message> getMessagesForUser ( String username  )


    @Requires({ groupName })
    @Ensures({ result != null })
    List<Message> getMessagesForGroup( String groupName )


    @Ensures({ result != null })
    List<Message> getMessagesForAll  ()


    @Requires({ message && ( message.id > 0 ) })
    @Ensures({ result == message.id })
    long addMessage( Message message )
}
