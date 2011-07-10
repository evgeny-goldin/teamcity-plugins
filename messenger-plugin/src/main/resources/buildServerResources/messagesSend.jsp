<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/include.jsp" %>

<%-- MessagesSendExtension.fillModel() --%>
<jsp:useBean id="groups"  scope="request" type="java.util.Collection"/>
<jsp:useBean id="users"   scope="request" type="java.util.Collection"/>
<jsp:useBean id="action"  scope="request" type="java.lang.String"/>


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
                <select name="urgency" id="messages-send-urgency">
                    <option selected="selected">Info</option>
                    <option>Warning</option>
                    <option>Critical</option>
                </select>
            </p>

            <p>
                <label for="messages-send-longevity-number">Valid For: </label>
                <input class="textfield" id="messages-send-longevity-number" name="longevity-number" type="text" size="3"
                       maxlength="5" value="7">
                <select id="messages-send-longevity-unit" name="longevity-unit">
                    <option>hours</option>
                    <option selected="selected">days</option>
                    <option>weeks</option>
                    <option>months</option>
                </select>
                <span class="error" id="messages-send-error-longevity" style="margin-left: 10.5em;"></span>
            </p>

            <p><label for="messages-send-message">Message: <span class="mandatoryAsterix" title="Mandatory field">*</span></label>
                <textarea class="textfield" id="messages-send-message" name="message" cols="30" rows="12"></textarea>
                <span class="error" id="messages-send-error-message" style="margin-left: 10.5em;"></span>
            </p>

            <p><label for="messages-send-all">Send to All:</label>
                <input style="margin:0" type="checkbox" id="messages-send-all" name="all">
            </p>

            <p>
                <label for="messages-send-groups">Send to Groups:</label>
                <select id="messages-send-groups" name="groups" multiple="multiple" size="2">
                <c:forEach items="${groups}" var="group">
                    <option>${group}</option>
                </c:forEach>
                </select>
            </p>

            <p>
                <label for="messages-send-users">Send to Users:</label>
                <select id="messages-send-users" name="users" multiple="multiple" size="2">
                <c:forEach items="${users}" var="user">
                    <option>${user}</option>
                </c:forEach>
                </select>
                <span class="error" id="messages-send-error-selection" style="margin-left: 10.5em;"></span>
            </p>

            <p>
                <input type="submit" value="Send" id="messages-send-button">
                <img id="messages-send-progress" src="${teamcityPluginResourcesPath}images/ajax-loader.gif" style="display: none"/>
            </p>
        </form>
    </div>
</div>
