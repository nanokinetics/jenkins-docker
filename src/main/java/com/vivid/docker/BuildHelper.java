package com.vivid.docker;

import jenkins.model.*;

public class BuildHelper {

    public static final String getDockerBinary() {
       return getBuildImageBuildStepDescriptor().getDockerBinary();
    }

    public static final String getDockerHost() {
        return getBuildImageBuildStepDescriptor().getDockerHost();
    }

    private static BuildImageBuildStepDescriptor getBuildImageBuildStepDescriptor() {
        return (BuildImageBuildStepDescriptor) Jenkins.getInstance().getDescriptor(BuildImageBuildStep.class);
    }
}
