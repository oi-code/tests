package main;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import java.util.concurrent.*;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class Test {

    public static final String HOME = System.getProperty("user.home");
    public static volatile boolean check = false, isAlive = true;

    public static void getLine() {
	System.err.print("--->\sLine:\s" + Thread.currentThread().getStackTrace()[2].getLineNumber() + ".\sThread:\s"
		+ Thread.currentThread().getName() + ".\sInfo:\s");
    }

    public static void main(String... args) {
	try {
	    System.out.println("init test main...");
	    // Initializer.init1();
	    // Initializer.init2();
	    // Initializer.init3();
	    Initializer.init4();
	    // Runtime.getRuntime().exec("java -jar " + Test.HOME +
	    // "\\Desktop\\helper.jar");
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    System.out.println("exit test main...");
	    // System.exit(0);
	}

    }
}

class Transactions {
    public static Session session;

    public static Session getSession() {

	return session;
    }
}

class Initializer {

    private static final Logger log;
    private static List<Hoba> hobas;
    static {
	log = Logger.getLogger("logger");
	log.warn("logger started...");
    }

    public static void init1() throws Exception {
	Hoba t = new Hoba();
	t.setText("HoHoHo_" + t.getClass().getSimpleName() + ".class");
	Hoba c = new HobaSuccessor();

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
			em.persist(c);
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
	query.where(cb.like(p, cb.parameter(String.class, "RANDOM_WORDS")));
	// query.select(fromHoba);
	// List<Hoba> hobas = em.createQuery(query).getResultList();
	hobas = em.createQuery(query).setParameter("RANDOM_WORDS", t.getText()).getResultList();
	em.getTransaction().commit();

	em.getTransaction().begin();
	CriteriaBuilder cb2 = em.getCriteriaBuilder();
	CriteriaQuery<HobaSuccessor> query2 = cb2.createQuery(HobaSuccessor.class);
	Root<HobaSuccessor> fromHoba2 = query2.from(HobaSuccessor.class);
	Path<String> p2 = fromHoba2.get("des");
	query2.where(cb2.like(p2, cb2.parameter(String.class, "RANDOM_WORDS")));
	List<HobaSuccessor> added = em.createQuery(query2).setParameter("RANDOM_WORDS", c.getText()).getResultList();
	em.getTransaction().commit();
	hobas.addAll(added);

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
	// Test.check = false;
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

    public static void init2() throws Exception {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("try");
	EntityManager em = emf.createEntityManager();

	em.getTransaction().begin();
	User u = new User();
	em.persist(u);
	em.getTransaction().commit();

	em.getTransaction().begin();
	User result = em.find(User.class, 1L);
	if (result == null) {
	    log.warn("user is null");
	} else {
	    log.warn(result);
	}
	em.getTransaction().commit();

	em.getTransaction().begin();
	User change = em.find(User.class, 2L);
	Address add = new Address();
	add.setCity("gagaga");
	if (change == null) {
	    log.warn("change is null");
	} else {
	    change.setAddress(add);
	    change.setName("somewhere");
	    em.persist(change);
	}
	em.getTransaction().commit();

	em.getTransaction().begin();
	User forDel = em.find(User.class, 1L);
	if (forDel == null) {
	    log.warn("not found");
	} else {
	    em.remove(forDel);
	}
	em.getTransaction().commit();

	Connection c = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "1234");
	Statement st = c.createStatement();
	ResultSet res = st.executeQuery("select * from test.user");
	if (res.next() == false) {
	    log.error("db is empty");
	} else {
	    log.warn("db is not empty");
	    // c.createStatement().execute("drop table test.user");
	}
	c.close();
    }

    public static void init3() throws Exception {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("try");
	EntityManager em = emf.createEntityManager();

	Item it = new Item();
	em.getTransaction().begin();
	em.persist(it);
	em.getTransaction().commit();

	em.getTransaction().begin();
	Item i = em.find(Item.class, 1L);
	log.warn(i);
	em.getTransaction().commit();

	em.getTransaction().begin();
	Item smi = new Item();
	em.persist(smi);
	Bid bid = new Bid(smi);
	smi.getBids().add(bid);
	Bid bid2 = new Bid(smi);
	smi.getBids().add(bid2);
	em.getTransaction().commit();

	em.getTransaction().begin();
	Item rem = em.find(Item.class, 30L);
	if (rem == null) {
	    log.warn("not found ITEM");
	} else {
	    log.warn("ITEM found, id=" + rem.getId() + ", remove...");
	    em.remove(rem);
	}
	em.getTransaction().commit();

    }

