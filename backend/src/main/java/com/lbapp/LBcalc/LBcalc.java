package com.lbapp.LBcalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@EnableConfigurationProperties
@EnableJpaRepositories(basePackages = "com.lbapp.LBcalc.repos")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class LBcalc {

	public static void main(String[] args) {
		SpringApplication.run(LBcalc.class, args);
	}

	@Component
	@ConfigurationProperties(prefix="api.lb")
	public class PropsConfig {

		private List<URL> forex = new ArrayList<>();

		public List<URL> getForex() { return this.forex; }
	}
}
