<%@ include file="/include.jsp" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="buildQueuePauser" type="jetbrains.sample.extensions.buildQueuePause.StartBuildPrecondition" scope="request"/>
<c:url var="actionUrl" value="/queuePauser.html"/>
<c:choose
    ><c:when test="${buildQueuePauser.queuePaused}">
  <div class="successMessage">
    Build Queue was paused <bs:_commentUserInfo user="${buildQueuePauser.user}"/>
    <%@include file="buildQueuePauseButton.jsp" %>
  </div>
</c:when
    ><c:otherwise>
  <%@include file="buildQueuePauseButton.jsp" %>
</c:otherwise
    ></c:choose>