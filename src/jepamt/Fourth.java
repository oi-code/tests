package jepamt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Fourth {

    public static String generateString() {
	ThreadLocalRandom tlr = ThreadLocalRandom.current();
	String result = "";
	for (int j = 0; j < 10; j++) {
	    if (tlr.nextBoolean()) {
		result += (char) tlr.nextInt(97, 123);
	    } else {
		result += (char) tlr.nextInt(65, 91);
	    }
	}
	return result;
    }

    public static void main(String... args) {

	System.out.println("Students:");
	StudentSolution.solution(10);

	System.out.println("\nTrains:");
	TrainSolution.solution(2);
	TrainSolution.sortByNumber();
	TrainSolution.sortByDestination();

	System.out.println("\nDemicals:");
	DemicalCounterSolution.init();

	System.out.println("\nTime:");
	TimeViewSolution.init();

	System.out.println("\nCustomers:");
	CustomerSolution.showBuyers();
	CustomerSolution.showBuyersBetweenCardNumbers(4, 9);

	System.out.println("\nBooks:");
	BookSolution.showAuthorBook("kek");
	BookSolution.showBookAfteryear(2021);
	BookSolution.showPublishHouse("cheburek");

    }
}

class Test1 {
    private int n1, n2;

    public int getN1() {
	return n1;
    }

    public void setN1(int n1) {
	this.n1 = n1;
    }

    public int getN2() {
	return n2;
    }

    public void setN2(int n2) {
	this.n2 = n2;
    }

    public int getSum() {
	return n1 + n2;
    }

    public int getMax() {
	return Math.max(n2, n1);
    }
}

class Test2 {
    private int n1, n2;

    public Test2() {
	n1 = 1;
	n2 = 2;
    }

    public Test2(int n1, int n2) {
	super();
	this.n1 = n1;
	this.n2 = n2;
    }

    public int getN1() {
	return n1;
    }

    public void setN1(int n1) {
	this.n1 = n1;
    }

    public int getN2() {
	return n2;
    }

    public void setN2(int n2) {
	this.n2 = n2;
    }

}

class Student {
    private String firstName, lastName;
    private int groupNumber;
    private int[] perfomance;

    public Student(String firstName, String lastName, int groupNumber, int[] perfomance) {
	super();
	this.firstName = firstName;
	this.lastName = lastName;
	this.groupNumber = groupNumber;
	this.perfomance = perfomance;
    }

    public int[] getPerfomance() {
	return perfomance;
    }

    public void setPerfomance(int[] perfomance) {
	this.perfomance = perfomance;
    }

    public String getFirstName() {
	return firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public int getGroupNumber() {
	return groupNumber;
    }

}

class StudentSolution {

    static List<Student> sol;
    static {
	sol = new ArrayList<>();
	for (int i = 0; i < 10; i++) {
	    int[] arr = ThreadLocalRandom.current().ints(10, 11).limit(5).toArray();
	    Student s = new Student(Fourth.generateString(), "lastName", i, arr);
	    sol.add(s);
	}
    }

    public static void solution(int rate) {
	boolean check = false;
	for (Student s : sol) {
	    int[] temp = s.getPerfomance();
	    for (int i = 0; i < temp.length; i++) {
		if (temp[i] != rate) {
		    check = true;
		    break;
		}
	    }
	    if (!check) {
		System.out.printf("First name: %s, group: %d\n", s.getFirstName(), s.getGroupNumber());
	    }
	}
    }
}

class Train {

    private String destination;
    private int trainNumber;
    private Date departureTime;

    public Train(String endPoint, int trainNumber, Date departureTime) {
	super();
	this.destination = endPoint;
	this.trainNumber = trainNumber;
	this.departureTime = departureTime;
    }

    public String getDestination() {
	return destination;
    }

    public int getTrainNumber() {
	return trainNumber;
    }

    public Date getDepartureTime() {
	return departureTime;
    }

    @Override
    public String toString() {
	return "Train [destination=" + destination + ", trainNumber=" + trainNumber + ", departureTime=" + departureTime
		+ "]";
    }

}

class TrainSolution {

    static List<Train> trains = new ArrayList<>();
    static {
	for (int i = 0; i < 10; i++) {
	    Train t = new Train(Fourth.generateString(), ThreadLocalRandom.current().nextInt(100), new Date());
	    trains.add(t);
	}
    }

    public static void solution(int trainNumber) {
	Train temp = null;
	for (Train t : trains) {
	    if (t.getTrainNumber() == trainNumber) {
		temp = t;
		break;
	    }
	}
	if (temp != null) {
	    System.out.println(temp);
	} else {
	    System.out.println("No train with number: " + trainNumber);
	}
    }

