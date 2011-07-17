package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires


/**
 * Messages persistency service
 */
interface MessagesPersistency
{

   /**
    * Saves persistency data to the permanent storage.
    * @param data persistency data
    */
    @Requires({ data != null })
    void save ( Map data )


    /**
     * Restores persistency data from the permanent storage.
     * @return persistency data
     */
    @Ensures({ result != null })
    Map restore()
}
