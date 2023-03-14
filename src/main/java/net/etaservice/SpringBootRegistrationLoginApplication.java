package net.etaservice;

import net.etaservice.comon.utilservice.telegram.BotNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("net.etaservice.comon.utilservice.telegram")
@ComponentScan("net.etaservice.appmanager")
public class SpringBootRegistrationLoginApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootRegistrationLoginApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRegistrationLoginApplication.class, args);

	}

	@Bean
	@Profile("dev")
	public String dev() {
		System.out.println("Running development environment");
		return "dev";
	}
	@Bean
	@Profile("prod")
	public String prod() {
		System.out.println("Running production environment");
		return "prod";
	}

}
