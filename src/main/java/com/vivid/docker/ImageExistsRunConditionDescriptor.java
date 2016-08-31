package com.vivid.docker;

import hudson.*;
import org.jenkins_ci.plugins.run_condition.*;

@Extension
public class ImageExistsRunConditionDescriptor extends RunCondition.RunConditionDescriptor {

    public ImageExistsRunConditionDescriptor() {
        super(ImageExistsRunCondition.class);
    }

    @Override
    public String getDisplayName() {
        return "Docker Image Exists";
    }
}
