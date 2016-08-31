package com.vivid.docker;


import com.vivid.docker.argument.RemoveContainerCommandArgumentBuilder;
import com.vivid.docker.command.DockerCommandExecutor;
import com.vivid.docker.exception.ContainerRemovalException;
import com.vivid.docker.exception.EnvironmentConfigurationException;
import com.vivid.docker.helper.FieldHelper;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.*;

public class RemoveContainerPostBuildStep extends DockerPostBuildStep {

    private final String id;
    private final String name;
    private final String cidFilePath;
    private final boolean force;
    private final boolean fail;

    @DataBoundConstructor
    public RemoveContainerPostBuildStep(String id,
                                        String name,
                                        String cidFilePath,
                                        String alternativeDockerHost,
                                        boolean force,
                                        boolean fail) {
        super(alternativeDockerHost);
        this.id = id;
        this.name = name;
        this.cidFilePath = cidFilePath;
        this.force = force;
        this.fail = fail;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        try {
            EnvVars environment = getEnvironment(build, listener);

            String containerId;
            String[] containerIds = FieldHelper.tokenize(id, environment);
            String[] containerNames = FieldHelper.tokenize(name, environment);

            if(ArrayUtils.isNotEmpty(containerIds)) {
                for (String container : containerIds) {
                    listener.getLogger().append("Attempting to remove container with id: " + container + ".\n");
                    removeContainerById(container, build, launcher, listener, environment);
                }
            }

            if(ArrayUtils.isNotEmpty(containerNames)) {
                for (String containerName : containerNames) {
                    containerId = getContainerIdFromContainerName(containerName.toLowerCase(), launcher, environment);
                    listener.getLogger().append("Attempting to remove container with name \"" + containerName + "\", id: " + containerId + ".\n");
                    removeContainerById(containerId, build, launcher, listener, environment);
                }
            }

            if (StringUtils.isNotBlank(cidFilePath)) {
                File cidFile = new File(FieldHelper.getMacroReplacedFieldValue(cidFilePath, environment));
                containerId = getContainerIdFromCidFile(cidFile);
                listener.getLogger().append("Attempting to remove container with id: " + containerId + ".\n");
                removeContainerById(containerId, build, launcher, listener, environment);
            }

            return true;
        } catch (EnvironmentConfigurationException | ContainerRemovalException e) {
            launcher.getListener().fatalError(String.format("Error: %s\n", e.getMessage()));
            return false;
        }
    }


    private void removeContainerById(String containerId, AbstractBuild build, Launcher launcher, BuildListener listener, EnvVars environment) throws ContainerRemovalException {
        if (StringUtils.isNotBlank(containerId)) {
            RemoveContainerCommandArgumentBuilder arguments = new RemoveContainerCommandArgumentBuilder()
                    .containerId(containerId)
                    .forceStop(force);

            DockerCommandExecutor command = new DockerCommandExecutor(arguments, environment);
            boolean removed = command.execute(build, launcher, listener);

            if(!removed && fail) {
                throw new ContainerRemovalException(containerId);
            }
        }
    }

    private String getContainerIdFromCidFile(File cidFile) {
        String containerId = null;
        if(cidFile != null) {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(cidFile))){
                containerId = fileReader.readLine();
            } catch (IOException e) {
            }
        }
        return containerId;
    }

    private String getContainerIdFromContainerName(String containerName, Launcher launcher, EnvVars envVars)  {
        try (PipedInputStream pipedInputStream = new PipedInputStream();
             PipedOutputStream consoleOutput = new PipedOutputStream(pipedInputStream);){
            int result = launcher.launch()
                    .cmdAsSingleString(getDockerConfigurationDescriptor().getDockerBinary() + " ps -a --filter=name=" + containerName + " --format={{.ID}}")
                    .stdout(consoleOutput)
                    .envs(envVars)
                    .quiet(true)
                    .join();

            consoleOutput.flush();
            consoleOutput.close();

            if(result == SUCCESS) {
                StringBuilder containerIdBuilder = new StringBuilder();
                int b;
                while((b = pipedInputStream.read()) != -1) {
                    containerIdBuilder.append((char) b);
                }

                return containerIdBuilder.toString();
            }

        } catch (IOException | InterruptedException e) {
            launcher.getListener().fatalError(String.format("Error: %s\n", e.getMessage()));
        }

        return null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCidFilePath() {
        return cidFilePath;
    }

    public boolean isForce() {
        return force;
    }

    public boolean isFail() {
        return fail;
    }
}

