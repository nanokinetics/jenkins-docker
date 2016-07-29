package com.vivid.docker;

import com.vivid.docker.argument.*;
import com.vivid.docker.command.*;
import com.vivid.docker.exception.*;
import com.vivid.docker.util.*;
import hudson.*;
import hudson.model.*;
import org.kohsuke.stapler.*;

public class PushImagePostBuildStep extends DockerPostBuildStep {

    private final String image;
    private final String tag;
    private final String buildTrigger;
    private final boolean disableContentTrust;
    private final boolean fail;

    @DataBoundConstructor
    public PushImagePostBuildStep(String image,
                                  String tag,
                                  String buildTrigger,
                                  boolean disableContentTrust,
                                  boolean fail) {
        this.image = image;
        this.tag = tag;
        this.disableContentTrust = disableContentTrust;
        this.fail = fail;
        this.buildTrigger = buildTrigger;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        if (shouldTriggerBuild(build)) {
            try {
                EnvVars environment = getEnvironment(build, listener);

                String tagName = FieldUtil.getMacroReplacedFieldValue(tag, environment);

                PushCommandArgumentBuilder pushCommandArgumentBuilder = new PushCommandArgumentBuilder()
                        .disableContentTrust(disableContentTrust)
                        .image(String.format("%s:%s", image, tagName));

                listener.getLogger().append(String.format("Pushing \"%s:%s\" to Docker Hub.", image, tagName));

                DockerCommandExecutor command = new DockerCommandExecutor(pushCommandArgumentBuilder, environment);
                return command.execute(build, launcher, listener);

            } catch (EnvironmentConfigurationException e) {
                listener.getLogger().append(e.getMessage());
                return false;
            }
        }
        return true;
    }

    private boolean shouldTriggerBuild(AbstractBuild build) {
        switch (buildTrigger) {
            case "SUCCESS":
                if (build.getResult() == Result.SUCCESS || build.getResult() == Result.UNSTABLE) {
                    return true;
                }
            case "FAILURE":
                return build.getResult() == Result.FAILURE;
        }
        return false;
    }

    public String getImage() {
        return image;
    }

    public String getTag() {
        return tag;
    }

    public boolean isFail() {
        return fail;
    }

    public boolean isDisableContentTrust() {
        return disableContentTrust;
    }

    public String getBuildTrigger() {
        return buildTrigger;
    }
}

