package com.vivid.docker.command;

import com.vivid.docker.argument.ArgumentBuilder;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

/**
 * Created by Phil Madden on 9/17/15.
 */
public abstract class AbstractCommandExecutor {

    protected final int SUCCESS = 0;
    protected int RESULT = 1;
    private final ArgumentBuilder argumentBuilder;
    private final EnvVars environment;

    protected AbstractCommandExecutor(ArgumentBuilder argumentBuilder, EnvVars environment) {
        this.argumentBuilder = argumentBuilder;
        this.environment = environment;
    }

    protected abstract int executeCommand(AbstractBuild build, Launcher launcher, BuildListener listener, boolean quiet);

    public boolean execute(AbstractBuild build, Launcher launcher, BuildListener listener) {
        return executeCommand(build, launcher, listener, false) == SUCCESS;
    }

    public boolean executeQuiet(AbstractBuild build, Launcher launcher, BuildListener listener) {
        return executeCommand(build, launcher, listener, true) == SUCCESS;
    }

    public EnvVars getEnvironment() {
        return environment;
    }

    public ArgumentBuilder getArgumentBuilder() {
        return argumentBuilder;
    }
}
