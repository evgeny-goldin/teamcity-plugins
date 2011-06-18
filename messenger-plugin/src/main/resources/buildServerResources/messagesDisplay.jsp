<%@ include file="/include.jsp" %>

<%-- MessagesDisplayExtension.fillModel() --%>
<jsp:useBean id="intervalSec" scope="request" type="java.lang.Integer"/>
<jsp:useBean id="action"      scope="request" type="java.lang.String"/>


<style type="text/css">
    .ui-dialog .ui-dialog-content { padding: 0 } /* Disabling widget-enforced text padding in dialog */
</style>
<script type="text/javascript">
    var j               = jQuery
    var messagesDisplay = {

        /**
         * Template to be used for message title
         * See http://api.prototypejs.org/language/Template/
         */
        titleTemplate : new Template( 'Message "#{id}", sent by #{sender} on #{date} at #{time}' ),

       /**
        * User messages and index of the message currently displayed
        */
        messages         : [],
        messageDisplayed : 0,

        /**
         * Chooses first non-null object
         */
        choose : function ( o1, o2 ) { return ( o1 != null ) ? o1 : o2 },

        /**
         * Assert condition specified holds true
         */
        assert : function ( condition, message ) { if ( ! condition ){ alert( message ) }},

        /**
         * Displays message and title specified in a dialog widget
         */
        dialog : function( options ) {

            var title       = options.title;
            var text        = options.text;
            var showStatus  = messagesDisplay.choose( options.showStatus, true );
            var closeAfter  = messagesDisplay.choose( options.closeAfter,  -1  );
            var urgency     = messagesDisplay.choose( options.urgency,     ''  );

            if ( showStatus ) { j( '#messages-display-dialog-status' ).show(); }
            else              { j( '#messages-display-dialog-status' ).hide(); }

            j( '#messages-display-dialog-text' ).text( text );
            j( '#messages-display-dialog' ).dialog( 'destroy' );
            j( '#messages-display-dialog' ).dialog({ height      : 115,
                                                     width       : 490,
                                                     position    : 'top',
                                                     title       : title,
                                                     close       : messagesDisplay.dialogClose });
            if ( urgency )
            {
                j( 'div.ui-widget-header' ).addClass( 'dialog-' + urgency );
            }
            else
            {
                j( 'div.ui-widget-header' ).removeClass( 'dialog-info' ).
                                            removeClass( 'dialog-warning' ).
                                            removeClass( 'dialog-critical' );
            }

            if ( closeAfter > 0 )
            {
                messagesDisplay.dialogClose.delay( closeAfter );
            }
        },

        /**
         * Opens a dialog with details of the message specified by "messagesDisplay.messageDisplayed"
         */
        dialogMessage : function() {

            var message = messagesDisplay.messages[ messagesDisplay.messageDisplayed ];

            messagesDisplay.assert( ! message.deleted,
                                    'dialogMessage(): message [' + message.id + '] is deleted' );

            j( '#messages-display-counter'       ).text( messagesDisplay.messageDisplayed + 1 );
            j( '#messages-display-counter-total' ).text( messagesDisplay.messages.length );
            j( '#messages-display-id'            ).text( message.id );
            
            messagesDisplay.dialog({ title   : messagesDisplay.titleTemplate.evaluate( message ),
                                     text    : message.text,
                                     urgency : message.urgency });
        },

        /**
         * Makes an Ajax request, retrieves messages for the current user and shows the first one in a dialog
         */
        getMessages   : function() {
            j.get( '${action}',
                   { t: j.now() },
                   function ( messages )
                   {   /* JSON array of messages, as sent by MessagesDisplayController.handleRequest() */
                       if ( messages.length > 0 )
                       {
                           messagesDisplay.messages         = messages.slice( 0 ); // Shallow copy of messages array
                           messagesDisplay.messageDisplayed = 0;
                           messagesDisplay.dialogMessage();
                       }
                   },
                   'json'
            );
        },

        /**
         * Retrieves next index of message in array of messages
         */
        nextIndex : function( index ) {
            messagesDisplay.assert( messagesDisplay.messages.length > 0,
                                    'nextIndex(): messages length is [' + messagesDisplay.messages.length + ']' );
            return (( index < ( messagesDisplay.messages.length - 1 )) ? index + 1 : 0 );
        },

        /**
         * Closes the message
         */
        dialogClose : function()
        {
            messagesDisplay.messageDisplayed = messagesDisplay.nextIndex( messagesDisplay.messageDisplayed );
            if ( messagesDisplay.messageDisplayed == 0 )
            {
                // User has cycled through all messages in the list, dialog is closed
                j( '#messages-display-dialog' ).dialog( 'destroy' );
            }
            else
            {   // Showing next message in a dialog
                messagesDisplay.dialogMessage();
            }
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

                         var displayedMessage = messagesDisplay.messages[ messagesDisplay.messageDisplayed ];

                         messagesDisplay.assert( messageId == displayedMessage.id,
                                                 'delete() click: [' + messageId + '] != [' + displayedMessage.id + '] (displayedMessage.id)' );

                         messagesDisplay.assert( messageId == response,
                                                 'delete() click: [' + messageId + '] != [' + response + '] (response)' );

                         messagesDisplay.dialog({ title      : 'Message Deleted',
                                                  text       : 'Message "' + response + '" was deleted',
                                                  showStatus : false,
                                                  closeAfter : 1 });
                     },
                     error    : function() {
                         messagesDisplay.dialog({ title      : 'Message not Deleted',
                                                  text       : 'Failed to delete message "' + messageId + '"',
                                                  showStatus : false,
                                                  urgency    : 'critical' });
                     },
                     complete : function() {
                         j( '#messages-display-progress' ).hide();
                     }
                    });
            
            return false;
        });


        /**
         * Setting an interval to fire up a periodic "Get Messages" request
         * http://api.prototypejs.org/language/PeriodicalExecuter/
         */
        new PeriodicalExecuter( messagesDisplay.getMessages, ${intervalSec} );
        messagesDisplay.getMessages();
    })
</script>

<div id="messages-display-dialog"  style="display:none; overflow:hidden;">
    <span id="messages-display-id" style="display:none;"></span>
	<p>
		<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
        <span id="messages-display-dialog-text"></span>
        <div  id="messages-display-dialog-status" style="position: absolute; bottom: 0; width: 100%; margin-left: 5px;">
            <a id="messages-display-dialog-prev"  href="#" class="text-link" style="">[Prev]</a>
            <a id="messages-display-dialog-next"  href="#" class="text-link" style="">[Next]</a>
            <span style="margin-left: 20%">
                Message <span id="messages-display-counter"></span> of <span id="messages-display-counter-total"></span>.
            </span>
            <a id="messages-display-dialog-close"  href="#" class="text-link" style="float: right; margin-right: 10px">[Close]</a>
            <a id="messages-display-dialog-delete" href="#" class="text-link" style="float: right; margin-right: 5px">[Delete]</a>
            <img id="messages-display-progress" src="${teamcityPluginResourcesPath}images/ajax-loader.gif" style="float: right; margin-right: 5px; display: none"/>
        </div>
	</p>
</div>
