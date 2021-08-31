package main;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@SuppressWarnings("serial")
@Entity
class SerClass implements Serializable {
    @Id
    @GeneratedValue
    @Access(AccessType.FIELD)
    private Long id;
    @Access(AccessType.PROPERTY)
    private String res = this.getClass().getSimpleName() + ".class.";
    @Access(AccessType.FIELD)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastAcces", insertable = true, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    private Date lastModificationTime;// LocalDateTime.now(ZoneId.of("GMT+3")).toString();

    public void setRes(String res) {
	this.res = res;
    }

    public String getRes() {
	return res;
    }

    @Override
    public String toString() {
	return res;
    }
}