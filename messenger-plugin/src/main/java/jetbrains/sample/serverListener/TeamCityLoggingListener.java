package jetbrains.sample.serverListener;

import com.intellij.util.containers.HashMap;
import java.util.*;
import jetbrains.buildServer.Build;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.User;
import jetbrains.buildServer.vcs.VcsModification;
import jetbrains.buildServer.vcs.VcsRoot;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import org.jetbrains.annotations.Nullable;


/**
 * Sample listener of server events
 */
public class TeamCityLoggingListener extends BuildServerAdapter {

  private final List<String> myLog = new ArrayList<String>();
  private final Map<String, List<String>> myConfigurationLog = new HashMap<String, List<String>>();
  private final SBuildServer myBuildServer;

  public TeamCityLoggingListener(SBuildServer sBuildServer) {

    myBuildServer = sBuildServer;
  }

  public void register() {
    myBuildServer.addListener(this);
  }

  @Override
  public void agentRegistered(SBuildAgent sBuildAgent, long l) {
    addToLog("Agent " + sBuildAgent.getName() + " registered");
  }

  @Override
  public void agentUnregistered(SBuildAgent sBuildAgent) {
    addToLog("Agent " + sBuildAgent.getName() + " unregistered");
  }

  @Override
  public void agentRemoved(SBuildAgent sBuildAgent) {
    addToLog("Agent " + sBuildAgent.getName() + " removed");
  }

  @Override
  public void buildTypeAddedToQueue(SBuildType buildTypeDescriptor) {
    addToLog("Configuration  " + buildTypeDescriptor.getFullName() + " added to queue", buildTypeDescriptor.getBuildTypeId());
  }

  @Override
  public void buildRemovedFromQueue(@NotNull SQueuedBuild queuedBuild, User user, String comment) {
    SBuildType buildType = queuedBuild.getBuildType();
    addToLog("Configuration  " + buildType.getFullName() + " removed from queue", buildType.getBuildTypeId());
  }

  @Override
  public void buildQueueOrderChanged() {
    addToLog("Build configurations order changed");
  }

  @Override
  public void buildTypeRegistered(SBuildType buildTypeDescriptor) {
    addToLog("Configuration  " + buildTypeDescriptor.getFullName() + " registered", buildTypeDescriptor.getBuildTypeId());
  }

  @Override
  public void buildTypeUnregistered(SBuildType buildTypeDescriptor) {
    addToLog("Configuration  " + buildTypeDescriptor.getFullName() + " unregistered", buildTypeDescriptor.getBuildTypeId());
  }

  @Override
  public void buildTypeActiveStatusChanged(SBuildType buildTypeDescriptor) {
    addToLog("Configuration  " + buildTypeDescriptor.getFullName() + (buildTypeDescriptor.isPaused() ? " paused" : " unpaused"),
             buildTypeDescriptor.getBuildTypeId());
  }

  @Override
  public void buildStarted(SRunningBuild sRunningBuild) {
    addToLog("Build " + sRunningBuild.getFullName() + " started", sRunningBuild);
  }

  @Override
  public void changesLoaded(SRunningBuild sRunningBuild) {
    addToLog("Changes loaded for build " + sRunningBuild.getFullName(), sRunningBuild);
  }

  @Override
  public void buildChangedStatus(SRunningBuild sRunningBuild, Status status, Status status1) {
    addToLog("Build " + sRunningBuild.getFullName() + " changed status", sRunningBuild);
  }

  @Override
  public void buildFinished(SRunningBuild sRunningBuild) {
    addToLog("Build " + sRunningBuild.getFullName() + " finished", sRunningBuild);
  }

  @Override
  public void beforeBuildFinish(SRunningBuild sRunningBuild) {
    addToLog("Build " + sRunningBuild.getFullName() + " is going to finish", sRunningBuild);
  }

  @Override
  public void responsibleChanged(@NotNull SBuildType sBuildType,
                                 @NotNull ResponsibilityInfo responsibilityInfo,
                                 @NotNull ResponsibilityInfo responsibilityInfo1,
                                 boolean b) {
    addToLog("Responsible changed for " + sBuildType.getFullName(), sBuildType.getBuildTypeId());
  }

  @Override
  public void entryDeleted(SFinishedBuild sFinishedBuild) {
    addToLog("Build " + sFinishedBuild.getFullName() + " deleted", sFinishedBuild);
  }

  @Override
  public void projectCreated(String s) {
    addToLog("Project " + s + " created");
  }

  @Override
  public void projectRemoved(String s) {
    addToLog("Project " + s + " removed");
  }

  @Override
  public void buildInterrupted(SRunningBuild sRunningBuild) {
    addToLog("Build " + sRunningBuild.getFullName() + " interrupted", sRunningBuild);
  }

  @Override
  public void changeAdded(@NotNull final VcsModification modification,
                          @NotNull final VcsRoot root,
                          @Nullable final Collection<SBuildType> buildTypes) {
    addToLog("Change added to " + root.getName());
  }

  @Override
  public void agentStatusChanged(@NotNull SBuildAgent sBuildAgent, boolean wasEnabled, final boolean wasAuthorized) {
    addToLog("Agent " + sBuildAgent.getName() + " changed status");
  }

  private void addToLog(String message) {
    synchronized (myLog) {
      addToLog(myLog, message);
    }
  }

  private void addToLog(String message, Build build) {
    addToLog(message, build.getBuildTypeId());
  }

  private void addToLog(String message, String buildConfigId) {
    synchronized (myConfigurationLog) {
      if (!myConfigurationLog.containsKey(buildConfigId)) {
        myConfigurationLog.put(buildConfigId, new ArrayList<String>());
      }
      addToLog(myConfigurationLog.get(buildConfigId), message);
    }
    addToLog(message);
  }

  private void addToLog(List<String> log, String message) {
    while (log.size() > 9) {
      log.remove(log.size() - 1);
    }
    log.add(0, DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()) + ": " + message);
  }

  public List<String> getMessages() {
    synchronized (myLog) {
      return new ArrayList<String>(myLog);
    }
  }

  public boolean hasLogFor(SBuildType buildType) {
    synchronized (myConfigurationLog) {
      return myConfigurationLog.get(buildType.getBuildTypeId()) != null;
    }
  }

  public List<String> getLogFor(SBuildType buildType) {
    synchronized (myConfigurationLog) {
      return new ArrayList<String>(myConfigurationLog.get(buildType.getBuildTypeId()));
    }
  }
}
