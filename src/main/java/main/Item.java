package main;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

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