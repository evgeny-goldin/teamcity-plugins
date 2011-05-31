<%@ include file="/include.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
  p.sampleStyle {
    background: url( "<c:url value='/plugins/samplePlugin/sample.gif'/>" ) no-repeat 0 2px;
  }

  #sidebar .blockAgents p.sampleStyle {
    padding-left:1.9em;
    margin-left:5px;
    margin-right:5px;
  }
</style>
<c:url var="actionUrl" value="/viewPermissions.html"/>
<div class="divider"></div>
<p class="sampleStyle">Sample Plugin</p>
<form action="${actionUrl}">
    <input class="submitButton" style="margin:0;padding:0;font-size:80%" id="search" type="submit" value="View My Permissions"/></form>
<br clear="all">
