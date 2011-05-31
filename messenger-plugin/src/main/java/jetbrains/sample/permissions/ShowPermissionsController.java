package jetbrains.sample.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

/**
 * Adds to model all proviects available for the current user, lists of global and project permissions.
 * Works after user press "View My Permissions" button on user profile page
 */
public class ShowPermissionsController extends BaseController {
    private final WebControllerManager myWebManager;

    public ShowPermissionsController(SBuildServer server, WebControllerManager webManager) {
        super(server);
        myWebManager = webManager;
    }

    public void register(){
      myWebManager.registerController("/viewPermissions.html", this);
    }

    @Nullable
    protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HashMap params = new HashMap();
        //List of all project permissions
        List<Permission> projectPermissionList = new ArrayList<Permission>();
        for (Permission p: Permission.values()) {
          if (p.isProjectAssociationSupported()) {
            projectPermissionList.add(p);
          }
        }

        //List of all permissions without project ones
        List<Permission> globalPermissionList = new ArrayList<Permission>(Arrays.asList(Permission.values()));
        globalPermissionList.removeAll(projectPermissionList);

        params.put("projects", myServer.getProjectManager().getProjects());
        params.put("projectPermissions", projectPermissionList);
        params.put("globalPermissions", globalPermissionList);
        params.put("userName", SessionUser.getUser(request).getDescriptiveName());

        return new ModelAndView("/plugins/samplePlugin/viewPermissions.jsp", params);
    }
}
