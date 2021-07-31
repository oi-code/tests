package jepamt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Six_3_server {

    private static ServerSocket serverSocket;
    private static final ExecutorService exec = Executors.newCachedThreadPool();
    private static final LoginService ls = LoginService.getInstance();

    public static void main(String... args) throws Exception {
	init();
	serverSocket = new ServerSocket(9764);

	System.out.println("Server started");

	while (!Thread.currentThread().isInterrupted()) {
	    Socket socket = serverSocket.accept();
	    SocketThread socketThread = new SocketThread(socket);
	    exec.execute(socketThread);
	    System.out.println("Socked accepted");
	}
    }

    private static void init() throws Exception {
	User admin = new User("admin", 1);
	User user = new User("user", 2);

	Field f = User.class.getDeclaredField("role");
	f.setAccessible(true);
	f.set(admin, Role.ADMIN);

	ls.registry("admin", 1, "123");
	ls.registry("user", 2, "321");

	// ls.login(1, "123".hashCode());
	// ls.login(2, "321".hashCode());

	Database db = Database.getInstance();
	String stringUser = db.addOrUpdateData(user);
	String adminString = db.addOrUpdateData(admin);
    }
}

class SocketThread implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private LoginService ls = LoginService.getInstance();
    private boolean isLogined = false;
    private User _user;
    private static Database db = Database.getInstance();

    public SocketThread(Socket s) {
	socket = s;
	try {
	    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void run() {
	try {
	    if (!login()) {
		System.err.println("Serverlog: client cant login");
		return;
	    }
	    initCommunication();
	} catch (Exception e) {
	    try {
		out.close();
		in.close();
		socket.close();
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	} finally {
	    ls.logOut(_user.getId());
	    System.out.println("Serverlog: " + _user + " logouted");
	    try {
		out.close();
		in.close();
		socket.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    private boolean login() {
	while (!this.isLogined) {
	    try {
		out.println("Server response: Enter your id:");
		Integer id = Integer.valueOf(in.readLine());

		out.println("Server response: Enter your password:");
		String pass = in.readLine();

		System.out.printf("Serverlog: id: %d, pass: %s\n", id, pass);
		if (ls.login(id, pass.hashCode())) {
		    this.isLogined = true;
		    System.out.println(ls.getUserFromId(id) + " logined");
		    out.println("Server response: Login sucessfull");
		    _user = ls.getUserFromId(id);
		    return true;
		} else {
		    out.println("Server response: Acces denied. try again");
		}
	    } catch (NumberFormatException e) {
		out.println("Server response: Error");
	    } catch (Exception e) {
		return false;
	    }
	}
	return false;
    }

    private void initCommunication() throws Exception {
	String inMsg = "";
	String serverMainMenuMessage = "Server response: What you can to do:\n" + "\t1: look file by id\n";
	if (_user.getRole() == Role.ADMIN) {
	    serverMainMenuMessage += "\t2: change file by id\n";
	    serverMainMenuMessage += "\t3: create new file\n";
	}
	serverMainMenuMessage += "\t4: write \"stop\" for exit program\n";
	out.println(serverMainMenuMessage);
	while (!(inMsg = in.readLine()).equals("stop")) {
	    System.out.println(inMsg);
	    switch (inMsg) {
	    case "1": {
		out.println(lookFileById());
		break;
	    }
	    case "2": {
		out.println("for exit enter -1. enter id for change:");
		try {
		    changeFileById(in.readLine());
		} catch (Exception e) {
		    out.write("data can't been updated");
		}
		break;
	    }
	    case "3": {
		try {
		    createNewDocument();
		} catch (Exception e) {
		    out.println("data can't been added");
		}
		break;
	    }
	    default:
		break;
	    }
	    out.println(serverMainMenuMessage);
	}
    }

    private String lookFileById() throws Exception {
	out.println("\tenter data id:");
	String id = in.readLine();
	String data = db.getData(id);
	if (data == null) {
	    return "no data";
	} else {
	    return data;
	}
    }

    private void changeFileById(String id) throws Exception {
	try {
	    if (id.equals("-1")) {
		out.println("return to start");
		return;
	    }
	    if (db.getData(id) == null) {
		throw new NumberFormatException();
	    }
	} catch (NumberFormatException e) {
	    out.println("broken id. return to start");
	    return;
	}

	String data = db.getData(id);
	JAXBContext jaxbcontext = JAXBContext.newInstance(User.class);
	Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
	StringReader sw = new StringReader(data);
	User u = (User) unmarshaller.unmarshal(sw);
	out.println("you will change: " + u);

	/*
	 * DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	 * DocumentBuilder db = dbf.newDocumentBuilder();
	 * Document document = db.parse(new InputSource(new StringReader(data)));
	 */
	while (true) {
	    out.println("enter -1 for exit. enter element for change:");
	    String msg = in.readLine();
	    if (msg.equals("-1")) {
		break;
	    }
	    Field f = User.class.getDeclaredField(msg);
	    f.setAccessible(true);
	    switch (msg) {
	    case "id": {
		out.println("enter new value:");
		String _msg = in.readLine();
		f.set(u, Integer.valueOf(_msg));
		break;
	    }
	    case "name": {
		out.println("enter new value:");
		String _msg = in.readLine();
		f.set(u, _msg);
		break;
	    }
	    case "role": {
		out.println("you have no acces to change role");
		break;
	    }
	    }
	    out.println("success");
	}
	db.addOrUpdateData(u);
    }

    public void createNewDocument() throws Exception {
	out.println("if id will match, data been replaced. be careful.\nenter new id:");
	String id = in.readLine();
	out.println("enter new name");
	String name = in.readLine();
	User u = new User(name, Integer.valueOf(id));
	db.addOrUpdateData(u);
	out.println("success");
    }
}

//THIS DATABASE EXAMPLE OPERATE XML FILES, NOT STRINGS!!!!!!!!!!!!!!!!!!
class Database {

    private static final Map<Integer, String> xmlData = new HashMap<>();

    private static final Database db = new Database();

    private Database() {
    }

    public static Database getInstance() {
	return db;
    }

    public String addOrUpdateData(User user) throws Exception {
	JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	StringWriter sw = new StringWriter();
	jaxbMarshaller.marshal(user, sw);
	xmlData.put(user.getId(), sw.toString());
	return sw.toString();
    }

    public boolean removeData(String data) throws Exception {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db = dbf.newDocumentBuilder();
	Document document = db.parse(new InputSource(new StringReader(data)));
	NodeList node = document.getElementsByTagName("id");
	Integer id = Integer.valueOf(node.item(0).getTextContent());
	if (xmlData.containsKey(id)) {
	    xmlData.remove(id);
	    return true;
	}
	return false;
    }

    public String getData(String id) throws Exception {
	int _id = Integer.valueOf(id);
	return xmlData.get(_id);
    }

}
