
/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Custom jQuery plugins
 * http://docs.jquery.com/Plugins/Authoring
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

( function( j ){

    /**
     * Alerts, logs and throws execution fatal error
     */
    j.error = function( message )
    {
        var errorMessage = 'Assertion Failure!\n[' + message + ']';
        alert         ( errorMessage );
        console.error ( errorMessage );
        throw         ( errorMessage );
    };

    
   /**
    * Determines if object specified is defined and not null
    */
    j.defined = function( o ) {
        return ( ! (( typeof o == 'undefined' ) || ( o == null )))
    };


    /**
     * Counts how many array elements satisfy closure condition.
     * When closure is invoked it is passed an array element and its index in the array.
     */
    j.count = function( array, closure ) {
        var counter = 0;
        j.each( array, function( index, element ) {
            if ( closure( element, index )) { counter++ }
        });
        return counter;
    };

    
    /**
     * Verifies that condition specified is true and displays an alert() if it's not
     */
    j.assert = function( condition, message ) {
        if ( ! j.defined( condition )) { j.error( 'alert(): condition is not specified'   )}
        if ( ! j.defined( message   )) { j.error( 'alert(): message is not specified'     )}
        if ( ! condition )             { j.error( message )}
    };


    /**
     * Enables jQuery object, taking optional boolean argument into consideration as well.
     */
    j.fn.enable  = function( condition ) {
		return this.disable( j.defined( condition ) ? ( ! condition ) : false )
    };

    
    /**
     * Disables jQuery object, taking optional boolean argument into consideration as well.
     */
    j.fn.disable = function( condition ) {
        
		var tagName        = this.get( 0 ).tagName.toLowerCase();
		var disabled       = ( j.defined( condition ) && ( ! condition )) ? null : 'disabled';
        var nativeDisabled = j.count( $w( 'button command fieldset input keygen optgroup option select textarea' ),
                                      function( element ){ return ( element == tagName )} );
		if ( nativeDisabled )
		{
			return this.attr({ disabled: disabled })
		}
        else if ( tagName == 'a' ) 
		{
            // http://css-tricks.com/6379-pointer-events-current-nav/
			return this.css({ 'pointer-events' : disabled ? 'none'    : 'auto', 
			                  'cursor'         : disabled ? 'default' : 'pointer',
                              'color'          : disabled ? '#151515' : '#1564c2' })
		}
    };

})( jQuery );
