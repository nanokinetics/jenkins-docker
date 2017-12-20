package com.vivid.docker.argument;

import com.vivid.docker.BuildImageBuildStep;
import com.vivid.docker.BuildImageBuildStepDescriptor;
import com.vivid.docker.command.DockerCommandExecutor;
import hudson.EnvVars;
import hudson.util.ArgumentListBuilder;
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
import static com.vivid.docker.Constants.BUILD_COMMAND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Matchers.eq;

/**
 * Created by Phil Madden on 9/10/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
public class BuildCommandArgumentBuilderTest {

    private BuildImageBuildStepDescriptor mockBuildImageBuildStepDescriptor;
    private BuildCommandArgumentBuilder buildCommandArgumentBuilder;
    private Jenkins mockJenkins;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Jenkins.class);

        mockJenkins     = PowerMockito.mock(Jenkins.class);
        mockBuildImageBuildStepDescriptor = PowerMockito.mock(BuildImageBuildStepDescriptor.class);

        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getDescriptor(eq(BuildImageBuildStep.class))).thenReturn(mockBuildImageBuildStepDescriptor);
        PowerMockito.when(mockBuildImageBuildStepDescriptor.getDockerBinary()).thenReturn(BINARY);

        buildCommandArgumentBuilder = new BuildCommandArgumentBuilder();
    }

    @Test
    public void testCommandIsBuild() {
        assertThat(buildCommandArgumentBuilder.build().toList().get(COMMAND_INDEX), equalTo(BUILD_COMMAND));
    }

    @Test
    public void testDockerFileSpecified() {
        buildCommandArgumentBuilder.file(DOCKER_FILE_PATH);
        List<String> arguments = buildCommandArgumentBuilder.build().toList();
        assertThat(arguments, hasItem("--file=" + DOCKER_FILE_PATH));
        assertThat(arguments.get(arguments.size() - 1), equalTo("."));
    }

    @Test
    public void testNoCacheSpecified_true() {
        buildCommandArgumentBuilder.noCache(true);
        assertThat(buildCommandArgumentBuilder.build().toList(), hasItem("--no-cache"));
    }

    @Test
    public void testNoCacheSpecified_false() {
        buildCommandArgumentBuilder.noCache(false);
        assertThat(buildCommandArgumentBuilder.build().toList(), not(hasItem("--no-cache")));
    }

    @Test
    public void testPullLatestImageSpecified_true() {
        buildCommandArgumentBuilder.pull(true);
        assertThat(buildCommandArgumentBuilder.build().toList(), hasItem("--pull"));
    }

    @Test
    public void testPullLatestImageSpecified_false() {
        buildCommandArgumentBuilder.pull(false);
        assertThat(buildCommandArgumentBuilder.build().toList(), not(hasItem("--pull")));
    }

    @Test
    public void testDisableContentTrustSpecified_true() {
        buildCommandArgumentBuilder.disbaleContentTrust(true);
        assertThat(buildCommandArgumentBuilder.build().toList(), hasItem("--disable-content-trust"));
    }

    @Test
    public void testDisableContentTrustSpecified_false() {
        buildCommandArgumentBuilder.disbaleContentTrust(false);
        assertThat(buildCommandArgumentBuilder.build().toList(), not(hasItem("--disable-content-trust")));
    }

    @Test
    public void testForceRemovalOfIntermediateContainersSpecified_true() {
        buildCommandArgumentBuilder.forceRemove(true);
        assertThat(buildCommandArgumentBuilder.build().toList(), hasItem("--force-rm"));
    }

    @Test
    public void testForceRemovalOfIntermediateContainersSpecified_false() {
        buildCommandArgumentBuilder.forceRemove(false);
        assertThat(buildCommandArgumentBuilder.build().toList(), not(hasItem("--force-rm")));
    }

    @Test
    public void testRemoveIntermediateContainersSpecified_true() {
        buildCommandArgumentBuilder.remove(true);
        assertThat(buildCommandArgumentBuilder.build().toList(), hasItem("--rm"));
    }

    @Test
    public void testRemoveIntermediateContainersSpecified_false() {
        buildCommandArgumentBuilder.remove(false);
        assertThat(buildCommandArgumentBuilder.build().toList(), not(hasItem("--rm")));
    }

    @Test
    public void testBuildContextNotSpecified() {
        List<String> arguments = buildCommandArgumentBuilder.build().toList();
        assertThat(arguments.get(arguments.size() - 1), equalTo("."));
    }

    @Test
    public void testBuildContextSpecified() {
        buildCommandArgumentBuilder.buildContext("/foo");
        List<String> arguments = buildCommandArgumentBuilder.build().toList();
        assertThat(arguments.get(arguments.size() - 1), equalTo("/foo"));
    }

    @Test
    public void testBuildContextAndDockerFileSpecified() {
        buildCommandArgumentBuilder.file(DOCKER_FILE_PATH);
        buildCommandArgumentBuilder.buildContext("/foo");
        List<String> arguments = buildCommandArgumentBuilder.build().toList();
        assertThat(arguments, hasItem("--file=" + DOCKER_FILE_PATH));
        assertThat(arguments.get(arguments.size() - 1), equalTo("/foo"));
    }
}
