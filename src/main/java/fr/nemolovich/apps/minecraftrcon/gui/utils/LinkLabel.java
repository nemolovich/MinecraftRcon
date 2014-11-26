/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.JLabel;

/**
 *
 * @author Nemolovich
 */
public class LinkLabel extends JLabel {
    private final String link;

    public LinkLabel(String link) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        this.setForeground(Color.decode("#01A0DB"));

        Font font = this.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        this.setFont(font.deriveFont(attributes));

        this.link = link;
    }

    public String getLink() {
        return link;
    }
    
}
