package com.vivid.docker;


import com.vivid.docker.argument.RemoveContainerCommandArgumentBuilder;
import com.vivid.docker.argument.RunCommandArgumentBuilder;
import com.vivid.docker.command.DockerCommandExecutor;
import com.vivid.docker.exception.ContainerRemovalException;
import com.vivid.docker.exception.EnvironmentConfigurationException;
import com.vivid.docker.exception.ImageNotFoundException;
import com.vivid.docker.util.FieldUtil;
import com.vivid.docker.util.StreamUtil;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.*;

public class RunImageBuildStep extends DockerBuildStep {

    private static final int MAX_PULL_RETRIES = 5;

    private final String image;
    private final String tag;
    private final String fallbackTag;
    private final String name;
    private final String labels;
    private final String command;
    private final String cidFilePath;
    private final String commandArguments;
    private final String environmentFiles;
    private final String environmentVariables;
    private final String hostName;
    private final String networkMode;
    private final String macAddress;
    private final String hostMappings;
    private final String dnsServers;
    private final String dnsSearchServers;
    private final String exposedPorts;
    private final String publishPorts;
    private final String user;
    private final Integer cpuShares;
    private final Integer cpuPeriod;
    private final Integer cpuQuota;
    private final String cpuConstraint;
    private final String memoryNodeConstraint;
    private final String memoryLimit;
    private final String memorySwap;
    private final String workingDirectory;
    private final String volumeDriver;
    private final String mountVolumes;
    private final String links;
    private final boolean publishAllPorts;
    private final boolean removeRunningContainers;
    private final boolean pseudoTTY;
    private final boolean privileged;
    private final boolean remove;
    private final boolean readOnly;
    private final boolean detach;
    private final boolean disableContentTrust;
    private final boolean removeIntermediateContainers;

