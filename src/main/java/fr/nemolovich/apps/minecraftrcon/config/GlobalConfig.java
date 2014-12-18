/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.config;

import java.util.Properties;

/**
 *
 * @author Nemolovich
 */
public class GlobalConfig extends Properties {
    public static final String PLAYERS_IP_AVAILABLE="players.ip.available";
    public static final String PLAYERS_IP_COMMAND="players.ip.command";
    
    private static final GlobalConfig INSTANCE;
    
    static {
        INSTANCE=new GlobalConfig();
    }

    private GlobalConfig() {
    }

    public static GlobalConfig getInstance() {
        return INSTANCE;
    }
}
