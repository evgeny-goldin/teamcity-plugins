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
    private final String mimeType
    private final String charset
    private final Locale locale


    @Requires({ text && mimeType && charset && locale })
    TextView ( String text, String mimeType = 'text/plain', String charset = 'UTF-8', Locale locale )
    {
        this.text     = text
        this.mimeType = mimeType
        this.charset  = charset
        this.locale   = locale
    }

    
    @Override
    @Ensures({ result })
    String getContentType () { "$mimeType; charset=$charset" }

    
    @Override
    @Requires({ response })
    void render ( Map model, HttpServletRequest request, HttpServletResponse response )
    {
        def bytes              = text.getBytes( charset )
        response.contentType   = contentType
        response.contentLength = bytes.size()
        response.locale        = locale
        
        response.outputStream.write( bytes )
        response.outputStream.flush()
    }
}
