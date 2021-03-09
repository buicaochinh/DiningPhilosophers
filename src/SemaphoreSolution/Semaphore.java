package SemaphoreSolution;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Semaphore {
    private int value;

    public Semaphore(int value) {
        this.value = value;
    }

    public synchronized void up(){
        if (this.value == 0) {
            notify();
        }
        this.value++;
    }

    public synchronized void down() {
        if (this.value == 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Semaphore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.value--;
    }

    public synchronized int getValue() {
        return this.value;
    }
}