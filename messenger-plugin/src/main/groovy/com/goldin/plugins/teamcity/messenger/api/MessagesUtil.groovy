package com.goldin.plugins.teamcity.messenger.api


/**
 * Various messages utils.
 */
interface MessagesUtil
{

   /**
    * Escapes all HTML tags in the String specified.
    *
    * @param s text to escape
    * @return original text with all HTML tags escaped
    */
    String htmlEscape( String s )
}
