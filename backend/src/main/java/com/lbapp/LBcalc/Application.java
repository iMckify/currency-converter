package com.lbapp.LBcalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@EnableScheduling
@EnableConfigurationProperties
@EnableJpaRepositories(basePackages = "com.lbapp.LBcalc.repos")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Component
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public class CorsFilter implements Filter {

		public void init(FilterConfig filterConfig) {
		} // not needed

		public void destroy() {} // not needed

		public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
			HttpServletResponse response = (HttpServletResponse) res;
			HttpServletRequest request = (HttpServletRequest) req;
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Methods",
					"ACL, CANCELUPLOAD, CHECKIN, CHECKOUT, COPY, DELETE, GET, HEAD, LOCK, MKCALENDAR, MKCOL, MOVE, OPTIONS, POST, PROPFIND, PROPPATCH, PUT, REPORT, SEARCH, UNCHECKOUT, UNLOCK, UPDATE, VERSION-CONTROL");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers",
					"Origin, X-Requested-With, Content-Type, Accept, Key, Authorization");

			if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				chain.doFilter(req, res);
			}
		}
	}

	@Component
	@ConfigurationProperties(prefix = "api.lb")
	public class PropsConfig {

		private URI current;

		private String history;

		public URI getCurrent() {
			return current;
		}

		public void setCurrent(URI current) {
			this.current = current;
		}

		public URI getHistory(String symbol, String dateFrom, String dateTo) {
			URI uri = null;
			try {
				String url = MessageFormat.format(history, symbol, dateFrom, dateTo);
				uri = new URL(url).toURI();
			} catch (NullPointerException | IllegalArgumentException formatException) {
			} catch (URISyntaxException | MalformedURLException ignored) {
			}
			return ofNullable(uri)
					.orElseThrow(() -> new IllegalStateException("Forex data provider unreachable."));
		}

		public void setHistory(String history) {
			this.history = history;
		}
	}
}
