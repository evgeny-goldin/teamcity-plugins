<%@ include file="/include.jsp" %>

<c:url value='${teamcityPluginResourcesPath}report.jsp' var="reportUrl"/>

<script type="text/javascript">
    ( function( j ){
        j( function(){
            j( 'ul.tabs' ).append( j( 'div#reportLink' ).children())
        });
    })( jQuery );
</script>

<div style="display: none;" id="reportLink">
    <li id="reportTab" class="last leftBorder">
        <p><a class="tabs" href="${reportUrl}">Report</a></p>
    </li>
</div>


<%--<jsp:useBean id="serverTable" scope="request" type="java.util.Map"/>--%>
<%----%>
<%--<h1>Server</h1>--%>

${serverTable}
