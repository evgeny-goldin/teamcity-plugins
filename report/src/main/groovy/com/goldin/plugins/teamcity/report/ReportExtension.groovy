package com.goldin.plugins.teamcity.report

import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.SimplePageExtension
import org.springframework.context.ApplicationContext

import javax.servlet.http.HttpServletRequest


class ReportExtension extends SimplePageExtension
{
    private final ApplicationContext context
    private final SBuildServer       server


    ReportExtension ( PagePlaces         pagePlaces,
                      PluginDescriptor   descriptor,
                      ApplicationContext context,
                      SBuildServer       server )
    {
        super( pagePlaces, PlaceId.ALL_PAGES_HEADER, descriptor.getParameterValue( 'name' ), 'reportLink.jsp' )
        this.context = context
        this.server  = server
        register()
    }


    @Override
    void fillModel ( Map<String , Object> model, HttpServletRequest request )
    {
        final serverTable  = [:]
        final springTable1 = [:]
        final springTable2 = [:]

        model.context      = context
        model.server       = server
        model.serverTable  = serverTable
        model.springTable1 = springTable1
        model.springTable2 = springTable2

        serverTable.TeamCityVersion = server.fullServerVersion
        serverTable.RootPath        = server.serverRootPath

        for ( beanName in context.getBeanNamesForType( Object ).sort())
        {
            //noinspection GroovyGetterCallCanBePropertyAccess
            springTable1[ beanName ] = context.getBean( beanName ).getClass().name
        }

        for ( beanName in context.parent.getBeanNamesForType( Object ).sort())
        {
            //noinspection GroovyGetterCallCanBePropertyAccess
            springTable2[ beanName ] = context.getBean( beanName ).getClass().name
        }
    }
}
