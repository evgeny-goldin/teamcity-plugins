
/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Custom jQuery plugins
 * http://docs.jquery.com/Plugins/Authoring
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

( function( j ){

   /**
    * Determines if object specified is defined and not null
    */
    j.defined = function( o ) {
        return ( ! (( typeof o == 'undefined' ) || ( o == null )))
    };
    

    /**
     * Verifies that condition specified is true and displays an alert() if it's not
     */
    j.assert = function( condition, message ) {
        if ( ! j.defined( condition )) { alert( 'alert(): condition is not defined' )}
        if ( ! j.defined( message   )) { alert( 'alert(): message is not defined'   )}
        if ( ! condition )             { alert( message ) }
    };


    /**
     * Enables jQuery object, taking optional boolean argument into consideration as well.
     */
    j.fn.enable  = function( condition ) {
		return this.disable( j.defined( condition ) ? ( ! condition ) : true )
    };

    
    /**
     * Disables jQuery object, taking optional boolean argument into consideration as well.
     */
    j.fn.disable = function( condition ) {
        
		var tagName  = this.get( 0 ).tagName.toLowerCase();
		var disabled = ( j.defined( condition ) && ( ! condition )) ? null : 'disabled';

		if ( tagName == 'input' )
		{
			return this.attr({ disabled: disabled })
		}
        else if ( tagName == 'a' ) 
		{
			return this.css({ 'pointer-events' : disabled ? 'none'    : 'auto', 
			                  'cursor'         : disabled ? 'default' : 'pointer',
                              'color'          : disabled ? '#151515' : '#1564c2' })
		}
        else 
        {
			alert( 'disable()/enable() are only supported for <input> and <a> elements' );
            return this;
		}
    };

})( jQuery );
