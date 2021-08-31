package main;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
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