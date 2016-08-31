package com.vivid.docker;

import com.vivid.docker.helper.*;
import hudson.*;
import hudson.model.*;
import jenkins.model.*;
import org.jenkins_ci.plugins.run_condition.*;
import org.kohsuke.stapler.*;

import java.io.*;


public class ImageExistsRunCondition extends RunCondition {
    protected static final int SUCCESS = 0;
    private final String tag;
    private final String variant;

    @DataBoundConstructor
    public ImageExistsRunCondition(String tag,
                                   String variant) {
        this.tag = tag;
        this.variant = variant;
    }

    public String getTag() {
        return tag;
    }

    public String getVariant() {
        return variant;
    }

    @Override
    public boolean runPrebuild(AbstractBuild<?, ?> abstractBuild, BuildListener buildListener) throws Exception {
        return true;
    }

    @Override
    public boolean runPerform(AbstractBuild<?, ?> abstractBuild, BuildListener buildListener) throws Exception {
        EnvVars envVars = abstractBuild.getEnvironment(buildListener);
        String imageTag = FieldHelper.getMacroReplacedFieldValue(tag, envVars).toLowerCase();
        String imageVariant = FieldHelper.getMacroReplacedFieldValue(variant, envVars).toLowerCase();

        Launcher launcher = Jenkins.getInstance().createLauncher(buildListener);

        try {
            int result = launcher.launch()
                    .cmdAsSingleString(BuildHelper.getDockerBinary() + " history " + imageTag + ":" + imageVariant)
                    .quiet(true)
                    .envs(envVars)
                    .join();

            return result == SUCCESS;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

}
