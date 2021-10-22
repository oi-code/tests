package mvcTest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan
@EnableAsync
public class WebConfig implements WebMvcConfigurer{
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
        .addResourceLocations("/")
        .setCachePeriod(30_000_000);
    }
    
    @Bean
    public InternalResourceViewResolver jspReslover() {
	InternalResourceViewResolver res=new InternalResourceViewResolver();
	res.setOrder(2);
	res.setPrefix("/WEB-INF/views/");
	res.setSuffix(".jsp");
	res.setRequestContextAttribute("requestContext");
	return res;
    }
    
    @Bean
    public InternalResourceViewResolver htmlReslover() {
	InternalResourceViewResolver res=new InternalResourceViewResolver();
	res.setOrder(3);
	res.setPrefix("/WEB-INF/views/");
	res.setSuffix(".html");
	res.setRequestContextAttribute("requestContext");
	return res;
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
       registry.addViewController("/").setViewName("singers/list");
    }
}
