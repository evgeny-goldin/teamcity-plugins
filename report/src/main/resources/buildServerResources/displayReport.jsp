<%@ page import="jetbrains.buildServer.web.util.SessionUser" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/include.jsp" %>

<%-- com.goldin.plugins.teamcity.report.ReportController#doHandle() --%>
<jsp:useBean id="tables" scope="request" type="java.util.List"/>

<html>
    <head>
        <title>TeamCity Report</title>
    </head>
    <body>
        <table border="1" width="100%">
            <%-- Every "table" is a 4-elements list: table title, left column header, right column header, values map --%>
            <c:forEach items="${tables}" var="table">
                <tr>
                    <td colspan="2" style="text-align: center; vertical-align: middle;"><h2>${ table[ 0 ] }</h2></td>
                </tr>
                <tr>
                    <th>${ table[ 1 ] }</th>
                    <th>${ table[ 2 ] }</th>
                </tr>
                <c:forEach items="${ table[ 3 ].keySet() }" var="key">
                <tr>
                    <td><code>${ key }</code></td>
                    <td><code>${ table[ 3 ].get( key ) }</code></td>
                </tr>
                </c:forEach>
            </c:forEach>
        </table>
    </body>
</html>

