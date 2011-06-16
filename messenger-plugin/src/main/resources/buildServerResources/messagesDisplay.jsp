<script type="text/javascript">
jQuery( function() {

    var j = jQuery

    j.get( 'messagesDisplay.html',
           function ( messages ){
               j.each( messages, function( index, message ) {
                   alert( "[" + message.id + "][" + message.sender + "]=>[" + message.recipient + "][" + message.date + "][" + message.text + "]" );
               });
           },
           'json'
    );

})
</script>
