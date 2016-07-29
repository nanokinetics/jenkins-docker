package com.vivid.docker.argument;

import hudson.util.*;

public class PushCommandArgumentBuilder extends DockerHubInteractionArgumerntBuilder<PushCommandArgumentBuilder> {
    private String image;

    public PushCommandArgumentBuilder() {
        super("push");
    }

    public final PushCommandArgumentBuilder image(String image) {
        this.image = image;
        return this;
    }

    @Override
    public ArgumentListBuilder build() {
        argumentListBuilder.add(image);
        return argumentListBuilder;
    }
}
