package com.vivid.docker.argument;

import com.vivid.docker.BuildImageBuildStep;
import com.vivid.docker.BuildImageBuildStepDescriptor;
import com.vivid.docker.Constants;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static com.vivid.docker.Constants.*;

/**
 * Created by Phil Madden on 9/21/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
public class ArgumentBuilderTest {

    private BuildImageBuildStepDescriptor mockBuildImageBuildStepDescriptor;
    private ArgumentBuilder argumentBuilder;
    private Jenkins mockJenkins;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Jenkins.class);

        mockJenkins     = PowerMockito.mock(Jenkins.class);
        mockBuildImageBuildStepDescriptor = PowerMockito.mock(BuildImageBuildStepDescriptor.class);

        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getDescriptor(eq(BuildImageBuildStep.class))).thenReturn(mockBuildImageBuildStepDescriptor);
        PowerMockito.when(mockBuildImageBuildStepDescriptor.getDockerBinary()).thenReturn(BINARY);

        argumentBuilder = new ArgumentBuilder("command");
    }

    @Test
    public void testBinarySet() {
        assertThat(argumentBuilder.build().toList().get(BINARY_INDEX), equalTo(BINARY));
    }

    @Test
    public void testCommandSet() {
        assertThat(argumentBuilder.build().toList().get(COMMAND_INDEX), equalTo("command"));
    }

    @Test
    public void testIsNotEmptyString() {
        assertThat(argumentBuilder.isNotEmpty(""), is(false));
    }

    @Test
    public void testIsNotEmptyArray() {
        assertThat(argumentBuilder.isNotEmpty(new String[0]), is(false));
    }
}
