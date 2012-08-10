<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/include.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- com.goldin.plugins.teamcity.report.ReportExtension#fillModel() --%>
<jsp:useBean id="report"    scope="request" type="java.util.List"/>
<jsp:useBean id="action"    scope="request" type="java.lang.String"/>
<jsp:useBean id="delimiter" scope="request" type="java.lang.String"/>

<c:url var="ajaxAction" value="${ action }"/>

<style type="text/css">
    table#reportTable    { border       : 1px dotted }
    table#reportTable td,
    table#reportTable th { border-bottom: 1px dotted;
                           border-right : 1px dotted }
    .title { text-align: center }
</style>

<script type="text/javascript">
    ( function( j ){
        j( function()
        {
            j( '#evalCode'     ).focus();
            j( '#evaluateLink' ).click( function(){
                j.post( "${ ajaxAction }", // Goes to ReportController
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

<table id="reportTable">
    <tr>
        <td colspan="2">
            <h2 class="title"><a href="http://javadoc.jetbrains.net/teamcity/openapi/current/">Open API Javadoc</a></h2>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <form action="#" id="codeForm">
<textarea name="evalCode" id="evalCode" style="width: 100%" rows="20">
# Type your script and click "Evaluate" or press Tab + Enter. Lines starting with '#' are ignored.

# Variables available:
# - 'request' - current HTTP request,     instance of type javax.servlet.http.HttpServletRequest
# - 'context' - plugins's Spring context, instance of type org.springframework.context.ApplicationContext
# - 'server'  - server Spring bean,       instance of type jetbrains.buildServer.serverSide.SBuildServer

# Helper methods available:
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
    <c:forEach items="${report}" var="table">

        <%-- Every "table" is a 4-elements list: table title, left column header, right column header, data table --%>
        <c:set var="title"       value="${ table[ 0 ] }"/>
        <c:set var="leftHeader"  value="${ table[ 1 ] }"/>
        <c:set var="rightHeader" value="${ table[ 2 ] }"/>
        <c:set var="dataTable"   value="${ table[ 3 ] }"/>

        <tr>
            <td colspan="2" class="title"><h2 class="title">${ title }</h2></td>
        </tr>
        <tr>
            <th>${ leftHeader  }</th>
            <th>${ rightHeader }</th>
        </tr>
        <c:forEach items="${ dataTable.keySet() }" var="key">
        <tr>
            <td><code>${ key }</code></td>
            <td><code>${ dataTable.get( key ) }</code></td>
        </tr>
        </c:forEach>
    </c:forEach>
</table>
