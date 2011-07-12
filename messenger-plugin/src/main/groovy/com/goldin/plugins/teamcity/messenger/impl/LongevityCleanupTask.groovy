package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import org.gcontracts.annotations.Invariant
import org.gcontracts.annotations.Requires

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
        def now           = System.currentTimeMillis()
        long[] messageIds = messagesBean.allMessages.findAll { it.longevity > 0 }.
                                                     findAll { (( now - it.timestamp ) / 3600000 ) > it.longevity }*.id
        if ( messageIds )
        {
            messagesBean.deleteMessage( messageIds )

            context.log.info(
                "Longevity cleanup: [${ messageIds.size() }] message${( messageIds.size() == 1 ) ? '' : 's' } deleted" +
                "${ messageIds ? ': ' + messageIds : '' }." )
        }
    }
}
