<%@ include file="/include.jsp" %>
<bs:page>
    <jsp:attribute name="body_include">
        <h4>Hello ${userName}!</h4>
        Demonstrated extensions:
        <ul>
            <li>"Click me!" button on Overview page</li>
            <li>Run a build to gather extension-specific log: The log can be seen on this page and on the Build Configuration "Sample Extension" tab</li>
            <li>"Sample Build Result Tab" tab on the build results page</li>
            <li>Additional Chart on build Statistics page</li>
            <li>"View My Permissions" button on the "My Settings and Tools" page</li>
            <li>"Pause Build Queue" button on the "Build Queue" page</li>
        </ul>

        Found log messages:
        <c:forEach items="${messages}" var="message">
          <br>${message}
        </c:forEach>
    </jsp:attribute>
</bs:page>
