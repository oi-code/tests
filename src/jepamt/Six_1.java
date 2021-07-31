package jepamt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class Six_1 {
    public static void main(String[] asd) {
	EBook e = new EBook("TT", List.of("KEK", "CHEBURATOR"), false);
	Book b = new Book("SS", List.of("DS", "SSD"), true);
	BookAccouting ba = new BookAccouting();
	LoginService ls = LoginService.getInstance();

	ls.registry("owner", 0, "000");
	ls.registry("admin", 1, "123");
	ls.registry("notAdmin", 2, "321");

	try {
	    User user = ls.getUserFromId(0);
	    Field f = user.getClass().getDeclaredField("role");
	    f.setAccessible(true);
	    f.set(user, Role.OWNER);
	    ls.setRole(0, 1, Role.ADMIN);
	} catch (Exception e1) {
	    e1.printStackTrace();
	}

	ls.login(1, "123".hashCode());
	ls.login(2, "321".hashCode());

	ba.addBook(e, 1);
	ba.addBook(b, 2);

	ba.addBookDescription(1, e, "UUUUU");
	ba.addBookDescription(2, b, "KKKKK");

	ba.offerBook(2, b);
    }
}

enum Role {
    USER, ADMIN, OWNER
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

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class User {

    @XmlElement
    private String name;
    @XmlElement
    private Integer id;
    @XmlElement
    private Role role;

    @Deprecated
    private User() {
    }

    public User(String name, Integer id) {
	super();
	this.name = name;
	this.id = id;
	this.role = Role.USER;
    }

    public Role getRole() {
	return role;
    }

    public String getName() {
	return name;
    }

    public Integer getId() {
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
	return true;
    }

    @Override
    public String toString() {
	return "User [name=" + name + ", id=" + id + ", role=" + role + "]";
    }

}

class EMailService {
    public static void sendMail(User u, String message) {
	System.out.println("Mail sended to " + u + ". " + message);
	// sending message on mail
    }
}

class LoginService {

    private static final LoginService ls = new LoginService();
    private static final Map<Integer, User> allUsers = new ConcurrentHashMap<Integer, User>();
    private static final Set<User> loginedUser = new CopyOnWriteArraySet<User>();
    private static final Map<Integer, Integer> passwords = new ConcurrentHashMap<Integer, Integer>();

    private LoginService() {
	// loading all users from file
    }

    public static LoginService getInstance() {
	return ls;
    }

    public Collection<User> getAllUsers() {
	return allUsers.values();
    }

    private boolean userAlreadyExist(Integer id) {
	return allUsers.containsKey(id);
    }

    public void addNewUser(String userName, Integer id, String password) {
	if (!userAlreadyExist(id)) {
	    Integer pass = password.hashCode();
	    User user = new User(userName, id);
	    allUsers.put(id, user);
	    passwords.put(id, pass);
	}
	// here calling the method for update and synchronize text file
    }

    public boolean isLogined(Integer id) {
	Optional<User> user = loginedUser.stream().filter(e -> e.getId() == id).findAny();
	if (user != null) {
	    return true;
	}
	return false;
    }

    public boolean login(Integer id, Integer password) {
	if (allUsers.containsKey(id)) {
	    if ((int) passwords.get(id) == password) {
		loginedUser.add(allUsers.get(id));
		return true;
	    }
	}
	return false;
    }

    public boolean logOut(Integer id) {
	if (isLogined(id)) {
	    loginedUser.remove(getUserFromId(id));
	    return true;
	}
	return false;
    }

    public void registry(String userName, Integer id, String pass) {
	User check = getUserFromId(id);
	if (check == null) {
	    int psw = pass.hashCode();
	    User user = new User(userName, id);
	    try {
		if (id == 0) {
		    Field f;
		    f = User.class.getDeclaredField("role");
		    f.setAccessible(true);
		    f.set(user, Role.OWNER);
		}
		if (id == 1) {
		    Field f = User.class.getDeclaredField("role");
		    f.setAccessible(true);
		    f.set(user, Role.ADMIN);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    allUsers.put(id, user);
	    passwords.put(id, psw);
	}
    }

    public User getUserFromId(Integer id) {
	User user = allUsers.get(id);
	if (user != null) {
	    return user;
	}
	return null;
    }

    public void setRole(Integer ownerId, Integer userId, Role role) {
	if (allUsers.get(ownerId).getRole() == Role.OWNER) {
	    User user = allUsers.get(userId);
	    try {
		Field f = user.getClass().getDeclaredField("role");
		f.setAccessible(true);
		f.set(user, Role.ADMIN);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
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

    public void addBook(Book book, Integer id) {
	if (ls.isLogined(id) && ls.getUserFromId(id).getRole() == Role.ADMIN) {
	    if (lobrary.contains(book)) {
		System.out.println(book + " already exsist");
		return;
	    }
	    System.out.println(book + " added to library by " + ls.getUserFromId(id));
	    lobrary.add(book);
	} else {
	    System.out.println("Access denied to " + ls.getUserFromId(id));
	}
    }

    public void showAllBooks(Integer u) {
	List<Book> result = new ArrayList<>();
	if (ls.isLogined(u)) {
	    int page = pages.get(u);
	    for (int i = page; i < lobrary.size() && i < i + 20; i++) {
		result.add(lobrary.get(i));
		page++;
	    }
	    // and somehow return to 0 after view all books
	    pages.put(ls.getUserFromId(u), page);
	    System.out.println(result);
	}
    }

    public void searchBook(Integer id, Book book) {
	if (ls.isLogined(id)) {
	    Optional<Book> b = lobrary.stream().filter(e -> e.equals(book)).findFirst();
	    if (b != null) {
		System.out.println("Book exist in library");
	    } else {
		System.out.println("Book not exsist");

	    }
	}
    }

    public void addBookDescription(Integer id, Book book, String desctiption) {
	boolean added = false;
	if (ls.isLogined(id) && ls.getUserFromId(id).getRole() == Role.ADMIN) {
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

    public void offerBook(Integer id, Book b) {
	if (ls.isLogined(id)) {
	    for (User us : ls.getAllUsers()) {
		if (us.getRole() == Role.ADMIN) {
		    EMailService.sendMail(us, String.format("User %s offer book %s", ls.getUserFromId(id), b));
		}
	    }
	}
    }

}