    public static void sortByNumber() {
	System.out.println(trains.toString());
	Comparator<Train> comparator = new Comparator<Train>() {
	    @Override
	    public int compare(Train o1, Train o2) {
		return o1.getTrainNumber() == o2.getTrainNumber() ? 0
			: o1.getTrainNumber() < o2.getTrainNumber() ? -1 : 1;
	    }
	};
	Collections.sort(trains, comparator);
	System.out.println(trains.toString());
    }

    public static void sortByDestination() {
	System.out.println(trains);
	trains = trains.stream()
		.sorted(Comparator.comparing(Train::getDestination).thenComparing(Train::getDepartureTime))
		.collect(Collectors.toList());
	System.out.println(trains);
    }
}

class DemicalCounter {

    private final int maxValue = 200;
    private final int minValue = 0;
    private int now = 0;

    public DemicalCounter() {
    }

    public DemicalCounter(int count) {
	if (count > maxValue || count < minValue) {
	    throw new RuntimeException();
	}
	now = count;
    }

    public void increment() {
	if (now < maxValue) {
	    now++;
	}
    }

    public void decrement() {
	if (now > minValue) {
	    now--;
	}
    }

    public String toString() {
	return String.valueOf(now);
    }
}

class DemicalCounterSolution {

    static DemicalCounter dc = new DemicalCounter();

    public static void init() {
	System.out.println(dc);
	dc.increment();
	System.out.println(dc);
	dc.decrement();
	dc = new DemicalCounter(66);
	System.out.println(dc);
	dc.increment();
	System.out.println(dc);
	dc.decrement();
	System.out.println(dc);
	try {
	    dc = new DemicalCounter(222);
	} catch (RuntimeException e) {
	    System.out.println("too big value");
	}
	try {
	    dc = new DemicalCounter(-1);
	} catch (RuntimeException e) {
	    System.out.println("too low value");
	}
    }
}

class TimeView {
    private Date ldt = new Date();

    public void setHour(int hours) {
	if (hours < 0 || hours > 23) {
	    ldt.setHours(0);
	    return;
	}
	ldt.setHours(hours);
    }

    public void setMinutes(int minutes) {
	if (minutes < 0 || minutes > 59) {
	    ldt.setMinutes(0);
	    return;
	}
	ldt.setMinutes(minutes);
    }

    public void setSeconds(int seconds) {
	if (seconds < 0 || seconds > 59) {
	    ldt.setSeconds(0);
	    return;
	}
	ldt.setSeconds(seconds);
    }

    public String toString() {
	return ldt.toString();
    }
}

class TimeViewSolution {
    public static void init() {
	TimeView t = new TimeView();
	System.out.println(t);
	t.setHour(12);
	System.out.println(t);
	t.setMinutes(213);
	System.out.println(t);
	t.setSeconds(-1);
	System.out.println(t);
    }
}

class Triangle {
    private int firstAngle, secondAngle, thirdAngle;
}

class TriangleSolution {

}

class Customer {
    private int id, cardNumber, accountNumber;
    private String lastName, firstName, thirdName, addrress;

