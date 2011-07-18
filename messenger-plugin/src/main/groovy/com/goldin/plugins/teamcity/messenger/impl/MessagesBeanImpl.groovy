package com.goldin.plugins.teamcity.messenger.impl

import jetbrains.buildServer.serverSide.BuildServerAdapter
import jetbrains.buildServer.serverSide.BuildServerListener
import jetbrains.buildServer.util.EventDispatcher
import org.gcontracts.annotations.Invariant
import org.gcontracts.annotations.Requires
import org.springframework.context.ApplicationContext
import com.goldin.plugins.teamcity.messenger.api.*

/**
 * {@link MessagesBean} implementation
 */
@Invariant({ this.messagesTable && this.usersTable && this.context && this.util && this.persistencyExecutor })
class MessagesBeanImpl implements MessagesBean
{
    private final MessagesTable   messagesTable
    private final UsersTable      usersTable
    private final MessagesContext context
    private final MessagesUtil    util
    private final TaskExecutor    persistencyExecutor


    @Delegate( interfaces = true )
    final BuildServerListener listener = new BuildServerAdapter()


    @Requires({ messagesTable && usersTable && persistency && context && util && springContext })
    MessagesBeanImpl ( MessagesTable messagesTable, UsersTable usersTable, MessagesPersistency persistency,
                       MessagesContext context, MessagesUtil util, ApplicationContext springContext )
    {
        this.messagesTable       = messagesTable
        this.usersTable          = usersTable
        this.context             = context
        this.util                = util
        this.persistencyExecutor = new TaskExecutor({ persistency.save( messagesTable.persistencyData ) }, context )

        /**
         * Adding this bean as server life cycle listener when not in test environment.
         */
        if ( ! context.isTest())
        {
            EventDispatcher<BuildServerListener> dispatcher = ( EventDispatcher ) springContext.getBean( 'serverDispatcher', EventDispatcher )
            assert dispatcher, "Failed to locate [${ EventDispatcher.class.name }] bean in Spring context"
            dispatcher.addListener( this )
        }

        /**
         * Restoring data from persistent storage
         */
        messagesTable.restoreFromPersistencyData( persistency.restore())
        usersTable.restore( messagesTable.allMessages )
    }


    /**
     * Persists messages table in the current thread
     */
    @Override
    void serverShutdown () { persistencyExecutor.execute( false ) }


    /**
     * Persists messages table in the background thread
     */
    private void persistMessages () { persistencyExecutor.execute( true ) }


    @Override
    List<Message> getAllMessages () { messagesTable.allMessages }


    @Override
    long sendMessage ( Message message )
    {
        assert ( context.isTest() || context.getUser( message.sender )), "Sender [${ message.sender }] doesn't exist"
        Message newMessage = messagesTable.addMessage( message ) // New Message is a copy of the sent one but with "id" set
        usersTable.addMessage( newMessage )

        persistMessages()

        context.log.info( "[$newMessage] sent" )
        newMessage.id
    }


    @Override
    List<Message> getMessagesForUser ( String username )
    {
        List<Message> messages = []

        /**
         * Find messages for user, all his groups and "all" users
         */
        messages.addAll( usersTable.getMessagesForUser( username ))
        context.getUserGroups( username ).each { messages.addAll( usersTable.getMessagesForGroup( it )) }
        messages.addAll( usersTable.messagesForAll )

        /**
         * Filter out duplicates, deleted messages and sort by importance for this user
         */
        util.sortForUser( messages.unique  { Message m1, Message m2 -> m1.id <=> m2.id }.
                                   findAll { Message m -> ! m.usersDeleted.contains( username )}.
                                   findAll { Message m -> messagesTable.containsMessage( m.id )},
                          username )
    }


    @Override
    List<Message> deleteMessage ( long ... messageIds )
    {
        def messages = messagesTable.deleteMessage( messageIds )
        if ( messages ) { persistMessages() }
        messages
    }


    @Override
    Message deleteMessageByUser ( long messageId, String username )
    {
        def  message = messagesTable.deleteMessageByUser( messageId, username )
        if ( message ){ persistMessages() }
        message
    }
}
