package main;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ListContainer {
    @Id
    @GeneratedValue
    public Long id;
    public String kek;
    @OneToMany(mappedBy = "lc", cascade = CascadeType.PERSIST)    
    public Set<ListItem> item=new HashSet<ListItem>();      
}
