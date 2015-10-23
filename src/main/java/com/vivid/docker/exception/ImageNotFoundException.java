package com.vivid.docker.exception;

/**
 * Created by Phil Madden on 9/16/15.
 */
public class ImageNotFoundException extends Exception {

    public ImageNotFoundException(String imageName, String tag) {
        super("Unable to locate image \"" + imageName +  "\" by tag \"" + tag + "\".");
    }

    public ImageNotFoundException(String imageName, String tag, String fallbackTag) {
        super("Unable to locate image \"" + imageName + "\" by tag \"" + tag + " or \"" + fallbackTag + "\".");
    }

}
