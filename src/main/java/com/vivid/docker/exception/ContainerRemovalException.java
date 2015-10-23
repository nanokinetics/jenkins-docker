package com.vivid.docker.exception;

/**
 * Created by Phil Madden on 9/16/15.
 */
public class ContainerRemovalException extends Exception {

    private final String containerId;

    public ContainerRemovalException(String containerId) {
        super("Unable to remove container with ID: " + containerId);
        this.containerId = containerId;
    }

    public String getContainerId() {
        return containerId;
    }
}
