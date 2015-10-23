package com.vivid.docker.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Phil Madden on 9/17/15.
 */
public final class StreamUtil {

    public static final void closeStream(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
