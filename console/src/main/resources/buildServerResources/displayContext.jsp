<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/include.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- ContextExtension#fillModel() --%>
<jsp:useBean id="context" scope="request" type="java.util.List"/>


<style type="text/css">
    table#contextTable    { border       : 1px dotted }
    table#contextTable td,
    table#contextTable th { border-bottom: 1px dotted;
                            border-right : 1px dotted }
    .title                { text-align   : center     }
</style>

<br/>
<p/>

<table id="contextTable">
    <tr>
        <td colspan="2">
            <h2 class="title"><a href="http://javadoc.jetbrains.net/teamcity/openapi/current/">Open API Javadoc</a></h2>
        </td>
    </tr>
    <c:forEach items="${context}" var="table">

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
