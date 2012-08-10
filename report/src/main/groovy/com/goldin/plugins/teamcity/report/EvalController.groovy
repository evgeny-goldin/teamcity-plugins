package com.goldin.plugins.teamcity.report
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.WebControllerManager
import jetbrains.buildServer.web.util.SessionUser
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import org.springframework.context.ApplicationContext
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 * Reads code evaluation ajax request and returns the evaluation result.
 */
class EvalController extends BaseController
{
    static final String MAPPING = '/evalCode.html'

    private final ApplicationContext context


    /**
     * Classes that are not allowed to be used when evaluating the code.
     */
    private final Set<Class>            forbiddenClasses      = [ System, Runtime, Class, ClassLoader, URLClassLoader ] as Set
    private final CompilerConfiguration compilerConfiguration = compilerConfiguration( forbiddenClasses*.name )


    EvalController ( SBuildServer         server,
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
        String code = request.getParameter( 'code' )?.trim()

        if ( code && SessionUser.getUser( request )?.systemAdministratorRoleGranted )
        {
            code = code.readLines()*.trim().grep().findAll{ ! it.startsWith( '#' ) }.join( '\n' )

            if ( code )
            {
                final responseBytes    = ( getValue( request, code )?.toString() ?: 'null' ).getBytes( 'UTF-8' )
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
            new GroovyShell( new Binding([ request : request,
                                           context : context,
                                           server  : myServer,
                                           c       : this.&loadClass ]), compilerConfiguration ).
            evaluate( expression )
        }
        catch ( Throwable t )
        {
            final writer = new StringWriter()
            t.printStackTrace( new PrintWriter( writer ))
            writer.toString()
        }
    }


    /**
     * {@link ClassLoader#loadClass(java.lang.String)} convenience wrapper, provided as "c" in code console.
     *
     * @param className name of the class to load, can omit 'jetbrains.buildServer.' or use 'j.b.' instead
     * @return class loaded
     * @throws ClassNotFoundException if no attempts resulted in class loaded successfully
     */
    private Class loadClass ( String className )
    {
        for ( name in [ className,
                        'jetbrains.buildServer.' + className,
                        'jetbrains.buildServer.' + className.replace( 'j.b.', '' ) ])
        {
            try   { return this.class.classLoader.loadClass( name )}
            catch ( ClassNotFoundException ignored ){}
        }

        throw new ClassNotFoundException( className )
    }


    /**
     * Creates {@link CompilerConfiguration} instance secured from running any of {@link #forbiddenClasses}.
     * @param forbiddenClasses fully-qualified name of classes that are not allowed to be used
     * @return {@link CompilerConfiguration} instance secured from running any of {@link #forbiddenClasses}
     */
    private CompilerConfiguration compilerConfiguration( Collection<String> forbiddenClasses )
    {
        /**
         * Code evaluation method checker is based on
         * http://www.jroller.com/melix/entry/customizing_groovy_compilation_process
         */

        final customizer = new SecureASTCustomizer()
        customizer.addExpressionCheckers({
            Expression e ->

            try   { ! e.objectExpression.type.name in forbiddenClasses }
            catch ( ignored ){ true }

        } as SecureASTCustomizer.ExpressionChecker )

        final config = new CompilerConfiguration()
        config.addCompilationCustomizers( customizer )

        config
    }
}
