package jetbrains.sample.extensions.buildQueuePause;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import jetbrains.buildServer.web.util.WebUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Yegor.Yarko
 *         Date: 10.03.2009
 */
public class BuildQueuePauseButton extends SimplePageExtension {
  private StartBuildPrecondition myStartBuildPrecondition;

  public BuildQueuePauseButton(PagePlaces pagePlaces) {
    super(pagePlaces);
  }

  @Override
  public boolean isAvailable(@NotNull final HttpServletRequest request) {
    return WebUtil.getPathWithoutAuthenticationType(request).startsWith("/queue.html");
  }

  @Override
  public void fillModel(@NotNull final Map<String, Object> model, @NotNull final HttpServletRequest request) {
    model.put("buildQueuePauser", myStartBuildPrecondition);
  }

  public void setStartBuildPrecondition(final StartBuildPrecondition startBuildPrecondition) {
    myStartBuildPrecondition = startBuildPrecondition;
  }
}
