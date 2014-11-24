/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.update;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author Nemolovich
 */
public abstract class Updater {

    public static final String UPDATE_FILE_WIN = "cmd /c update.cmd";

    private final String[] args;
    private static final Logger LOGGER = Logger.getLogger(Updater.class);

    public Updater(String[] executable, String oldJar, String newJar,
        String... optArgs) {
        assert executable != null && executable.length >= 1 && oldJar != null
            && !oldJar.isEmpty() && newJar != null && !newJar.isEmpty();
        this.args = new String[executable.length + 2 + optArgs.length];

        int i = 0;
        for (String arg : executable) {
            this.args[i++] = arg;
        }
        this.args[i++] = oldJar;
        this.args[i++] = newJar;
        for (String arg : optArgs) {
            this.args[i++] = arg;
        }
    }

    public void update() throws IOException {
        ProcessBuilder process = new ProcessBuilder(this.args);
        File errFile = new File("updateError.log");
        process.redirectError(errFile);
        LOGGER.info("Restarting application...");
        process.start();
    }
}
