package com.vivid.docker;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.util.regex.Pattern;


@Extension
public class RemoveContainerPostBuildStepDescriptor extends BuildStepDescriptor<Publisher> {
    private static final Pattern VARIABLE = Pattern.compile("\\$([A-Za-z0-9_]+|\\{[A-Za-z0-9_.]+\\}|\\$)");

    public RemoveContainerPostBuildStepDescriptor() {
        super(RemoveContainerPostBuildStep.class);
        load();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        return true;
    }

    public String getDisplayName() {
        return "Remove Docker Container";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        save();
        return super.configure(req,formData);
    }

    public boolean containsVariables(String value) {
        return VARIABLE.matcher(value).find();
    }

    private boolean isValid(String value, Pattern pattern) {
        return value == null || pattern.matcher(value).find();
    }
}
