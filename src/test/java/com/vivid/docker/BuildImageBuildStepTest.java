package com.vivid.docker;

import com.vivid.docker.argument.ArgumentBuilder;
import com.vivid.docker.argument.BuildCommandArgumentBuilder;
import com.vivid.docker.command.DockerCommandExecutor;
import com.vivid.docker.exception.EnvironmentConfigurationException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.util.ArgumentListBuilder;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
import static com.vivid.docker.Constants.*;
/**
 * Created by Phil Madden on 9/21/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Jenkins.class,
        AbstractBuild.class,
        Launcher.class
})
public class BuildImageBuildStepTest {

    private Launcher                        mockLauncher;
    private AbstractBuild                   mockAbstractBuild;
    private BuildListener                   mockBuildListener;
    private BuildImageBuildStepDescriptor   mockBuildImageBuildStepDescriptor;
    private Jenkins                         mockJenkins;
    private DockerCommandExecutor           mockDockerCommandExecutor;
    private PrintStream                     mockPrintStream;
    private Launcher.ProcStarter            mockProcStarter;

    private EnvVars                         environment;
    private FilePath                        workspace;

    @Captor
    private ArgumentCaptor<ArgumentBuilder> argumentBuilderArgumentCaptor;
    private ArgumentCaptor<EnvVars>         envVarsArgumentCaptor;

    @Before
    public void setUp() throws IOException, InterruptedException {
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.mockStatic(Launcher.class);
        PowerMockito.mockStatic(AbstractBuild.class);

        mockLauncher                        = PowerMockito.mock(Launcher.class);
        mockAbstractBuild                   = PowerMockito.mock(AbstractBuild.class);
        mockBuildListener                   = PowerMockito.mock(BuildListener.class);
        mockJenkins                         = PowerMockito.mock(Jenkins.class);
        mockBuildImageBuildStepDescriptor   = PowerMockito.mock(BuildImageBuildStepDescriptor.class);
        mockDockerCommandExecutor           = PowerMockito.mock(DockerCommandExecutor.class);
        mockPrintStream                     = PowerMockito.mock(PrintStream.class);
        mockProcStarter                     = PowerMockito.mock(Launcher.ProcStarter.class);

        argumentBuilderArgumentCaptor       = ArgumentCaptor.forClass(ArgumentBuilder.class);
        envVarsArgumentCaptor               = ArgumentCaptor.forClass(EnvVars.class);

        environment                         = new EnvVars();
        workspace                           = new FilePath(new File(""));

        PowerMockito.when(Jenkins.getInstance()).thenReturn(mockJenkins);
        PowerMockito.when(mockAbstractBuild.getWorkspace()).thenReturn(workspace);
        PowerMockito.when(mockAbstractBuild.getEnvironment(mockBuildListener)).thenReturn(environment);
        PowerMockito.when(mockJenkins.getDescriptor(eq(BuildImageBuildStep.class))).thenReturn(mockBuildImageBuildStepDescriptor);
        PowerMockito.when(mockBuildImageBuildStepDescriptor.getDockerBinary()).thenReturn("/usr/bin/docker");


        //Stub launcher builder pattern
        PowerMockito.when(mockLauncher.launch()).thenReturn(mockProcStarter);
        PowerMockito.when(mockLauncher.getListener()).thenReturn(mockBuildListener);
        PowerMockito.when(mockBuildListener.getLogger()).thenReturn(mockPrintStream);

        PowerMockito.when(mockProcStarter.cmds(any(ArgumentListBuilder.class))).thenReturn(mockProcStarter);
        PowerMockito.when(mockProcStarter.envs(any(EnvVars.class))).thenReturn(mockProcStarter);
        PowerMockito.when(mockProcStarter.stderr(any(OutputStream.class))).thenReturn(mockProcStarter);
        PowerMockito.when(mockProcStarter.stdout(any(OutputStream.class))).thenReturn(mockProcStarter);
        PowerMockito.when(mockProcStarter.quiet(anyBoolean())).thenReturn(mockProcStarter);
        PowerMockito.when(mockProcStarter.pwd(any(FilePath.class))).thenReturn(mockProcStarter);
        PowerMockito.when(mockProcStarter.join()).thenReturn(0);
    }


    private BuildImageBuildStep constructDefault(String imageName, String tag) {
        return new BuildImageBuildStep(imageName, tag, null, null, null, null, null, null, null, null, null, null, null, false, false, false, false, false, false, false, null);
    }

    @Test
    public void testFailsWhenConfiguringEnvironment() throws EnvironmentConfigurationException {
        BuildImageBuildStep stepSpy = Mockito.spy(constructDefault(IMAGE_NAME, TAG_NAME));
        PowerMockito.doThrow(new EnvironmentConfigurationException("", null)).when(stepSpy).getEnvironment(mockAbstractBuild, mockBuildListener);
        boolean success = stepSpy.perform(mockAbstractBuild, mockLauncher, mockBuildListener);
        assertThat(success, is(false));
    }

    @Test
    public void testEnvironmentGetsDockerHostWhenSetInDescriptor() {
        BuildImageBuildStep stepSpy = Mockito.spy(constructDefault(IMAGE_NAME, TAG_NAME));
        PowerMockito.when(mockBuildImageBuildStepDescriptor.getDockerHost()).thenReturn("docker host");
        PowerMockito.when(stepSpy.getCommand(any(ArgumentBuilder.class), envVarsArgumentCaptor.capture())).thenReturn(mockDockerCommandExecutor);

        stepSpy.perform(mockAbstractBuild, mockLauncher, mockBuildListener);

        EnvVars capturedEnvironment = envVarsArgumentCaptor.getValue();
        assertThat(capturedEnvironment.get(DOCKER_HOST_KEY), equalTo("docker host"));
    }

    @Test
    public void testTaggedWithSpecifiedNameAndTag() {
        BuildImageBuildStep stepSpy = Mockito.spy(constructDefault(IMAGE_NAME, TAG_NAME));
        PowerMockito.when(stepSpy.getCommand(argumentBuilderArgumentCaptor.capture(), any(EnvVars.class))).thenReturn(mockDockerCommandExecutor);

        stepSpy.perform(mockAbstractBuild, mockLauncher, mockBuildListener);
        ArgumentBuilder argumentBuilder = argumentBuilderArgumentCaptor.getValue();
        assertThat(argumentBuilder.build().toList(), hasItem(("--tag=" + IMAGE_NAME + ":" + TAG_NAME).toLowerCase()));
    }
}
