package com.vivid.docker;

import com.vivid.docker.helper.*;
import hudson.*;
import hudson.model.*;
import jenkins.model.*;
import org.apache.commons.lang.*;
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
        return false;
    }

    @Override
    public boolean runPerform(AbstractBuild<?, ?> abstractBuild, BuildListener buildListener) throws Exception {
        return checkImageExists(abstractBuild, buildListener);
    }

    private boolean checkImageExists(AbstractBuild<?, ?> abstractBuild, BuildListener buildListener) {
        Launcher launcher = Jenkins.getInstance().createLauncher(buildListener);
        try {

            EnvVars envVars = abstractBuild.getEnvironment(buildListener);
            String imageTag = FieldHelper.getMacroReplacedFieldValue(tag, envVars).toLowerCase();
            String imageVariant = FieldHelper.getMacroReplacedFieldValue(variant, envVars).toLowerCase();

            try (PipedInputStream pipedInputStream = new PipedInputStream();
                 PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(pipedInputStream))) {

                launcher.launch()
                        .cmdAsSingleString(String.format("%s images %s:%s", BuildHelper.getDockerBinary(), imageTag, imageVariant))
                        .envs(envVars)
                        .quiet(true)
                        .stdout(pipedOutputStream)
                        .join();


                String containerId = reader.readLine();
                if (StringUtils.isNotEmpty(containerId)) {
                    return true;
                } else {
                    return false;
                }

            } catch (IOException e) {
                launcher.getListener().fatalError(String.format("Error: %s\n", e.getMessage()));
            }

        } catch (IOException | InterruptedException e) {
            launcher.getListener().fatalError(String.format("Error: %s\n", e.getMessage()));
        }

        return false;
    }

}
