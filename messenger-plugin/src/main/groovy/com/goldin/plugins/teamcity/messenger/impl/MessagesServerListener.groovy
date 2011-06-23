package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import jetbrains.buildServer.serverSide.BuildServerAdapter
import org.gcontracts.annotations.Requires
import org.gcontracts.annotations.Invariant
import com.goldin.plugins.teamcity.messenger.api.MessagesContext


/**
 * {@link jetbrains.buildServer.serverSide.BuildServerListener} implementation.
 */
@Invariant({ this.context && this.messagesBean })
class MessagesServerListener extends BuildServerAdapter
{
    private final MessagesContext context
    private final MessagesBean    messagesBean


    @Requires({ context && messagesBean })
    MessagesServerListener ( MessagesContext context, MessagesBean messagesBean )
    {
        this.context      = context
        this.messagesBean = messagesBean
    }


    @Override
    void serverShutdown ()
    {
        context.log.info( 'Server shutdown - persisting messages' )
        messagesBean.persistMessages()
        context.log.info( 'Server shutdown - messages persisted' )
    }
}
