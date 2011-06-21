package com.goldin.plugins.teamcity.messenger.controller

import org.gcontracts.annotations.Requires
import org.springframework.web.servlet.ModelAndView


/**
 * {@link ModelAndView} extension sending a text output as a view.
 */
class TextModelAndView extends ModelAndView
{

    @Requires({ text && mimeType && charset && locale })
    TextModelAndView ( String text, String mimeType = 'text/plain', String charset = 'UTF-8', Locale locale )
    {
        super( new TextView( text, mimeType, charset, locale ))
    }
}
