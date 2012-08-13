<%@ include file="before.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- ContextExtension#fillModel() --%>
<jsp:useBean id="context" scope="request" type="java.util.List"/>

<c:forEach items="${context}" var="table">

    <c:set var="title"       value="${ table[ 0 ] }"/>
    <c:set var="leftHeader"  value="${ table[ 1 ] }"/>
    <c:set var="rightHeader" value="${ table[ 2 ] }"/>
    <c:set var="dataTable"   value="${ table[ 3 ] }"/>

    <tr>
        <td colspan="2" class="title"><h2 class="title">${ title }</h2></td> <%-- No "<c:out>" is used, may contain markup --%>
    </tr>
    <tr>
        <th><c:out value="${ leftHeader  }"/></th>
        <th><c:out value="${ rightHeader }"/></th>
    </tr>
    <c:forEach items="${ dataTable.keySet() }" var="key">
    <tr>
        <td><code>${ key }</code></td> <%-- No "<c:out>" is used, may contain markup --%>
        <td><code><c:out value="${ dataTable.get( key ) }"/></code></td>
    </tr>
    </c:forEach>
</c:forEach>

<%@ include file="after.jsp" %>