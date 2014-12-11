package fr.nemolovich.apps.minecraftrcon.gui.table;

import fr.nemolovich.apps.minecraftrcon.gui.MainFrame;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

public class CustomTable extends JXTable {

    /**
     * UID.
     */
    private static final long serialVersionUID = -8144879202300663838L;
    private Color defaultForeground;
    private Color defaultBackground;

    public CustomTable(CustomTableModel model) {
        this.setModel(model);
        this.initComponents();
    }

    private void initComponents() {
        this.defaultForeground = new Color(170, 170, 170);
        this.defaultBackground = new Color(51, 51, 51);
        this.setBackground(this.defaultBackground);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        this.setForeground(this.defaultForeground);
        this.setAutoStartEditOnKeyStroke(false);
        this.setAutoscrolls(false);
        this.setEditable(false);
        this.setFont(MainFrame.MIRIAM_FONT_SMALL);
        this.setRowSelectionAllowed(false);
        this.getTableHeader().setResizingAllowed(false);
        this.getTableHeader().setReorderingAllowed(false);
        this.getTableHeader().setFont(MainFrame.MIRIAM_FONT_NORMAL_BOLD);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ((ITableModel) this.getModel()).setTableSorter(this);
        this.getRowSorter().toggleSortOrder(0);
    }

    @Override
    public Component prepareRenderer(
        TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (isRowSelected(row)) {
            c.setBackground(Color.decode("#3399FF"));
            c.setForeground(Color.decode("#FFFFFF"));
        } else {
            c.setBackground(this.defaultBackground);
            c.setForeground(this.defaultForeground);
        }
        return c;
    }
}
