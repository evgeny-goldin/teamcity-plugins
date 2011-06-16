<jsp:useBean id="intervalMs" scope="request" type="java.lang.Integer"/>

<script type="text/javascript">
jQuery( function() {

    var j = jQuery

    function messagesDisplay() {
        j.get( 'messagesDisplay.html',
               { timestamp: new Date().getTime() },
               function ( messages ){
                   j.each( messages, function( index, message ) {
                       alert( "[" + message.id + "][" + message.sender + "]=>[" + message.recipient + "][" + message.date + "][" + message.text + "]" );
                   });
               },
               'json'
        );
    }

    window.setInterval( messagesDisplay, ${intervalMs} );
    messagesDisplay()
})
</script>
