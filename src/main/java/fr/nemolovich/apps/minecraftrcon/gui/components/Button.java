/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.components;

import fr.nemolovich.apps.minecraftrcon.gui.MainFrame;
import javax.swing.JButton;

/**
 *
 * @author Brian GOHIER
 */
public class Button extends JButton {
    private static final long serialVersionUID = 1L;

    public Button(String text) {
        this(text, text);
    }

    public Button(String text, String tooltip) {
        super(text);
        this.setToolTipText(tooltip);
        this.setFont(MainFrame.MIRIAM_FONT_NORMAL_BOLD);
        this.setMaximumSize(new java.awt.Dimension(97, 26));
        this.setMinimumSize(new java.awt.Dimension(97, 26));
        this.setPreferredSize(new java.awt.Dimension(97, 26));
    }

    public void setWidth(int width) {
        this.setMaximumSize(new java.awt.Dimension(width, 26));
        this.setMinimumSize(new java.awt.Dimension(width, 26));
        this.setPreferredSize(new java.awt.Dimension(width, 26));
    }
}
