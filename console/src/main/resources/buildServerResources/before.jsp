<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/include.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id ="idPrefix" scope="request" type="java.lang.String"/>
<c:set       var="tableId"  value="${ idPrefix }-table"/>

<style type="text/css">
    table#${ tableId }    { border       : 1px dotted }
    table#${ tableId } td,
    table#${ tableId } th { border-bottom: 1px dotted;
                            border-right : 1px dotted }
    .title                { text-align   : center     }
</style>

<table id="${ tableId }" width="100%">
    <tr>
        <td colspan="2">
            <h2 class="title"><a href="http://javadoc.jetbrains.net/teamcity/openapi/current/">Open API Javadoc</a></h2>
        </td>
    </tr>
