package main;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

public class Test {

    public static Logger log = Logger.getLogger("kekw");
    private static List<Hoba> hobas;

    public static void main(String... args) throws Exception {
	Test.init();
    }
    
    private static void init() throws Exception{
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
		    log.warn("start thread");
		    em.getTransaction().begin();
		    Hoba h = new Hoba();// em.getReference(Hoba.class, 3);		    
		    em.persist(h);
		    em.persist(t);		   
		    em.getTransaction().commit();
		    // System.out.println(h+"\n"+t);
		    log.warn("end thread");
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

	CriteriaBuilder cb = em.getCriteriaBuilder();
	CriteriaQuery<Hoba> query = cb.createQuery(Hoba.class);
	Root<Hoba> fromHoba = query.from(Hoba.class);
	Path<String> p = fromHoba.get("text");

	query.where(cb.like(p, cb.parameter(String.class, "pattern")));
	// query.select(fromHoba);
	// List<Hoba> hobas = em.createQuery(query).getResultList();
	hobas = em.createQuery(query).setParameter("pattern", t.getText()).getResultList();
	// TimeUnit.SECONDS.sleep(3);
	em.close();
	emf.close();
	TimeUnit.SECONDS.sleep(2);
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
	for (Hoba b : hobas) {
	    System.out.println(b);
	}

	System.exit(0);
    }
}

@Entity
@Table(name = "object")
class Hoba {
    private static int q = 0;
    @Id
    @GeneratedValue(generator = "generator")
    // @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String text;
    @Column(name = "date")
    private String ldt = LocalDateTime.now()
	    .format(DateTimeFormatter.ofPattern("EEE, dd.MMMM.yyyy, HH:mm:ss", Locale.ENGLISH)).toString();

    public Hoba() {
	// id = q++;// ThreadLocalRandom.current().nextInt();
	text = "NewName_" + id;
    }

    public void setText(String text) {
	this.text = text + id;
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
	return String.format("--->" + this.getClass().getName() + "\n\tid:\s%d\n\tname:\s%s\n\tdate:\s%s", id, text,
		ldt);
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
