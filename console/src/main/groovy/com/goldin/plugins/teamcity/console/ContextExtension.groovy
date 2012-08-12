package com.goldin.plugins.teamcity.console

import jetbrains.buildServer.web.openapi.CustomTab
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.SimplePageExtension
import jetbrains.buildServer.web.util.SessionUser

import javax.servlet.http.HttpServletRequest


/**
 * Adds "Context" tab to Administration => Diagnostics.
 */
final class ContextExtension extends SimplePageExtension implements CustomTab
{   /**
     * Extending SimpleCustomTab (as with ConsoleExtension) will not allow using a tab title ("consoleContext")
     * that is different from plugin name ("console") - TeamCity throws
     * ServletException: File "/plugins/consoleContext/displayContext.jsp" not found
     */

    private final ContextReportHelper reportHelper
    private final PluginDescriptor    descriptor

    // CustomTab methods implementations (properties instead of getters)
    final String  tabId    = 'context'
    final String  tabTitle = 'Context'
    final boolean visible  = true


    ContextExtension ( PagePlaces          pagePlaces,
                       PluginDescriptor    descriptor,
                       ContextReportHelper reportHelper )
    {
        super( pagePlaces, PlaceId.ADMIN_SERVER_DIAGNOSTIC_TAB, descriptor.getParameterValue( 'name' ),
               'displayContext.jsp' )

        this.reportHelper = reportHelper
        this.descriptor   = descriptor
        register()
    }


    @Override
    boolean isAvailable ( HttpServletRequest request ){ SessionUser.getUser( request )?.systemAdministratorRoleGranted }


    @Override
    void fillModel ( Map<String , Object> model, HttpServletRequest request )
    {
        model << [ context  : reportHelper.contextReport,
                   idPrefix : ContextExtension.name.replace( '.', '_' ) ]
    }
}
