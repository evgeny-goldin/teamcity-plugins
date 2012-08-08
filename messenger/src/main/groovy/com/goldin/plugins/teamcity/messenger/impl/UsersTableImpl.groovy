package com.goldin.plugins.teamcity.messenger.impl

import java.util.concurrent.ConcurrentHashMap
import org.gcontracts.annotations.Requires
import com.goldin.plugins.teamcity.messenger.api.*
import org.gcontracts.annotations.Invariant


/**
 * {@link UsersTable} implementation
 */
@Invariant({ this.context && this.config && this.util &&
             ( this.all != null ) && ( this.groups != null ) && ( this.users != null ) })
class UsersTableImpl implements UsersTable
{
    private final MessagesContext            context
    private final MessagesConfiguration      config
    private final MessagesUtil               util
    private final List<Message>              all
    private final Map<String, List<Message>> groups
    private final Map<String, List<Message>> users


    @Requires({ context && config && util })
    UsersTableImpl ( MessagesContext context, MessagesConfiguration config, MessagesUtil util )
    {
        this.context = context
        this.config  = config
        this.util    = util
        this.all     = new ArrayList<Message>( 16 )
        this.groups  = new ConcurrentHashMap( 128, 0.75f, 10 ).withDefault { [] }
        this.users   = new ConcurrentHashMap( 128, 0.75f, 10 ).withDefault { [] }
    }


    @Override
    void restore ( List<Message> messages )
    {
        if ( messages )
        {
            messages.each{ addMessage( it ) }

            /**
             * Ordering messages for possible "over-the-limit" cleanup
             */
            util.sortForAll( all )
            for ( groupName in groups.keySet()) { util.sortForGroup( groups[ groupName ], groupName ) }
            for ( username  in users.keySet())  { util.sortForUser ( users[ username   ], username  ) }
        }
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
    long addMessage ( Message message )
    {
        if ( message.sendToAll )  { all          << message }
        message.sendToGroups.each { groups[ it ] << message }
        message.sendToUsers. each { users [ it ] << message }
        message.id
    }
}
