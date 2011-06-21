package com.goldin.plugins.teamcity.messenger.impl

import java.util.concurrent.ConcurrentHashMap
import org.gcontracts.annotations.Requires
import com.goldin.plugins.teamcity.messenger.api.*

/**
 * {@link UsersTable} implementation
 */
class UsersTableImpl implements UsersTable
{
    final MessagesConfiguration configuration
    final MessagesContext       context
    final MessagesUtil          util

    final private             List<Message>  all    = new ArrayList<Message>( 16 )
    final private Map<String, List<Message>> groups = new ConcurrentHashMap( 128, 0.75f, 10 ).withDefault { [] }
    final private Map<String, List<Message>> users  = new ConcurrentHashMap( 128, 0.75f, 10 ).withDefault { [] }


    @Requires({ configuration && context && util })
    UsersTableImpl ( MessagesConfiguration configuration, MessagesContext context, MessagesUtil util )
    {
        this.configuration = configuration
        this.context       = context
        this.util          = util
    }


    @Override
    void init ( List<Message> messages )
    {
        messages.each{ addMessage( it ) }

        /**
         * Ordering messages for possible "over-the-limit" cleanup
         */
        util.sortForAll( all )
        for ( groupName in groups.keySet()) { util.sortForGroup( groups[ groupName ], groupName ) }
        for ( username  in users.keySet())  { util.sortForUser ( users[ username   ], username  ) }
    }


    @Override
    List<Message> getMessagesForUser ( String username )
    {
        new ArrayList( users[ username ] ).asImmutable()
    }


    @Override
    List<Message> getMessagesForGroup ( String groupName )
    {
        new ArrayList( groups[ groupName ] ).asImmutable()
    }


    @Override
    List<Message> getMessagesForAll ()
    {
        new ArrayList( all ).asImmutable()
    }


    @Override
    long addMessage ( Message message)
    {
        if ( message.sendToAll )  { all          << message }
        message.sendToGroups.each { groups[ it ] << message }
        message.sendToUsers. each { users [ it ] << message }
        message.id
    }
}
