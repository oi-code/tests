package mvcTest;

import javax.swing.JFrame;
import javax.swing.JTextPane;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
//public class SpringMvcTestApplication extends SpringBootServletInitializer {

public class SpringMvcTestApplication {
    
    public static String [] args;

    public static void main(String[] args) {
	SpringMvcTestApplication.args=args;
	SpringApplicationBuilder sb=new SpringApplicationBuilder(SpringMvcTestApplication.class);
	//SpringApplication.run(SpringMvcTestApplication.class, args);
	sb.headless(false);
	sb.run(args);
	
	
    }
    
    public static String[] getArgs() {
	return args;
    }

}
