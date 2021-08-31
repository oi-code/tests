package main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn
class HobaSuccessor extends Hoba {
    @Column(name = "name")
    private String des = this.getClass().getSimpleName() + ".class";

    public HobaSuccessor() {
	super();
	setText(this.getClass().getAnnotatedSuperclass() + "_" + des);
    }

    @Override
    public String getText() {
	return des;
    }
}