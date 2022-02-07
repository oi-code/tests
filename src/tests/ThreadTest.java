package tests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadTest {
    public static void main(String... args) {
	ExecutorService exec=Executors.newCachedThreadPool();
	Forks f=new Forks();
	for(int i=0;i<20;i++) {
	    exec.execute(new Philosoph(i, f));
	}
    }
}

class Philosoph implements Runnable {
    
    Forks forks;
    int count;

    public Philosoph(int c, Forks f) {
	forks = f;
	count = c;
    }

    @Override
    public void run() {
	boolean succes = false;
	while (!Thread.interrupted()&&!succes) {
	    try {
		synchronized (forks) {
		    if (forks.leftFork.get() > 0) {
			if (forks.rightFork.get() > 0) {
			    forks.rightFork.getAndDecrement();
			    forks.leftFork.getAndDecrement();
			    succes = true;
			}
		    }
		}
		Thread.sleep(2000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    } finally {
		if (succes) {
		    forks.leftFork.addAndGet(1);
		    forks.rightFork.addAndGet(1);
		}
	    }

	}
	System.out.printf("Philosoph %d ate%n", count);
    }

}

class Forks {
    AtomicInteger leftFork = new AtomicInteger(5);
    AtomicInteger rightFork = new AtomicInteger(5);
}
