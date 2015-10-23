package com.vivid.docker.argument;

import hudson.util.ArgumentListBuilder;

import java.io.File;

/**
 * Created by Phil Madden on 9/10/15.
 */
public class RunCommandArgumentBuilder extends ImageArgumentBuilder<RunCommandArgumentBuilder> {

    private String image;
    private String command;
    private String[] commandArguments;

    public RunCommandArgumentBuilder() {
        super("run");
    }

    public final RunCommandArgumentBuilder image(String image) {
        this.image = image;
        return this;
    }

    public final RunCommandArgumentBuilder name(String name) {
        if(isNotEmpty(name)) {
            argumentListBuilder.addKeyValuePair("--", "name", wrapInQuotes(stripWhitespace(name.toLowerCase())), false);
        }
        return this;
    }

    public final RunCommandArgumentBuilder user(String user) {
        if(isNotEmpty(user)) {
            argumentListBuilder.addKeyValuePair("--", "user", wrapInQuotes(stripWhitespace(user)), false);
        }
        return this;
    }

    public final RunCommandArgumentBuilder cidFile(File cidFile) {
        if(cidFile != null) {
            if(!cidFile.exists()) {
                cidFile.mkdirs();
            }
            argumentListBuilder.addKeyValuePair("--", "cidfile", wrapInQuotes(cidFile.getAbsolutePath()), false);
        }
        return this;
    }

    public final RunCommandArgumentBuilder command(String command) {
        this.command = command;
        return this;
    }

    public final RunCommandArgumentBuilder commandArguments(String... commandArguments) {
        this.commandArguments = commandArguments;
        return this;
    }

    public final RunCommandArgumentBuilder environmentVariables(String... environmentVariables) {
        if(isNotEmpty(environmentVariables)) {
            for(String environmentVariable : environmentVariables) {
                argumentListBuilder.addKeyValuePair("--", "env", wrapInQuotes(environmentVariable), false);
            }
        }
        return this;
    }

    public final RunCommandArgumentBuilder labels(String... labels) {
        if(isNotEmpty(labels)) {
            for(String label : labels) {
                argumentListBuilder.addKeyValuePair("--", "label", wrapInQuotes(label), false);
            }
        }
        return this;
    }

    public final RunCommandArgumentBuilder volumes(String... volumes) {
        if(isNotEmpty(volumes)) {
            for(String volume : volumes) {
                argumentListBuilder.addKeyValuePair("--", "volume", wrapInQuotes(volume), false);
            }
        }
        return this;
    }

    public final RunCommandArgumentBuilder links(String... links) {
        if(isNotEmpty(links)) {
            for(String link : links) {
                argumentListBuilder.addKeyValuePair("--", "link", wrapInQuotes(link.toLowerCase()), false);
            }
        }
        return this;
    }

    public final RunCommandArgumentBuilder expose(String... exposedPorts) {
        if(isNotEmpty(exposedPorts)) {
            for(String environmentVariable : exposedPorts) {
                argumentListBuilder.addKeyValuePair("--", "expose", wrapInQuotes(environmentVariable), false);
            }
        }
        return this;
    }

    public final RunCommandArgumentBuilder publishPorts(Integer... publishPorts) {
        if(isNotEmpty(publishPorts)) {
            for(Integer port : publishPorts) {
                argumentListBuilder.addKeyValuePair("--", "publish", Integer.toString(port), false);
            }
        }
        return this;
    }

    public final RunCommandArgumentBuilder workingDirectory(String workingDirectory) {
        if(isNotEmpty(workingDirectory)) {
            argumentListBuilder.addKeyValuePair("--", "workdir", wrapInQuotes(workingDirectory), false);
        }
        return this;
    }

    public final RunCommandArgumentBuilder volumeDriver(String volumeDriver) {
        if(isNotEmpty(volumeDriver)) {
            argumentListBuilder.addKeyValuePair("--", "volume-driver", wrapInQuotes(volumeDriver), false);
        }
        return this;
    }

    public final RunCommandArgumentBuilder pseudoTTY(boolean value) {
        if(value) {
            argumentListBuilder.add("--tty");
        }
        return this;
    }

    public final RunCommandArgumentBuilder remove(boolean value) {
        if(value) {
            argumentListBuilder.add("--rm");
        }
        return this;
    }

    public final RunCommandArgumentBuilder privileged(boolean value) {
        if(value) {
            argumentListBuilder.add("--privileged");
        }
        return this;
    }

    public final RunCommandArgumentBuilder readOnly(boolean value) {
        if(value) {
            argumentListBuilder.add("--read-only");
        }
        return this;
    }

    public final RunCommandArgumentBuilder detach(boolean value) {
        if(value) {
            argumentListBuilder.add("--detach");
        }
        return this;
    }

    public final RunCommandArgumentBuilder disbaleContentTrust(boolean value) {
        if(value) {
            argumentListBuilder.add("--disable-content-trust");
        }
        return this;
    }

    public final RunCommandArgumentBuilder publishAllPorts(boolean value) {
        if(value) {
            argumentListBuilder.add("--publish-all");
        }
        return this;
    }

    @Override
    public ArgumentListBuilder build() {
        argumentListBuilder.add(image);
        if(isNotEmpty(command)) {
            argumentListBuilder.addTokenized(command);
            if(isNotEmpty(commandArguments)) {
                argumentListBuilder.add(commandArguments);
            }
        }
        return argumentListBuilder;
    }

}
