package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures

/**
 * Various messages utils.
 */
class MessagesUtil
{

   /**
    * Escapes all HTML tags in the String specified.
    *
    * @param s text to escape
    * @return original text with all HTML tags escaped
    */
    @Ensures({ ! result.with{ contains( '<' ) || contains( '>' ) }})
    String htmlEscape ( String s )
    {
        assert s
        s.replace( '&', '&amp;'  ).
          replace( '"', '&quot;' ).
          replace( '<', '&lt;'   ).
          replace( '>', '&gt;'   )
    }


    List<Message> sort ( List<Message> messages )
    {
        return null
    }
}
