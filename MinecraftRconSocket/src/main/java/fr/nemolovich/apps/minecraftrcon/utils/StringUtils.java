/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.utils;

/**
 *
 * @author Nemolovich
 */
public class StringUtils {

    public static final String DEFAULT_ARRAY_SEPARATOR = " ";

    public static String parseColorString(String msg) {
        return msg != null ? msg.replaceAll("\u00A7(\\d|[a-f])", "") : "";
    }

    public static final String join(String[] args) {
        return join(DEFAULT_ARRAY_SEPARATOR, args);
    }

    public static final String join(String separator, String[] args) {
        StringBuilder result = new StringBuilder();
        if (args.length > 0) {
            for (String arg : args) {
                if (result.length() > 0) {
                    result.append(separator);
                }
                result.append(arg);
            }
        }

        return result.toString();
    }

    public static final String getCommonStringStart(String str1, String str2) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(str1.length(), str2.length()); i++) {
            char c1 = str1.charAt(i);
            if (c1 != str2.charAt(i)) {
                break;
            }
            result.append(c1);
        }
        return result.toString();
    }
}
