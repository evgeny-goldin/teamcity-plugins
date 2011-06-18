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
         * Displays message and title specified in a dialog widget
         */
        dialog : function( options ) {

            var title       = options.title;
            var text        = options.text;
            var showDelete  = messagesDisplay.choose( options.showDelete,  true );
            var closeAfter  = messagesDisplay.choose( options.closeAfter,  -1   );
            var dialogClass = messagesDisplay.choose( options.dialogClass, ''   );

            if ( showDelete ) { j( '#messages-display-dialog-delete' ).show(); }
            else              { j( '#messages-display-dialog-delete' ).hide(); }

            j( '#messages-display-dialog-text' ).text( text );
            j( '#messages-display-dialog' ).dialog( 'destroy' );
            j( '#messages-display-dialog' ).dialog({ height      : 80,
                                                     width       : 490,
                                                     position    : 'top',
                                                     dialogClass : dialogClass,
                                                     title       : title,
                                                     close       : messagesDisplay.dialogClose });
            if ( closeAfter > 0 )
            {
                messagesDisplay.dialogClose.delay( closeAfter );
            }
        },

        /**
         * Displays message specified in a dialog widget
         */
        dialogMessage : function( message ) {
            j( '#messages-display-id' ).text( message.id );
            messagesDisplay.dialog({ title       : messagesDisplay.titleTemplate.evaluate( message ),
                                     text        : message.text,
                                     dialogClass : 'messages-display-dialog-' + message.urgency });
        },

        /**
         * Makes an Ajax request, retrieves messages for the current user and shows the first one in a dialog
         */
        getMessages   : function() {
            j.get( '${action}',
                   { timestamp: j.now() },
                   function ( messages )
                   {   /* JSON array of messages, as sent by MessagesDisplayController.handleRequest() */
                       if ( messages.length > 0 )
                       {
                           messagesDisplay.messages         = messages.slice( 0 ); // Shallow copying messages array
                           messagesDisplay.messageDisplayed = 0;
                           messagesDisplay.dialogMessage( messagesDisplay.messages[ messagesDisplay.messageDisplayed ] );
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
            if ( messagesDisplay.messageDisplayed < ( messagesDisplay.messages.length - 1 ))
            {
                messagesDisplay.dialogMessage( messagesDisplay.messages[ ++ messagesDisplay.messageDisplayed ] );
            }
            else
            {
                j( '#messages-display-dialog' ).dialog( 'destroy' );
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
                         messagesDisplay.dialog({ title      : 'Message Deleted',
                                                  text       : 'Message "' + response + '" was deleted',
                                                  showDelete : false,
                                                  closeAfter : 1 });
                     },
                     error    : function() {
                         messagesDisplay.dialog({ title       : 'Message not Deleted',
                                                  text        : 'Failed to delete message "' + messageId + '"',
                                                  showDelete  : false,
                                                  dialogClass : 'messages-display-dialog-critical' });
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
        new PeriodicalExecuter( messagesDisplay.getMessages, ${intervalMs} );
    })
</script>

<div id="messages-display-dialog"  style="display:none; overflow:hidden;">
    <span id="messages-display-id" style="display:none;"></span>
	<p>
		<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
        <span id="messages-display-dialog-text"></span>
        <br/>
        <a id="messages-display-dialog-close"  href="#" class="text-link" style="float: right; margin-right: 5px">[Close]</a>
        <a id="messages-display-dialog-delete" href="#" class="text-link" style="float: right; margin-right: 10px; display: none">[Delete]</a>
        <img id="messages-display-progress" src="${teamcityPluginResourcesPath}images/ajax-loader.gif" style="float: right; margin-right: 5px; display: none"/>
	</p>
</div>
