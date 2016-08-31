package com.vivid.docker.helper;

import hudson.EnvVars;
import hudson.Util;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Phil Madden on 9/17/15.
 */
public final class FieldHelper {

    public static final String getMacroReplacedFieldValue(String fieldValue, EnvVars envVars) {
        if(StringUtils.isNotBlank(fieldValue)) {
            if(fieldValue.contains("JOB_NAME")) {
                return Util.replaceMacro(fieldValue, envVars).replaceAll(".*/", "");
            }
            return Util.replaceMacro(fieldValue, envVars);
        }
        return fieldValue;
    }

    public static final  Integer[] tokenizeToIntArray(String value) {
        return tokenizeToInts(tokenize(value));
    }

    public static final  Integer[] tokenizeToIntArray(String value, EnvVars envVars) {
        return tokenizeToInts(tokenize(value, envVars));
    }

    public static final  String[] tokenize(String value) {
        if(StringUtils.isNotBlank(value)) {
            return Util.tokenize(value);
        }
        return null;
    }

    public static final  String[] tokenize(String value, EnvVars envVars) {
        if(StringUtils.isNotBlank(value)) {
            String[] tokens = Util.tokenize(value);
            for(int idx = 0; idx < tokens.length; idx++) {
                tokens[idx] = getMacroReplacedFieldValue(tokens[idx], envVars);
            }
            return tokens;
        }
        return null;
    }
    private static Integer[] tokenizeToInts(String[] tokens) {
        if(tokens != null) {
            Integer[] intTokens = new Integer[tokens.length];
            for (int idx = 0; idx < tokens.length; idx++) {
                try {
                    intTokens[idx] = Integer.parseInt(tokens[idx]);
                } catch (NumberFormatException e) {
                    intTokens[idx] = 0;
                }
            }
            return intTokens;
        } return null;
    }
}
