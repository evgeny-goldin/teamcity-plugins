<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/include.jsp" %>

<%-- MessagesSendExtension.fillModel() --%>
<jsp:useBean id="groups"                      scope="request" type="java.util.List<java.lang.String>"/>
<jsp:useBean id="users"                       scope="request" type="java.util.Map<java.lang.String, java.lang.String>"/>
<jsp:useBean id="action"                      scope="request" type="java.lang.String"/>
<jsp:useBean id="teamcityPluginResourcesPath" scope="request" type="java.lang.String"/>

<style type="text/css">
    .ui-dialog-titlebar { display: none } /* Hiding dialog title */
</style>

<div id="messages-send-dialog" style="display:none; overflow:hidden;">
    <div id="messages-send-dialog-text" style="float:left; margin-top:3px"></div>
    <a id="messages-send-dialog-ok" href="#" class="text-link" style="float: right; margin-top:3px">[Ok]</a>
</div>

<div class="settingsBlock" style="">
    <div style="background-color:#fff; padding: 10px;">
        <form action="${action}" method="post" id="messages-send-form">

            <p>
                <label for="messages-send-urgency">Urgency: </label>
                <select name="urgency" id="messages-send-urgency" style="width: 210px">
                    <option selected="selected">Info</option>
                    <option>Warning</option>
                    <option>Critical</option>
                </select>
            </p>

            <p>
                <label for="messages-send-longevity-number">Valid For: </label>
                <input class="textfield" id="messages-send-longevity-number" name="longevity-number" type="text" maxlength="5" style="width: 50px">
                <select id="messages-send-longevity-unit" name="longevity-unit" style="width: 151px">
                    <option>Hours</option>
                    <option selected="selected">Days</option>
                    <option>Weeks</option>
                    <option>Months</option>
                </select>
                <span class="error" id="messages-send-error-longevity" style="margin-left: 10.5em;"></span>
            </p>

            <p><label for="messages-send-text">Message: <span class="mandatoryAsterix" title="Mandatory field">*</span></label>
                <textarea class="textfield" id="messages-send-text" name="message" cols="30" rows="12"></textarea>
                <br/>
                <span id="messages-send-counter" style="margin-left: 315px"></span>
                <span class="error" id="messages-send-error-message" style="margin-left: 10.5em;"></span>
            </p>

            <p><label for="messages-send-all">Send to All:</label>
                <input style="margin:0" type="checkbox" id="messages-send-all" name="all">
            </p>

            <p>
                <label for="messages-send-groups">Send to Groups:</label>
                <select id="messages-send-groups" name="groups" multiple="multiple" size="5" style="width: 210px; overflow: auto;">
                <c:forEach items="${groups}" var="group">
                    <option>${group}</option>
                </c:forEach>
                </select>
            </p>

            <p>
                <label for="messages-send-users">Send to Users:</label>
                <select id="messages-send-users" name="users" multiple="multiple" size="5" style="width: 210px; overflow: auto;">
                <c:forEach items="${users}" var="entry">
                    <option value="${entry.key}">${entry.value}</option>
                </c:forEach>
                </select>
                <span class="error" id="messages-send-error-selection" style="margin-left: 10.5em;"></span>
            </p>

            <p>
                <input type="submit" value="Send" id="messages-send-button">
                <img id="messages-send-progress" src="<c:url value='${teamcityPluginResourcesPath}images/ajax-loader.gif'/>" style="display: none"/>
            </p>
        </form>
    </div>
</div>
