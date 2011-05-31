package jetbrains.sample.extensions.buildQueuePause;

import java.util.Map;
import jetbrains.buildServer.BuildAgent;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.buildDistribution.BuildDistributorInput;
import jetbrains.buildServer.serverSide.buildDistribution.QueuedBuildInfo;
import jetbrains.buildServer.serverSide.buildDistribution.SimpleWaitReason;
import jetbrains.buildServer.serverSide.buildDistribution.WaitReason;
import jetbrains.buildServer.users.SUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Yegor.Yarko
 *         Date: 10.03.2009
 */
public class StartBuildPrecondition implements jetbrains.buildServer.serverSide.buildDistribution.StartBuildPrecondition {
  private boolean myQueuePaused = false;
  private @Nullable SUser myUser;

  public WaitReason canStart(@NotNull final QueuedBuildInfo queuedBuild,
                             @NotNull final Map<QueuedBuildInfo, BuildAgent> canBeStarted,
                             @NotNull final BuildDistributorInput buildDistributorInput,
                             final boolean emulationMode) {
    if (myQueuePaused) {
      return new SimpleWaitReason("Queue is paused.");
    } else {
      return null;
    }
  }

  public void setQueuePaused(boolean isPaused, @Nullable final SUser user) {
    myQueuePaused = isPaused;
    myUser = user;
    Loggers.SERVER.info("Build queue is " + (isPaused ? "paused" : "resumed") + getPresentableActionDetails() + ".");
  }

  public boolean isQueuePaused() {
    return myQueuePaused;
  }

  @NotNull
  private String getPresentableActionDetails() {
    if (myUser == null) {
      return "";
    }
    return "by " + myUser.getDescriptiveName();
  }

  @Nullable
  public SUser getUser() {
    return myUser;
  }
}
