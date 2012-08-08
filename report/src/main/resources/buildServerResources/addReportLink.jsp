<%@ include file="/include.jsp" %>

<c:url value = '${action}' var = 'reportUrl'/>

<script type="text/javascript">
    ( function( j ){
        j( function()
        {
            j( 'ul.tabs' ).append( j( 'div#reportLink' ).children())
        });
    })( jQuery );
</script>

<div style="display: none;" id="reportLink">
    <li id="reportTab" class="last leftBorder">
        <p><a class="tabs" href="${reportUrl}">Report</a></p>
    </li>
</div>
