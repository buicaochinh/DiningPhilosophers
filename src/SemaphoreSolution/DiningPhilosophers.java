package SemaphoreSolution;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiningPhilosophers {
    private static int eaten = 0; // number of philosophers finished eating
    private static int printStatements = 0; // number of statements printed
    private static final int N = 5; // number of total philosophers
    private static Philosopher[] philosophers = new Philosopher[N];
    private static Chopstick[] Chopsticks = new Chopstick[N];

    static void updateEaten() {
        eaten++;
        if (eaten == N) {
            System.out.println("All 5 philosophers have successfully completed dinner!");
            System.exit(0);
        }
    }

    static void updateEatenStatus() {
        printStatements++;
        if (printStatements % 4 == 0) {
            System.out.println("Till now num of philosophers completed dinner is " + eaten);
        }
    }

    static class Chopstick {
        private final Semaphore mutex = new Semaphore(1); // control access to limited resource to avoid deadlock
        private final int id;

        Chopstick(int id) {
            this.id = id + 1;
        }

        void acquire() {
            mutex.down();
        }

        void release() {
            mutex.up();
        }

        boolean isAvailable() {
            return mutex.getValue() != 0;
        }

        int getID() {
            return this.id;
        }
    }

    static class Philosopher extends Thread {
        private final int id;
        private final Chopstick leftChopstick;
        private final Chopstick rightChopstick;

        Philosopher(int id, Chopstick leftChopstick, Chopstick rightChopstick) {
            this.id = id + 1;
            this.leftChopstick = leftChopstick;
            this.rightChopstick = rightChopstick;
        }

        void eat() {
            try {
                Thread.sleep(500);
                System.out.println("Philosopher " + this.id + " completed their dinner");
                updateEaten();
                DiningPhilosophers.updateEatenStatus();
            } catch (InterruptedException ex) {
                Logger.getLogger(DiningPhilosophers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        void tryAcquireChopstick(Chopstick Chopstick) {
            if (!Chopstick.isAvailable()) {
                System.out.println("Philosopher " + this.id + " is waiting for Chopstick " + Chopstick.getID());
                DiningPhilosophers.updateEatenStatus();
            }
            Chopstick.acquire();
            System.out.println("Chopstick " + Chopstick.getID() + " taken by Philosopher " + this.id);
            DiningPhilosophers.updateEatenStatus();
        }

        @Override
        public void run() {
            try {
                // put threads to sleep for random activation
                Thread.sleep(new Random().nextInt(500));
                // try to acquire Chopstick
                this.tryAcquireChopstick(this.leftChopstick);
                this.tryAcquireChopstick(this.rightChopstick);
                // once both Chopsticks have been acquired, eat then release Chopsticks
                this.eat();
                System.out.println("Philosopher " + this.id + " released Chopstick " + this.leftChopstick.getID() + " and Chopstick " + this.rightChopstick.getID());
                this.leftChopstick.release();
                this.rightChopstick.release();
                DiningPhilosophers.updateEatenStatus();
            } catch (InterruptedException ex) {
                Logger.getLogger(DiningPhilosophers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        // init Chopsticks
        for (int i = 0; i < N; i++) {
            Chopsticks[i] = new Chopstick(i);
        }
        // init philosophers with appropriate Chopsticks
        for (int i = 0; i < N; i++) {
            philosophers[i] = new Philosopher(i, Chopsticks[i], Chopsticks[(i + 1) % N]);
            philosophers[i].start();
        }
    }
}
