package main;

import java.util.concurrent.TimeUnit;
import javax.persistence.*;

public class Test {   

    public static void main(String... args) throws Exception {

	Hoba t = new Hoba();
	t.setText("HoHoHo");

	EntityManagerFactory emf = Persistence.createEntityManagerFactory("try");
	EntityManager em = emf.createEntityManager();	
	new Thread() {
	    {
		this.setDaemon(true);
	    }
	    @Override
	    public void run() {
		em.getTransaction().begin();
		Hoba h=(Hoba)em.getReference(Hoba.class, 3);
		h.setText("jaja");
		em.merge(h);
		em.getTransaction().commit();
		System.out.println(h);
	    }
	}.start();
	
	TimeUnit.SECONDS.sleep(4);
	/*em.getTransaction().begin();
	@SuppressWarnings("unchecked")
	List<Message> rs = em.createQuery("select asdf from Message asdf").getResultList();
	System.out.println("---->\t\t" + em.getTransaction().isActive());
	em.getTransaction().commit();
	System.out.println("---->\t\t" + em.getTransaction().isActive());
	
	  for(Message m:rs) {
	  System.out.println(m);
	  }
	 
	em.getTransaction().commit();*/
	System.exit(0);
    }
}

@Entity
@Table(name = "object")
class Hoba {    
    private static int q = 0;
    @Id
 // @Column(name = "id") //это можно не указывать
    private int id;
    @Column(name = "name")
    private String text;

    public Hoba() {
	id = q++;//ThreadLocalRandom.current().nextInt();
	text = "NewName" + id;
    }

    public void setText(String text) {
	this.text = text + id;
    }

    public String getText() {
	return text;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String toString() {
	return String.format("id:\t%d\tname:\t%s", id, text);
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
