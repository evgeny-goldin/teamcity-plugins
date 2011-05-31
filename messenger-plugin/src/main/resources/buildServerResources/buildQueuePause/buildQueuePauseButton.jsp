<c:choose><c:when test="${buildQueuePauser.queuePaused}"
    ><c:set var="buildQueuePauseActionText">Resume</c:set
    ></c:when
    ><c:otherwise
    ><c:set var="buildQueuePauseActionText">Pause Build Queue</c:set
    ></c:otherwise
    ></c:choose>
<authz:authorize allPermissions="DISABLE_AGENT">
  <form action="${actionUrl}" id="queuePauserForm" method="post">
    <input class="submitButton" id="buildQueuePause" type="submit" value="${buildQueuePauseActionText}"/>
    <input type="hidden" name="newBuildQueuePausedState" value="${not buildQueuePauser.queuePaused}"/>
  </form>
</authz:authorize>
