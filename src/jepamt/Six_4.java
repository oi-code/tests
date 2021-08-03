package jepamt;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
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
    @Override
    public String toString() {
	return "Boat [boatId=" + boatId + ", maxLoad=" + maxLoad + ", currentLoad=" + currentLoad + "]";
    }

    private int currentLoad;
    private boolean state;

    public Boat(int boatId, int currentLoad, int maxLoad, boolean state) {
	super();
	this.boatId = boatId;
	this.currentLoad = currentLoad;
	this.maxLoad = maxLoad;
	this.state = state;
    }

    public boolean getState() {
	return state;
    }

    public void setState(boolean state) {
	this.state = state;
    }

    public int getBoatId() {
	return boatId;
    }

    public int getCurrentLoad() {
	return currentLoad;
    }

    public int getMaxLoad() {
	return maxLoad;
    }

    public boolean addContainer() {
	if (currentLoad < maxLoad) {
	    currentLoad++;
	    return true;
	}
	return false;
    }

    public boolean removeContainer() {
	if (currentLoad > 0) {
	    currentLoad--;
	    return true;
	}
	return false;
    }
}

class Port {
    private AtomicInteger currentLoad = new AtomicInteger(5000);
    private final int maxContainerCapacity = 5000;
    private List<Dock> docks = new CopyOnWriteArrayList<Dock>();
    private Queue<Boat> boats = new LinkedBlockingQueue<Boat>();
    private ExecutorService exec;

    void init() {
	for (int i = 0; i < 10; i++) {
	    Dock d = new Dock(i, this);
	    docks.add(d);
	}

	exec = Executors.newFixedThreadPool(docks.size());
	generator(this);

	for (Dock d : docks) {
	    exec.execute(d);
	}

    }

    private void generator(Port p) {
	new Thread(() -> {
	    ThreadLocalRandom tlr = ThreadLocalRandom.current();
	    int i = 0;
	    boolean stop = false;
	    while (!Thread.currentThread().isInterrupted()) {
		if (i > 25) {
		    stop = true;
		}
		int max = tlr.nextInt(1, 1001);
		boolean b = false;// = tlr.nextBoolean();
		if (tlr.nextInt(100) < 40) {
		    b = true;
		}
		if (!stop) {
		    Boat boat = new Boat(i++, tlr.nextInt(0, max), max, b);
		    boats.add(boat);
		    System.err.println("New boat enter in port:" +boat);
		    synchronized (p) {
			p.notifyAll();
		    }
		}
		try {
		    Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}).start();
    }

    public int getMaxContainerCapacity() {
	return maxContainerCapacity;
    }

    public int getCurrentLoad() {
	return currentLoad.get();
    }

    public boolean addContainer() {
	synchronized (currentLoad) {
	    if (currentLoad.get() < maxContainerCapacity) {
		currentLoad.getAndIncrement();
		return true;
	    }
	}
	return false;

    }

    public boolean removeContainer() {
	synchronized (currentLoad) {
	    if (currentLoad.get() > 0) {
		currentLoad.getAndDecrement();
		return true;
	    }
	}
	return false;
    }

    public Boat getBoat() {
	return boats.poll();
    }

    public boolean addBoat(Boat b) {
	return boats.offer(b);
    }

}

class Dock implements Runnable {

    private final int dockId;
    private final Port port;

    public Dock(int dockId, Port port) {
	super();
	this.dockId = dockId;
	this.port = port;
    }

    public int getDockId() {
	return dockId;
    }

    @Override
    public void run() {
	while (!Thread.currentThread().isInterrupted()) {
	  /*  Boat boat;	    
	    while ((boat = port.getBoat()) == null) {
		try {
		    System.out.printf("dock %d sleep...\n", dockId);
		    TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
		    System.out.println(Thread.currentThread().getName() + " interrupted.");
		    e.printStackTrace();
		}
	    }
	    int startLoad = boat.getCurrentLoad();
	    if (boat.getState()) {
		while (boat.getCurrentLoad() > 0) {
		    if (port.addContainer()) {
			boat.removeContainer();
		    }
		    if (port.getCurrentLoad() == port.getMaxContainerCapacity()) {
			break;
		    }
		}
	    } else {
		while (boat.getCurrentLoad() < boat.getMaxLoad()) {
		    if (port.removeContainer()) {
			boat.addContainer();
		    }
		    if (port.getCurrentLoad() <1) {
			break;
		    }
		}
	    }
	    try {
		TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(200, 1001));
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    if (boat.getCurrentLoad() == 0) {
		boat.setState(false);
	    } else {
		boat.setState(true);
	    }
	    System.out.printf(
		    "boatID: %d, portLoad: %d, startBoatLoad: %d, currentBoatState: %s, boatMaxLoad %d, boatCurrentLoad %d\n",
		    boat.getBoatId(), port.getCurrentLoad(), startLoad, boat.getState(), boat.getMaxLoad(),
		    boat.getCurrentLoad());
	    port.addBoat(boat);*/
	    Boat boat;	    
	    while ((boat = port.getBoat()) == null) {
		try {
		    System.out.printf("dock %d wait...\n", dockId);
		    synchronized (port) {
			port.wait();
		    }
		} catch (InterruptedException e) {
		    System.out.println(Thread.currentThread().getName() + " interrupted.");
		    e.printStackTrace();
		}
	    }
	    int startPortLoad=port.getCurrentLoad();
	    int startLoad = boat.getCurrentLoad();
	    if (boat.getState()) {
		while (boat.getCurrentLoad() > 0) {
		    if (port.addContainer()) {
			boat.removeContainer();
		    }
		    if (port.getCurrentLoad() == port.getMaxContainerCapacity()) {
			break;
		    }
		}
	    } else {
		while (boat.getCurrentLoad() < boat.getMaxLoad()) {
		    if (port.removeContainer()) {
			boat.addContainer();
		    }
		    if (port.getCurrentLoad() <1) {
			break;
		    }
		}
	    }
	    try {
		TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(200, 1001));
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    if (boat.getCurrentLoad() == 0) {
		boat.setState(false);
	    } else {
		boat.setState(true);
	    }
	    String state=boat.getState()==true?"loading ON boat":"unloading FROM boat";
	    System.out.printf("Dock id: %d, boatID: %d, startPortLoad: %d,  startBoatLoad: %d, currentBoatState: %s, boatMaxLoad %d, boatCurrentLoad %d, afterWorkPortLoad: %d\n",
		    dockId, boat.getBoatId(), port.getCurrentLoad(), startLoad, state, boat.getMaxLoad(),
		    boat.getCurrentLoad(), startPortLoad);
	    port.addBoat(boat);
	    synchronized (port) {
		port.notifyAll();
	    }
	}
    }
}
