
( function( j ){

    var ms = { /* Shortcut for "messagesSend" */

       /**
        * Opens the dialog with message specified
        */
        dialog : function( message, closeAfter )
        {
            j( '#messages-send-dialog-text' ).text( message );
            j( '#messages-send-dialog'      ).dialog({ height : 48,
                                                       width  : 180,
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
        * "Ok" button listener
        */
        j( '#messages-send-dialog-ok' ).click( function(){ ms.dialogClose(); return false; });

       /**
        * "All" checkbox listener, enables and disables groups/users according to checkbox value
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
                     success  : function( response ) { ms.dialog( 'Message "' + response + '" sent', 1 )},
                     error    : function()           { ms.dialog( 'Failed to send the message',     -1 )},
                     complete : function()           { j( '#messages-send-progress' ).hide()}
                    });

            return false;
        });
    })
})( jQuery );
