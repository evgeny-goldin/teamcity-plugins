var ms;  /* Shortcut for "messagesSend" */

( function( j ) {

    ms = {

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
        },


        /**
         * Validates longevity values and returns true if value is legal.
         * Returns false otherwise.
         * When invoked explicitly upon form submit - event parameter is undefined.
         */
        longevityValidate : function( event ) {

            var formSubmit = ( ! event );
            var longevity  = j.trim( j( this ).val());

            if ( longevity )
            {
                longevity = parseFloat( longevity );
                if ( isNaN( longevity ) || ( longevity <= 0.0 ))
                {
                    j( this ).addClass( 'errorField' );
                    j( '#messages-send-error-longevity' ).text( 'Message longevity should be a positive number' );
                    if ( formSubmit ) { j( this ).focus() }
                    return false;
                }
            }

            j( this ).removeClass( 'errorField' );
            j( '#messages-send-error-longevity' ).text( '' );
            return true;
        },


        /**
         * Validates text message and returns true if value is legal.
         * Returns false otherwise.
         * When invoked explicitly upon form submit - event parameter is undefined.
         */
        textValidate : function( event ) {

            var formSubmit  = ( ! event );
            var messageText = j.trim( j( this ).val());
            var left        = dialog_const.text_max_length - messageText.length;

            j( '#messages-send-counter' ).text( left ).css({ color : (( left < 10 ) ? 'red' : '#151515' ) });

            if ( formSubmit && (( ! messageText ) || ( left < 0 )))
            {
                j( this ).addClass( 'errorField' ).focus();
                j( '#messages-send-error-message' ).text(
                    messageText ? 'Message should be no longer than ' + dialog_const.text_max_length + ' characters.' :
                                  'Message should be specified' );
                return false;
            }

            j( this ).removeClass( 'errorField' );
            j( '#messages-send-error-message' ).text( '' );
            return true;
        },


        /**
         * Validates recipients selected and returns true if value is legal.
         * Returns false otherwise.
         * When invoked explicitly upon form submit - event parameter is undefined.
         */
        recipientsValidate : function( event ) {

            var  formSubmit = ( ! event );
            var  allChecked = j( '#messages-send-all' ).is( ':checked' );
            var  selected   = ( allChecked || j( '#messages-send-groups' ).val() || j( '#messages-send-users'  ).val());

            j( '#messages-send-error-selection' ).text( formSubmit && ( ! selected ) ? 'Recipients should be selected' : '' );
            j( '#messages-send-groups, #messages-send-users' ).disable( allChecked );
            return selected;
        },


        /**
         * Form submit listener
         */
        formSubmit : function() {

            // We always have to invoke all 3 validations to collect the errors, don't short-circuit them
            var longevityValid   = ms.longevityValidate.call( j( '#messages-send-longevity-number' ).get( 0 ));
            var messageTextValid = ms.textValidate.call( j( '#messages-send-text' ).get( 0 ));
            var recipientsValid  = ms.recipientsValidate();

            if ( longevityValid && messageTextValid && recipientsValid )
            {
                j( '#messages-send-button'   ).disable();
                j( '#messages-send-progress' ).show();

                j.ajax({ url      : j( this ).attr( 'action' ),
                         data     : j( this ).serialize(),
                         type     : 'POST',
                         dataType : 'text',
                         success  : function( response ) {
                             /**
                              * Response is 'id' of the new message sent
                              */
                             ms.dialog( 'Message "' + response + '" sent', 1 );
                             j( '#messages-send-text' ).val( '' ).change();
                         },
                         error    : function() { ms.dialog( 'Failed to send message', -1 ) },
                         complete : function() { j( '#messages-send-progress' ).hide() }
                       });
            }

            return false;
        }
    };

    j( function() {

        /**
         * Setting change listeners
         */
        j( '#messages-send-counter' ).text( dialog_const.text_max_length );
        j( '#messages-send-longevity-number' ).change( ms.longevityValidate );
        j( '#messages-send-text'             ).keyup ( ms.textValidate ).change( ms.textValidate ).focus();
        j( '#messages-send-all, #messages-send-groups, #messages-send-users' ).change( ms.recipientsValidate );

        /**
         * Listener submitting a request when form is submitted
         */
         j( '#messages-send-form' ).submit( ms.formSubmit );

       /**
        * Dialog message "Ok" button listener
        */
        j( '#messages-send-dialog-ok' ).click( function(){ ms.dialogClose(); return false; });

       /**
        * Checking "all" checkbox
        */
        j( '#messages-send-all' ).click().change();
    })

})( jQuery.noConflict());
