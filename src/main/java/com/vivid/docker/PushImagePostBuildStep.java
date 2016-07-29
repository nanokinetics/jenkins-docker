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
    private final boolean disableContentTrust;
    private final boolean fail;

    @DataBoundConstructor
    public PushImagePostBuildStep(String image,
                                  String tag,
                                  boolean disableContentTrust,
                                  boolean fail) {
        this.image = image;
        this.tag = tag;
        this.disableContentTrust = disableContentTrust;
        this.fail = fail;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
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

    public String getImage() {
        return image;
    }

    public String getTag() {
        return tag;
    }

    public boolean isFail() {
        return fail;
    }
}

