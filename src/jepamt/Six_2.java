package jepamt;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class Six_2 {

    private List<String> notes = new ArrayList<>();

    public static void main(String... args) throws Exception {

	Six_2 s2 = new Six_2();
	
	Long date=new Date().getTime();

	Note note = new Note("kek", String.valueOf(date), "mail@mail.mail", "ololo");

	s2.saveNote(note);
	s2.search("message", "olo");
	s2.search("theme", "k");
	s2.search("email", "@");
	s2.search("creationDate", String.valueOf(date));
    }

    private Note loadNote(/* Path path */String data) throws Exception {
	// code for load data from file
	JAXBContext jaxbcontext = JAXBContext.newInstance(Note.class);
	Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
	StringReader sw = new StringReader(data/* read data from path */);
	Note note = (Note) unmarshaller.unmarshal(sw);
	return note;
    }

    private void saveNote(Note note) throws JAXBException {
	JAXBContext jaxbContext = JAXBContext.newInstance(Note.class);
	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	StringWriter sw = new StringWriter();
	jaxbMarshaller.marshal(note, sw);
	notes.add(sw.toString());
	// code for save note to file
    }

    private void search(String key, String data){
	try {
	Field f = Note.class.getDeclaredField(key);
	f.setAccessible(true);
	notes.stream().filter(e -> {
	    try {		
		String res=(String)f.get(loadNote(e));
		Pattern pattern=Pattern.compile(data);
		Matcher mat=pattern.matcher(res);		
		return mat.find();
	    } catch (Exception e1) {
		e1.printStackTrace();
		return false;
	    }	    
	}).forEach(e->{
	    try {
		String res=(String)f.get(loadNote(e));
		System.out.println(res);
	    } catch (Exception e1) {
		e1.printStackTrace();
	    } 
	});
	}catch (Exception e) {
	    System.out.println("not found.");
	}
    }

}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class Note {

    @XmlElement
    private String theme;
    @XmlElement
    private String creationDate;
    @XmlElement
    private String email;
    @XmlElement
    private String message;

    private Note() {
    }

    public Note(String theme, String creationDate, String emeil, String message) throws ParseException {
	super();
	this.theme = theme;
	this.creationDate = creationDate;
	this.email = emeil;
	this.message = message;
    }

    public String getTheme() {
	return theme;
    }

    public void setTheme(String theme) {
	this.theme = theme;
    }

    public String getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(String creationDate) throws ParseException {
	this.creationDate = creationDate;
    }

    public String getEmeil() {
	return email;
    }

    public void setEmeil(String emeil) {
	this.email = emeil;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

}
