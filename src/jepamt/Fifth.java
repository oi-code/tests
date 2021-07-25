package jepamt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Fifth {
    public static void main(String... args) {
	ColsoleApplication cc = new ColsoleApplication();
	cc.init();
    }
}

//TextFile
class FileSystem {

    private List<Directory> directories;

    class File {
	private String fileName;
	private Object data;

	public File(String fn, Object d) {
	    fileName = fn;
	    data = d;
	}

	public String getFileName() {
	    return fileName;
	}

	public void setFileName(String fn) {
	    fileName = fn;
	}

	public Object getData() {
	    return data;
	}

	public void setData(Object data) {
	    this.data = data;
	}

	public void addData(Object data) {
	    // logic for copy data from one to second object
	}

	public void deleteDate(Object data) {
	    // logic for remove data from object
	}
    }

    class Directory {
	private List<File> files;

	public List<File> getFiles() {
	    return files;
	}

	public void setFiles(List<File> files) {
	    this.files = files;
	}

	public void addFile(File file) {
	    files.add(file);
	}

	public boolean deleteFile(File f) {
	    return files.remove(f);
	}
    }

}

class Payment {

    private Basket basket;

    public Payment() {
	basket = new Basket();
    }

    class Basket {
	private List<Item> items;

	public void addItem(Item item) {
	    items.add(item);
	}

	public boolean removeItem(Item item) {
	    return items.remove(item);
	}

	public int getTotalPrice() {
	    return items.stream().mapToInt(e -> e.getPrice()).sum();
	}
    }

    class Item {
	// item description with constructor, methods, title, price, ect
	private int price;

	public int getPrice() {
	    return price;
	}

	@Override
	public int hashCode() {
	    return 0;
	}

	@Override
	public boolean equals(Object o1) {
	    return false;
	}
    }

    public void addItem(Item item) {
	basket.addItem(item);
    }

    public boolean removeItem(Item item) {
	return basket.removeItem(item);
    }

}

class Calendarik {

    private Holidays holidays;

    public Calendarik() {
	holidays = new Holidays();
    }

    public boolean isHoliday(Date d) {
	return holidays.isHoliday(d);
    }

    public void addHiloday(Date d, String description) {
	holidays.addHoliday(d, description);
    }

    public void removeHoliday(Date d) {
	holidays.removeHoliday(d);
    }

    private class Holidays {
	Map<Date, String> holidays;

	public Holidays() {
	    holidays = new HashMap<>();
	}

	public void addHoliday(Date d, String description) {
	    holidays.put(d, description);
	}

	public boolean isHoliday(Date d) {
	    return holidays.containsKey(d);
	}

	public String removeHoliday(Date d) {
	    return holidays.remove(d);
	}
    }
}

//let's imagine each inner class is a separate package
class ColsoleApplication {

    private DragonCave dragonCave = new DragonCave();
    private BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public void init() {
	String chose = "";
	while (true) {
	    System.out.println("Chose action: ");
	    System.out.println("1: show all treasures in cave.");
	    System.out.println("2: show the expensivest treasure.");
	    System.out.println("3: chose treasures for price.");
	    System.out.println("4: exit.");
	    int input = input();
	    if (input < 0) {
		continue;
	    } else {
		action(input);
	    }
	}
    }

    private void showAllTreasures() {
	dragonCave.showAllTreasures();
    }

    private void showMaxPrice() {
	System.out.println(dragonCave.getTheExpensivestTreasure());
    }

    private void getTreasuresForPrice(int price) {
	List<Treasure> treasures = dragonCave.getBag(price);
	System.out.println(treasures);
	int checkSum = treasures.stream().mapToInt(e -> e.getPrice()).sum();
	System.err.println("CHECK: " + checkSum);
    }

    private void action(int action) {
	switch (action) {
	case 1 -> {
	    showAllTreasures();
	    break;
	}
	case 2 -> {
	    showMaxPrice();
	    break;
	}
	case 3 -> {
	    System.out.println("Input price:");
	    int price = input();
	    System.out.println("Result:");
	    getTreasuresForPrice(price);
	    break;
	}
	case 4 -> {
	    System.exit(0);
	}
	default -> {
	    break;
	}
	}
    }

    private int input() {
	try {
	    int input = Integer.valueOf(bufferedReader.readLine());
	    return input;
	} catch (Exception e) {
	    System.out.println("choose another option.");
	    return -1;
	}
    }

    class DragonCave {

	private List<Treasure> treasures = new ArrayList<>();
	private List<Treasure> bag = new ArrayList<>();
	private int startIndex = 0;
	private int targetPrice = 0;
	{
	    for (int i = 0; i < 100; i++) {
		Treasure t = new Treasure(String.valueOf(i), // i);
			ThreadLocalRandom.current().nextInt(999));
		treasures.add(t);
	    }
	    // System.out.println(treasures);
	}

	public void addTreasure(Treasure t) {
	    treasures.add(t);
	}

	public void showAllTreasures() {
	    treasures.stream().forEach(System.out::println);
	}

	public int getTheExpensivestTreasure() {
	    return treasures.stream().mapToInt(e -> e.getPrice()).max().getAsInt();
	}

	public List<Treasure> getBag(int price) {
	    bag.clear();
	    targetPrice = price;
	    searchTreasures(0, 0);
	    return bag;
	}

	// there is an error in algorithm, but it works in the most cases
	private void searchTreasures(int index, int weight) {
	    int start = treasures.get(index).getPrice();
	    int check = start + weight;
	    if (check == targetPrice) {
		bag.add(treasures.get(index));
		System.out.println("found");
		return;
	    } else if (index >= treasures.size() - 1) {
		if (startIndex == treasures.size() - 1) {
		    System.out.println("not found.");
		    bag.clear();
		    return;
		}
		bag.clear();
		searchTreasures(++startIndex, 0);
	    } else if (check < targetPrice) {
		bag.add(treasures.get(index));
		searchTreasures(++index, check);
	    } else if (check > targetPrice) {
		searchTreasures(++index, weight);
	    }

	}
    }

    class Treasure {
	private String description;
	private int price;

	public Treasure(String d, int p) {
	    description = d;
	    price = p;
	}

	public String getDescription() {
	    return description;
	}

	public int getPrice() {
	    return price;
	}

	@Override
	public String toString() {
	    return "Treasure [description=" + description + ", price=" + price + "]";
	}

    }

}