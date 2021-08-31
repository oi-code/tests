package main;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Persistence;


public class AllFieldSaver {  
    
    public static void main(String... args) {
	EntityManagerFactory emf=Persistence.createEntityManagerFactory("try");
	EntityManager  em=emf.createEntityManager();
	AllFieldSaverTest afs=new AllFieldSaverTest();
	em.getTransaction().begin();
	em.persist(afs);
	em.getTransaction().commit();
	System.out.println("kek");
    }

}

@Entity
class AllFieldSaverTest{
    @Id
    @GeneratedValue
    private Long id;
    private String st="st";
    private Integer intr =2; 
}
