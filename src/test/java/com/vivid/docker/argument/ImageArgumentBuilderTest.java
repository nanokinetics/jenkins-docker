package com.vivid.docker.argument;

import com.vivid.docker.BuildImageBuildStep;
import com.vivid.docker.BuildImageBuildStepDescriptor;
import com.vivid.docker.command.DockerCommandExecutor;
import hudson.EnvVars;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static com.vivid.docker.Constants.*;
import static com.vivid.docker.Constants.CPU_SHARES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

/**
 * Created by Phil Madden on 9/21/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
public class ImageArgumentBuilderTest {

    private BuildImageBuildStepDescriptor mockBuildImageBuildStepDescriptor;
    private ImageArgumentBuilder imageArgumentBuilder;
    private Jenkins mockJenkins;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Jenkins.class);

        mockJenkins     = PowerMockito.mock(Jenkins.class);
        mockBuildImageBuildStepDescriptor = PowerMockito.mock(BuildImageBuildStepDescriptor.class);

        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getDescriptor(eq(BuildImageBuildStep.class))).thenReturn(mockBuildImageBuildStepDescriptor);
        PowerMockito.when(mockBuildImageBuildStepDescriptor.getDockerBinary()).thenReturn(BINARY);

        imageArgumentBuilder = new ImageArgumentBuilder("command");
    }

    @Test
    public void testCommandIsCommand() {
        assertThat(imageArgumentBuilder.build().toList().get(COMMAND_INDEX), equalTo("command"));
    }

    @Test
    public void testMemoryLimitSpecified() {
        imageArgumentBuilder.memoryLimit(MEMORY_LIMIT);
        assertThat(imageArgumentBuilder.build().toList(), hasItem("--memory=\"" + MEMORY_LIMIT + "\""));
    }

    @Test
    public void testMemorySwapSpecified() {
        imageArgumentBuilder.memorySwap(MEMORY_SWAP);
        assertThat(imageArgumentBuilder.build().toList(), hasItem("--memory-swap=\"" + MEMORY_SWAP + "\""));
    }

    @Test
    public void testMemoryNodesSpecified() {
        imageArgumentBuilder.mems(MEMORY_NODES);
        assertThat(imageArgumentBuilder.build().toList(), hasItem("--cpuset-mems=\"" + MEMORY_NODES + "\""));
    }

    @Test
    public void testCpuPeriodSpecified() {
        imageArgumentBuilder.cpuPeriod(CPU_PERIOD);
        assertThat(imageArgumentBuilder.build().toList(), hasItem("--cpu-period=" + CPU_PERIOD));
    }

    @Test
    public void testCpuQuotaSpecified() {
        imageArgumentBuilder.cpuQuota(CPU_QUOTA);
        assertThat(imageArgumentBuilder.build().toList(), hasItem("--cpu-quota=" + CPU_QUOTA));
    }

    @Test
    public void testCpuConstraintSpecified() {
        imageArgumentBuilder.cpus(CPU_CONSTRAINT);
        assertThat(imageArgumentBuilder.build().toList(), hasItem("--cpuset-cpus=\"" + CPU_CONSTRAINT + "\""));
    }

    @Test
    public void testCpuSharesSpecified() {
        imageArgumentBuilder.cpuShares(CPU_SHARES);
        assertThat(imageArgumentBuilder.build().toList(), hasItem("--cpu-shares=" + CPU_SHARES));
    }
}
