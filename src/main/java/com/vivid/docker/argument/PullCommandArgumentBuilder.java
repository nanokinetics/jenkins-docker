package com.vivid.docker.argument;

import hudson.util.*;

public class PullCommandArgumentBuilder extends DockerHubInteractionArgumerntBuilder<PushCommandArgumentBuilder> {
    private String image;

    public PullCommandArgumentBuilder() {
        super("pull");
    }

    public final PullCommandArgumentBuilder image(String image) {
        this.image = image;
        return this;
    }

    public final PullCommandArgumentBuilder pullAllTags(boolean value) {
        if (value) {
            argumentListBuilder.add("--all-tags");
        }
        return this;
    }

    @Override
    public ArgumentListBuilder build() {
        argumentListBuilder.add(image);
        return argumentListBuilder;
    }
}
