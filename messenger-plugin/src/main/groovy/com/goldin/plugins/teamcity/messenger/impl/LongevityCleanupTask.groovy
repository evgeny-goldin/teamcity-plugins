package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import org.gcontracts.annotations.Requires
import org.gcontracts.annotations.Invariant


/**
 * Longevity cleanup timer task
 */
@Invariant({ this.context && this.messagesBean })
class LongevityCleanupTask extends TimerTask
{
    private final MessagesContext context
    private final MessagesBean    messagesBean


    @Requires({ context && messagesBean })
    LongevityCleanupTask ( MessagesContext context, MessagesBean messagesBean )
    {
        this.context      = context
        this.messagesBean = messagesBean
    }


    @Override
    void run ()
    {
        def now     = System.currentTimeMillis()
        def deleted = []

        for ( Message message in messagesBean.allMessages )
        {   /**
             * Message age in hours. If due to clock changes message
             * comes "from the future" (message.timestamp > now),
             * then its age would be negative.
             */
            def  messageAge = (( now - message.timestamp ) / 3600000 )
            if ( messageAge > message.longevity )
            {
                deleted << messagesBean.deleteMessage( message.id, false ).id
            }
        }

        if ( deleted )
        {
            context.log.info(
                "Longevity cleanup: [${ deleted.size() }] message${( deleted.size() == 1 ) ? '' : 's' } deleted" +
                "${( deleted.size() > 0 ) ? ': ' + deleted  : '' }." )

            messagesBean.persistMessages()
        }
    }
}
