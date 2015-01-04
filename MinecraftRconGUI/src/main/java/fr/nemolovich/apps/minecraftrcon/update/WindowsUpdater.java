/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.update;

/**
 *
 * @author Nemolovich
 */
public class WindowsUpdater extends Updater {

    public WindowsUpdater(String oldJar, String newJar,
        String... optArgs) {
        super(Updater.UPDATE_FILE_WIN.split(" "), oldJar, newJar, optArgs);
    }

}
