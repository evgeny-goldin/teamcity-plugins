<%@ include file="/include.jsp" %>

<%-- MessagesSendExtension.fillModel() --%>
<jsp:useBean id="groups"  scope="request" type="java.util.Collection"/>
<jsp:useBean id="users"   scope="request" type="java.util.Collection"/>
<jsp:useBean id="action"  scope="request" type="java.lang.String"/>


<style type="text/css">
    .ui-dialog-titlebar{ display: none } /* Hiding dialog title */
</style>
<script type="text/javascript">

    var j  = jQuery;
    var ms = { /* Shortcut for "messagesSend" */

       /**
        * Opens the dialog with message specified
        */
        dialog : function( message, closeAfter )
        {
            j( '#messages-send-dialog-text' ).text( message );
            j( '#messages-send-dialog'      ).dialog({ height : 55,
                                                       width  : 240,
                                                       close  : ms.dialogClose });

            if ( closeAfter > 0 ) { ms.dialogClose.delay( closeAfter ) }
        },
        /**
         * Closes the dialog and enables "Send" button
         */
        dialogClose : function()
        {
            j( '#messages-send-dialog'  ).dialog( 'destroy' );
            j( '#messages-send-message' ).val( '' ).focus();
            j( '#messages-send-button'  ).enable();
        }
    };

    j( function() {

        j( '#messages-send-message' ).focus();

       /**
        * "Message Sent" Ok button listener
        */
        j( '#messages-send-dialog-ok' ).click( function(){ ms.dialogClose(); return false; });

       /**
        * Listener enabling and disabling groups and users according to "Send to All" checkbox
        */
        j( '#messages-send-all' ).change( function() {
            j( '#messages-send-groups, #messages-send-users' ).disable( this.value == 'on' );
        });
        j( '#messages-send-all' ).click().change();
        
       /**
        * Listener submitting a request when form is submitted
        */
        j( '#messages-send-form' ).submit( function() {

            if ( ! j.trim( j( '#messages-send-message' ).val()))
            {
                j( '#messages-send-message'       ).addClass( 'errorField'   );
                j( '#messages-send-error-message' ).text( 'Message is empty' );
                return false;
            }
            else
            {
                j( '#messages-send-message'       ).removeClass( 'errorField' );
                j( '#messages-send-error-message' ).text( '' );
            }

            var    recipientsSelected = ( j( '#messages-send-all'    ).attr( 'checked' ) ||
                                          j( '#messages-send-groups' ).val()             ||
                                          j( '#messages-send-users'  ).val());
            if ( ! recipientsSelected )
            {
                j( '#messages-send-error-selection' ).text( 'No recipients selected' );
                return false;
            }
            else
            {
                j( '#messages-send-error-selection' ).text( '' );
            }

            j( '#messages-send-button'   ).disable();
            j( '#messages-send-progress' ).show();

            j.ajax({ url      : this.action,
                     type     : 'POST',
                     data     : j( this ).serialize(),
                     dataType : 'text',
                     success  : function( response ) { ms.dialog( 'Message "' + response + '" was sent', 1 )},
                     error    : function()           { ms.dialog( 'Failed to send the message',         -1 )},
                     complete : function()           { j( '#messages-send-progress' ).hide()}
                    });

            return false;
        });
    })
</script>


<div id="messages-send-dialog" style="display:none; overflow:hidden;">
	<p>
		<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
        <span id="messages-send-dialog-text"></span>
        <a id="messages-send-dialog-ok" href="#" class="text-link" style="float: right">[Ok]</a>
	</p>
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
                <input class="textfield1" id="messages-send-longevity-number" name="longevity-number" type="text" size="3"
                       maxlength="3" value="7">
                <select id="messages-send-longevity-unit" name="longevity-unit">
                    <option>hours</option>
                    <option selected="selected">days</option>
                    <option>weeks</option>
                    <option>months</option>
                </select>
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
