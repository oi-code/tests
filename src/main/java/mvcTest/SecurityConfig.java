package mvcTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) {
	try {
	    auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder()).withUser("1")
	    //this fucking string is the encrypted password of value 1. online bcrypt generator. yea.
		    .password("$2a$12$/RA9pYwo2UhNgNylFIDKBO9p92mk6CHjLxx.IltekYuhqf2i/qwQe").roles("adm");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    protected void configure(HttpSecurity http) {
	try {
	    // super.configure(http);
	    http.authorizeRequests().antMatchers("/").permitAll().and().authorizeRequests().antMatchers("/update/*")
		    .authenticated().and().authorizeRequests().antMatchers("/delete/*").authenticated().and()
		    .formLogin().loginPage("/singers/login").failureUrl("/singers/error").permitAll().and().logout().permitAll().and().csrf()
		    .disable();

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
    }
}
