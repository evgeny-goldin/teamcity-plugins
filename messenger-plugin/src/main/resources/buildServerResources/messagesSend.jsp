<%@ include file="/include.jsp" %>


<jsp:useBean id="groups" scope="request" type="java.util.Collection"/>
<jsp:useBean id="users"  scope="request" type="java.util.Collection"/>

<link   rel="stylesheet"       href="">
<script type="text/javascript" src=""></script>
<script type="text/javascript">

    var j = jQuery

    function messagesSentDialog( message ) {
         j( '#messages-sent-text' ).text( message );
         j( "#messages-sent"      ).dialog({ height : 100, width : 250 });
    }

    j( function() {

        j( '#messages-message' ).focus();

       /**
        * "Message Sent" Ok buton listener
        */
        j( '#messages-sent-button' ).click( function(){
            j( "#messages-sent"    ).dialog( 'destroy' );
            j( '#messages-message' ).val( '' ).focus();
        });

       /**
        * Listener enabling and disabling groups and users according to "Send to All" checkbox
        */
        j( '#messages-all' ).change( function() {
            j( '#messages-groups' ).attr({ disabled: this.value });
            j( '#messages-users'  ).attr({ disabled: this.value });
        });

       /**
        * Listener submitting a request when form is submitted
        */
        j( '#messages-form' ).submit( function() {

            if ( ! j.trim( j( '#messages-message' ).val()))
            {
                j( '#messages-message'      ).addClass( 'errorField'   );
                j( '#messages-errormessage' ).text( 'Message is empty' );
                return false;
            }
            else
            {
                j( '#messages-message' ).removeClass( 'errorField' );
                j( '#messages-errormessage' ).text( '' );
            }

            var    recipientsSelected = ( j( '#messages-all' ).attr( 'checked' ) ||
                                          j( '#messages-groups' ).val()          ||
                                          j( '#messages-users' ).val());
            if ( ! recipientsSelected )
            {
                j( '#messages-errorselection' ).text( 'No recipients selected' );
                return false;
            }
            else
            {
                j( '#messages-errorselection' ).text( '' );
            }

            j.ajax({ url      : this.action,
                     data     : j( this ).serialize(),
                     dataType : 'text',  
                     success  : function( response ){ messagesSentDialog( 'Message "' + response + '" was sent' ) },
                     error    : function()          { messagesSentDialog( 'Failed to send the message' ) }
                    });

            return false;
        });
    })
</script>

<div id="messages-sent" title="Message Sent" style="display:none; overflow:hidden;">
	<p>
		<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
        <span  id="messages-sent-text"></span>
        <input id="messages-sent-button" type="button" value=" Ok "/>
	</p>
</div>

<div class="settingsBlock" style="">
    <div style="background-color:#fff; padding: 10px;">
        <form action="messagesSend.html" method="post" id="messages-form">

            <p>
                <label for="messages-urgency">Urgency: </label>
                <select name="urgency" id="messages-urgency">
                    <option selected="selected">Info</option>
                    <option>Warning</option>
                    <option>Critical</option>
                </select>
            </p>

            <p>
                <label for="messages-longevity-number">Keep For: </label>
                <input class="textfield1" id="messages-longevity-number" name="longevity-number" type="text" size="3"
                       maxlength="3" value="7">
                <select id="messages-longevity-unit" name="longevity-unit">
                    <option>hours</option>
                    <option selected="selected">days</option>
                    <option>weeks</option>
                    <option>months</option>
                </select>
            </p>

            <p><label for="messages-message">Message: <span class="mandatoryAsterix" title="Mandatory field">*</span></label>
                <textarea class="textfield" id="messages-message" name="message" cols="10" rows="8" value=""></textarea>
                <span class="error" id="messages-errormessage" style="margin-left: 10.5em;"></span>
            </p>

            <p><label for="messages-all">Send to All:</label>
                <input style="margin:0" type="checkbox" id="messages-all" name="all" checked="checked">
            </p>

            <p>
                <label for="messages-groups">Send to Groups:</label>
                <select id="messages-groups" name="groups" multiple="multiple" size="2" disabled="disabled">
                <c:forEach items="${groups}" var="group">
                    <option>${group}</option>
                </c:forEach>
                </select>
            </p>

            <p>
                <label for="messages-users">Send to Users:</label>
                <select id="messages-users" name="users" multiple="multiple" size="2" disabled="disabled">
                <c:forEach items="${users}" var="user">
                    <option>${user}</option>
                </c:forEach>
                </select>
                <span class="error" id="messages-errorselection" style="margin-left: 10.5em;"></span>
            </p>

            <p>
                <input type="submit" value="Send" id="messages-go" name="go">
            </p>

        </form>
    </div>
</div>
