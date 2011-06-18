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
         * See http://api.prototypejs.org/language/Template/
         *     MessagesDisplayController.handleRequest()
         */
        titleTemplate : new Template( 'Message "#{id}", sent by #{sender} on #{date} at #{time}' ),

        /**
         * Displays message and title specified in a dialog widget
         */
        dialog : function( title, text, closeAfter ) {
            j( '#messages-display-dialog-text' ).text( text );
            j( '#messages-display-dialog' ).dialog( 'destroy' );
            j( '#messages-display-dialog' ).dialog({ height   : 80,
                                                     width    : 490,
                                                     position : 'top',
                                                     title    : title,
                                                     close    : messagesDisplay.dialogClose });
            if ( closeAfter > 0 )
            {
                var timeoutId = window.setTimeout( function(){
                    messagesDisplay.dialogClose();
                    window.clearTimeout( timeoutId );
                },
                closeAfter * 1000 );
            }
        },
                
        /**
         * Displays message specified in a dialog widget
         */
        dialogMessage : function( message ) {
            j( '#messages-display-id' ).text( message.id );
            messagesDisplay.dialog( messagesDisplay.titleTemplate.evaluate( message ), message.text, -1 );
        },

        /**
         * Makes an Ajax request, retrieves messages for the current user and shows the first one in a dialog
         */
        getMessages   : function() {
            j.get( '${action}',
                   { timestamp: new Date().getTime() },
                   function ( messages )
                   {   /* JSON array of messages, as sent by MessagesDisplayController.handleRequest() */
                       if ( messages.length > 0 ) {
                           // Store all messages !!!
                           messagesDisplay.dialogMessage( messages[ 0 ] )
                       }
                   },
                   'json'
            );
        },

        /**
         * Closes the message
         */
        dialogClose : function()
        {
            // Display next message  !!!
            j( '#messages-display-dialog' ).dialog( 'destroy' );
        }
    };

    j( function() {

       /**
        * Message "Close" button listener
        */
        j( '#messages-display-dialog-close' ).click( function(){
            messagesDisplay.dialogClose();
            return false;
        });

       /**
        * Message "Delete" button listener
        */
        j( '#messages-display-dialog-delete' ).click( function(){

            var messageId = j( '#messages-display-id' ).text();

            j( '#messages-display-progress' ).show();
            
            j.ajax({ url      : '${action}',
                     type     : 'POST',
                     data     : { id : messageId },
                     dataType : 'text',
                     success  : function( response ) {
                         messagesDisplay.dialog( 'Message Deleted', 'Message "' + response + '" was deleted', 2 );
                     },
                     error    : function() {
                         messagesDisplay.dialog( 'Message not Deleted', 'Failed to delete message "' + messageId + '"', -1 );
                     },
                     complete : function() {
                         j( '#messages-display-progress' ).hide();
                     }
                    });
            
            return false;
        });

       /**
        * Setting interval to fire up a periodic "Get Messages" request
        */
        window.setInterval( messagesDisplay.getMessages, ${intervalMs} );
        messagesDisplay.getMessages();
    })
</script>

<div id="messages-display-dialog"  style="display:none; overflow:hidden;">
    <span id="messages-display-id" style="display:none;"></span>
	<p>
		<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
        <span id="messages-display-dialog-text"></span>
        <br/>
        <a id="messages-display-dialog-close"  href="#" class="text-link" style="float: right; margin-right: 5px">[Close]</a>
        <a id="messages-display-dialog-delete" href="#" class="text-link" style="float: right; margin-right: 10px">[Delete]</a>
        <img id="messages-display-progress" src="${teamcityPluginResourcesPath}images/ajax-loader.gif" style="float: right; margin-right: 5px; display: none"/>
	</p>
</div>

