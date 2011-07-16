var ms;

( function( j ) {

    ms = { /* Shortcut for "messagesSend" */

       /**
        * Opens the dialog with message specified
        */
        dialog : function( message, closeAfter )
        {
            j( '#messages-send-dialog-text' ).text( message );
            j( '#messages-send-dialog'      ).dialog({ height : 40,
                                                       width  : 180,
                                                       close  : ms.dialogClose });

            if ( closeAfter > 0 ) { ms.dialogClose.delay( closeAfter ) }
        },
        /**
         * Closes the dialog and enables "Send" button
         */
        dialogClose : function()
        {
            j( '#messages-send-dialog' ).dialog( 'destroy' );
            j( '#messages-send-button' ).enable();
            j( '#messages-send-text'   ).focus();
        }
    };

    j( function() {

        /**
         * Twitter-like message length counter
         */
        j( '#messages-send-counter' ).text( messages_const.text_max_length );
        j( '#messages-send-text'    ).focus().keyup( function(){
            var text      = j( this ).val();
            var charsLeft = messages_const.text_max_length - text.length;
            j( '#messages-send-counter' ).text( charsLeft ).
                                          css({ color : ( charsLeft < 1 ? 'red' : '#151515' ) })
        });

       /**
        * "Ok" button listener
        */
        j( '#messages-send-dialog-ok' ).click( function(){ ms.dialogClose(); return false; });

       /**
        * "All" checkbox listener, enables and disables groups/users according to checkbox value
        */
        j( '#messages-send-all' ).change( function() {
            j( '#messages-send-groups, #messages-send-users' ).disable( j( this ).is( ':checked' ));
        });
        j( '#messages-send-all' ).click().change();

       /**
        * Listener submitting a request when form is submitted
        */
        j( '#messages-send-form' ).submit( function()
        {
            var error     = false;
            var longevity = j.trim( j( '#messages-send-longevity-number' ).val());

            if ( longevity.length )
            {
                longevity = parseFloat( longevity );
                if ( isNaN( longevity ) || ( longevity <= 0.0 ))
                {
                    j( '#messages-send-longevity-number' ).addClass( 'errorField' );
                    j( '#messages-send-error-longevity'  ).text( 'Message longevity should be a positive number' );
                    error = true;
                }
            }

            if ( ! error )
            {
                j( '#messages-send-longevity-number' ).removeClass( 'errorField' );
                j( '#messages-send-error-longevity' ).text( '' );
            }

            var messageText = j( '#messages-send-text' ).val();
            if ( j.trim( messageText ))
            {
                if ( messageText.length > messages_const.text_max_length )
                {
                    j( '#messages-send-text'          ).addClass( 'errorField' );
                    j( '#messages-send-error-message' ).text( 'Message should be no longer than ' + messages_const.text_max_length + ' characters.' );
                    error = true;
                }
                else
                {
                    j( '#messages-send-text'          ).removeClass( 'errorField' );
                    j( '#messages-send-error-message' ).text( '' );
                }
            }
            else
            {
                j( '#messages-send-text'          ).addClass( 'errorField' );
                j( '#messages-send-error-message' ).text( 'Message should be specified' );
                error = true;
            }

            var    recipientsSelected = ( j( '#messages-send-all'    ).attr( 'checked' ) ||
                                          j( '#messages-send-groups' ).val()             ||
                                          j( '#messages-send-users'  ).val());
            if ( recipientsSelected )
            {
                j( '#messages-send-error-selection' ).text( '' );
            }
            else
            {
                j( '#messages-send-error-selection' ).text( 'Recipients should be selected' );
                error = true;
            }

            if ( ! error )
            {
                j( '#messages-send-button'   ).disable();
                j( '#messages-send-progress' ).show();

                j.ajax({ url      : this.action,
                         type     : 'POST',
                         data     : j( this ).serialize(),
                         dataType : 'text',
                         success  : function( response ) {
                             /**
                              * Response is 'id' of the new message sent
                              */
                             ms.dialog( 'Message "' + response + '" sent', 1 );
                             j( '#messages-send-text' ).val( '' )
                         },
                         error    : function() { ms.dialog( 'Failed to send message', -1 ) },
                         complete : function() { j( '#messages-send-progress' ).hide() }
                       });
            }

            return false;
        })
    })

})( jQuery.noConflict());
