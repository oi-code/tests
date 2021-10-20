package mvcTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringMvcTestApplication {
    
    public static String [] args;

    public static void main(String[] args) {
	SpringMvcTestApplication.args=args;
	SpringApplication.run(SpringMvcTestApplication.class, args);
    }
    
    public static String[] getArgs() {
	return args;
    }

}