    @DataBoundConstructor
    public RunImageBuildStep(String image,
                             String tag,
                             String fallbackTag,
                             String name,
                             String labels,
                             String command,
                             String commandArguments,
                             String cidFilePath,
                             String environmentFiles,
                             String environmentVariables,
                             String user,
                             String hostName,
                             String macAddress,
                             String networkMode,
                             String hostMappings,
                             String dnsServers,
                             String dnsSearchServers,
                             String exposedPorts,
                             String publishPorts,
                             String workingDirectory,
                             String volumeDriver,
                             String mountVolumes,
                             String links,
                             String cpuConstraint,
                             String memoryNodeConstraint,
                             String memoryLimit,
                             String memorySwap,
                             String cpuShares,
                             String cpuPeriod,
                             String cpuQuota,
                             String alternativeDockerHost,
                             boolean removeRunningContainers,
                             boolean remove,
                             boolean pseudoTTY,
                             boolean privileged,
                             boolean detach,
                             boolean readOnly,
                             boolean disableContentTrust,
                             boolean publishAllPorts,
                             boolean removeIntermediateContainers) {
        super(alternativeDockerHost);
        this.image = image;
        this.tag = tag;
        this.fallbackTag = fallbackTag;
        this.name = name;
        this.labels = labels;
        this.command = command;
        this.commandArguments = commandArguments;
        this.cidFilePath = cidFilePath;
        this.environmentFiles = environmentFiles;
        this.environmentVariables = environmentVariables;
        this.hostMappings = hostMappings;
        this.dnsServers = dnsServers;
        this.dnsSearchServers = dnsSearchServers;
        this.exposedPorts = exposedPorts;
        this.publishPorts = publishPorts;
        this.publishAllPorts = publishAllPorts;
        this.hostName = hostName;
        this.networkMode = networkMode;
        this.macAddress = macAddress;
        this.user = user;
        this.workingDirectory = workingDirectory;
        this.volumeDriver = volumeDriver;
        this.mountVolumes = mountVolumes;
        this.links = links;
        this.removeRunningContainers = removeRunningContainers;
        this.cpuConstraint = cpuConstraint;
        this.memoryNodeConstraint = memoryNodeConstraint;
        this.memoryLimit = memoryLimit;
        this.memorySwap = memorySwap;
        this.cpuShares = (Integer) Util.tryParseNumber(cpuShares, null);
        this.cpuPeriod = (Integer) Util.tryParseNumber(cpuPeriod, null);
        this.cpuQuota = (Integer) Util.tryParseNumber(cpuQuota, null);
        this.detach = detach;
        this.pseudoTTY = pseudoTTY;
        this.remove = remove;
        this.privileged = privileged;
        this.readOnly = readOnly;
        this.disableContentTrust = disableContentTrust;
        this.removeIntermediateContainers = removeIntermediateContainers;

    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        try {
            EnvVars environment = getEnvironment(build, listener);

            String containerName = FieldUtil.getMacroReplacedFieldValue(name, environment).toLowerCase();
            String imageName = FieldUtil.getMacroReplacedFieldValue(image, environment).toLowerCase();
            String imageTag = FieldUtil.getMacroReplacedFieldValue(tag, environment).toLowerCase();

            launcher.getListener().getLogger().append("\nCreating container from image: \"" + imageName + "\" tag \"" + imageTag + "\"..\n");

            File cidFile = null;
            if (StringUtils.isNotBlank(cidFilePath)) {
                cidFile = new File(FieldUtil.getMacroReplacedFieldValue(cidFilePath, environment));
            }

            if (!imageExists(imageName, imageTag, launcher, environment)) {
                String fallbackImageTag = FieldUtil.getMacroReplacedFieldValue(fallbackTag, environment).toLowerCase();
                launcher.getListener().getLogger().append("Unable to locate image \"" + imageName + "\" by tag \"" + imageTag + "\n");
                if(StringUtils.isEmpty(fallbackImageTag)) {
                    launcher.getListener().getLogger().append("No fallback tag has been specified.\n");
                    throw new ImageNotFoundException(imageName, imageTag);
                } else if (!imageExists(imageName, fallbackImageTag, launcher, environment)) {
                    throw new ImageNotFoundException(imageName, imageTag, fallbackImageTag);
                } else {
                    launcher.getListener().getLogger().append("Using \"" + fallbackImageTag + "\" as the fallback tag.\n");
                    imageTag = fallbackImageTag;
                }
            } else {
                launcher.getListener().getLogger().printf("Image [%s:%s] found.\n", imageName, imageTag);
            }

            if (removeRunningContainers) {
                attemptRemovalOfExistingContainers(containerName, cidFile, launcher, environment);
            }

            launcher.getListener().getLogger().printf("Attempting to launch image [%s:%s]\n", imageName, imageTag);
            RunCommandArgumentBuilder arguments = new RunCommandArgumentBuilder()
                    .image(imageName + ":" + imageTag)
                    .name(containerName)
                    .labels(FieldUtil.tokenize(labels, environment))
                    .user(user)
                    .command(command)
                    .commandArguments(FieldUtil.tokenize(commandArguments, environment))
                    .expose(FieldUtil.tokenize(exposedPorts))
                    .publishPorts(FieldUtil.tokenizeToIntArray(publishPorts, environment))
                    .cpuPeriod(cpuPeriod)
                    .cpuQuota(cpuQuota)
                    .cpuShares(cpuShares)
                    .cpus(cpuConstraint)
                    .cidFile(cidFile)
                    .memoryLimit(memoryLimit)
                    .memorySwap(memorySwap)
                    .mems(memoryNodeConstraint)
                    .environmentVariables(FieldUtil.tokenize(environmentVariables, environment))
                    .workingDirectory(workingDirectory)
                    .volumeDriver(volumeDriver)
                    .volumes(FieldUtil.tokenize(mountVolumes, environment))
                    .links(FieldUtil.tokenize(links, environment))
                    .disbaleContentTrust(disableContentTrust)
                    .readOnly(readOnly)
                    .pseudoTTY(pseudoTTY)
                    .remove(remove)
                    .privileged(privileged)
                    .detach(detach)
                    .publishAllPorts(publishAllPorts);

            DockerCommandExecutor command = new DockerCommandExecutor(arguments, environment);
            return command.execute(build, launcher, listener);

        } catch (EnvironmentConfigurationException e) {
            return false;
        } catch (IOException e) {
            Util.displayIOException(e, listener);
            listener.fatalError(e.getMessage());
            return false;
        } catch (InterruptedException e) {
            listener.fatalError(e.getMessage());
            return false;
        } catch (ContainerRemovalException e) {
            listener.fatalError(e.getMessage());
            return false;
        } catch (ImageNotFoundException e) {
            listener.fatalError(e.getMessage());
            return false;
        }
    }

    private void attemptRemovalOfExistingContainers(String containerName, File cidFile, Launcher launcher, EnvVars envVars) throws ContainerRemovalException, IOException, InterruptedException {
        String containerId = null;

        if(cidFile != null && cidFile.exists()) {
            containerId = getContainerIdFromCidFile(cidFile);
            cidFile.delete();
        }

        if(StringUtils.isNotBlank(containerName)) {
            containerId = getContainerIdFromContainerName(containerName, launcher, envVars);
        }

        if(StringUtils.isNotBlank(containerId)) {
            int result = launcher.launch()
                    .cmds(new RemoveContainerCommandArgumentBuilder()
                            .containerId(containerId)
                            .forceStop(true)
                            .build())
                    .envs(envVars)
                    .join();

            if(result != SUCCESS) {
                throw new ContainerRemovalException(containerId);
            }
        }
    }

    private String getContainerIdFromCidFile(File cidFile) {
        String containerId = null;
        if(cidFile != null) {
            BufferedReader fileReader = null;
            try {
                fileReader = new BufferedReader(new FileReader(cidFile));
                containerId = fileReader.readLine();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                StreamUtil.closeStream(fileReader);
            }
        }
        return containerId;
    }

