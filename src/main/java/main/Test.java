package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public class Test {

    public static final String HOME = System.getProperty("user.home");
    public static volatile boolean check = false, isAlive = true;

    public static void getLine() {
	System.err.print("--->\sLine:\s" + Thread.currentThread().getStackTrace()[2].getLineNumber() + ".\sThread:\s"
		+ Thread.currentThread().getName() + ".\sInfo:\s");
    }

    public static String getRandomMail() {
	StringBuilder sb = new StringBuilder();
	ThreadLocalRandom tlr = ThreadLocalRandom.current();
	tlr.ints(97, 119).limit(tlr.nextInt(5, 15)).mapToObj(q -> q).collect(Collectors.toList()).stream().map(e -> e)
		.forEach(eas -> {
		    if (tlr.nextInt(10) < 7) {
			sb.append((char) ((int) eas));
		    } else {
			sb.append(((char) ((int) eas - 32)));
		    }
		});
	int rnd = tlr.nextInt(3);
	switch (rnd) {
	case 0: {
	    sb.append("@gmail.com");
	    break;
	}
	case 1: {
	    sb.append("@yandex.ru");
	    break;
	}
	case 2: {
	    sb.append("@microsoft.com");
	    break;
	}
	}
	return sb.toString();
    }

    @SuppressWarnings("static-access")
    public static void main(String... args) {
	try {
	    System.out.println("init test main...");
	    Connection con = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "1234");
	    Statement st = con.createStatement();
	    try {
		st.executeUpdate("DROP DATABASE test");
		System.err.println("DATABASE DELETED");
	    } catch (Exception e) {
		System.err.println("database not dropped");
	    }
	    try {
		st.executeUpdate("CREATE DATABASE test");
		System.err.println("DATABASE CREATED");
	    } catch (Exception e) {
		System.err.println("database not created");
	    }

	    con.close();
	    /*
	     * Connection con=DriverManager.getConnection("jdbc:mysql://localhost/test",
	     * "root", "1234");
	     * System.out.println(con.isClosed());
	     * Statement st=con.createStatement();
	     * String sql="select*from test.kek";
	     * //Boolean q=st.execute(sql);
	     * ResultSet rs=st.executeQuery(sql);
	     * while(rs.next()) {
	     * System.out.println(rs.getInt("id"));
	     * }
	     * con.close();
	     */
	    Initializer i = new Initializer();
	    // i.init1();
	    // i.init2();
	    // i.init3();
	    // i.init4();
	    // i.init5();
	    // i.init6();
	    // i.init7();
	    i.init8();
	    // Runtime.getRuntime().exec("java -jar " + Test.HOME +
	    // "\\Desktop\\helper.jar");
	    // for (int i = 0; i < 10; i++) {
	    // System.err.println(Test.getRandomMail());
	    // }

	} catch (Throwable e) {
	    e.printStackTrace();
	} finally {
	    System.out.println("exit test main...");
	    try {
		System.out.println("before main exit...");
		TimeUnit.SECONDS.sleep(2);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    System.out.println("done.");
	    System.exit(0);
	}

    }
}
