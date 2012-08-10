package com.goldin.plugins.teamcity.report
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.WebControllerManager
import jetbrains.buildServer.web.util.SessionUser
import org.springframework.context.ApplicationContext
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Reads code evaluation ajax request and returns the evaluation result.
 */
class ReportController extends BaseController
{
    static final String MAPPING = '/displayReport.html'

    private final ApplicationContext context


    ReportController ( SBuildServer         server,
                       WebControllerManager manager,
                       ApplicationContext   context )
    {
        super( server )

        manager.registerController( MAPPING, this )
        this.context = context
    }


    @Override
    protected ModelAndView doHandle ( HttpServletRequest  request,
                                      HttpServletResponse response )
    {
        if ( ! SessionUser.getUser( request )?.systemAdministratorRoleGranted )
        {
            return null
        }

        String code = request.getParameter( 'code' )?.trim()
        if   ( code )
        {
            final   linesOfCode    = []
            boolean delimiterFound = false

            for ( line in code.readLines()*.trim().grep().findAll{ ! it.startsWith( '#' ) })
            {
                delimiterFound = ( delimiterFound || ( line == ReportExtension.DELIMITER ))
                if ( delimiterFound ) { break }
                linesOfCode << line
            }

            if ( linesOfCode )
            {
                final responseBytes    = ( getValue( request, linesOfCode.join( '\n' ))?.toString() ?: 'null' ).getBytes( 'UTF-8' )
                response.contentLength = responseBytes.size()
                response.contentType   = 'Content-Type: text/plain; charset=utf-8'
                response.outputStream.write( responseBytes )
                response.flushBuffer()
            }
        }

        null
    }


    /**
     * Evaluates expression specified and returns the result.
     *
     * @param request    current HTTP request to pass to binding
     * @param expression expression to evaluate
     * @return           evaluation result or stack trace as a String
     */
    @SuppressWarnings([ 'CatchThrowable' ])
    private Object getValue( HttpServletRequest request, String expression )
    {
        assert request && expression

        try
        {
            final c = {
                String className ->
                try
                {
                    Class.forName( className )
                }
                catch ( ClassNotFoundException ignored )
                {
                    Class.forName( 'jetbrains.buildServer.' + className )
                }
            }

            new GroovyShell( new Binding([ request : request,
                                           context : context,
                                           server  : myServer,
                                           c       : c ])).
            evaluate( expression )
        }
        catch ( Throwable t )
        {
            final writer = new StringWriter()
            t.printStackTrace( new PrintWriter( writer ))
            writer.toString()
        }
    }
}
