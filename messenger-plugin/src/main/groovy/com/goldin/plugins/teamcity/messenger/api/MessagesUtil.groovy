package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

/**
 * Various messages utils.
 */
class MessagesUtil
{
    final MessagesContext context

    @Requires({ context })
    MessagesUtil ( MessagesContext context )
    {
        this.context = context
    }

    
   /**
    * Escapes all HTML tags in the String specified.
    *
    * @param s text to escape
    * @return original text with all HTML tags escaped
    */
    @Requires({ s })
    @Ensures({ ! result.with{ contains( '<' ) || contains( '>' ) }})
    String htmlEscape ( String s )
    {
        s.replace( '&', '&amp;'  ).
          replace( '"', '&quot;' ).
          replace( '<', '&lt;'   ).
          replace( '>', '&gt;'   )
    }


    /**
     * Determines if one of booleans specified is true while other is false.
     */
    boolean different ( boolean b1, boolean b2 ) { ( b1 || b2 ) && ( ! ( b1 && b2 )) }



    @Requires({ messages && username })
    List<Message> sort( List<Message> messages, String username )
    {
        messages.sort {
            Message m1, Message m2 ->

            [ m1, m2 ].each{ assert it.forUser( username ), "[$it] wasn't sent to user [$username]" }

            int urgencyCompare = m1.urgency.compareTo( m2.urgency )
            if ( urgencyCompare != 0 ) { return urgencyCompare }

            if ( different( m1.sendToAll, m2.sendToAll )){ return ( m1.sendToAll ? 1 : -1 ) }

            boolean m1ForUser = m1.sendToUsers.contains( username )
            boolean m2ForUser = m2.sendToUsers.contains( username )

            if ( different( m1ForUser, m2ForUser )){ return ( m1ForUser ? -1 : 1 ) }

            Set<String> userGroups = context.getUserGroups( username )
            boolean m1ForUserGroup = ! m1.sendToGroups.disjoint( userGroups )
            boolean m2ForUserGroup = ! m2.sendToGroups.disjoint( userGroups )

            if ( different( m1ForUserGroup, m2ForUserGroup )){ return ( m1ForUserGroup ? -1 : 1 ) }

            (( m1.timestamp > m2.timestamp ) ? -1 : 1 )
        }
    }

}
