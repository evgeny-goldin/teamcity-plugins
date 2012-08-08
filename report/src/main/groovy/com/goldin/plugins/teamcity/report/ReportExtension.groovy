package com.goldin.plugins.teamcity.report

import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.SimplePageExtension

import javax.servlet.http.HttpServletRequest


class ReportExtension extends SimplePageExtension
{
    ReportExtension ( PagePlaces pagePlaces )
    {
        super( pagePlaces, PlaceId.ALL_PAGES_HEADER, 'reportPage', 'report.jsp' )
    }


    @Override
    boolean isAvailable ( HttpServletRequest request )
    {
        true
    }
}
