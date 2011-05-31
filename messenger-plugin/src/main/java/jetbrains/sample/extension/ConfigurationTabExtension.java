package jetbrains.sample.extension;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.openapi.buildType.BuildTypeTab;
import jetbrains.sample.serverListener.TeamCityLoggingListener;
import org.jetbrains.annotations.NotNull;

public class ConfigurationTabExtension extends BuildTypeTab {
  private final TeamCityLoggingListener myListener;

  public ConfigurationTabExtension(WebControllerManager manager, TeamCityLoggingListener listener, ProjectManager projectManager) {
    super("samplePlugin", "Sample Extension", manager, projectManager);
    myListener = listener;
  }

  @Override
  public boolean isAvailable(@NotNull HttpServletRequest request) {
    SBuildType buildType = getBuildType(request);
    return buildType != null && super.isAvailable(request) && myListener.hasLogFor(buildType);
  }

  @Override
  protected void fillModel(Map model, HttpServletRequest request, @NotNull SBuildType buildType, SUser user) {
    model.put("messages", myListener.getLogFor(buildType));
  }
}
