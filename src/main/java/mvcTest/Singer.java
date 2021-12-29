package mvcTest;

import java.io.Serializable;
import java.util.Base64;

import javax.persistence.AttributeConverter;
import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Singer implements Serializable {
    /**
    
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Convert(converter = SingerImageCinvertor.class)
    private String image = "";

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getImage() {
	return image;
    }

    public void setImage(String image) {
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

@Converter
class SingerImageCinvertor implements AttributeConverter<String, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(String attribute) {
	if (attribute == null || attribute.isEmpty() || attribute.isEmpty()) {
	    return new byte[0];
	}
	byte[] result = Base64.getDecoder().decode(attribute);
	return result;
    }

    @Override
    public String convertToEntityAttribute(byte[] dbData) {
	String result = Base64.getEncoder().encodeToString(dbData);
	return result;
    }

}
