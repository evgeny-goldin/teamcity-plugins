<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/include.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- ConsoleExtension#fillModel() --%>
<jsp:useBean id="action" scope="request" type="java.lang.String"/>

<c:url var="ajaxAction" value="${ action }"/>

<style type="text/css">
    table#consoleTable    { border       : 1px dotted }
    table#consoleTable td,
    table#consoleTable th { border-bottom: 1px dotted;
                            border-right : 1px dotted }
    .title                { text-align   : center     }
</style>

<script type="text/javascript">
    ( function( j ){
        j( function()
        {
            j( '#evalCode'     ).focus();
            j( '#evaluateLink' ).click( function(){
                j.post( "${ ajaxAction }", // Goes to CodeEvalController
                        { code: j( '#evalCode' ).val() },
                        function( response ) {
                            j( '#evalResult' ).val( response );
                            j( '#evalCode'   ).focus();
                        },
                        'text' );
            })
        });
    })( jQuery );
</script>

<br/>
<p/>

<table id="consoleTable">
    <tr>
        <td colspan="2">
            <h2 class="title"><a href="http://javadoc.jetbrains.net/teamcity/openapi/current/">Open API Javadoc</a></h2>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <form action="#" id="codeForm">
<textarea name="evalCode" id="evalCode" style="width: 100%" rows="22">
# Type your script and click "Evaluate" or press "Tab" + "Enter". Lines starting with '#' are ignored.

# Variables available in the script context:
# - "request" - current HTTP request,     instance of type javax.servlet.http.HttpServletRequest
# - "context" - plugins's Spring context, instance of type org.springframework.context.ApplicationContext
# - "server"  - server Spring bean,       instance of type jetbrains.buildServer.serverSide.SBuildServer

# Helper methods available in the script context:
# - c( 'className' )         - ClassLoader.loadClass( 'className' ) wrapper, allows to omit 'jetbrains.buildServer.' in class name or use 'j.b.' instead.
# - b( 'name' / c( 'type' )) - attempts to retrieve Spring bean or beans specified in all contexts.

# Examples:
# request.headerNames.collect{ [ it, request.getHeader( it )] }
# c( 'jetbrains.buildServer.web.util.SessionUser' ).getUser( request ).allUserGroups
# c( 'web.util.SessionUser'                       ).getUser( request ).allUserGroups
# context.getBean( c( 'serverSide.SBuildServer' )).properties
# context.getBean( 'buildServer'                 ).dump()
# b( c( 'serverSide.SBuildServer' )).properties
# b( 'buildServer'                 ).dump()
# assert b( 'buildServer' ) == server


</textarea>
            <br/>
<h2 class="title"><a href="#" id="evaluateLink" class="title">Evaluate</a></h2>
<textarea name="evalResult" id="evalResult" style="width: 100%;" rows="10"></textarea>
            </form>
        </td>
    </tr>
</table>
