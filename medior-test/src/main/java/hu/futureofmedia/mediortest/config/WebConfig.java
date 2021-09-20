package hu.futureofmedia.mediortest.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.util.StdDateFormat;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers( ViewControllerRegistry registry ) {
//        registry.addViewController( "/auth/login" ).setViewName( "login" );
        registry.addViewController("/").setViewName("forward:index.html");
        registry.addViewController("/**").setViewName("/");
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.dateFormat(
                new StdDateFormat()
                        .withColonInTimeZone(true)
        );
    }
}
