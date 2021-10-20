package mvcTest;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

@Entity
public class Singer implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte [] image;
    @Transient
    private String b64i="";
    
    public void setB64i(String b64i) {
	this.b64i = b64i;
    }
    
    public String getB64i() {
	return b64i;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
   
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
	this.id = id;
    }
    @Override
    public String toString() {
	return "Singer [id=" + id + ", name=" + name + ", image=" + image + "]";
    }
    
    
}
