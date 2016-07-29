package com.vivid.docker.argument;

public class DockerHubInteractionArgumerntBuilder<T> extends ArgumentBuilder {

    protected DockerHubInteractionArgumerntBuilder(String command) {
        super(command);
    }

    public final T disableContentTrust(boolean value) {
        if (value) {
            argumentListBuilder.add("--disable-content-trust");
        }
        return (T) this;
    }
}
