package com.goldin.plugins.teamcity.messenger.impl

import com.intellij.util.containers.ConcurrentList
import java.util.concurrent.ConcurrentHashMap
import org.gcontracts.annotations.Ensures
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

    final private             List<Message>  all    = new ConcurrentList<Message>( 16 )
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
    @Requires({ messages != null })
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
    @Requires({ username })
    @Ensures({ result != null })
    List<Message> getMessagesForUser ( String username )
    {
        new ArrayList( users[ username ] ).asImmutable()
    }


    @Override
    @Requires({ groupName })
    @Ensures({ result != null })
    List<Message> getMessagesForGroup ( String groupName )
    {
        new ArrayList( groups[ groupName ] ).asImmutable()
    }


    @Override
    @Ensures({ result != null })
    List<Message> getMessagesForAll ()
    {
        new ArrayList( all ).asImmutable()
    }


    @Override
    @Requires({ m && ( m.id > 0 ) })
    @Ensures({ ( result > 0 ) && ( result == m.id ) })
    long addMessage ( Message m )
    {
        if ( m.sendToAll )  { all          << m }
        m.sendToGroups.each { groups[ it ] << m }
        m.sendToUsers. each { users [ it ] << m }
        m.id
    }
}
