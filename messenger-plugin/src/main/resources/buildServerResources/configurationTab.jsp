<%@ include file="/include.jsp" %>
Sample configuration tab content
<c:forEach items="${messages}" var="message">
  <tr>
    <br>${message}
  </tr>
</c:forEach>
