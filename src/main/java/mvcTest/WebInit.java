package mvcTest;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
	return new Class<?>[] { SecurityConfig.class, DataSourceConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
	return new Class<?>[] { WebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
	return new String[] { "/" };
    }

    @Override
    protected Filter[] getServletFilters() {
	CharacterEncodingFilter cef = new CharacterEncodingFilter();
	cef.setEncoding("utf-8");
	cef.setForceEncoding(true);
	return new Filter[] { new HiddenHttpMethodFilter(), cef };
    }

    protected void customizeRegistration(ServletRegistration.Dynamic reg) {
	reg.setMultipartConfig(getMultipartCinfigElement());
    }

    @Bean
    private MultipartConfigElement getMultipartCinfigElement() {
	return new MultipartConfigElement(null, 20_000_000, 20_000_000, 0);
    }

}
