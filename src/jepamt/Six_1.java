package jepamt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Six_1 {
    public static void main(String[] asd) {
	EBook e = new EBook("TT", List.of("KEK", "CHEBURATOR"), false);
	Book b = new Book("SS", List.of("DS", "SSD"), true);
	BookAccouting ba = new BookAccouting();
	User admin = new User("kek", 2L, Role.ADMIN);
	User notAdmin = new User("cheburek", 3L, Role.USER);
	LoginService ls=LoginService.getInstance();
	ls.registry(admin, "123");
	ls.registry(notAdmin, "321");
	ls.login(notAdmin, "321".hashCode());
	ls.login(admin, "123".hashCode());
	ba.addBook(e, admin);
	ba.addBook(b, notAdmin);
	ba.addBookDescription(admin, e, "UUUUU");
	ba.addBookDescription(notAdmin, b, "UUUUU");	
	ba.offerBook(notAdmin, b);
    }
}

enum Role {
    USER, ADMIN
}

class Book {

    private String title;
    private List<String> authors;
    private boolean isWooden;
    private String desctiption;

    public Book(String title, List<String> authors, boolean isWooden) {
	super();
	this.title = title;
	this.authors = authors;
	this.isWooden = isWooden;
    }

    public String getDesctiption() {
	return desctiption;
    }

    public void setDesctiption(String desctiption) {
	this.desctiption = desctiption;
    }

    public String getTitle() {
	return title;
    }

    public List<String> getAuthors() {
	return authors;
    }

    public boolean isWooden() {
	return isWooden;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((authors == null) ? 0 : authors.hashCode());
	result = prime * result + ((desctiption == null) ? 0 : desctiption.hashCode());
	result = prime * result + (isWooden ? 1231 : 1237);
	result = prime * result + ((title == null) ? 0 : title.hashCode());
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
	Book other = (Book) obj;
	if (authors == null) {
	    if (other.authors != null)
		return false;
	} else if (!authors.equals(other.authors))
	    return false;
	if (desctiption == null) {
	    if (other.desctiption != null)
		return false;
	} else if (!desctiption.equals(other.desctiption))
	    return false;
	if (isWooden != other.isWooden)
	    return false;
	if (title == null) {
	    if (other.title != null)
		return false;
	} else if (!title.equals(other.title))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "Book [title=" + title + ", authors=" + authors + ", isWooden=" + isWooden + ", desctiption="
		+ desctiption + "]";
    }

}

class EBook extends Book {

    public EBook(String title, List<String> authors, boolean isWooden) {
	super(title, authors, isWooden);
    }

}

class User {

    private String name;
    private Long id;
    private Role role;

    public User(String name, Long id, Role role) {
	super();
	this.name = name;
	this.id = id;
	this.role = role;
    }

    public Role getRole() {
	return role;
    }

    public void setRole(Role role) {
	this.role = role;
    }

    public String getName() {
	return name;
    }

    public Long getId() {
	return id;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((role == null) ? 0 : role.hashCode());
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
	User other = (User) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (role != other.role)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "User [name=" + name + ", id=" + id + ", role=" + role + "]";
    } 

}

class EMailService {
    public static void sendMail(User u, String message) {
	System.out.println("Mail sended to "+u+". "+message);
	// sending message on mail
    }
}

class LoginService {
    private static final LoginService ls = new LoginService();
    private final Map<User, Integer> allUsers = new HashMap<>();
    private final Set<User> loginedUser = new HashSet<User>();

    private LoginService() {
	// loading all users from file
    }

    public static LoginService getInstance() {
	return ls;
    }

    public Set<User> getAllUsers() {
	return allUsers.keySet();
    }

    private boolean userAlreadyExist(User user, Integer password) {
	Optional<User> u = allUsers.keySet().stream().filter(e -> e.equals(user) && allUsers.get(e) == password)
		.findFirst();
	if (u != null) {
	    return true;
	}
	return false;
    }

    public void addNewUser(User user, String password) {
	Integer pass = password.hashCode();
	if (!userAlreadyExist(user, pass)) {
	    allUsers.put(user, pass);
	}
	// here calling the method for update and synchronize text file
    }

    public boolean isLogined(User user) {
	return loginedUser.contains(user);
    }

    public boolean login(User user, Integer password) {
	Optional<User> u = allUsers.keySet().stream().filter(e -> e.equals(user) && allUsers.get(e) == password)
		.findFirst();
	if (u != null) {
	    loginedUser.add(user);
	    return true;
	}
	return false;
    }

    public boolean logOut(User user) {
	if (isLogined(user)) {
	    loginedUser.remove(user);
	    return true;
	}
	return false;
    }
    
    public void registry(User u, String pass) {
	if(!allUsers.keySet().contains(u)) {
	    int psw=pass.hashCode();
	    allUsers.put(u, psw);
	}
    }

}

class BookAccouting {

    private List<Book> lobrary;
    private LoginService ls;
    private Map<User, Integer> pages;

    public BookAccouting() {
	super();
	lobrary = new ArrayList<>();
	pages = new HashMap<>();
	ls = LoginService.getInstance();
    }

    public void addBook(Book book, User user) {
	if (ls.isLogined(user) && user.getRole() == Role.ADMIN) {
	    if (lobrary.contains(book)) {
		System.out.println(book + " already exsist");
		return;
	    }
	    System.out.println(book + " added to library by "+user);
	    lobrary.add(book);
	} else {
	    System.out.println("Access denied to "+user);
	}
    }

    public void showAllBooks(User u) {
	List<Book> result = new ArrayList<>();
	if (ls.isLogined(u)) {
	    int page = pages.get(u);
	    for (int i = page; i < lobrary.size() && i < i + 20; i++) {
		result.add(lobrary.get(i));
		page++;
	    }
	    //and somehow return to 0 after view all books
	    pages.put(u, page);
	    System.out.println(result);
	}
    }

    public void searchBook(User user, Book book) {
	if (ls.isLogined(user)) {
	    Optional<Book> b = lobrary.stream().filter(e -> e.equals(book)).findFirst();
	    if (b != null) {
		System.out.println("Book exist in library");
	    } else {
		System.out.println("Book not exsist");

	    }
	}
    }

    public void addBookDescription(User user, Book book, String desctiption) {
	boolean added = false;
	if (ls.isLogined(user) && user.getRole() == Role.ADMIN) {
	    if (lobrary.contains(book)) {
		book.setDesctiption(desctiption);
		added = true;
	    } else {
		System.out.println(book + " is not exist");
	    }
	}
	if (added) {
	    for (User u : ls.getAllUsers()) {
		EMailService.sendMail(u, book + " have new description");
	    }
	}
    }

    public void offerBook(User u, Book b) {
	if (ls.isLogined(u)) {
	    for (User us : ls.getAllUsers()) {
		if (us.getRole() == Role.ADMIN) {
		    EMailService.sendMail(us, String.format("User %s offer book %s", u.toString(), b.toString()));
		}
	    }
	}
    }

}