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
    private final Set<String>           forbiddenMethods      = [ 'getClassLoader', 'loadClass', 'forName' ] as Set
    private final Set<String>           forbiddenProperties   = [ 'classLoader' ] as Set
    private final Set<String>           forbiddenConstants    = forbiddenClasses*.name
    private final CompilerConfiguration compilerConfiguration = compilerConfiguration( forbiddenClasses,
                                                                                       forbiddenMethods,
                                                                                       forbiddenProperties,
                                                                                       forbiddenConstants )


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
     *
     * @param forbiddenClasses    fully-qualified name of classes that are not allowed to be used
     * @param forbiddenMethods    name of methods that are not allowed to be used
     * @param forbiddenProperties name of object properties that are not allowed to be used
     * @param forbiddenConstants  name of constants that are not allowed to be used
     * @return {@link CompilerConfiguration} instance secured from running forbidden code
     */
    private CompilerConfiguration compilerConfiguration( Set<Class>  forbiddenClasses,
                                                         Set<String> forbiddenMethods,
                                                         Set<String> forbiddenProperties,
                                                         Set<String> forbiddenConstants )
    {
        final is         = { Closure c -> try { c() } catch ( ignored ){ false }}
        final customizer = new SecureASTCustomizer()
        customizer.addExpressionCheckers({
            Expression e ->

            boolean forbiddenCall = is { e.objectExpression.type.name in forbiddenClasses*.name } ||
                                    is { e.methodAsString             in forbiddenMethods       } ||
                                    is { e.propertyAsString           in forbiddenProperties    } ||
                                    is { e.value                      in forbiddenConstants     }
            ( ! forbiddenCall )

        } as SecureASTCustomizer.ExpressionChecker )

        /**
         * Various "attacks" to check:
         * System.gc()
         * System."${ 'gc' }"()
         * System.methods.find{ it.name == 'gc' }.invoke( null )
         * Class.forName( 'java.lang.System' )."${ 'gc' }"()
         * this.class.classLoader.loadClass( 'java.lang.System' )."${ 'gc' }"()
         */

        final config = new CompilerConfiguration()
        config.addCompilationCustomizers( customizer )

        config
    }
}
