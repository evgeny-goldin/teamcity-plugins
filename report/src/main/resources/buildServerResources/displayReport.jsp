<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/include.jsp" %>

<%-- com.goldin.plugins.teamcity.report.ReportExtension#fillModel() --%>
<jsp:useBean id="tables" scope="request" type="java.util.List"/>
<jsp:useBean id="action" scope="request" type="java.lang.String"/>


<script type="text/javascript">
    ( function( j ){
        j( function()
        {
            j( '#sendLink' ).click( function(){ alert( 'aaaa' ); j( '#scriptForm' ).submit() })
        });
    })( jQuery );
</script>

<form action="${action}" method="post" id="scriptForm">
<textarea name="script" id="script" cols="80" rows="20">
# Type your script and press Enter
# Variables available in the script context:
# * "request" - instance of javax.servlet.http.HttpServletRequest
# * "context" - instance of org.springframework.context.ApplicationContext
# * "server"  - instance of jetbrains.buildServer.serverSide.SBuildServer
# To retrieve currently logged in user:
# * jetbrains.buildServer.web.util.SessionUser.getUser( request )


</textarea>
    <a href="#" id="sendLink">Send</a>
</form>

<table style="border: 0.5px dotted;">
    <c:forEach items="${tables}" var="table">

        <%-- Every "table" is a 4-elements list: table title, left column header, right column header, data table --%>
        <c:set var="title"       value="${ table[ 0 ] }"/>
        <c:set var="leftHeader"  value="${ table[ 1 ] }"/>
        <c:set var="rightHeader" value="${ table[ 2 ] }"/>
        <c:set var="dataTable"   value="${ table[ 3 ] }"/>

        <tr>
            <td colspan="2" style="text-align: center; vertical-align: middle;"><h2>${ title }</h2></td>
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
