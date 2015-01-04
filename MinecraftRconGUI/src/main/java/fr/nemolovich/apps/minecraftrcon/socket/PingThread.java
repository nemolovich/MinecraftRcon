/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.socket;

import javax.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 *
 * @author Nemolovich
 */
public class PingThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(PingThread.class);
    public static final int DEFAULT_DELAY = 30000;
    private static final int WAIT_DELAY = 60000;
    private static final PingThread INSTANCE;
    private int delay;
    private ClientSocket socket;
    private SwingWorker failAction;
    private volatile boolean threadSuspended;

    static {
        INSTANCE = new PingThread();
    }

    public static PingThread getInstance() {
        return INSTANCE;
    }

    /**
     * Create a thread to send ping on server and repeat with specific delay. If
     * the ping failed, the custom user action will be execute.
     *
     * command.
     */
    private PingThread() {
        super("Ping-Thread");
        this.socket = null;
        this.delay = DEFAULT_DELAY;
        threadSuspended = true;
    }

    @Override
    public void run() {
        LOGGER.info(String.format("Ping thread %s started",
                Thread.currentThread().getName()));
        while (true) {
            try {
                while (this.threadSuspended) {
                    synchronized (this) {
                        LOGGER.info("Ping thread is currently waiting for connection...");
                        this.wait(WAIT_DELAY);
                    }
                }
                if (this.socket != null) {
                    LOGGER.info("Sending ping...");
                    if (!this.socket.ping()) {
                        LOGGER.info("Ping failed!");
                        this.failAction();
                    }
                    LOGGER.info("Ping OK!");
                }
                Thread.sleep(this.delay);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOGGER.error("The ping thread has been stopped", ex);
                break;
            }
        }
        LOGGER.info(String.format("Thread %s closed",
                Thread.currentThread().getName()));
    }

    private void failAction() {
        this.failAction.execute();
    }

    public void setFailAction(SwingWorker action) {
        this.failAction = action;
    }

    /**
     *
     * @param socket {@link ClientSocket} - The client socket to ping.
     */
    public void setSocket(ClientSocket socket) {
        this.socket = socket;
    }

    /**
     *
     * @param delay {@link Integer int} - The delay in milliseconds to send
     * ping.
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void disable() {
        synchronized (this) {
            this.socket = null;
            this.delay = DEFAULT_DELAY;
            this.threadSuspended = true;
        }
    }

    public void enable() {
        synchronized (this) {
            this.threadSuspended = false;
            this.notify();
        }
    }
}
