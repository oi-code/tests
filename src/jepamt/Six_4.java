package jepamt;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Six_4 {
    public static void main(String[] as) {
	Port p = new Port();
	p.init();
    }
}

class Boat {
    private final int boatId;
    private final int maxLoad;
    private AtomicInteger currentLoad;

    public Boat(int boatId, int currentLoad, int maxLoad) {
	super();
	this.boatId = boatId;
	this.currentLoad = new AtomicInteger(currentLoad);
	this.maxLoad = maxLoad;
    }

    public int getBoatId() {
	return boatId;
    }

    public int getCurrentLoad() {
	return currentLoad.get();
    }

    public int getMaxLoad() {
	return maxLoad;
    }

    public void addContainer() {
	if (currentLoad.get() < maxLoad) {
	    currentLoad.getAndIncrement();
	}
    }

    public void removeContainer() {
	if (currentLoad.get() > 0) {
	    currentLoad.getAndDecrement();
	}
    }
}

class Port {
    private AtomicInteger currentLoad = new AtomicInteger(100);
    private final int maxContainerCapacity = 5000;
    private List<Dock> docks = new CopyOnWriteArrayList<>();
    private List<Boat> boats = new CopyOnWriteArrayList<>();
    private ExecutorService exec;

    public Port() {

    }

    void init() {
	for (int i = 0; i < 10; i++) {
	    Dock d = new Dock(i, this);
	    docks.add(d);
	}
	exec = Executors.newFixedThreadPool(docks.size());
	for (int i = 0; i < 100; i++) {
	    int max = ThreadLocalRandom.current().nextInt(1001);
	    Boat b = new Boat(i, max, ThreadLocalRandom.current().nextInt(0, max + 1));
	    boats.add(b);
	}
	Thread t = new Thread(() -> {
	    while (true) {
		if (currentLoad.get() > 0) {
		    currentLoad.getAndDecrement();
		    System.out.println(currentLoad.get());
		    try {
			Thread.currentThread().sleep(10);
		    } catch (InterruptedException e) {
			System.out.println("error in monitor thread after sleep");
		    }
		} else {
		    try {
			synchronized (currentLoad) {
			    currentLoad.wait();
			}
		    } catch (InterruptedException e) {
			System.out.println("error in monitor thread, wait section");
		    }
		}
	    }
	});
	t.start();
	while (true) {
	    try {
		Thread.currentThread().sleep(2000);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    currentLoad.set(412 + currentLoad.get());
	    synchronized (currentLoad) {
		currentLoad.notifyAll();
	    }
	}
    }

    public int getMaxContainerCapacity() {
	return maxContainerCapacity;
    }

    public int getCurrentLoad() {
	return currentLoad.get();
    }

}

class Dock {
    private Boat boat;
    private final int dockId;
    private boolean isBusy = false;
    private Port port;

    public Dock(int dockId, Port port) {
	super();
	this.dockId = dockId;
	this.port = port;
    }

    public int getDockId() {
	return dockId;
    }

    public void setBoat(Boat boat) {
	if (isBusy) {
	    return;
	} else {
	    this.boat = boat;
	    isBusy = true;
	}
    }

    public void removeBoat() {
	boat = null;
	isBusy = false;
    }

    public void loadContainers() {
	while (isBusy && boat.getCurrentLoad() < boat.getMaxLoad() && port.getCurrentLoad() > 0) {
	    boat.addContainer();
	}
	System.out.println("Boat " + boat.getBoatId() + " loaded");
    }

    public void unloadContainers() {
	while (isBusy && boat.getCurrentLoad() > 0 && port.getCurrentLoad() < port.getMaxContainerCapacity()) {
	    boat.removeContainer();
	}
	System.out.println("Boat " + boat.getBoatId() + " unloaded");
    }
}
