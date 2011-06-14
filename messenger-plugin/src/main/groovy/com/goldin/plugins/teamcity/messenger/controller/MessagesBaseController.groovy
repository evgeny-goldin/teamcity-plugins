package com.goldin.plugins.teamcity.messenger.controller

import javax.servlet.http.HttpServletRequest
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.SBuildServer
import org.gcontracts.annotations.Requires

/**
 * Base class for all controllers
 */
abstract class MessagesBaseController extends BaseController
{
    @Requires({ server })
    MessagesBaseController ( SBuildServer server )
    {
        super( server )
    }


//    @Requires({ request && name })
//    @Ensures({ result })
    String param( HttpServletRequest request, String name, boolean failIfMissing = true )
    {
        String parameter = request.getParameter( name ) ?: ''
        assert ( ! failIfMissing ) || parameter, \
               "Requests contains no parameter [$name]. It contains paramaters ${ request.parameterMap.keySet() }"
        parameter.trim()
    }


//    @Requires({ request && name })
//    @Ensures({ result })
    List<String> params( HttpServletRequest request, String name, boolean failIfMissing = true )
    {
        Set<String> parameters = ( request.getParameterValues( name ) ?: [] ) as List
        assert ( ! failIfMissing ) || parameters, \
               "Requests contains no parameters [$name]. It contains paramaters ${ request.parameterMap.keySet() }"
        parameters*.trim()
    }
}