    public Customer(int id, int cardNumber, int accountNumber, String lastName, String firstName, String thirdName,
	    String addrress) {
	super();
	this.id = id;
	this.cardNumber = cardNumber;
	this.accountNumber = accountNumber;
	this.lastName = lastName;
	this.firstName = firstName;
	this.thirdName = thirdName;
	this.addrress = addrress;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getCardNumber() {
	return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
	this.cardNumber = cardNumber;
    }

    public int getAccountNumber() {
	return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
	this.accountNumber = accountNumber;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getThirdName() {
	return thirdName;
    }

    public void setThirdName(String thirdName) {
	this.thirdName = thirdName;
    }

    public String getAddrress() {
	return addrress;
    }

    public void setAddrress(String addrress) {
	this.addrress = addrress;
    }

    @Override
    public String toString() {
	return "Customer [id=" + id + ", cardNumber=" + cardNumber + ", accountNumber=" + accountNumber + ", lastName="
		+ lastName + ", firstName=" + firstName + ", thirdName=" + thirdName + ", addrress=" + addrress + "]";
    }
}

class CustomerSolution {
    static List<Customer> customers;
    static {
	customers = new ArrayList<>();
	for (int i = 0; i < 10; i++) {
	    Customer temp = new Customer(i, i, i, Fourth.generateString(), Fourth.generateString(),
		    Fourth.generateString(), Fourth.generateString());
	    customers.add(temp);
	}
    }

    public static void showBuyers() {
	Comparator<Customer> comparator = new Comparator<Customer>() {
	    @Override
	    public int compare(Customer o1, Customer o2) {
		String name1 = o1.getFirstName() + " " + o1.getLastName() + " " + o1.getThirdName();
		String name2 = o2.getFirstName() + " " + o2.getLastName() + " " + o2.getThirdName();
		return name1.compareTo(name2);
	    }
	};
	Collections.sort(customers, comparator);
	System.out.println(customers);
    }

    public static void showBuyersBetweenCardNumbers(int lowBound, int highBound) {
	for (Customer customer : customers) {
	    int temp = customer.getCardNumber();
	    if (temp >= lowBound && temp <= highBound) {
		System.out.println(customer);
	    }
	}
    }
}

class Book {
    private String title, publishingHouse, cover;
    private List<String> authors;
    private int id, yearOfPublishing, sheetCount, price;

    public Book(String title, String publishingHouse, String cover, List<String> authors, int id, int yearOfPublishing,
	    int sheetCount, int price) {
	super();
	this.title = title;
	this.publishingHouse = publishingHouse;
	this.cover = cover;
	this.authors = authors;
	this.id = id;
	this.yearOfPublishing = yearOfPublishing;
	this.sheetCount = sheetCount;
	this.price = price;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getPublishingHouse() {
	return publishingHouse;
    }

    public void setPublishingHouse(String publishingHouse) {
	this.publishingHouse = publishingHouse;
    }

    public String getCover() {
	return cover;
    }

    public void setCover(String cover) {
	this.cover = cover;
    }

    public List<String> getAuthors() {
	return authors;
    }

    public void setAuthors(List<String> authors) {
	this.authors = authors;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getYearOfPublishing() {
	return yearOfPublishing;
    }

    public void setYearOfPublishing(int yearOfPublishing) {
	this.yearOfPublishing = yearOfPublishing;
    }

    public int getSheetCount() {
	return sheetCount;
    }

    public void setSheetCount(int sheetCount) {
	this.sheetCount = sheetCount;
    }

    public int getPrice() {
	return price;
    }

    public void setPrice(int price) {
	this.price = price;
    }

    @Override
    public String toString() {
	return "Book [title=" + title + ", publishingHouse=" + publishingHouse + ", cover=" + cover + ", authors="
		+ authors + ", id=" + id + ", yearOfPublishing=" + yearOfPublishing + ", sheetCount=" + sheetCount
		+ ", price=" + price + "]";
    }
}

class BookSolution {
    private static List<Book> books = new ArrayList<>();
    static {
	for (int i = 0; i < 10; i++) {
	    List<String> authors = new ArrayList<>();
	    for (int j = 0; j < ThreadLocalRandom.current().nextInt(5); j++) {
		authors.add(Fourth.generateString());
	    }
	    int publishingYear = ThreadLocalRandom.current().nextInt(1444, 2022);
	    Book temp = new Book(Fourth.generateString(), Fourth.generateString(), Fourth.generateString(), authors, i,
		    publishingYear, i, i);
	    books.add(temp);
	}
	books.get(5).getAuthors().add("kek");
	books.get(5).setTitle("Found");

	books.get(6).setPublishingHouse("cheburek");
	books.get(6).setTitle("Found2");

	books.get(3).setYearOfPublishing(2021);
	books.get(3).setTitle("Found3");
    }

    public static void showAuthorBook(String author) {
	for (Book book : books) {
	    for (String author_ : book.getAuthors()) {
		if (author_.equals(author)) {
		    System.out.println(book.getTitle());
		}
	    }
	}
    }

    public static void showPublishHouse(String house) {
	for (Book book : books) {
	    if (book.getPublishingHouse().equals(house)) {
		System.out.println(book.getTitle());
	    }
	}
    }

    public static void showBookAfteryear(int year) {
	for (Book book : books) {
	    if (book.getYearOfPublishing() == year) {
		System.out.println(book.getTitle());
	    }
	}
    }
}

class AirLine {
    private String destination;
    private int flightNumber, airplaneType, dayOfWeek;
    private Date date;

    public AirLine(String destination, int flightNumber, int airplaneType, int dayOfWeek, Date date) {
	super();
	this.destination = destination;
	this.flightNumber = flightNumber;
	this.airplaneType = airplaneType;
	this.dayOfWeek = dayOfWeek;
	this.date = date;
    }

    public String getDestination() {
	return destination;
    }

    public void setDestination(String destination) {
	this.destination = destination;
    }

    public int getFlightNumber() {
	return flightNumber;
    }

    public void setFlightNumber(int flightNumber) {
	this.flightNumber = flightNumber;
    }

    public int getAirplaneType() {
	return airplaneType;
    }

    public void setAirplaneType(int airplaneType) {
	this.airplaneType = airplaneType;
    }

    public int getDayOfWeek() {
	return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
	this.dayOfWeek = dayOfWeek;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    @Override
    public String toString() {
	return "AirLine [destination=" + destination + ", flightNumber=" + flightNumber + ", airplaneType="
		+ airplaneType + ", dayOfWeek=" + dayOfWeek + ", date=" + date + "]";
    }

}

class AirLineSolution {
    private static List<AirLine> airlines = null;

    public static void getFlightForDestination(String destination) {
	airlines.stream().filter(e -> e.getDestination().equals(destination)).forEach(System.out::println);
    }

    public static void getFlightForOdyOfWeak(int dayOfWeak) {
	airlines.stream().filter(e -> e.getDayOfWeek() == dayOfWeak).forEach(System.out::println);
    }

    public static void getFlightForDayOfWeekAndDateBiggerThan(int dayOfWeek, Date date) {
	airlines.stream().filter(e -> {
	    if (e.getDayOfWeek() == dayOfWeek && e.getDate().after(date)) {
		return true;
	    } else {
		return false;
	    }
	}).forEach(System.out::println);
    }
}

class Text {

    private String title;
    private List<Sentence> sentences = new ArrayList<>();

    class Sentence {

	private List<Word> words = new ArrayList<>();

	public Sentence(List<Word> words) {
	    words.addAll(words);
	}

	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    words.stream().forEach(e -> {
		sb.append(e + " ");
	    });
	    return sb.toString();
	}
    }

    class Word {
	private String word;

	public Word(String word) {
	    super();
	    this.word = word;
	}

	public String getWord() {
	    return word;
	}

	public String toString() {
	    return word;
	}
    }

    public void addText(Sentence sentence) {
	sentences.add(sentence);
    }

    public void showText() {
	sentences.stream().forEach(e -> System.out.print(e + " "));
    }

    public void addText(String text) {
	Text.Word word = new Text.Word(text);
	Sentence sentence = new Sentence(List.of(word));
	addText(sentence);
    }

    public void showTitle() {
	System.out.println(title);
    }

}

class Car {

    private String mark;
    private Engine engine;
    private List<Wheel> wheels;

    class Wheel {

    }

    class Engine {

    }

    public void run() {
	System.out.println("car run");
    }

    public void addFuel() {
	System.out.println("fuel added");
    }

    public void changeWheel(Wheel wheel, int number) {
	wheels.set(number, wheel);
    }

    public void showMark() {
	System.out.println(mark);
    }

}

class Government {
    private List<Region> regions;
    private City city;

    class Region {
	private City city;
	private List<District> districts;

	public City getCity() {
	    return city;
	};

    }

    class District {
	private List<String> districts;
    }

    class City {
	private String name;

	public City(String n) {
	    name = n;
	}

	public String getCity() {
	    return name;
	}
    }

    public void showCity() {
	city.getCity();
    }

    public void showRegionQuantity() {
	System.out.println(regions.size());
    }

    public void showSquare() {
	int square = 0;
	// area calculating here...
	System.out.println(square);
    }

    public void showRegionCities() {
	regions.stream().forEach(e -> System.out.println(e.getCity()));
    }
}

class Account {

    private String userName;
    private List<Billing> billings;

    class Billing {

	boolean isBlocked;
	int cardNumber;
	int moneyAmount;

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + getEnclosingInstance().hashCode();
	    result = prime * result + cardNumber;
	    result = prime * result + (isBlocked ? 1231 : 1237);
	    result = prime * result + moneyAmount;
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    Billing other = (Billing) obj;
	    if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
		return false;
	    if (cardNumber != other.cardNumber)
		return false;
	    if (isBlocked != other.isBlocked)
		return false;
	    if (moneyAmount != other.moneyAmount)
		return false;
	    return true;
	}

	private Account getEnclosingInstance() {
	    return Account.this;
	}
    }

    public void sort(Comparator<Billing> comparator) {
	Collections.sort(billings, comparator);
    }

    public Billing getBilling(Billing billing) {
	if (billings.contains(billing)) {
	    return billing;
	}
	return null;
    }

    public int getSum() {
	int result = billings.stream().mapToInt(e -> e.moneyAmount).sum();
	return result;
    }

    public int getPositiveSum() {
	int result = billings.stream().mapToInt(e -> e.moneyAmount).filter(e -> (e > -1)).sum();
	return result;
    }

    public int getNegativeSum() {
	int result = billings.stream().mapToInt(e -> e.moneyAmount).filter(e -> (e < 0)).sum();
	return result;
    }

}

class TouristsVoucher {
    private List<Object> razlichnieProdlojeniya;

    public void getOptimalWay() {
	String result = "";
	int count = ThreadLocalRandom.current().nextInt(razlichnieProdlojeniya.size());
	for (int i = 0; i < count; i++) {
	    result += razlichnieProdlojeniya.get(i)+"\n";
	}
	System.out.println(
		"VASHE LYCHSEE PRIDLOJENITT TYT! TOL`KO SEGODNYA SO SKIDKOI 999%! (tol`ki v kredit pod 0.1% ejesecundno, skida ne u4itivaet summu credita):\n"
			+ result.trim());
    }
}
