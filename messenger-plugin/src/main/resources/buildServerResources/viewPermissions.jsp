<%@ include file="/include.jsp" %>
<c:set var="title" value="View Permissions" scope="request"/>
<bs:page>
    <jsp:attribute name="head_include">
      <bs:linkCSS>
        /css/admin/adminMain.css
        /css/admin/projectConfig.css
        /css/forms.css
        /css/admin/vcsRootsTable.css
      </bs:linkCSS>
      <bs:linkScript>
        /js/bs/blocks.js
        /js/bs/blocksWithHeader.js
        /js/bs/forms.js
        /js/bs/modalDialog.js
        /js/bs/editProject.js
        /js/bs/adminActions.js
      </bs:linkScript>

      <script type="text/javascript">
        BS.Navigation.items = [
          {title: "Profile", url: '<c:url value="/profile.html"/>'},
          {title: "${title}", selected:true}
        ];
    
      </script>
    </jsp:attribute>
    
    <jsp:attribute name="body_include">
        <h4>Global permissions:</h4>
        <table class="runnerFormTable">
        <c:forEach items="${globalPermissions}" var="perm">
          <tr>
            <td>${perm}</td>  
              <authz:authorize allPermissions="${perm}">
                <jsp:attribute name="ifAccessGranted">
                <td>+</td>
                </jsp:attribute>
                <jsp:attribute name="ifAccessDenied">
                <td>-</td>
                </jsp:attribute>
              </authz:authorize>            
          </tr>        
        </c:forEach>
        </table>

        <h4>Project permissions:</h4>
        <table class="runnerFormTable">
        <tr>
            <td>&nbsp;</td>
            <c:forEach items="${projects}" var="proj">
                <th>${proj.name}</th>
            </c:forEach>                
        </tr>
            
            <c:forEach items="${projectPermissions}" var="perm">
                <tr>
                
                <td>${perm}</td>
                <c:forEach items="${projects}" var="proj">
                    <authz:authorize projectId="${proj.projectId}" allPermissions="${perm}">
                      <jsp:attribute name="ifAccessGranted">
                      <td>+</td>
                      </jsp:attribute>
                      <jsp:attribute name="ifAccessDenied">
                      <td>-</td>
                      </jsp:attribute>
                    </authz:authorize>
                    
                </c:forEach>
                </tr>                
                
            </c:forEach>                
        </table>
        
    </jsp:attribute>
</bs:page>
