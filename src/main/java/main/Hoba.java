package main;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
//@MappedSuperclass
@Table(name = "OBJECTS")
@Inheritance(strategy = InheritanceType.JOINED)
class Hoba {
    @Id
    @GeneratedValue(generator = "generator")
    // @Column(name = "id")
    private Long id;
    @Column(name = "name")
    @Lob
    private String text;
    @Column(name = "date")
    private Date ldt = new Date();
    // String ldt=LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE,
    // dd.MMMM.yyyy, HH:mm:ss", Locale.ENGLISH));
    @Column(name = "mass", length = 1000)
    private int[] arr = ThreadLocalRandom.current().ints(0, 500).limit(10).toArray();
    @Column(name = "serClass")
    private SerClass serClass = new SerClass();
    @Column(name = "randomInt")
    private int randInt = ThreadLocalRandom.current().nextInt();

    public Hoba() {
	// id = q++;// ThreadLocalRandom.current().nextInt();
	text = "NewName_" + getClass().getSimpleName();
    }

    public void setText(String text) {
	this.text = text;
    }

    public String getText() {
	return text;
    }

    /*
     * public int getId() {
     * return id;
     * }
     * 
     * public void setId(int id) {
     * this.id = id;
     * }
     */

    public String toString() {
	return String.format("\n\t--->" + this.getClass().getName() + "\n\tid:\s%d\n\tname:\s%s\n\tdate:\s%s", id, text,
		ldt + "\n\t" + Arrays.toString(arr) + "\n\t" + serClass + "\n");
    }

    /*
     * Statement st=con.createStatement();
     * String sq="insert into test.objects (id) value ('"+t.getId()+"');";
     * String sq2="insert into test.objects (name) value ('"+t.getText()+"');";
     * System.out.println(sq+"\t"+sq2);
     * st.execute(sq);
     * st.execute(sq2);
     * Statement st3=con.createStatement();
     * String sq4="delete from test.objects where id=0";
     * st3.execute(sq4);
     * 
     * Statement st2=con.createStatement();
     * String sq3="select id, name from test.objects";
     * ResultSet rs=st2.executeQuery(sq3);
     * while(rs.next()) {
     * System.out.println(rs.getInt(1)+"\t"+rs.getString(2));
     * }
     */
}