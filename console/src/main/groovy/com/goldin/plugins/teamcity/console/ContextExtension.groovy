package com.goldin.plugins.teamcity.console

import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.SimpleCustomTab
import jetbrains.buildServer.web.util.SessionUser

import javax.servlet.http.HttpServletRequest


/**
 * Adds "Context" tab to Administration => Diagnostics.
 */
class ContextExtension extends SimpleCustomTab
{
    private final ContextReportHelper helper


    ContextExtension ( PagePlaces          pagePlaces,
                       PluginDescriptor    descriptor,
                       ContextReportHelper helper )
    {
        super( pagePlaces, PlaceId.ADMIN_SERVER_DIAGNOSTIC_TAB, descriptor.getParameterValue( 'name' ),
               'displayContext.jsp', 'Context' )

        this.helper = helper
        register()
    }


    @Override
    boolean isAvailable ( HttpServletRequest request ){ SessionUser.getUser( request )?.systemAdministratorRoleGranted }


    @Override
    void fillModel ( Map<String , Object> model, HttpServletRequest request )
    {
        model << [ context : helper.contextReport ]
    }
}
