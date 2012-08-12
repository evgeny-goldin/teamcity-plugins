package com.goldin.plugins.teamcity.console

import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.SimpleCustomTab
import jetbrains.buildServer.web.util.SessionUser

import javax.servlet.http.HttpServletRequest


/**
 * Adds "Console" tab to Administration => Diagnostics.
 */
final class ConsoleExtension extends SimpleCustomTab
{
    ConsoleExtension ( PagePlaces         pagePlaces,
                       PluginDescriptor   descriptor )
    {
        super( pagePlaces, PlaceId.ADMIN_SERVER_DIAGNOSTIC_TAB, descriptor.getParameterValue( 'name' ),
               'displayConsole.jsp', 'Console' )
        register()
    }


    @Override
    boolean isAvailable ( HttpServletRequest request ){ SessionUser.getUser( request )?.systemAdministratorRoleGranted }


    @Override
    void fillModel ( Map<String , Object> model, HttpServletRequest request )
    {
        model << [ action   : CodeEvalController.MAPPING,
                   idPrefix : ConsoleExtension.name.replace( '.', '_' ) ]
    }
}
