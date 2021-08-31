package main;

import java.util.Set;
import java.util.HashSet;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

@Entity
public class EmbededSet {
    @Id
    @GeneratedValue
    private Long id;
    @ElementCollection
    /*
     * will be created table with name embededset_setik, which contains this set
     * id for this table will be got from embededset id
     */
    public Set<String> setik = new HashSet<>();
    {
	for (int i = 0; i < 10; i++) {
	    setik.add(Test.getRandomMail());
	}
    }

}
