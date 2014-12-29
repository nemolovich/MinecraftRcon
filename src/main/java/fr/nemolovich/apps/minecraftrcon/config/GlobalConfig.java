/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author Nemolovich
 */
public class GlobalConfig extends Properties {

    private static final Logger LOGGER = Logger.getLogger(GlobalConfig.class);

    public static final String PLAYERS_IP_AVAILABLE = "players.ip.available";
    public static final String PLAYERS_IP_COMMAND = "players.ip.command";

    private static final String CONFIG_FILE_PATH = "global_config.cfg";

    private static final GlobalConfig INSTANCE;

    static {
        INSTANCE = new GlobalConfig();
    }

    private GlobalConfig() {
        String configFileName = CONFIG_FILE_PATH;
        String configFileProperty = System.getProperty("configFile");
        if (configFileProperty != null && !configFileProperty.isEmpty()) {
            configFileName = configFileProperty;
        }
        try {
            File configFile = new File(configFileName);
            if (configFile.exists()) {
                LOGGER.info(String.format("Loading file '%s'...",
                    configFileName));
                this.load(new FileReader(configFile));
                LOGGER.info(String.format("Config loaded from '%s'",
                    configFileName));
            } else {
                throw new IOException("Can not locate config file");
            }
        } catch (IOException ex) {
            LOGGER.warn(String.format(
                "Can not load config file '%s'. Using default configuration.",
                configFileName), ex);
        }
    }

    public static GlobalConfig getInstance() {
        return INSTANCE;
    }
}
