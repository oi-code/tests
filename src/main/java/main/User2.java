package main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User2 {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = false, name = "kek")
    public String mail;

    public String getMail() {
	return mail;
    }

    public String toString() {
	return mail + " ID " + id;
    }
}
