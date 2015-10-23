package com.vivid.docker.argument;

import com.vivid.docker.BuildImageBuildStepDescriptor;
import com.vivid.docker.BuildImageBuildStep;
import hudson.util.ArgumentListBuilder;
import jenkins.model.Jenkins;

/**
 * Created by Phil Madden on 9/10/15.
 */
public class ArgumentBuilder {
    protected final ArgumentListBuilder argumentListBuilder = new ArgumentListBuilder();

    protected ArgumentBuilder(String command) {
        argumentListBuilder.addTokenized(((BuildImageBuildStepDescriptor) Jenkins.getInstance().getDescriptor(BuildImageBuildStep.class)).getDockerBinary() + " " + command);
    }

    protected final String wrapInQuotes(Object value) {
        return "\"" + value + "\"";
    }

    protected final String stripWhitespace(String value) {
        if(isNotEmpty(value)) {
            return value.replaceAll("\\s", "");
        }
        return value;
    }

    protected final boolean isNotEmpty(Object[] value) {
        return value != null && value.length > 0;
    }

    protected final boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public ArgumentListBuilder build() {
        return argumentListBuilder;
    }

    @Override
    public String toString() {
        return argumentListBuilder.toString();
    }
}
