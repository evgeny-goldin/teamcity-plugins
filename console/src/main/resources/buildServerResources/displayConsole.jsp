<%@ include file="before.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- ConsoleExtension#fillModel() --%>
<jsp:useBean id="action" scope="request" type="java.lang.String"/>

<c:url var="evalAction"       value="${ action }"/>
<c:url var="hotkeysSrc"       value="${ teamcityPluginResourcesPath }js/jquery.hotkeys.js"/>

<c:set var="evalCodeId"       value="${ idPrefix }_evalCode"/>
<c:set var="evalCodeFunction" value="${ idPrefix }_evalCodeFunction"/>
<c:set var="evalLinkId"       value="${ idPrefix }_evalLink"/>
<c:set var="evalResultId"     value="${ idPrefix }_evalResult"/>
<c:set var="timeId"           value="${ idPrefix }_time"/>

<script type="text/javascript" src="${ hotkeysSrc }"></script>
<script type="text/javascript">
    ( function( j ){
        //noinspection NestedFunctionJS
        function ${ evalCodeFunction }()
        {
            var time = new Date().getTime()
            j.post( "${ evalAction }", // Goes to CodeEvalController
                    { code: j( '#${ evalCodeId }' ).val() },
                    function( response ) {
                        j( '#${ evalResultId }' ).val( response );
                        j( '#${ evalCodeId }'   ).focus();
                        j( '#${ timeId }'       ).html( '<code>[' + ( new Date().getTime() - time ) + '] ms</code>' )
                    },
                    'text' );
        }

        j( function()
        {
            j( '#${ evalCodeId }' ).focus().bind ( 'keydown', 'alt+r', ${ evalCodeFunction } ); // https://github.com/jeresig/jquery.hotkeys
            j( '#${ evalLinkId }' ).click( ${ evalCodeFunction } )
        });
    })( jQuery );
</script>

<tr>
    <td colspan="2">
        <form action="#">
<textarea name="${ evalCodeId }" id="${ evalCodeId }" style="width: 100%" rows="15">
# Type your script and click "Evaluate" or press "Alt+R" or "Tab"+"Enter". Lines starting with '#' are ignored.
# Use o.properties and o.dump() to see internal details of any object - see examples below.

# Variables available in script context:
# - "request" - current HTTP request,    instance of type javax.servlet.http.HttpServletRequest
# - "context" - plugin's Spring context, instance of type org.springframework.context.ApplicationContext
# - "server"  - server Spring bean,      instance of type jetbrains.buildServer.serverSide.SBuildServer

# Helper methods available in script context:
# - c( 'className' )         - ClassLoader.loadClass( 'className' ) wrapper, allows to omit 'jetbrains.buildServer.' from the class name or use 'j.b.' instead
# - b( 'name' / c( 'type' )) - attempts to retrieve Spring bean or beans specified in all contexts

# Examples:
# request.request.dump()
# new String( request.request.postData )
# request.headerNames.collect{ [ it, request.getHeader( it )] }
# server
# server.projectManager.activeProjects
# server.history.getEntries( true ).join( '\n' )
# assert server == context.getBean( 'buildServer' )
# c( 'web.util.SessionUser' ).getUser( request ).associatedUser.descriptiveName
# b( c( 'serverSide.SBuildServer' )).properties
# b( 'buildServer'                 ).dump()
# b( c( 'web.openapi.SimplePageExtension' )).values()

</textarea>
            <br/>
<h2 class="title"><a href="#" id="${ evalLinkId }" class="title">Evaluate</a></h2>
<textarea name="${ evalResultId }" id="${ evalResultId }" style="width: 100%;" rows="15"></textarea>
<span style="float: right; margin-right: 10px;" id="${ timeId }"></span>
        </form>
    </td>
</tr>

<%@ include file="after.jsp" %>