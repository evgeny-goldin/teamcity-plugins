package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesUtil
import com.goldin.plugins.teamcity.messenger.api.Message

/**
 * {@link MessagesUtil} implementation
 */
class MessagesUtilImpl implements MessagesUtil
{

    @Override
    String htmlEscape ( String s )
    {
        assert s
        s.replace( '&', '&amp;'  ).
          replace( '"', '&quot;' ).
          replace( '<', '&lt;'   ).
          replace( '>', '&gt;'   )
    }
    

    @Override
    List<Message> sort (List<Message> messages)
    {
        return null
    }


}
