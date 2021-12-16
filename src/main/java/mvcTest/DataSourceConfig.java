package mvcTest;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableJpaRepositories
@ComponentScan
public class DataSourceConfig {

    private static List<byte[]> list = new ArrayList<byte[]>();

    @Bean
    public DataSource datasource() {
	ComboPooledDataSource dataSource = new ComboPooledDataSource();

	try {
	    dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
	} catch (PropertyVetoException e) {
	    e.printStackTrace();
	}
	dataSource.setJdbcUrl("jdbc:mysql://localhost/test");
	dataSource.setUser("root");
	dataSource.setPassword("1234");
	dataSource.setMaxPoolSize(200);
	dataSource.setMinPoolSize(50);
	// dataSource.setCheckoutTimeout(30);
	dataSource.setMaxStatements(50);
	dataSource.setMaxIdleTime(120);

	return dataSource;
	// IF RETURN DEFAULT, LIKE NEXT, WILL BE RETURNED HIKARI DATASOURCE, BECAUSE
	// AUTOCONFIGURATION
	/*
	 * return
	 * DataSourceBuilder.create().driverClassName("com.mysql.cj.jdbc.Driver").url(
	 * "jdbc:mysql://localhost/test")
	 * .username("root").password("1234").build();
	 */
    }

    private Properties hibernateProperties() {
	Properties prop = new Properties();

	prop.put("javax.persistence.schema-generation.database.action", "none");
	prop.put("hibernate.show_sql", true);
	prop.put("hibernate.format_sql", false);
	prop.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
	prop.put("hibernate.connection.autocommit", true);

	return prop;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
	return new HibernateJpaVendorAdapter();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
	LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
	factory.setPackagesToScan("mvcTest");
	factory.setPersistenceUnitName("newName");
	factory.setDataSource(datasource());
	factory.setJpaProperties(hibernateProperties());
	factory.setJpaVendorAdapter(jpaVendorAdapter());
	factory.afterPropertiesSet();
	return factory.getNativeEntityManagerFactory();

    }

    @Bean
    public PlatformTransactionManager transactionManager() throws IOException {
	return new JpaTransactionManager(entityManagerFactory());
    }

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
	return new TomcatServletWebServerFactory();
    }

    // @PostConstruct
    public void init() {
	try {
	    setImage();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
	EntityManagerFactory emf = entityManagerFactory();
	EntityManager em = emf.createEntityManager();
	List<Singer> singers = new ArrayList<Singer>();
	for (int i = 0; i < 20; i++) {
	    Singer singer = new Singer();
	    singer.setName(RandomStringUtils.random(ThreadLocalRandom.current().nextInt(1, 10), true, true));
	    // singer.setImage(list.get(ThreadLocalRandom.current().nextInt(list.size() -
	    // 1)));
	    singers.add(singer);
	}
	singers.stream().forEach(e -> {
	    e.setImage(Base64.getEncoder().encodeToString(list.remove(list.size() - 1)));
	    em.persist(e);
	});
	em.getTransaction().begin();
	em.getTransaction().commit();
    }

    private static void setImage() throws IOException {
	Path p = Paths.get(/*SpringMvcTestApplication.args[0]*/"");
	List<Path> paths = new ArrayList<Path>();
	Files.walkFileTree(p, new HashSet<>(), 1, new FileVisitor<Path>() {

	    @Override
	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		/*
		 * if (file.toFile().length() > 3_500_000) {
		 * return FileVisitResult.CONTINUE;
		 * }
		 */
		try {
		    ImageIO.read(file.toFile());
		    paths.add(file);
		    list.add(Files.readAllBytes(file));
		} catch (IOException e) {
		    return FileVisitResult.CONTINUE;
		}
		return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	    }

	});
	/*
	 * paths.stream().forEach(e->{
	 * System.out.println("fileName: "+e.toString()+" size: "+e.toFile().length());
	 * });
	 */
    }
}
