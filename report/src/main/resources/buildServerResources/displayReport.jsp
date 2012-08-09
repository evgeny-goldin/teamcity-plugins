<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/include.jsp" %>

<%-- com.goldin.plugins.teamcity.report.ReportExtension#fillModel() --%>
<jsp:useBean id="tables"     scope="request" type="java.util.List"/>
<jsp:useBean id="action"     scope="request" type="java.lang.String"/>
<jsp:useBean id="evalCode"   scope="request" type="java.lang.String"/>
<jsp:useBean id="evalResult" scope="request" type="java.lang.String"/>


<style type="text/css">
    table#reportTable    { border       : 1px dotted }
    table#reportTable td,
    table#reportTable th { border-bottom: 1px dotted;
                           border-right : 1px dotted }
</style>

<script type="text/javascript">
    ( function( j ){
        j( function()
        {
            j( '#sendLink' ).click( function(){ j( '#scriptForm' ).submit() })
        });
    })( jQuery );
</script>

<c:url var="formAction" value="${ action }"/>

<form action="${ formAction }" method="post" id="scriptForm">

<textarea name="evalCode" id="evalCode" cols="80" rows="15">${ evalCode }</textarea>
<a href="#" id="sendLink" style="padding-bottom: 40px;">Evaluate</a>
<textarea name="evalResult" id="evalResult" cols="80" rows="15">${ evalResult }</textarea>

</form>

<table id="reportTable">
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
