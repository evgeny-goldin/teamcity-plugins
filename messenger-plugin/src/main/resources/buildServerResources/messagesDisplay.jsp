<jsp:useBean id="intervalMs" scope="request" type="java.lang.Integer"/>

<script type="text/javascript">
    var j = jQuery

    j( function() {
        function messagesDisplay() {
            j.get( 'messagesDisplay.html',
                   { timestamp: new Date().getTime() },
                   function ( messages ){
                       j.each( messages, function( index, message ) {
//                           alert( "[" + message.id + "][" + message.sender + "]=>[" + message.recipient + "][" + message.date + "][" + message.text + "]" );
                       });
                   },
                   'json'
            );
        }

        window.setInterval( messagesDisplay, ${intervalMs} );
        messagesDisplay()
    })
</script>
