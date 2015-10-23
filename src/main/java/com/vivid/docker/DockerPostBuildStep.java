package com.vivid.docker;

import com.vivid.docker.exception.EnvironmentConfigurationException;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.*;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * Created by Phil Madden on 9/17/15.
 */
public class DockerPostBuildStep extends Recorder {

    protected static final int SUCCESS = 0;

    public BuildImageBuildStepDescriptor getDockerConfigurationDescriptor() {
        return (BuildImageBuildStepDescriptor) Jenkins.getInstance().getDescriptor(BuildImageBuildStep.class);
    }

    public EnvVars getEnvironment(AbstractBuild build, BuildListener listener) throws EnvironmentConfigurationException {
        try {
            EnvVars envVars = build.getEnvironment(listener);
            if(StringUtils.isNotBlank(getDockerConfigurationDescriptor().getDockerHost())) {
                envVars.put("DOCKER_HOST", getDockerConfigurationDescriptor().getDockerHost());
            }
            return envVars;
        } catch (IOException e) {
            throw new EnvironmentConfigurationException(e);
        } catch (InterruptedException e) {
            throw new EnvironmentConfigurationException(e);
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
}
