package com.vivid.docker.argument;

import hudson.util.ArgumentListBuilder;

/**
 * Created by Phil Madden on 9/10/15.
 */
public class RemoveContainerCommandArgumentBuilder extends ImageArgumentBuilder<RemoveContainerCommandArgumentBuilder> {

    private String containerId;

    public RemoveContainerCommandArgumentBuilder() {
        super("rm");
    }

    public final RemoveContainerCommandArgumentBuilder containerId(String containerId) {
        this.containerId = containerId;
        return this;
    }

    public final RemoveContainerCommandArgumentBuilder forceStop(boolean forceStop) {
        if(forceStop) {
            argumentListBuilder.add("--force");
        }

        return this;
    }

    public final ArgumentListBuilder build() {
        argumentListBuilder.addTokenized(containerId);
        return argumentListBuilder;
    }

}
