<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/include.jsp" %>

<%-- MessagesDisplayExtension.fillModel() --%>
<jsp:useBean id="action"              scope="request" type="java.lang.String"/>
<jsp:useBean id="ajaxRequestInterval" scope="request" type="java.lang.Integer"/>

<style type="text/css">
    .ui-dialog .ui-dialog-content { padding: 0 } /* Disabling widget-enforced text padding in dialog */
</style>

<script type="text/javascript">
    var j = jQuery.noConflict();

    j.assert( '${action}', 'action is [${action}]' );
    md.action = '${action}';

    j.assert(( ${ajaxRequestInterval} > 0 ), 'ajaxRequestInterval is [${ajaxRequestInterval}]' );
    new PeriodicalExecuter( md.getMessages, ${ajaxRequestInterval} );
    md.getMessages();
</script>

<div id="messages-display-dialog"  style="display:none; overflow:hidden;">
    <div id="messages-display-dialog-text" style="margin:5px; margin-bottom: -5px"></div>
    <div  id="messages-display-dialog-buttons" style="position: absolute; bottom: 0; width: 100%; margin-left: 5px; margin-top: 5px">
        <hr style="border: 0.1em ridge; margin-bottom: 1px"/>
        <div style="text-align: center;">
            <a id="messages-display-dialog-prev"  href="#" class="text-link" style="float: left; margin-left: 0;">[Prev]</a>
            <a id="messages-display-dialog-next"  href="#" class="text-link" style="float: left; margin-left: 5px;">[Next]</a>
            Message <span id="messages-display-counter"></span> of <span id="messages-display-counter-total"></span>
            <a id="messages-display-dialog-close"  href="#" class="text-link" style="float: right; margin-right: 10px">[Close]</a>
            <a id="messages-display-dialog-delete" href="#" class="text-link" style="float: right; margin-right: 5px">[Delete]</a>
            <img id="messages-display-progress" src="<c:url value='${teamcityPluginResourcesPath}images/ajax-loader.gif'/>" style="float: right; margin-right: 5px; display: none"/>
        </div>
    </div>
</div>
