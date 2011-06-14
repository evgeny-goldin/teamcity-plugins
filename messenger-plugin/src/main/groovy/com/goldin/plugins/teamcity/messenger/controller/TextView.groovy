package com.goldin.plugins.teamcity.messenger.controller

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.springframework.web.servlet.View

/**
 * Textual {@link View} implementation.
 */
class TextView implements View
{
    private final String text


    @Requires({ text })
    TextView ( String text )
    {
        this.text = text
    }

    
    @Override
    @Ensures({ result })
    String getContentType () { 'text/plain; charset=UTF-8' }

    
    @Override
    @Requires({ response })
    void render ( Map model, HttpServletRequest request, HttpServletResponse response )
    {
        response.writer.print( text )
    }
}
