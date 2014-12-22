/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.table.frame;

import fr.nemolovich.apps.minecraftrcon.gui.table.CustomTable;
import fr.nemolovich.apps.minecraftrcon.gui.table.listener.CommandListSelectionListener;
import fr.nemolovich.apps.minecraftrcon.gui.table.model.CustomTableModel;
import javax.swing.JButton;

/**
 *
 * @author Nemolovich
 */
public class TableFrameModel {

    private String frameTitle;
    private String frameHeaderLabel;
    private String frameBoxLabel;
    private String frameFilterTooltip;

    private CommandListSelectionListener listener;

    private CustomTable table;
    private CustomTableModel model;

    private JButton[] buttonsList;
    private int nbButton;

    public TableFrameModel() {
        this.buttonsList = new JButton[3];
        this.nbButton = 0;
    }

    public String getFrameTitle() {
        return this.frameTitle;
    }

    public void setFrameTitle(String frameTitle) {
        this.frameTitle = frameTitle;
    }

    public String getFrameHeaderLabel() {
        return this.frameHeaderLabel;
    }

    public void setFrameHeaderLabel(String frameHeaderLabel) {
        this.frameHeaderLabel = frameHeaderLabel;
    }

    public String getFrameBoxLabel() {
        return this.frameBoxLabel;
    }

    public void setFrameBoxLabel(String frameBoxLabel) {
        this.frameBoxLabel = frameBoxLabel;
    }

    public String getFrameFilterTooltip() {
        return this.frameFilterTooltip;
    }

    public void setFrameFilterTooltip(String frameFilterTooltip) {
        this.frameFilterTooltip = frameFilterTooltip;
    }

    public CommandListSelectionListener getListener() {
        return this.listener;
    }

    public void setListener(CommandListSelectionListener listener) {
        this.listener = listener;
    }

    public CustomTableModel getModel() {
        return this.model;
    }

    public void setModel(CustomTableModel model) {
        this.model = model;
    }

    public CustomTable getTable() {
        return this.table;
    }

    public void setTable(CustomTable table) {
        this.table = table;
    }

    public void addButton(JButton button) {
        if (this.nbButton < 3) {
            this.buttonsList[this.nbButton] = button;
            this.nbButton++;
        }
    }
}
