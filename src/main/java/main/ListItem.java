package main;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ListItem {
    @Id
    @GeneratedValue
    public Long id;
    public String name;
    @ManyToOne
    @JoinColumn(name="listitem_id")
    public ListContainer lc;
}
