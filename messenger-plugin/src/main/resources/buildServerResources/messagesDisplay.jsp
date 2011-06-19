<%@ include file="/include.jsp" %>

<%-- MessagesDisplayExtension.fillModel() --%>
<jsp:useBean id="intervalSec" scope="request" type="java.lang.Integer"/>
<jsp:useBean id="action"      scope="request" type="java.lang.String"/>


<style type="text/css">
    .ui-dialog .ui-dialog-content { padding: 0 } /* Disabling widget-enforced text padding in dialog */
</style>
<script type="text/javascript">

    var j  = jQuery;
    var md = { /* Shortcut for "messagesDisplay" */

        /**
         * Template to be used for message dialog title
         * http://api.prototypejs.org/language/Template/
         */
        titleTemplate    : new Template( 'Message "#{id}", sent by #{sender} on #{date} at #{time}' ),

       /**
        * User messages received, deleted and index of the message currently displayed
        */
        messages         : [],
        messagesDeleted  : [],
        messageDisplayed : 0,

        /**
         * Displays dialog widget according to options specified
         */
        dialog : function( options ) {

            options = j.extend({ showStatus : true, closeAfter : -1, urgency : '' }, options );

            j.assert( options.title, 'dialog(): \'options.title\' is not specified' );
            j.assert( options.text,  'dialog(): \'options.text\' is not specified'  );

            if ( options.showStatus ) { j( '#messages-display-dialog-status' ).show(); }
            else                      { j( '#messages-display-dialog-status' ).hide(); }

            j( '#messages-display-dialog-text' ).text( options.text );
            j( '#messages-display-dialog' ).dialog( 'destroy' );
            j( '#messages-display-dialog' ).dialog({ height   : 115,
                                                     width    : 490,
                                                     position : 'top',
                                                     title    : options.title,
                                                     close    : md.dialogNext });
            if ( options.urgency )
            {
                j( 'div.ui-widget-header' ).addClass( 'dialog-' + options.urgency );
            }
            else
            {
                j( 'div.ui-widget-header' ).removeClass( 'dialog-info' ).
                                            removeClass( 'dialog-warning' ).
                                            removeClass( 'dialog-critical' );
            }

            if ( options.closeAfter > 0 ){ md.dialogNext.delay( options.closeAfter ) }
        },

        /**
         * Opens a dialog with details of the message specified by "md.messageDisplayed"
         */
        dialogMessage : function() {

            var message        = md.messages[ md.messageDisplayed ];
            var messageCounter = md.messageDisplayed + 1 -
                                 j.count( md.messagesDeleted, function( m, index ){ return ( index < md.messageDisplayed ) });
            var messagesTotal  = md.messages.length - md.messagesDeleted.length;

            j( '#messages-display-dialog-prev'   ).disable( messageCounter == 1             ).click( function(){ });
            j( '#messages-display-dialog-next'   ).disable( messageCounter == messagesTotal ).click( function(){ });
            j( '#messages-display-counter'       ).text( messageCounter );
            j( '#messages-display-counter-total' ).text( messagesTotal  );

            md.dialog({ title   : md.titleTemplate.evaluate( message ),
                        text    : message.text,
                        urgency : message.urgency });
        },

        /**
         * Makes an Ajax request, retrieves messages for the current user and shows the first one in a dialog
         */
        getMessages   : function() {
            j.get( '${action}',
                   { t: j.now() },
                   function ( messages ) /* JSON array of messages, as sent by MessagesDisplayController.handleRequest() */
                   {
                       if ( messages && messages.length )
                       {
                           md.messages         = messages.slice( 0 ); // Shallow copy of messages array
                           md.messageDisplayed = 0;
                           md.dialogMessage();
                       }
                   },
                   'json'
            );
        },

        /**
         * Retrieves next index of message in array of messages
         */
        nextIndex : function( index ) {
            j.assert( md.messages.length > 0, 'nextIndex(): messages length is [' + md.messages.length + ']' );
            return (( index < ( md.messages.length - 1 )) ? index + 1 : 0 );
        },

        /**
         * Shows the next message or closes the dialog if there are no messages left to show
         */
        dialogNext : function()
        {
            md.messageDisplayed = md.nextIndex( md.messageDisplayed );
            if ( md.messageDisplayed == 0 )
            {
                // User has cycled through all messages in the list, dialog is closed
                j( '#messages-display-dialog' ).dialog( 'destroy' );
            }
            else
            {   // Showing next message in a dialog
                md.dialogMessage();
            }
        }
    };

    j( function() {

       /**
        * Message dialog "Close" button listener
        */
        j( '#messages-display-dialog-close' ).click( function(){
            md.dialogNext();
            return false;
        });

       /**
        * Message dialog "Delete" button listener
        */
        j( '#messages-display-dialog-delete' ).click( function(){

            j( '#messages-display-progress' ).show();

            var message = md.messages[ md.messageDisplayed ];

            j.ajax({ url      : '${action}',
                     type     : 'POST',
                     data     : { id : message.id },
                     dataType : 'text',
                     success  : function( response ) {

                         j.assert( message.id == response,
                                   'delete(): [' + message.id + '] != [' + response + ']' );

                         md.messagesDeleted.push( message );

                         md.dialog({ title      : 'Message Deleted',
                                     text       : 'Message "' + response + '" was deleted',
                                     showStatus : false,
                                     closeAfter : 1 });
                     },
                     error    : function() {
                         md.dialog({ title      : 'Message not Deleted',
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
         * Setting up periodic "get all messages" request
         * http://api.prototypejs.org/language/PeriodicalExecuter/
         */
        j.assert(( ${intervalSec} > 0 ), 'intervalSec is [{intervalSec}]' );
        new PeriodicalExecuter( md.getMessages, ${intervalSec} );
        md.getMessages();
    })
</script>

<div id="messages-display-dialog"  style="display:none; overflow:hidden;">
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
