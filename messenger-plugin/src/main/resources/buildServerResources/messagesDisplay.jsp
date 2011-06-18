<%@ include file="/include.jsp" %>

<%-- MessagesDisplayExtension.fillModel() --%>
<jsp:useBean id="intervalMs" scope="request" type="java.lang.Integer"/>
<jsp:useBean id="action"     scope="request" type="java.lang.String"/>

<style type="text/css">
    .ui-dialog .ui-dialog-content { padding: 0 } /* Disabling widget-enforced text padding in dialog */
</style>
<script type="text/javascript">
    var j               = jQuery
    var messagesDisplay = {

        /**
         * Template to be used for message title
         * http://api.prototypejs.org/language/Template/
         */
        titleTemplate : new Template( 'Message "#{id}", sent by #{sender} on #{date} at #{time}' ),

        /**
         * Makes an Ajax request, retrieves messages for the current user and shows them in a dialog
         */
        getMessages   : function() {
            j.get( '${action}',
                   { timestamp: new Date().getTime() },
                   function ( messages )
                   {   /* JSON array of messages, as sent by MessagesDisplayController */
                       j.each( messages, function( index, m ) {
                           j( '#messages-display-dialog-text' ).text( m.text );
                           j( '#messages-display-dialog'      ).dialog({ height   : 80,
                                                                         width    : 490,
                                                                         position : 'top',
                                                                         title    : messagesDisplay.titleTemplate.evaluate( m ),
                                                                         close    : messagesDisplay.dialogClose });
                       });
                   },
                   'json'
            );
        },

        /**
         * Closes the message
         */
        dialogClose : function()
        {
            j( '#messages-display-dialog' ).dialog( 'destroy' );
        }
    };

    j( function() {

       /**
        * Message "Close" button listener
        */
        j( '#messages-display-dialog-close' ).click( function(){ messagesDisplay.dialogClose(); return false; });

       /**
        * Setting interval to fire up a periodic "Get Messages" request
        */
        window.setInterval( messagesDisplay.getMessages, ${intervalMs} );
        messagesDisplay.getMessages();
    })
</script>

<div id="messages-display-dialog" style="display:none; overflow:hidden;">
	<p>
		<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
        <span id="messages-display-dialog-text"></span>
        <br/>
        <a id="messages-display-dialog-close"  href="#" class="text-link" style="float: right; margin-right: 5px">Close</a>
        <a id="messages-display-dialog-delete" href="#" class="text-link" style="float: right; margin-right: 5px">Delete</a>
	</p>
</div>
