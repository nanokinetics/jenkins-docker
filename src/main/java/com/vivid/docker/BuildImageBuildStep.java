package com.vivid.docker;

import com.vivid.docker.argument.BuildCommandArgumentBuilder;
import com.vivid.docker.command.DockerCommandExecutor;
import com.vivid.docker.exception.EnvironmentConfigurationException;
import com.vivid.docker.util.FieldUtil;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.*;

public class BuildImageBuildStep extends DockerBuildStep {

    private final String dockerFile;
    private final String name;
    private final String tag;
    private final Integer cpuShares;
    private final Integer cpuPeriod;
    private final Integer cpuQuota;
    private final String cpuConstraint;
    private final String memoryNodeConstraint;
    private final String memoryLimit;
    private final String memorySwap;
    private final String dockerFileContent;
    private final boolean noCache;
    private final boolean pull;
    private final boolean disableContentTrust;
    private final boolean forceRemoveIntermediateContainers;
    private final boolean removeIntermediateContainers;
    private final boolean dockerFileContentChecked;


    @DataBoundConstructor
    public BuildImageBuildStep(String name,
                               String tag,
                               String dockerFile,
                               String cpuConstraint,
                               String memoryNodeConstraint,
                               String memoryLimit,
                               String memorySwap,
                               String cpuShares,
                               String cpuPeriod,
                               String cpuQuota,
                               String dockerFileContent,
                               String alternativeDockerHost,
                               boolean noCache,
                               boolean pull,
                               boolean disableContentTrust,
                               boolean forceRemoveIntermediateContainers,
                               boolean removeIntermediateContainers,
                               boolean dockerFileContentChecked) {
        super(alternativeDockerHost);
        this.name = name;
        this.tag = tag;
        this.dockerFile = dockerFile;
        this.cpuConstraint = cpuConstraint;
        this.memoryNodeConstraint = memoryNodeConstraint;
        this.memoryLimit = memoryLimit;
        this.memorySwap = memorySwap;
        this.cpuShares = (Integer) Util.tryParseNumber(cpuShares, null);
        this.cpuPeriod = (Integer) Util.tryParseNumber(cpuPeriod, null);
        this.cpuQuota = (Integer) Util.tryParseNumber(cpuQuota, null);
        this.dockerFileContent = dockerFileContent;
        this.dockerFileContentChecked = dockerFileContentChecked;
        this.noCache = noCache;
        this.pull = pull;
        this.disableContentTrust = disableContentTrust;
        this.forceRemoveIntermediateContainers = forceRemoveIntermediateContainers;
        this.removeIntermediateContainers = removeIntermediateContainers;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        try {
            EnvVars environment = getEnvironment(build, listener);
            String dockerFilePath = dockerFile;

            if (dockerFileContent != null && !dockerFileContent.isEmpty()) {
                dockerFilePath = environment.get("WORKSPACE") + "/Dockerfile";

                launcher.getListener().getLogger().println("Creating Docker File : " + dockerFilePath);
                launcher.getListener().getLogger().println("Content: " + dockerFileContent);
                createDockerFile(dockerFilePath);
            }

            BuildCommandArgumentBuilder arguments = new BuildCommandArgumentBuilder()
                    .disbaleContentTrust(disableContentTrust)
                    .cpuPeriod(cpuPeriod)
                    .cpuQuota(cpuQuota)
                    .cpus(cpuConstraint)
                    .cpuShares(cpuShares)
                    .file(FieldUtil.getMacroReplacedFieldValue(dockerFilePath, environment))
                    .forceRemove(forceRemoveIntermediateContainers)
                    .memoryLimit(memoryLimit)
                    .memorySwap(memorySwap)
                    .mems(memoryNodeConstraint)
                    .noCache(noCache)
                    .pull(pull)
                    .remove(removeIntermediateContainers)
                    .tag((FieldUtil.getMacroReplacedFieldValue(name, environment) + ":" + FieldUtil.getMacroReplacedFieldValue(tag, environment)).toLowerCase());

            DockerCommandExecutor command = getCommand(arguments, environment);
            return command.execute(build, launcher, listener);

        } catch (EnvironmentConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createDockerFile(String path) {
        try(FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            PrintWriter printWriter = new PrintWriter(fileOutputStream)) {

            printWriter.write(dockerFileContent);
            printWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDockerFile() {
        return dockerFile;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public Number getCpuShares() {
        return cpuShares;
    }

    public Number getCpuPeriod() {
        return cpuPeriod;
    }

    public Number getCpuQuota() {
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

    public String getDockerFileContent() {
        return dockerFileContent;
    }

    public boolean isDockerFileContentChecked() {
        return dockerFileContentChecked;
    }

    public boolean isNoCache() {
        return noCache;
    }

    public boolean isPull() {
        return pull;
    }

    public boolean isDisableContentTrust() {
        return disableContentTrust;
    }

    public boolean isForceRemoveIntermediateContainers() {
        return forceRemoveIntermediateContainers;
    }

    public boolean isRemoveIntermediateContainers() {
        return removeIntermediateContainers;
    }
}

