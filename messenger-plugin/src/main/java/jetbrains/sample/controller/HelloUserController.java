package jetbrains.sample.controller;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import jetbrains.sample.serverListener.TeamCityLoggingListener;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Our sample controller
 */
public class HelloUserController extends BaseController {
    private final WebControllerManager myManager;
    private final TeamCityLoggingListener myLoggingListener;

    public HelloUserController(SBuildServer sBuildServer, WebControllerManager manager,
                               TeamCityLoggingListener listener) {
        super(sBuildServer);
        myManager = manager;

        myLoggingListener = listener;
    }

    /**
     * Main method which works after user presses 'Hello' button.
     *
     * @param httpServletRequest  http request
     * @param httpServletResponse http response
     * @return object containing model object and view (page aggress)
     * @throws Exception
     */
    protected ModelAndView doHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        SUser user = SessionUser.getUser(httpServletRequest);
        HashMap params = new HashMap();
        params.put("userName", user.getDescriptiveName());
        params.put("messages", myLoggingListener.getMessages());
        return new ModelAndView("/plugins/samplePlugin/hello.jsp", params);
    }

    public void register() {
        myManager.registerController("/helloUser.html", this);
    }

}
