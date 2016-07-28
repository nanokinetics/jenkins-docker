package com.vivid.docker.argument;

import com.vivid.docker.*;
import jenkins.model.*;
import org.junit.*;
import org.junit.runner.*;
import org.powermock.api.mockito.*;
import org.powermock.core.classloader.annotations.*;
import org.powermock.modules.junit4.*;

import java.util.*;

import static com.vivid.docker.Constants.BINARY;
import static com.vivid.docker.Constants.BINARY_INDEX;
import static com.vivid.docker.Constants.COMMAND_INDEX;
import static com.vivid.docker.Constants.PUL_COMMAND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.eq;

/**
 * Created by Phil Madden on 9/21/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
public class PullCommandArgumentBuilderTest {

    private BuildImageBuildStepDescriptor mockBuildImageBuildStepDescriptor;
    private PullCommandArgumentBuilder pullCommandArgumentBuilder;
    private Jenkins mockJenkins;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Jenkins.class);

        mockJenkins     = PowerMockito.mock(Jenkins.class);
        mockBuildImageBuildStepDescriptor = PowerMockito.mock(BuildImageBuildStepDescriptor.class);

        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getDescriptor(eq(BuildImageBuildStep.class))).thenReturn(mockBuildImageBuildStepDescriptor);
        PowerMockito.when(mockBuildImageBuildStepDescriptor.getDockerBinary()).thenReturn(BINARY);

        pullCommandArgumentBuilder = new PullCommandArgumentBuilder();
    }

    @Test
    public void testCommandSyntax_NoCommandOrArguments() {
        pullCommandArgumentBuilder
                .image("vividseats/docker:test");

        List<String> comand = pullCommandArgumentBuilder.build().toList();
        assertThat(comand.get(BINARY_INDEX), equalTo(BINARY));
        assertThat(comand.get(COMMAND_INDEX), equalTo(PUL_COMMAND));
        assertThat(comand.get(2), equalTo("vividseats/docker:test"));
    }

    @Test
    public void testCommandSyntax_pullAllTags() {
        pullCommandArgumentBuilder
                .image("vividseats/docker")
                .pullAllTags(true);

        List<String> comand = pullCommandArgumentBuilder.build().toList();
        assertThat(comand.get(BINARY_INDEX), equalTo(BINARY));
        assertThat(comand.get(COMMAND_INDEX), equalTo(PUL_COMMAND));
        assertThat(comand.get(2), equalTo("--all-tags"));
        assertThat(comand.get(3), equalTo("vividseats/docker"));
    }

    @Test
    public void testCommandSyntax_disableContentTrust() {
        pullCommandArgumentBuilder
                .image("vividseats/docker")
                .disableContentTrust(true);

        List<String> comand = pullCommandArgumentBuilder.build().toList();
        assertThat(comand.get(BINARY_INDEX), equalTo(BINARY));
        assertThat(comand.get(COMMAND_INDEX), equalTo(PUL_COMMAND));
        assertThat(comand.get(2), equalTo("--disable-content-trust"));
        assertThat(comand.get(3), equalTo("vividseats/docker"));
    }

}
