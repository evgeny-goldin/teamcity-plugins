package com.goldin.plugins.teamcity.report

import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.SimplePageExtension

import javax.servlet.http.HttpServletRequest


class ReportExtension extends SimplePageExtension
{
    ReportExtension ( PagePlaces         pagePlaces,
                      PluginDescriptor   descriptor )
    {
        super( pagePlaces, PlaceId.ALL_PAGES_HEADER, descriptor.getParameterValue( 'name' ), 'addReportLink.jsp' )
        register()
    }


    @Override
    void fillModel ( Map<String , Object> model, HttpServletRequest request )
    {
        model.action = ReportController.MAPPING
    }
}
