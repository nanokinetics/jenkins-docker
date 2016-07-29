package com.vivid.docker;

import hudson.*;
import hudson.model.*;
import hudson.tasks.*;
import hudson.util.*;
import net.sf.json.*;
import org.apache.commons.lang.*;
import org.kohsuke.stapler.*;

import java.util.regex.*;


@Extension
public class PushImagePostBuildStepDescriptor extends BuildStepDescriptor<Publisher> {
    private static final Pattern VARIABLE = Pattern.compile("\\$([A-Za-z0-9_]+|\\{[A-Za-z0-9_.]+\\}|\\$)");
    private static final Pattern IMAGE_NAME_PATTERN = Pattern.compile("^[a-z\\d_\\-]+/{1}[a-z\\d_\\-]+$|^[a-z\\d_\\-]+$");
    private static final Pattern ID_PATTERN = Pattern.compile("[a-z\\d_\\-]+");

    public PushImagePostBuildStepDescriptor() {
        super(PushImagePostBuildStep.class);
        load();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        return true;
    }

    public String getDisplayName() {
        return "Push Docker Image";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        save();
        return super.configure(req,formData);
    }

    public FormValidation doCheckImage(@QueryParameter(fixEmpty = true) String value) {
        if(StringUtils.isEmpty(value)) {
            return FormValidation.warning("Must specify a value.");
        }

        if(isValid(value, IMAGE_NAME_PATTERN)) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("Formatting: [user/]<image_name>");
        }
    }

    public FormValidation doCheckTag(@QueryParameter(required = true) String value) {
        if(containsVariables(value)) {
            return FormValidation.warning("Unable to validate image tag as it contains a variable");
        }
        if(isValid(value, ID_PATTERN)) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("Image tags may only container letters, numbers hyphens and underscores. i.e. my-user_1234");
        }
    }

    public boolean containsVariables(String value) {
        return VARIABLE.matcher(value).find();
    }

    private boolean isValid(String value, Pattern pattern) {
        return value == null || pattern.matcher(value).find();
    }
}
