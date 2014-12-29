/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon;

/**
 *
 * @author Nemolovich
 */
public class StringUtils {

    public static final String DEFAULT_ARRAY_SEPARATOR = " ";

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
}
