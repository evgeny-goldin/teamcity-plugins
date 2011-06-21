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
        * User messages retrieved and index of the message being displayed currently
        */
        messageDisplayed : -1,
        messages         : new Array(),
        nMessages        : function(){ return md.messages.length },

        /**
         * Displays dialog widget according to options specified
         */
        dialog : function( options ) {

            options = j.extend({ showStatus : true, urgency : '' }, options );

            j.assert( options.title, 'dialog(): \'options.title\' is not specified' );
            j.assert( options.text,  'dialog(): \'options.text\' is not specified'  );

            md.dialogClose();

            if ( options.showStatus ) { j( '#messages-display-dialog-status' ).show(); }
            else                      { j( '#messages-display-dialog-status' ).hide(); }

            j( '#messages-display-dialog-text' ).text( options.text.length > 250 ?
                                                       options.text.substring( 0, 250 ) + ' ..' :
                                                       options.text );
            j( '#messages-display-dialog' ).dialog({ height   : 115,
                                                     width    : 490,
                                                     position : 'top',
                                                     title    : options.title,
                                                     close    : md.dialogClose });
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
        },

        /**
         * Determines if message specified is deleted
         */
        isDeleted : function( message ) { return j.choose( message.deleted, false )},

        /**
         * Opens a dialog with details of the message specified by "md.messageDisplayed"
         */
        dialogMessage : function() {

            j.assert(( md.messageDisplayed > -1 ) && ( md.messageDisplayed < md.nMessages()),
                     'dialogMessage(): [' + md.messageDisplayed + '][' + md.nMessages() + '] (md.messageDisplayed, md.nMessages())' );
            
            var message = md.messages[ md.messageDisplayed ];

            j.assert( ! message.deleted,
                     'dialogMessage(): [' + md.messageDisplayed + '][' + message.id + '] (md.messageDisplayed, message.id - deleted)' );

           /**
            * "Message x of y" dialog status - counter is 'x', total is 'y'
            */
            var counter = md.messageDisplayed + 1 - md.messages.count( md.isDeleted, 0, md.messageDisplayed );

            var total   = md.nMessages() -
                          ( md.messageDisplayed + 1 - counter ) -                     // messages deleted *before* current message
                          md.messages.count( md.isDeleted, md.messageDisplayed + 1 ); // messages deleted *after*  current message

            j.assert( counter > 0,      'dialogMessage(): [' + counter + '] (counter > 0)' );
            j.assert( total   > 0,      'dialogMessage(): [' + total   + '] (total   > 0)' );
            j.assert( counter <= total, 'dialogMessage(): [' + counter + '][' + total + '] (counter <= total)' );

            j( '#messages-display-counter'       ).text( counter );
            j( '#messages-display-counter-total' ).text( total   );

            // "Prev" button
            j( '#messages-display-dialog-prev' ).enable( counter > 1 );

            // "Next" button
            j( '#messages-display-dialog-next' ).enable( counter < total );

            md.dialog({ title   : md.titleTemplate.evaluate( message ),
                        text    : message.text,
                        urgency : message.urgency });
        },

        /**
         * Makes an Ajax request, retrieves messages for the current user and shows the first one in a dialog
         */
        unknownMessage : function( message ) { return ( ! md.messages.contains( function( m ){ return ( m.id == message.id ) } )) },
        getMessages    : function() {
            j.get( '${action}',
                   { t: j.now() },
                   function ( newMessages ) /* JSON array of messages, as sent by MessagesDisplayController.handleRequest() */
                   {
                       if ( ! ( newMessages && newMessages.length )) { return }

                       var isUpdate =
                           (( md.nMessages() < 1 )                   || // No existing message yet
                            ( md.nMessages() != newMessages.length ) || // There are more or less messages on the server
                            ( newMessages.any( md.unknownMessage )));   // Is there any new message that is unknown ?

                       if ( isUpdate )
                       {
                           md.messageDisplayed = 0;
                           md.messages         = newMessages.slice( 0 ); // Shallow copy of messages array

                           j.assert( md.nMessages(), 'getMessages(): [' + md.nMessages() + '] (md.nMessages())' );
                           
                           md.dialogMessage();
                       }
                   },
                   'json'
            );
        },


        /**
         * Closes the dialog
         */
        dialogClose : function()
        {
            j( '#messages-display-dialog' ).dialog( 'destroy' );
        }
    };

    j( function() {

        /**
         * "Prev" button listener
         */
        j( '#messages-display-dialog-prev' ).click( function(){

            j.assert( md.messageDisplayed > 0,
                      '"Prev" click: [' + md.messageDisplayed + '] (md.messageDisplayed)' );

            var  prevMessage;
            for( prevMessage = md.messageDisplayed - 1; md.messages[ prevMessage ].deleted; prevMessage-- ){}

            j.assert(( prevMessage > -1 ) && ( prevMessage < md.messageDisplayed ),
                     '"Prev" click: [' + prevMessage + '][' + md.messageDisplayed + '] (prevMessage, md.messageDisplayed)' );

            md.messageDisplayed = prevMessage;
            md.dialogMessage();
        });


        /**
         * "Next" button listener
         */
        j( '#messages-display-dialog-next' ).click( function(){

            j.assert( md.messageDisplayed < ( md.nMessages() - 1 ),
                      '"Next" click: [' + md.messageDisplayed + '][' + md.nMessages() + '] (md.messageDisplayed, md.nMessages())' );

            var   nextMessage;
            for ( nextMessage = md.messageDisplayed + 1; md.messages[ nextMessage ].deleted; nextMessage++ ){}

            j.assert(( nextMessage < md.nMessages()) && ( nextMessage > md.messageDisplayed ),
                     '"Next" click: [' + nextMessage + '][' + md.messageDisplayed + '] (nextMessage, md.messageDisplayed)' );

            md.messageDisplayed = nextMessage;
            md.dialogMessage();
        });

        
       /**
        * "Close" button listener
        */
        j( '#messages-display-dialog-close' ).click( function(){
            md.dialogClose();
            return false;
        });


       /**
        * "Delete" button listener
        */
        j( '#messages-display-dialog-delete' ).click( function(){

            j( '#messages-display-progress' ).show();

            j.assert(( md.messageDisplayed > -1 ) && ( md.messageDisplayed < md.nMessages()),
                     '"Delete" click: [' + md.messageDisplayed + '] (md.messageDisplayed)' );
            
            var message = md.messages[ md.messageDisplayed ];

            j.ajax({ url      : '${action}',
                     type     : 'POST',
                     data     : { id : message.id },
                     dataType : 'text',
                     success  : function( response ) {

                         j.assert( message.id == response,
                                   '"Delete" click: [' + message.id + '] != [' + response + ']' );

                         message.deleted = true;

                         md.dialog({ title      : 'Message Deleted',
                                     text       : 'Message "' + response + '" deleted',
                                     showStatus : false });
                        /**
                         * Invoked in one second, displays next message after the current one is deleted
                         * or closes the dialog if all messages are deleted
                         */
                         ( function(){

                             // Searching for the next not deleted message right to the current message
                             var nextMessage = md.messages.newIndexOf( md.isDeleted, true, md.messageDisplayed + 1 );

                             if ( nextMessage < 0 ) {
                                 // Searching for the previous not deleted message left to the current message
                                 nextMessage = md.messages.newIndexOf( md.isDeleted, true, 0, md.messageDisplayed  );
                             }

                             if ( nextMessage < 0 ) {
                                 // All messages are deleted
                                 md.dialogClose();
                             }
                             else {
                                 md.messageDisplayed = nextMessage;
                                 md.dialogMessage();
                             }
                             
                         }).delay( 1 );
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
    <div id="messages-display-dialog-text" style="margin:5px; margin-top: 2px; margin-bottom: -5px;"></div>
    <hr style="border: 0.1em ridge"/>
    <div  id="messages-display-dialog-status" style="position: absolute; bottom: 0; width: 100%; margin-left: 5px; margin-top: 5px">
        <a id="messages-display-dialog-prev"  href="#" class="text-link" style="">[Prev]</a>
        <a id="messages-display-dialog-next"  href="#" class="text-link" style="">[Next]</a>
        <span style="margin-left: 20%">
            Message <span id="messages-display-counter"></span> of <span id="messages-display-counter-total"></span>
        </span>
        <a id="messages-display-dialog-close"  href="#" class="text-link" style="float: right; margin-right: 10px">[Close]</a>
        <a id="messages-display-dialog-delete" href="#" class="text-link" style="float: right; margin-right: 5px">[Delete]</a>
        <img id="messages-display-progress" src="${teamcityPluginResourcesPath}images/ajax-loader.gif" style="float: right; margin-right: 5px; display: none"/>
    </div>
</div>
