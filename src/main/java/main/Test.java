package main;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

public class Test {

    public static Logger log = Logger.getLogger("logger");
    private static List<Hoba> hobas;
    public static final String HOME = System.getProperty("user.home");
    public static boolean check = false, isAlive = true;;

    public static void getLine() {
	System.err.print("--->\sLine:\s" + Thread.currentThread().getStackTrace()[2].getLineNumber() + ".\sThread:\s"
		+ Thread.currentThread().getName() + ".\sInfo:\s");
    }

    public static void main(String... args) {
	try {
	    Test.init();
	    //Runtime.getRuntime().exec("java -jar " + Test.HOME + "\\Desktop\\helper.jar");
	    log.warn("OK");
	} catch (Exception e) {
	    e.printStackTrace();
	    log.error("NOT_OK");
	}
    }

    private static void init() throws Exception {
	Hoba t = new Hoba();
	t.setText("HoHoHo_");

	EntityManagerFactory emf = Persistence.createEntityManagerFactory("try");
	EntityManager em = emf.createEntityManager();

	new Thread() {
	    {
		this.setDaemon(true);
	    }

	    @Override
	    public void run() {
		try {
		    Test.getLine();
		    log.warn("start thread, ENTER IN thread SYNC BLOCK");
		    Hoba h = new Hoba();// em.getReference(Hoba.class, 3);
		    synchronized (log) {
			Test.getLine();
			log.warn("IN thread SYNC BLOCK");
			while (Test.check) {
			    log.wait();
			}
			Test.getLine();
			log.warn("IN thread SYNC BLOCK AFTER SYNC");

			em.getTransaction().begin();
			em.persist(h);
			em.persist(t);
			em.getTransaction().commit();

			Test.getLine();
			log.warn("END thread SYNC BLOCK, TRANSACTION END");
			Test.check = true;
			log.notifyAll();
		    }
		    // System.out.println(h+"\n"+t);
		    Test.getLine();
		    log.warn("END thread, EXIT SYNC BLOCK");
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}.start();
	/*
	 * Metamodel m=emf.getMetamodel();
	 * Set<ManagedType<?>>mt=m.getManagedTypes();
	 * ManagedType it=mt.iterator().next();
	 * System.err.println(it.getPersistenceType());
	 */

	// waiting for creating thread described above
	TimeUnit.SECONDS.sleep(1);

	em.getTransaction().begin();
	CriteriaBuilder cb = em.getCriteriaBuilder();
	CriteriaQuery<Hoba> query = cb.createQuery(Hoba.class);
	Root<Hoba> fromHoba = query.from(Hoba.class);
	Path<String> p = fromHoba.get("text");
	query.where(cb.like(p, cb.parameter(String.class, "pattern")));
	// query.select(fromHoba);
	// List<Hoba> hobas = em.createQuery(query).getResultList();
	hobas = em.createQuery(query).setParameter("pattern", t.getText()).getResultList();
	em.getTransaction().commit();

	/*
	 * em.getTransaction().begin();
	 * 
	 * @SuppressWarnings("unchecked")
	 * List<Message> rs =
	 * em.createQuery("select asdf from Message asdf").getResultList();
	 * System.out.println("---->\t\t" + em.getTransaction().isActive());
	 * em.getTransaction().commit();
	 * System.out.println("---->\t\t" + em.getTransaction().isActive());
	 * 
	 * for(Message m:rs) {
	 * System.out.println(m);
	 * }
	 * 
	 * em.getTransaction().commit();
	 */
	//Test.check = false;
	Thread q = new Thread() {
	    @Override
	    public void run() {
		try {
		    for (int i = 1; i < 6; i++) {
			Test.getLine();
			log.warn(i);
			TimeUnit.SECONDS.sleep(1);
		    }
		    Test.check = true;
		    synchronized (log) {
			log.notifyAll();
		    }
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}

	    }
	};	
	synchronized (log) {
	    Test.getLine();
	    log.warn("enter IN main SYNC BLOCK");
	    while (!Test.check) {
		q.start();
		log.wait();
	    }
	    em.close();
	    emf.close();
	    Test.check = false;
	    log.notifyAll();
	    Test.getLine();
	    log.warn("exit main SYNC BLOCK");
	}
	if (hobas.isEmpty()) {
	    Test.getLine();
	    log.warn("hobas is empty");
	} else {
	    for (Hoba b : hobas) {
		log.warn(b);
	    }
	}	
	System.exit(0);	
    }
}

@Entity
@Table(name = "OBJECTS")
class Hoba {
    @Id
    @GeneratedValue(generator = "generator")
    // @Column(name = "id")
    private Long id;
    @Column(name = "name")
    @Lob
    private String text;
    @Column(name = "date")
    private Date ldt = new Date();
    // String ldt=LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE,
    // dd.MMMM.yyyy, HH:mm:ss", Locale.ENGLISH));
    @Column(name = "mass", length = 1000)
    private int[] arr = ThreadLocalRandom.current().ints(0, 500).limit(10).toArray();
    @Column(name = "serClass")
    private SerClass serClass = new SerClass();

    public Hoba() {
	// id = q++;// ThreadLocalRandom.current().nextInt();
	text = "NewName_";
    }

    public void setText(String text) {
	this.text = text;
    }

    public String getText() {
	return text;
    }

    /*
     * public int getId() {
     * return id;
     * }
     * 
     * public void setId(int id) {
     * this.id = id;
     * }
     */

    public String toString() {
	return String.format("\n\t--->" + this.getClass().getName() + "\n\tid:\s%d\n\tname:\s%s\n\tdate:\s%s", id, text,
		ldt + "\n\t" + Arrays.toString(arr) + "\n\t" + serClass+"\n");
    }

    /*
     * Statement st=con.createStatement();
     * String sq="insert into test.objects (id) value ('"+t.getId()+"');";
     * String sq2="insert into test.objects (name) value ('"+t.getText()+"');";
     * System.out.println(sq+"\t"+sq2);
     * st.execute(sq);
     * st.execute(sq2);
     * Statement st3=con.createStatement();
     * String sq4="delete from test.objects where id=0";
     * st3.execute(sq4);
     * 
     * Statement st2=con.createStatement();
     * String sq3="select id, name from test.objects";
     * ResultSet rs=st2.executeQuery(sq3);
     * while(rs.next()) {
     * System.out.println(rs.getInt(1)+"\t"+rs.getString(2));
     * }
     */
}

@Entity
class SerClass implements Serializable {
    @Id
    @GeneratedValue
    @Access(AccessType.FIELD)
    private Long id;
    @Access(AccessType.PROPERTY)
    private String res = this.getClass().getSimpleName() + ".class.";
    @Access(AccessType.FIELD)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastAcces", insertable = true, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    private Date lastModificationTime;//LocalDateTime.now(ZoneId.of("GMT+3")).toString();

    public void setRes(String res) {
	this.res = res;
    }
    public String getRes() {
	return res;
    }

    @Override
    public String toString() {
	return res;
    }
}
