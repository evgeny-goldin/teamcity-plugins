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
    * Un-escapes all HTML tags in the String specified.
    *
    * @param s text to un-escape
    * @return original text with all HTML tags un-escaped
    */
    @Requires({ s })
    @Ensures({ ! result.with{ contains( '&lt;' ) || contains( '&gt;' ) }})
    String htmlUnescape ( String s )
    {
        s.replace( '&quot;', '"' ).
          replace( '&lt;',   '<' ).
          replace( '&gt;',   '>' ).
          replace( '&amp;',  '&' )
    }


    /**
     * Determines if one of booleans specified is true while other is false.
     */
    boolean different ( boolean b1, boolean b2 ) { ( b1 || b2 ) && ( ! ( b1 && b2 )) }


    /**
     * Determines if two sets intersect
     * @param c1 first set to check
     * @param c2 second set to check
     * @return true of sets specified have elements in common, false otherwise
     */
    @Requires({ ( c1 != null ) && ( c2 != null ) })
    boolean intersect ( Collection<?> c1, Collection<?> c2 )
    {
        def ( Collection smaller, Collection larger ) = ( c1.size() <= c2.size()) ? [ c1, c2 ] : [ c2, c1 ]
        assert smaller.size() <= larger.size()

        for ( o in smaller ) { if ( larger.contains( o )) { return true }}
        false
    }


    /**
     * Sorts messages specified  ignoring their groups and user recipients.
     * @param  messages messages to sort
     * @return messages sorted ignoring messages groups and user recipients,
     *         if collection specified is a <code>List</code> then sorting happens <b>in place</b> and
     *         original <code>List</code> is modified and returned
     */
    @Requires({ ( messages != null ) })
    @Ensures({  ( result   != null ) && ( result.size() == messages.size()) })
    List<Message> sortForAll ( Collection<Message> messages )
    {
        messages.each { Message m -> assert m.sendToAll, "[$m] wasn't sent to all" }
        messages.sort {
            Message m1, Message m2 ->

            int  urgencyCompare = ( m1.urgency <=> m2.urgency )
            if ( urgencyCompare != 0 ){ return urgencyCompare }

            - ( m1.timestamp <=> m2.timestamp )
        }
    }


    /**
     * Sorts messages specified for specific group, ignoring their user recipients.
     * @param  messages messages to sort
     * @param  groupName name of the group to sort the messages for
     * @return messages sorted ignoring messages user recipients,
     *         if collection specified is a <code>List</code> then sorting happens <b>in place</b> and
     *         original <code>List</code> is modified and returned
     */
    @Requires({ ( messages != null ) && groupName })
    @Ensures({  ( result   != null ) && ( result.size() == messages.size()) })
    List<Message> sortForGroup ( Collection<Message> messages, String groupName )
    {
        messages.each { Message m -> assert m.forGroup( groupName ), "[$m] wasn't sent to group [$groupName]" }
        messages.sort {
            Message m1, Message m2 ->

            int urgencyCompare = ( m1.urgency <=> m2.urgency )

            if ( urgencyCompare != 0                    ){ return urgencyCompare }
            if ( different( m1.sendToAll, m2.sendToAll )){ return ( m1.sendToAll ? 1 : -1 ) }

            - ( m1.timestamp <=> m2.timestamp )
        }
    }


    /**
     * Sorts messages specified for specific user.
     * @param  messages messages to sort
     * @param  username name of the user to sort the messages for
     * @return messages sorted for the user provided,
     *         if collection specified is a <code>List</code> then sorting happens <b>in place</b> and
     *         original <code>List</code> is modified and returned
     */
    @Requires({ ( messages != null ) && username })
    @Ensures({  ( result   != null ) && ( result.size() == messages.size()) })
    List<Message> sortForUser ( Collection<Message> messages, String username )
    {
        messages.each { Message m -> assert m.forUser( username ), "[$m] wasn't sent to user [$username]" }
        messages.sort {
            Message m1, Message m2 ->

            int  urgencyCompare = ( m1.urgency <=> m2.urgency )
            if ( urgencyCompare != 0                    ){ return urgencyCompare }

            if ( different( m1.sendToAll, m2.sendToAll )){ return ( m1.sendToAll ? 1 : -1 ) }

            boolean m1ForUser = m1.sendToUsers.contains( username )
            boolean m2ForUser = m2.sendToUsers.contains( username )

            if ( different( m1ForUser, m2ForUser )){ return ( m1ForUser ? -1 : 1 ) }

            Set<String> userGroups = context.getUserGroups( username )
            boolean m1ForUserGroup = intersect( m1.sendToGroups, userGroups )
            boolean m2ForUserGroup = intersect( m2.sendToGroups, userGroups )

            if ( different( m1ForUserGroup, m2ForUserGroup )){ return ( m1ForUserGroup ? -1 : 1 ) }

            - ( m1.timestamp <=> m2.timestamp )
        }
    }
}
