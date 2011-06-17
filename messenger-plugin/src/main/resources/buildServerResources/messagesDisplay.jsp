<jsp:useBean id="intervalMs" scope="request" type="java.lang.Integer"/>

<script type="text/javascript">
    var j               = jQuery
    var messagesDisplay = {
        // http://api.prototypejs.org/language/Template/
        titleTemplate : new Template( 'Message "#{id}", sent by "#{sender}" at "#{date}"' ),
        makeRequest   : function() {
            j.get( 'messagesDisplay.html',
                   { timestamp: new Date().getTime() },
                   function ( messages /* JSON array of messages, as sent by MessagesDisplayController */ )
                   {
                       j.each( messages, function( index, m ) {
                           j( '#messages-display-dialog' ).attr({ title : messagesDisplay.titleTemplate.evaluate(
                                                                { id    : m.id, sender : m.sender, date : m.date })});
                           j( '#messages-display-dialog-text' ).text( m.text );
                           j( '#messages-display-dialog'      ).dialog();
                       });
                   },
                   'json'
            );
        }
    };

    j( function() {
        window.setInterval( messagesDisplay.makeRequest, ${intervalMs} );
        messagesDisplay.makeRequest();
    })
    
</script>
<div id="messages-display-dialog" title="" style="display:none; overflow:hidden;">
	<p>
		<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
        <span id="messages-display-dialog-text"></span>
        <a id="messages-display-dialog-close" href="#" style="margin-left: 15px; color: #1564c2">Close</a>
        <a id="messages-display-dialog-delete" href="#" style="margin-left: 15px; color: #1564c2">Delete</a>
	</p>
</div>
