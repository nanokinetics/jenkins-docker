package com.vivid.docker;

/**
 * Created by philmadden on 9/14/15.
 */
public final class Constants {
    public static final int BINARY_INDEX = 0;
    public static final int COMMAND_INDEX = 1;

    public static final String BUILD_COMMAND = "build";
    public static final String RUN_COMMAND = "run";
    public static final String PUL_COMMAND = "pull";
    public static final String REMOVE_COMMAND = "rm";

    public static final String BINARY = "/usr/bin/docker";
    public static final String IMAGE_NAME = "TEST-IMAGE";
    public static final String DOCKER_FILE_PATH = "/usr/local/DockerFile";
    public static final String TAG_NAME = "TEST-TAG";
    public static final String MEMORY_LIMIT = "1g";
    public static final String MEMORY_SWAP = "2g";
    public static final String MEMORY_NODES = "1-2";
    public static final String CPU_CONSTRAINT = "1-3";
    public static final String CONTAINER_ID = "86686babb094";
    public static final int CPU_QUOTA = 50;
    public static final int CPU_PERIOD = 1;
    public static final int CPU_SHARES = 2;

    public static final String DOCKER_HOST_KEY = "DOCKER_HOST";
}
