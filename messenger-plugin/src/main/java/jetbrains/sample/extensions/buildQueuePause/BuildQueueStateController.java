package jetbrains.sample.extensions.buildQueuePause;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrains.buildServer.controllers.BaseActionController;
import jetbrains.buildServer.serverSide.CriticalErrors;
import jetbrains.buildServer.util.PropertiesUtil;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.ControllerAction;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Yegor.Yarko
 *         Date: 10.03.2009
 */

public class BuildQueueStateController extends BaseActionController {
  private final StartBuildPrecondition myStartBuildPrecondition;
  private static final String BUILD_QUEUE_PAUSE_PARAMETER_NAME = "newBuildQueuePausedState";
  @NonNls public static final String QUEUE_IS_PAUSED = "buildQueueIsPaused";
  private final CriticalErrors myCriticalErrors;

  public BuildQueueStateController(final WebControllerManager manager,
                                   StartBuildPrecondition startBuildPrecondition,
                                   final CriticalErrors criticalErrors) {
    super(manager);
    myCriticalErrors = criticalErrors;

    manager.registerController("/queuePauser.html", this);

    myStartBuildPrecondition = startBuildPrecondition;
    init();
  }

  private void init() {
    registerAction(new ControllerAction() {
      public boolean canProcess(final HttpServletRequest request) {
        return request.getParameter(BUILD_QUEUE_PAUSE_PARAMETER_NAME) != null;
      }

      public void process(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response, @Nullable final Element ajaxResponse) {
        boolean newPausedState = PropertiesUtil.getBoolean(request.getParameter(BUILD_QUEUE_PAUSE_PARAMETER_NAME));
        myStartBuildPrecondition.setQueuePaused(newPausedState, SessionUser.getUser(request));
        if (newPausedState){
          myCriticalErrors.putError(QUEUE_IS_PAUSED, "Build Queue is paused");
        } else{
          myCriticalErrors.clearError(QUEUE_IS_PAUSED);
        }
      }
    });
  }

  @Override
  protected ModelAndView doHandle(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    doAction(request, response, null);

    String redirectTo = request.getParameter("redirectTo");
    if (StringUtil.isEmpty(redirectTo)) {
      redirectTo = request.getHeader("Referer");
      if (redirectTo == null) return null;
    }

    return new ModelAndView(new RedirectView(redirectTo));
  }
}