    public static void init4() throws Exception {
	Test.getLine();
	System.out.println(" start");
	Configuration cfg = new Configuration().configure();
	cfg.addAnnotatedClass(User2.class);
	SessionFactory sf = cfg.buildSessionFactory();
	new Thread(new Runnable() {
	    public void run() {
		for (int i = 0; i < 100; i++) {
		    new Thread(new Runnable() {
			public void run() {
			    Session s = null;
			    try {
				s = sf.openSession();
				s.getTransaction().begin();
				// Session session = sf.getCurrentSession();
				User2 user2 = new User2();
				user2.mail = "melkotnya.google@.sadfas.cqom2";
				s.persist(user2);
				s.flush();
				s.getTransaction().commit();
				// s.close();
				s.disconnect();
			    } catch (Exception e) {
				System.out.println("ERROR");
			    } finally {
				//s.close();
				System.out.println(s.getStatistics());
			    }
			}
		    }).start();
		    if (i % 20 == 0) {
			try {
			    Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			System.err.println("next...");
		    }
		}
	    }
	}).start();

	System.out.println("end");
    }
}

@Entity
class User2 {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = false, name = "kek"
    // columnDefinition = "EMAIL_ADDRESS(255)"
    )
    public String mail;

    public String getMail() {
	return mail;
    }
}

@Entity
//@MappedSuperclass
@Table(name = "OBJECTS")
@Inheritance(strategy = InheritanceType.JOINED)
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
    @Column(name = "randomInt")
    private int randInt = ThreadLocalRandom.current().nextInt();

    public Hoba() {
	// id = q++;// ThreadLocalRandom.current().nextInt();
	text = "NewName_" + getClass().getSimpleName();
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
		ldt + "\n\t" + Arrays.toString(arr) + "\n\t" + serClass + "\n");
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

@SuppressWarnings("serial")
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
    private Date lastModificationTime;// LocalDateTime.now(ZoneId.of("GMT+3")).toString();

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

@Entity
@PrimaryKeyJoinColumn
class HobaSuccessor extends Hoba {
    @Column(name = "name")
    private String des = this.getClass().getSimpleName() + ".class";

    public HobaSuccessor() {
	super();
	setText(this.getClass().getAnnotatedSuperclass() + "_" + des);
    }

    @Override
    public String getText() {
	return des;
    }
}

@Entity
@Table(name = "user")
class User implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    private String name = "userName";
    private Address address = new Address();

    public void setName(String name) {
	this.name = name;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public void setAddress(Address address) {
	this.address = address;
    }

    public Address getAddress() {
	return address;
    }

    public String toString() {
	return String.format("\n\tName:\s%s\n\tAddress:\s%s", name, address);
    }
}

@Embeddable
class Address {
    /*
     * @Id
     * 
     * @GeneratedValue
     * private Long id;
     */
    private String city = "Moskow";
    private String country = "Russia";

    /*
     * @Deprecated
     * public Address() {
     * 
     * }
     */

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {

	this.city = city;
    }

    public String toString() {
	return String.format("\n\t\tCountry:\s%s\n\t\tCity:\s%s\n", country, city);
    }
}

class ReverseStringComparatop implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
	return o2.compareTo(o1);
    }
}

@Entity
class Item {
    @Id
    @GeneratedValue
    private Long id;
    @ElementCollection
    @CollectionTable(name = "IMG", joinColumns = @JoinColumn(name = "img_id"))
    @Column(name = "item_name")
    // @org.hibernate.annotations.SortComparator(ReverseStringComparatop.class)
    @org.hibernate.annotations.OrderBy(clause = "item_name desc")
    private Set<String> set = new HashSet<String>();
    {
	set.add("1");
	set.add("2");
	set.add("3");
    }
    @OneToMany(mappedBy = "item", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<Bid> bids = new HashSet<Bid>();

    public Set<Bid> getBids() {
	return bids;
    }

    public Long getId() {
	return id;
    }

    @Override
    public String toString() {
	StringBuilder result = new StringBuilder();
	set.stream().forEach(e -> result.append("\t" + e));
	return result.toString();
    }
}

@Entity
class Bid {
    @Id
    @GeneratedValue
    private Long id;

    public Bid() {
    }

    public Bid(Item it) {
	item = it;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @Column(name = "rndint")
    private Integer rnd = ThreadLocalRandom.current().nextInt(42);
}
