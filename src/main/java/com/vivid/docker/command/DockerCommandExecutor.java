package com.vivid.docker.command;

import com.vivid.docker.argument.ArgumentBuilder;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

import java.io.IOException;

/**
 * Created by Phil Madden on 9/17/15.
 */
public class DockerCommandExecutor extends AbstractCommandExecutor {

    public DockerCommandExecutor(ArgumentBuilder argumentBuilder, EnvVars environment) {
        super(argumentBuilder, environment);
    }

    @Override
    protected int executeCommand(AbstractBuild build, Launcher launcher, BuildListener listener, boolean quiet) {
        try {
            RESULT = launcher.launch()
                    .cmds(getArgumentBuilder().build())
                    .envs(getEnvironment())
                    .stderr(listener.getLogger())
                    .stdout(listener.getLogger())
                    .quiet(quiet)
                    .pwd(build.getWorkspace())
                    .join();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return RESULT;
    }

}
