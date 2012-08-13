package com.github.goldin.plugins.teamcity.messenger.api

import java.text.DateFormat
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.jdom.Element


/**
 * Configuration data
 */
interface MessagesConfiguration
{

    /**
     * Reads data from XML configuration
     * @param root XML configuration root
     */
    @Requires({ root })
    void readFrom ( Element root )


    /**
     * Retrieves plugin log category
     * @return plugin log category
     */
    @Ensures({ result })
    String getLogCategory()


    /**
     * Determines if static resources (js, css) should be minified
     * @return true if static resources should be minified, false otherwise
     */
    boolean isMinify ()


    /**
     * Retrieves interval in seconds between ajax requests pulling update for user messages
     * @return interval in seconds between ajax requests pulling update for user messages
     */
    @Ensures({ result > 0 })
    int    getAjaxRequestInterval()


    /**
     * Retrieves messages limit per user - how many last messages are kept for each sender.
     * @return number of last messages kept for each sender
     */
    @Ensures({ result > 0 })
    int    getMessagesLimitPerUser()


    /**
     * Retrieves date formatting pattern to display it in message dialog.
     * @return date formatting pattern to display it in message dialog
     */
    @Ensures({ result })
    String getDateFormatPattern()


    /**
     * Retrieves time formatting pattern to display it in message dialog.
     * @return time formatting pattern to display it in message dialog
     */
    @Ensures({ result })
    String getTimeFormatPattern()


    /**
     * Retrieves date formatter to display it in message dialog.
     * @return date formatter to display it in message dialog
     */
    @Ensures({ result })
    DateFormat getDateFormatter()


    /**
     * Retrieves time formatter to display it in message dialog.
     * @return time formatter to display it in message dialog
     */
    @Ensures({ result })
    DateFormat getTimeFormatter()
}
