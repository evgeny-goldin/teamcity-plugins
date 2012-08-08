<%@ include file="/include.jsp" %>

<jsp:useBean id="serverTable" scope="request" type="java.util.Map"/>


<h1>Server</h1>

${serverTable}

<table>
    <c:forEach items="${serverTable.keySet()}" var="key">
        <tr>
            <td>${key}</td>
            <td>${serverTable.get(key)}</td>
        </tr>
    </c:forEach>
</table>