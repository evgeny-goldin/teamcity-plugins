<%@ include file="before.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- ConsoleExtension#fillModel() --%>
<jsp:useBean id="action" scope="request" type="java.lang.String"/>

<c:url var="evalAction"   value="${ action }"/>
<c:url var="resources"    value="${ teamcityPluginResourcesPath }"/>

<c:set var="evalCodeId"   value="${ idPrefix }_evalCode"/>
<c:set var="evalLinkId"   value="${ idPrefix }_evalLink"/>
<c:set var="evalResultId" value="${ idPrefix }_evalResult"/>
<c:set var="timeId"       value="${ idPrefix }_time"/>
<c:set var="progressId"   value="${ idPrefix }_progress"/>

<script type="text/javascript" src="${ resources }js/jquery.hotkeys.js"></script>
<script type="text/javascript">
    ( function( j ){

        function evaluate()
        {
            var time = new Date().getTime();
            j( '#${ timeId }' ).html( j( '#${ progressId }' ).html());
            j.ajax({ url      : "${ evalAction }", // Goes to CodeEvalController
                     type     : 'POST',
                     data     : { code: j( '#${ evalCodeId }' ).val() },
                     dataType : 'text',
                     complete : function(){
                         j( '#${ timeId }'     ).html( '<code>[' + ( new Date().getTime() - time ) + '] ms</code>' );
                         j( '#${ evalCodeId }' ).focus();
                     },
                     error    : function()           { j( '#${ evalResultId }' ).val( 'Failed to send request' )},
                     success  : function( response ) { j( '#${ evalResultId }' ).val( response )}
                   });
        }

        j( function()
        {
            j( '#${ evalCodeId }' ).focus().bind ( 'keydown', 'alt+r', evaluate ); // https://github.com/jeresig/jquery.hotkeys
            j( '#${ evalLinkId }' ).click( evaluate )
        });
    })( jQuery );
</script>

<tr>
    <td colspan="2">
        <form action="#">
<textarea name="${ evalCodeId }" id="${ evalCodeId }" style="width: 100%" rows="15">
# This a Groovy console where you can evaluate your code in TeamCity environment.
# Click "Evaluate" or press "Alt + R" or "Tab" + "Enter" to run the script. Lines starting with '#' are ignored.
# Use o.properties and o.dump() to see internal details of any object - see examples below.

# Variables and helper methods available in script context:
# - "request" - current HTTP request,    instance of type javax.servlet.http.HttpServletRequest
# - "context" - plugin's Spring context, instance of type org.springframework.context.ApplicationContext
# - "server"  - server Spring bean,      instance of type jetbrains.buildServer.serverSide.SBuildServer
# - c( 'className' ) - ClassLoader.loadClass( 'className' ) wrapper, allows to omit 'jetbrains.buildServer.' from the class name or use 'j.b.' instead
# - b( 'beanName' / c( 'beanClass' )) - attempts to retrieve Spring bean or beans specified in all contexts

# Examples:
# server
# [ request, context, server ]
# request.request.dump()
# new String( request.request.postData )
# request.headerNames.collect{ [ it, request.getHeader( it )] }
# server.projectManager.activeProjects
# server.history.getEntries( true ).join( '\n' )
# assert server == context.getBean( 'buildServer' )
# c( 'web.util.SessionUser' ).getUser( request ).associatedUser.descriptiveName
# b( 'buildServer' ).properties
# b( c( 'web.openapi.SimplePageExtension' )).values()

</textarea>
            <br/>
<h2 class="title"><a href="#" id="${ evalLinkId }" class="title">Evaluate</a></h2>
<textarea name="${ evalResultId }" id="${ evalResultId }" style="width: 100%;" rows="15"></textarea>
<span id="${ timeId }" style="float: right; margin-right: 10px"></span>
        </form>
    </td>
</tr>
<span id="${ progressId }" style="display: none"><img src="${ resources }img/progress.gif"/></span>

<%@ include file="after.jsp" %>