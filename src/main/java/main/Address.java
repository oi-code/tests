package main;

import javax.persistence.Embeddable;

@Embeddable
class Address {
    /*
     * @Id
     * 
     * @GeneratedValue
     * private Long id;
     */
    private String city = "Moskow";
    private String country = "Russia";

    /*
     * @Deprecated
     * public Address() {
     * 
     * }
     */

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {

	this.city = city;
    }

    public String toString() {
	return String.format("\n\t\tCountry:\s%s\n\t\tCity:\s%s\n", country, city);
    }
}