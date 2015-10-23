package com.vivid.docker;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.regex.Pattern;

@Extension
public class BuildImageBuildStepDescriptor extends BuildStepDescriptor<Builder> {
    private static final Pattern NUMBERS_ONLY_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern VARIABLE = Pattern.compile("\\$([A-Za-z0-9_]+|\\{[A-Za-z0-9_.]+\\}|\\$)");
    private static final Pattern ID_PATTERN = Pattern.compile("[a-z\\d_\\-]+");
    private static final Pattern IMAGE_NAME_PATTERN = Pattern.compile("^[a-z\\d_\\-]+/{1}[a-z\\d_\\-]+$|^[a-z\\d_\\-]+$");
    private static final Pattern CPU_CONSTRAINT_PATTERN = Pattern.compile("^\\d+-{0,1}\\d*$|^[\\d,]+[^,]$");
    private static final Pattern MEMORY_PATTERN = Pattern.compile("^\\d+[b|k|m|g]{0,1}$");


    private String dockerHost;
    private String dockerBinary;

    public BuildImageBuildStepDescriptor() {
        super(BuildImageBuildStep.class);
        load();
    }
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        return true;
    }

    public String getDisplayName() {
        return "Build Docker Image";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        dockerHost = formData.getString("dockerHost");
        dockerBinary = formData.getString("dockerBinary");
        save();
        return super.configure(req,formData);
    }

    public boolean isUsingDockerHost() {
        return dockerHost != null && !dockerHost.isEmpty();
    }

    public String getDockerHost() {
        return dockerHost;
    }

    public String getDockerBinary() {
        return dockerBinary;
    }

    public FormValidation doCheckDockerFile(@QueryParameter(fixEmpty = true) String value) {
        return FormValidation.ok();
    }

    public FormValidation doCheckName(@QueryParameter(fixEmpty = true) String value) {
        if(StringUtils.isEmpty(value)) {
            return FormValidation.warning("Must specify a value.");
        }

        if(containsVariables(value)) {
            return FormValidation.warning("Unable to validate image name as it contains a variable");
        }
        if(isValid(value, IMAGE_NAME_PATTERN)) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("Formatting: [user/]<image_name>");
        }
    }

    public FormValidation doCheckTag(@QueryParameter(required = true) String value) {
        if(StringUtils.isEmpty(value)) {
            return FormValidation.warning("Must specify a value.");
        }

        if(containsVariables(value)) {
            return FormValidation.warning("Unable to validate image tag as it contains a variable");
        }
        if(isValid(value, ID_PATTERN)) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("Image tags may only container letters, numbers hyphens and underscores. i.e. my-user_1234");
        }
    }

    public FormValidation doCheckCpuConstraint(@QueryParameter(fixEmpty = true) String value) {
        if (isValid(value, CPU_CONSTRAINT_PATTERN)) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("Valid formats are X,Y or X-Y where X and Y are unique numbers in the range of 0-9.");
        }
    }

    public FormValidation doCheckCpuShares(@QueryParameter(fixEmpty = true) String value) {
        if (isValid(value, NUMBERS_ONLY_PATTERN)) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("Value must be a number.");
        }
    }

    public FormValidation doCheckCpuPeriod(@QueryParameter(fixEmpty = true) String value) {
        if (isValid(value, NUMBERS_ONLY_PATTERN)) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("Value must be a number.");
        }
    }

    public FormValidation doCheckCpuQuota(@QueryParameter(fixEmpty = true) String value) {
        if (isValid(value, NUMBERS_ONLY_PATTERN)) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("Value must be a number.");
        }
    }

    public FormValidation doCheckMemoryLimit(@QueryParameter(fixEmpty = true) String value) {
        if(isValid(value, MEMORY_PATTERN)) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("Value must be in the format of <number>[unit] where unit is either b, k, m or g.");
        }
    }

    public FormValidation doCheckMemorySwap(@QueryParameter(fixEmpty = true) String value) {
        if(isValid(value, MEMORY_PATTERN)) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("Value must be in the format of <number>[unit] where unit is either b, k, m or g.");
        }
    }

    public boolean containsVariables(String value) {
        return VARIABLE.matcher(value).find();
    }

    private boolean isValid(String value, Pattern pattern) {
        return value == null || pattern.matcher(value).find();
    }
}
