<%@ include file="/include.jsp" %>

<%-- com.goldin.plugins.teamcity.report.ReportController#doHandle() --%>
<jsp:useBean id="tables" scope="request" type="java.util.List"/>


<table border="1">
    <%-- Every "table" is 2-elements list: link, values map --%>
    <c:forEach items="${tables}" var="table">
        <tr>
            <td colspan="2" style="text-align: center"><h1>${ table[0] }</h1></td>
        </tr>
        <c:forEach items="${table[1].keySet()}" var="key">
        <tr>
            <td><code>${key}</code></td>
            <td><code>${table[1].get( key )}</code></td>
        </tr>
        </c:forEach>
    </c:forEach>
</table>
