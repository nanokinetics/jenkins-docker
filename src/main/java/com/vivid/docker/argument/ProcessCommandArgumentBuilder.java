package com.vivid.docker.argument;

import hudson.util.ArgumentListBuilder;

/**
 * Created by Phil Madden on 9/10/15.
 */
public class ProcessCommandArgumentBuilder extends ImageArgumentBuilder<ProcessCommandArgumentBuilder> {

    public ProcessCommandArgumentBuilder() {
        super("ps");
    }

    public final ProcessCommandArgumentBuilder all(boolean value) {
        if(value) {
            argumentListBuilder.add("--all");
        }
        return this;
    }

    public final ProcessCommandArgumentBuilder formart(String value) {
        if(isNotEmpty(value)) {
            argumentListBuilder.addKeyValuePair("--", "format", value, false);
        }
        return this;
    }

    public final ProcessCommandArgumentBuilder filters(String... filters) {
        if(isNotEmpty(filters)) {
            for(String filter : filters) {
                argumentListBuilder.addKeyValuePair("--", "filter", filter, false);
            }
        }
        return this;
    }

    @Override
    public ArgumentListBuilder build() {
        return argumentListBuilder;
    }

}
