<%@ include file="before.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- ConsoleExtension#fillModel() --%>
<jsp:useBean id ="action" scope="request" type="java.lang.String"/>

<c:url var="ajaxAction"   value="${ action }"/>
<c:set var="evalCodeId"   value="${ idPrefix }_evalCode"/>
<c:set var="evalLinkId"   value="${ idPrefix }_evalLink"/>
<c:set var="evalResultId" value="${ idPrefix }_evalResult"/>

<script type="text/javascript">
    ( function( j ){
        j( function()
        {
            j( '#${ evalCodeId }' ).focus();
            j( '#${ evalLinkId }' ).click( function(){
                j.post( "${ ajaxAction }", // Goes to CodeEvalController
                        { code: j( '#${ evalCodeId }' ).val() },
                        function( response ) {
                            j( '#${ evalResultId }' ).val( response );
                            j( '#${ evalCodeId }'   ).focus();
                        },
                        'text' );
            })
        });
    })( jQuery );
</script>

<tr>
    <td colspan="2">
        <form action="#">
<textarea name="${ evalCodeId }" id="${ evalCodeId }" style="width: 100%" rows="15">
# Type your script and click "Evaluate" or press "Tab" + "Enter". Lines starting with '#' are ignored.
# Use o.properties and o.dump() to see internal details of any object.

# Variables available in script context:
# - "request" - current HTTP request,     instance of type javax.servlet.http.HttpServletRequest
# - "context" - plugins's Spring context, instance of type org.springframework.context.ApplicationContext
# - "server"  - server Spring bean,       instance of type jetbrains.buildServer.serverSide.SBuildServer

# Helper methods available in script context:
# - c( 'className' )         - ClassLoader.loadClass( 'className' ) wrapper, allows to omit 'jetbrains.buildServer.' in class name or use 'j.b.' instead.
# - b( 'name' / c( 'type' )) - attempts to retrieve Spring bean or beans specified in all contexts.

# Examples:
# request.request.dump()
# new String( request.request.postData )
# request.headerNames.collect{ [ it, request.getHeader( it )] }
# c( 'web.util.SessionUser' ).getUser( request ).allUserGroups
# b( c( 'serverSide.SBuildServer' )).properties
# b( 'buildServer'                 ).dump()
# b( c( 'web.openapi.SimplePageExtension' )).entrySet()*.key
# assert b( 'buildServer' ) == server
# server

</textarea>
            <br/>
<h2 class="title"><a href="#" id="${ evalLinkId }" class="title">Evaluate</a></h2>
<textarea name="${ evalResultId }" id="${ evalResultId }" style="width: 100%;" rows="15"></textarea>
        </form>
    </td>
</tr>

<%@ include file="after.jsp" %>