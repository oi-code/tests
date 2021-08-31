package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

class Initializer {

    private static final Logger log;
    private static List<Hoba> hobas;
    private static EntityManagerFactory emf;
    static {
	log = Logger.getLogger("logger");
	log.warn("logger started...");
	emf = Persistence.createEntityManagerFactory("try");
    }

    public void init1() throws Exception {
	Hoba t = new Hoba();
	t.setText("HoHoHo_" + t.getClass().getSimpleName() + ".class");
	Hoba c = new HobaSuccessor();
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
	// System.exit(0);
    }

    public void init2() throws Exception {
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

    public void init3() throws Exception {
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

    @SuppressWarnings("static-access")
    public void init4() throws Exception {
	Test.getLine();
	System.out.println(" start");
	Configuration cfg = new Configuration().configure();
	// cfg.addAnnotatedClass(User2.class);
	SessionFactory sf = cfg.buildSessionFactory();
	AtomicBoolean stopWork = new AtomicBoolean(false);
	new Thread(new Runnable() {
	    @SuppressWarnings("static-access")
	    public void run() {
		for (int i = 0; i < 100; i++) {
		    new Thread(new Runnable() {
			public void run() {
			    /*
			     * try (Session s = sf.openSession()) {
			     * s.getTransaction().begin();
			     * // Session session = sf.getCurrentSession();
			     * User2 user2 = s.load(User2.class, 1L);
			     * user2.mail = "kek@cheburek.com";
			     * User2 user1 = s.get(User2.class, 444L);
			     * User2 user3 = new User2();
			     * user3.mail = "newMail@asd.asd";
			     * s.persist(user3);
			     * try {
			     * s.delete(user1);
			     * } catch (Exception e) {
			     * // System.err.println(e);
			     * System.out.println("user not exist");
			     * }
			     * // User2 user2 = new User2();
			     * // user2.mail = "melkotnya.google@.sadfas.cqom2";
			     * // s.lock(user2, LockMode.PESSIMISTIC_WRITE);
			     * s.persist(user2);
			     * // s.flush();
			     * s.getTransaction().commit();
			     * // s.disconnect();
			     * // s.close();
			     * System.out.println(user3);
			     * System.out.println(user2);
			     * System.out.println(user1);
			     * // System.out.println(s.isConnected()+" "+s.isOpen());
			     * // System.out.println(s.getStatistics());
			     * } catch (Exception e) {
			     * System.err.println("ERROR " + e);
			     * // s.disconnect();
			     * } finally {
			     * /*
			     * System.out.println(s.isConnected()+" "+s.isOpen());
			     * s.disconnect();
			     * //s.close();
			     * //sf.close();
			     * //System.out.println(s.getStatistics());
			     * System.out.println(s.getStatistics().getEntityCount());
			     * System.out.println(s.isConnected()+" "+s.isOpen());
			     *
			     * }
			     */
			    try {
				EntityManager em = emf.createEntityManager();
				em.getTransaction().begin();
				User2 user1 = em.find(User2.class, 444L);
				User2 user2 = em.getReference(User2.class, 1L);
				user2.mail = Test.getRandomMail();
				User2 user3 = new User2();
				user3.mail = Test.getRandomMail();
				User2 user4 = new User2();
				user4.mail = Test.getRandomMail();
				em.persist(user3);
				em.persist(user4);
				em.getTransaction().commit();
				System.out.println(Hibernate.isInitialized(user2));
				Hibernate.initialize(user2);
				System.out.println(Hibernate.isInitialized(user2));
				System.out.println(user4);
				System.out.println(user3);
				System.out.println(user2);
				System.out.println(user1);
				try {
				    em.remove(user1);
				} catch (Exception e) {
				    System.out.println("user not exist");
				    // throw new RuntimeException(e);
				}
				em.close();
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			}
		    }).start();
		    if (i % 2 == 0) {
			try {
			    Thread.currentThread().sleep(200);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			System.out.println("next iteration init4 started...");
		    }
		}
		System.err.println("init4 done...");
		stopWork.set(true);
	    }
	}).start();
	while (!stopWork.get()) {
	    Thread.currentThread().sleep(1000);
	}
	System.out.println("init4 end");
	// System.exit(0);
    }

    public void init5() {
	EntityManager em = emf.createEntityManager();
	CriteriaBuilder cb = em.getCriteriaBuilder();

	CriteriaQuery<Number> criteria = cb.createQuery(Number.class);
	criteria.select(cb.sum(criteria.from(User2.class).<Long>get("id")));
	System.out.println(em.createQuery(criteria).getSingleResult());
	em.close();
    }

    public void init6() {
	ListContainer lc = new ListContainer();
	lc.kek = "kek";
	for (int i = 0; i < 15; i++) {
	    ListItem li = new ListItem();
	    li.name = Test.getRandomMail();
	    li.lc = lc;
	    lc.item.add(li);
	}
	EntityManager em = emf.createEntityManager();
	em.getTransaction().begin();
	em.persist(lc);
	em.getTransaction().commit();
	em.close();
    }

    public void init7() {
	EmbededSet es = new EmbededSet();
	EntityManager em = emf.createEntityManager();

	em.getTransaction().begin();
	em.persist(es);
	em.getTransaction().commit();

	em.getTransaction().begin();
	EmbededSet est = em.find(EmbededSet.class, 1L);
	EmbededSet est2 = new EmbededSet();
	em.persist(est2);
	em.getTransaction().commit();
	System.out.println(est.setik.toString());
    }
    
    public void init8() {
	EntityManager em=emf.createEntityManager();
	em.getTransaction().begin();
	
	em.getTransaction().commit();

    }
}