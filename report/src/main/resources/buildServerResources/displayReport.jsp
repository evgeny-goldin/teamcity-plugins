<%@ include file="/include.jsp" %>

<%-- com.goldin.plugins.teamcity.report.ReportController#doHandle() --%>
<jsp:useBean id="tables" scope="request" type="java.util.List"/>


<table border="0">
    <%-- Every "table" is 3-elements list: description, description link, values map --%>
    <c:forEach items="${tables}" var="table">
        <tr>
            <td colspan="2" style="text-align: center;"><h1><a href="${ table[1] }"><c:out value="${ table[0] }"/></a></h1></td>
        </tr>
        <c:forEach items="${table[2].keySet()}" var="key">
        <tr>
            <td><code><c:out value="[${key}]"/></code></td>
            <td><code><c:out value="[${table[2].get( key )}]"/></code></td>
        </tr>
        </c:forEach>
    </c:forEach>
</table>