    private String getContainerIdFromContainerName(String containerName, Launcher launcher, EnvVars envVars)  {
        PipedInputStream pipedInputStream = null;
        PipedOutputStream consoleOutput;

        try {
            pipedInputStream = new PipedInputStream();
            consoleOutput = new PipedOutputStream(pipedInputStream);

            int result = launcher.launch()
                    .cmdAsSingleString(getDockerConfigurationDescriptor().getDockerBinary() + " ps -a --filter=\"name=^/" + containerName + "$\" --format={{.ID}}")
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

        } catch (IOException e) {
            launcher.getListener().getLogger().append(e.getMessage());
            launcher.getListener().fatalError(e.getMessage());
        } catch (InterruptedException e) {
            launcher.getListener().getLogger().append(e.getMessage());
            launcher.getListener().fatalError(e.getMessage());
        } finally {
            StreamUtil.closeStream(pipedInputStream);
        }

        return null;
    }

    private boolean imageExists(String imageName, String tag, Launcher launcher, EnvVars envVars) {
        try {
            launcher.getListener().getLogger().printf("Checking if image exists [%s:%s]...\n", imageName, tag);
            int result = launcher.launch()
                    .cmdAsSingleString(getDockerConfigurationDescriptor().getDockerBinary() + " history " + imageName + ":" + tag)
                    .quiet(true)
                    .envs(envVars)
                    .join();

            if(result != SUCCESS) {
                //Not found locally, attempt to pull it from docker hub
                launcher.getListener().getLogger().printf("Image [%s:%s] does not exist locally. Attempting to pull image\n", imageName, tag);
                return pullImage(imageName, tag, launcher, envVars);
            }

            return result == SUCCESS;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean pullImage(String imageName, String tag, Launcher launcher, EnvVars envVars) throws InterruptedException, IOException {
        int result, attempt = 0;
        boolean pullAlreadyInProgress;
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        String command = String.format("%s pull %s:%s", getDockerConfigurationDescriptor().getDockerBinary(), imageName, tag);

         do {
            attempt++;
            result = launcher.launch()
                    .cmdAsSingleString(command)
                    .quiet(true)
                    .stdout(stdout)
                    .stderr(stderr)
                    .envs(envVars)
                    .join();

            String output = stdout.toString() + stderr.toString();
            pullAlreadyInProgress = output.contains("already being pulled by another client");

            if (pullAlreadyInProgress) {
                if (attempt < MAX_PULL_RETRIES) {
                    launcher.getListener().getLogger().printf("Could not pull image [%s:%s] - another pull in progress. Retrying in 10s...\n", imageName, tag);
                    Thread.sleep(10000);
                } else {
                    launcher.getListener().getLogger().printf("Could not pull image [%s:%s] - another pull in progress. Aborting.\n", imageName, tag);
                }
                stdout.reset();
                stderr.reset();
            }
        } while (pullAlreadyInProgress && attempt < MAX_PULL_RETRIES);
        return result == SUCCESS;
    }

    public String getImage() {
        return image;
    }

    public String getTag() {
        return tag;
    }

    public String getFallbackTag() {
        return fallbackTag;
    }

    public String getName() {
        return name;
    }

    public String getLabels() {
        return labels;
    }

    public String getCommand() {
        return command;
    }

    public String getCidFilePath() {
        return cidFilePath;
    }

    public String getCommandArguments() {
        return commandArguments;
    }

    public String getEnvironmentFiles() {
        return environmentFiles;
    }

    public String getEnvironmentVariables() {
        return environmentVariables;
    }

    public String getHostName() {
        return hostName;
    }

    public String getNetworkMode() {
        return networkMode;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getHostMappings() {
        return hostMappings;
    }

    public String getDnsServers() {
        return dnsServers;
    }

    public String getDnsSearchServers() {
        return dnsSearchServers;
    }

    public String getExposedPorts() {
        return exposedPorts;
    }

    public String getPublishPorts() {
        return publishPorts;
    }

    public boolean getPublishAllPorts() {
        return publishAllPorts;
    }

    public String getUser() {
        return user;
    }

    public Integer getCpuShares() {
        return cpuShares;
    }

    public Integer getCpuPeriod() {
        return cpuPeriod;
    }

    public Integer getCpuQuota() {
        return cpuQuota;
    }

    public String getCpuConstraint() {
        return cpuConstraint;
    }

    public String getMemoryNodeConstraint() {
        return memoryNodeConstraint;
    }

    public String getMemoryLimit() {
        return memoryLimit;
    }

    public String getMemorySwap() {
        return memorySwap;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public String getVolumeDriver() {
        return volumeDriver;
    }

    public String getMountVolumes() {
        return mountVolumes;
    }

    public String getLinks() {
        return links;
    }

    public boolean isRemoveRunningContainers() {
        return removeRunningContainers;
    }

    public boolean isPseudoTTY() {
        return pseudoTTY;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public boolean isRemove() {
        return remove;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isDetach() {
        return detach;
    }

    public boolean isDisableContentTrust() {
        return disableContentTrust;
    }

    public boolean isRemoveIntermediateContainers() {
        return removeIntermediateContainers;
    }
}

