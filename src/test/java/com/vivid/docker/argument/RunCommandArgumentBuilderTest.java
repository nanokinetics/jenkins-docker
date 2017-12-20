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

import java.io.File;
import java.util.List;

import static com.vivid.docker.Constants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.eq;

/**
 * Created by Phil Madden on 9/10/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
public class RunCommandArgumentBuilderTest {

    private BuildImageBuildStepDescriptor mockBuildImageBuildStepDescriptor;
    private RunCommandArgumentBuilder runCommandArgumentBuilder;
    private Jenkins mockJenkins;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Jenkins.class);

        mockJenkins     = PowerMockito.mock(Jenkins.class);
        mockBuildImageBuildStepDescriptor = PowerMockito.mock(BuildImageBuildStepDescriptor.class);

        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockJenkins.getDescriptor(eq(BuildImageBuildStep.class))).thenReturn(mockBuildImageBuildStepDescriptor);
        PowerMockito.when(mockBuildImageBuildStepDescriptor.getDockerBinary()).thenReturn(BINARY);

        runCommandArgumentBuilder = new RunCommandArgumentBuilder();
    }

    @Test
    public void testCommandSyntax_NoCommandOrArguments() {
        runCommandArgumentBuilder
                .image("vividseats/docker:test")
                .command(null)
                .commandArguments(null);

        List<String> comand = runCommandArgumentBuilder.build().toList();
        assertThat(comand.get(BINARY_INDEX), equalTo(BINARY));
        assertThat(comand.get(COMMAND_INDEX), equalTo(RUN_COMMAND));
        assertThat(comand.get(2), equalTo("vividseats/docker:test"));
    }

    @Test
    public void testCommandSyntax_CommandSpecified_NoArguments() {
        runCommandArgumentBuilder
                .image("vividseats/docker:test")
                .command("/bin/bash")
                .commandArguments(null);

        List<String> comand = runCommandArgumentBuilder.build().toList();
        assertThat(comand.get(BINARY_INDEX), equalTo(BINARY));
        assertThat(comand.get(COMMAND_INDEX), equalTo(RUN_COMMAND));
        assertThat(comand.get(2), equalTo("vividseats/docker:test"));
        assertThat(comand.get(3), equalTo("/bin/bash"));
    }

    @Test
    public void testCommandSyntax_CommandAndArgumentsSpecified() {
        runCommandArgumentBuilder
                .image("vividseats/docker:test")
                .command("/bin/echo")
                .commandArguments("test");

        List<String> comand = runCommandArgumentBuilder.build().toList();
        assertThat(comand.get(BINARY_INDEX), equalTo(BINARY));
        assertThat(comand.get(COMMAND_INDEX), equalTo(RUN_COMMAND));
        assertThat(comand.get(2), equalTo("vividseats/docker:test"));
        assertThat(comand.get(3), equalTo("/bin/echo"));
        assertThat(comand.get(4), equalTo("test"));
    }

    @Test
    public void testCommandIsBuild() {
        assertThat(runCommandArgumentBuilder.build().toList().get(COMMAND_INDEX), equalTo(RUN_COMMAND));
    }

    @Test
    public void testCidFileSpecified() {
        File cidFile = new File("");
        runCommandArgumentBuilder.cidFile(cidFile);
        assertThat(runCommandArgumentBuilder.build().toList(), hasItem("--cidfile=" + cidFile.getAbsolutePath()));
    }

    @Test
    public void testPublishPortsSpecified_true() {
        runCommandArgumentBuilder.publishAllPorts(true);
        assertThat(runCommandArgumentBuilder.build().toList(), hasItem("--publish-all"));
    }

    @Test
    public void testPublishPortsSpecified_false() {
        runCommandArgumentBuilder.publishAllPorts(false);
        assertThat(runCommandArgumentBuilder.build().toList(), not(hasItem("--publish-all")));
    }

    @Test
    public void testDetachSpecified_true() {
        runCommandArgumentBuilder.detach(true);
        assertThat(runCommandArgumentBuilder.build().toList(), hasItem("--detach"));
    }

    @Test
    public void testDetachSpecified_false() {
        runCommandArgumentBuilder.detach(false);
        assertThat(runCommandArgumentBuilder.build().toList(), not(hasItem("--detach")));
    }

    @Test
    public void testDisableContentTrustSpecified_true() {
        runCommandArgumentBuilder.disbaleContentTrust(true);
        assertThat(runCommandArgumentBuilder.build().toList(), hasItem("--disable-content-trust"));
    }

    @Test
    public void testDisableContentTrustSpecified_false() {
        runCommandArgumentBuilder.disbaleContentTrust(false);
        assertThat(runCommandArgumentBuilder.build().toList(), not(hasItem("--disable-content-trust")));
    }

    @Test
    public void testEnvironmentVariablesSpecified() {
        runCommandArgumentBuilder.environmentVariables("X=Y", "Y=X");
        assertThat(runCommandArgumentBuilder.build().toList(), hasItems("--env=X=Y", "--env=Y=X"));
    }

    @Test
    public void testExposePortsSpecified() {
        runCommandArgumentBuilder.expose("1234", "6789");
        assertThat(runCommandArgumentBuilder.build().toList(), hasItems("--expose=1234", "--expose=6789"));
    }

    @Test
    public void testLabelsSpecified() {
        runCommandArgumentBuilder.labels("test", "container");
        assertThat(runCommandArgumentBuilder.build().toList(), hasItems("--label=test", "--label=container"));
    }

    @Test
    public void testLinksSpecified() {
        runCommandArgumentBuilder.links("container1", "container2");
        assertThat(runCommandArgumentBuilder.build().toList(), hasItems("--link=container1", "--link=container2"));
    }

    @Test
    public void testNameSpecified() {
        runCommandArgumentBuilder.name("container1");
        assertThat(runCommandArgumentBuilder.build().toList(), hasItems("--name=container1"));
    }

    @Test
    public void testPublishPortsSpecified() {
        runCommandArgumentBuilder.publishPorts(1, 2);
        assertThat(runCommandArgumentBuilder.build().toList(), hasItems("--publish=1", "--publish=2"));
    }

    @Test
    public void testPrivilegedSpecified_true() {
        runCommandArgumentBuilder.privileged(true);
        assertThat(runCommandArgumentBuilder.build().toList(), hasItems("--privileged"));
    }

    @Test
    public void testPrivilegedSpecified_false() {
        runCommandArgumentBuilder.privileged(false);
        assertThat(runCommandArgumentBuilder.build().toList(), not(hasItems("--privileged")));
    }

    @Test
    public void testPseudoTTYSpecified_true() {
        runCommandArgumentBuilder.pseudoTTY(true);
        assertThat(runCommandArgumentBuilder.build().toList(), hasItems("--tty"));
    }

    @Test
    public void testPseudoTTYSpecified_false() {
        runCommandArgumentBuilder.pseudoTTY(false);
        assertThat(runCommandArgumentBuilder.build().toList(), not(hasItems("--tty")));
    }

    @Test
    public void testReadOnlySpecified_true() {
        runCommandArgumentBuilder.readOnly(true);
        assertThat(runCommandArgumentBuilder.build().toList(), hasItems("--read-only"));
    }

    @Test
    public void testReadOnlySpecified_false() {
        runCommandArgumentBuilder.readOnly(false);
        assertThat(runCommandArgumentBuilder.build().toList(), not(hasItems("--read-only")));
    }

    @Test
    public void testRemoveIntermediateContainersSpecified_true() {
        runCommandArgumentBuilder.remove(true);
        assertThat(runCommandArgumentBuilder.build().toList(), hasItem("--rm"));
    }

    @Test
    public void testRemoveIntermediateContainersSpecified_false() {
        runCommandArgumentBuilder.remove(false);
        assertThat(runCommandArgumentBuilder.build().toList(), not(hasItem("--rm")));
    }

    @Test
    public void testUserSpecified() {
        runCommandArgumentBuilder.user("test");
        assertThat(runCommandArgumentBuilder.build().toList(), hasItem("--user=test"));
    }

    @Test
    public void testVolumeDriverSpecified() {
        runCommandArgumentBuilder.volumeDriver("org.docker.driver");
        assertThat(runCommandArgumentBuilder.build().toList(), hasItem("--volume-driver=org.docker.driver"));
    }

    @Test
    public void testVolumesSpecified() {
        runCommandArgumentBuilder.volumes("/var/log:/var/logs:ro", "/etc/conf:/etc/confs");
        assertThat(runCommandArgumentBuilder.build().toList(), hasItems("--volume=/var/log:/var/logs:ro", "--volume=/etc/conf:/etc/confs"));
    }

    @Test
    public void testWorkingDirectorySpecified() {
        runCommandArgumentBuilder.workingDirectory("/root");
        assertThat(runCommandArgumentBuilder.build().toList(), hasItem("--workdir=/root"));
    }
}