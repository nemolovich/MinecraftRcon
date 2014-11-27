/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui;

import javax.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 *
 * @author Nemolovich
 */
public abstract class ParallelTask {

    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    protected Object doInBackground() throws Exception {
        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                runTask();
                return value;
            }
        };
        worker.execute();
        return worker.get();
    }

    public void execute() {
        try {
            runTask();
        } catch (Exception ex) {
            Logger.getLogger(ParallelTask.class).error("Can not run task", ex);
        }
    }

    protected abstract Object runTask() throws Exception;

}
