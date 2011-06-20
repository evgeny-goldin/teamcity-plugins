
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
    j.error = function( message ) {
        var errorMessage = 'Assertion Failure!\n[' + message + ']';
        alert         ( errorMessage );
        console.error ( errorMessage );
        throw         ( errorMessage );
    };


   /**
    * Determines if object specified is defined and not null
    */
    j.defined = function( o ) { return ( ! (( typeof o == 'undefined' ) || ( o == null ))) };


    /**
     * Verifies that condition specified is true and displays an alert() if it's not
     */
    j.assert = function( condition, message ) {
        if ( ! j.defined( condition )) { j.error( 'alert(): condition is not specified'   )}
        if ( ! j.defined( message   )) { j.error( 'alert(): message is not specified'     )}
        if ( ! condition )             { j.error( message )}
    };


   /**
    * Returns first element if it's defined or the second one (default option, should be defined) otherwise
    */
    j.choose  = function( o1, o2 ){
        j.assert( j.defined( o2 ), 'j.choose(): o2 is undefined' );
        return  ( j.defined( o1 ) ? o1 : o2 );
    };


    /**
     * Enables jQuery object, taking optional boolean argument into consideration as well.
     */
    j.fn.enable  = function( condition ) {
		return this.disable( j.defined( condition ) ? ( ! condition ) : false )
    };


    /**
     * Array of elements, supporting "disable" attribute.
     * https://developer.mozilla.org/en/HTML/Attributes
     */
    j.nativeDisable = $w( 'button command fieldset input keygen optgroup option select textarea' ),

            
    /**
     * Disables jQuery object, taking optional boolean argument into consideration as well.
     */
    j.fn.disable = function( condition ) {

        var disabled = ( j.defined( condition ) && ( ! condition )) ? null : 'disabled';
        var tagName  = this.get( 0 ).tagName.toLowerCase();

		if ( j.nativeDisable.indexOf( tagName ) > -1 )
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


/**
 * Various Array extensions
 */
Object.extend( Array.prototype, ( function( j ) {

    /**
     * Iterates over array specified, optionally breaking the iteration if 'returnOnFalse' is true and closure invoked returns false.
     * When closure is invoked it is passed an array element and its index in the array.
     */
    function iterate( closure, returnOnFalse, startIndex, endIndex ) {

        returnOnFalse = j.choose( returnOnFalse, false );
        startIndex    = j.choose( startIndex,    0 );
        endIndex      = j.choose( endIndex,      this.length );

        j.assert((( startIndex > -1 ) && ( endIndex > -1 ) && ( startIndex <= endIndex ) && ( endIndex <= this.length )),
                 'j.count(): [' + startIndex + '][' + endIndex + '][' + this.length + '] (startIndex, endIndex, this.length)' );

        for ( var i = startIndex; i < endIndex; i++ ) {
            if (( closure( this[ i ], i ) === false ) && ( returnOnFalse )) { return }
        }
    }


    /**
     * Counts how many array elements satisfy closure condition by returning true (=== matching).
     * When closure is invoked it is passed an array element and its index in the array.
     */
    function count( closure, startIndex, endIndex ) {

        var counter = 0;

        this.iterate( function( o, index ){ if ( closure( o, index ) === true ) { counter++ }},
                      false, startIndex, endIndex );

        return counter;
    }


    /**
     * Finds array index matching closure specified or -1 if not found.
     * If 'invert' is true, finds array index not matching closure specified or -1 if not found.
     * When closure is invoked it is passed an array element and its index in the array.
     */
    function newIndexOf( closure, invert, startIndex, endIndex ) {

        var indexOf = -1;
        invert      = j.choose( invert, false );

        this.iterate( function( o, index ){
            var result = closure( o, index );
            if (( result === true  ) && ( ! invert )){ indexOf = index; return false }
            if (( result === false ) && (   invert )){ indexOf = index; return false }
        },
        true, startIndex, endIndex );

        return indexOf;
    }


    /**
     * Determines if array specified contains element passing the closure filter.
     */
    function contains( closure, invert, startIndex, endIndex ) {
        return ( this.newIndexOf( closure, invert, startIndex, endIndex ) > -1 )
    }

    return {
        iterate    : iterate,
        count      : count,
        newIndexOf : newIndexOf,
        contains   : contains
    };
    
})( jQuery ));
