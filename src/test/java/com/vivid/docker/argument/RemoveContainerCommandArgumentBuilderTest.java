package com.vivid.docker.argument;

import com.vivid.docker.BuildImageBuildStep;
import com.vivid.docker.BuildImageBuildStepDescriptor;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static com.vivid.docker.Constants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.eq;

/**
 * Created by Phil Madden on 9/21/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
public class RemoveContainerCommandArgumentBuilderTest {

    private BuildImageBuildStepDescriptor mockBuildImageBuildStepDescriptor;
    private RemoveContainerCommandArgumentBuilder removeCommandArgumentBuilder;
    private Jenkins mockJenkins;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Jenkins.class);

        mockJenkins     = PowerMockito.mock(Jenkins.class);
        mockBuildImageBuildStepDescriptor = PowerMockito.mock(BuildImageBuildStepDescriptor.class);

        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getDescriptor(eq(BuildImageBuildStep.class))).thenReturn(mockBuildImageBuildStepDescriptor);
        PowerMockito.when(mockBuildImageBuildStepDescriptor.getDockerBinary()).thenReturn(BINARY);

        removeCommandArgumentBuilder = new RemoveContainerCommandArgumentBuilder();
    }

    @Test
    public void testBinarySet() {
        assertThat(removeCommandArgumentBuilder.build().toList().get(BINARY_INDEX), equalTo(BINARY));
    }

    @Test
    public void testCommandSet() {
        assertThat(removeCommandArgumentBuilder.build().toList().get(COMMAND_INDEX), equalTo(REMOVE_COMMAND));
    }

    @Test
    public void testCommandSyntax() {
        removeCommandArgumentBuilder.containerId(CONTAINER_ID);
        List<String> command = removeCommandArgumentBuilder.build().toList();
        assertThat(command.get(BINARY_INDEX), equalTo(BINARY));
        assertThat(command.get(COMMAND_INDEX), equalTo(REMOVE_COMMAND));
        assertThat(command.get(2), equalTo(CONTAINER_ID));
    }

    @Test
    public void testCommandSyntax_force() {
        removeCommandArgumentBuilder
                .containerId(CONTAINER_ID)
                .forceStop(true);

        List<String> command = removeCommandArgumentBuilder.build().toList();
        assertThat(command.get(BINARY_INDEX), equalTo(BINARY));
        assertThat(command.get(COMMAND_INDEX), equalTo(REMOVE_COMMAND));
        assertThat(command.get(2), equalTo("--force"));
        assertThat(command.get(3), equalTo(CONTAINER_ID));
    }

    @Test
    public void testDetachSpecified_true() {
        removeCommandArgumentBuilder.forceStop(true);
        assertThat(removeCommandArgumentBuilder.build().toList(), hasItem("--force"));
    }

    @Test
    public void testDetachSpecified_false() {
        removeCommandArgumentBuilder.forceStop(false);
        assertThat(removeCommandArgumentBuilder.build().toList(), not(hasItem("--force")));
    }
}
