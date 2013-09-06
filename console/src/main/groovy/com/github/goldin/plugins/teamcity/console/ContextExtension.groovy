package com.github.goldin.plugins.teamcity.console

import jetbrains.buildServer.web.openapi.*
import jetbrains.buildServer.web.util.SessionUser
import org.springframework.beans.factory.annotation.Autowired
import javax.servlet.http.HttpServletRequest


/**
 * Adds "Context" tab to Administration => Diagnostics.
 */
final class ContextExtension extends SimplePageExtension implements CustomTab
{
    /**
     * Extending SimpleCustomTab (as with ConsoleExtension) will not allow using a tab title ("context")
     * that is different from plugin name ("console") - TeamCity throws
     * ServletException: File "/plugins/context/displayContext.jsp" not found
     */

    @Autowired private ContextReportHelper reportHelper
    @Autowired private PluginDescriptor    descriptor

    final String  tabId    = 'context'
    final String  tabTitle = 'Context'
    final boolean visible  = true


    ContextExtension ( PagePlaces       pagePlaces,
                       PluginDescriptor descriptor )
    {
        super( pagePlaces, PlaceId.ADMIN_SERVER_DIAGNOSTIC_TAB, descriptor.getParameterValue( 'name' ), 'displayContext.jsp' )
        register()
    }

    @Override
    boolean isAvailable ( HttpServletRequest request ){ SessionUser.getUser( request )?.systemAdministratorRoleGranted }

    @Override
    String getTabTitle ( HttpServletRequest httpServletRequest ){ tabTitle }

    @Override
    void fillModel ( Map<String , Object> model, HttpServletRequest request )
    {
        model << [ context  : reportHelper.contextReport,
                   idPrefix : this.class.name.replace( '.', '_' ) ]
    }
}
