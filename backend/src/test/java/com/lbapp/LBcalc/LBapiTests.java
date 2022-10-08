package com.lbapp.LBcalc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lbapp.LBcalc.models.FxRate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LBapiTests {

	static final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);


	@BeforeAll
	public static void setUp() {
		logger.setLevel(Level.INFO);
	}

	private InputStream get(String url) {
		InputStream inputStream = null;
		try {
			HttpRequest request = HttpRequest
					.newBuilder()
					.uri(new URI(url))
					.setHeader("Content-type", "application/xml")
					.GET()
					.build();

			HttpResponse<InputStream> response = HttpClient
					.newBuilder()
					.build()
					.send(request, HttpResponse.BodyHandlers.ofInputStream());
			inputStream = response.body();
		} catch (Exception e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		return inputStream;
	}

	private List<FxRate> parse(InputStream inputStream) {
		try {
			XmlMapper xmlMapper = new XmlMapper();

			return xmlMapper.readValue(inputStream, new TypeReference<List<FxRate>>() {});
		} catch (Exception e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		return new ArrayList<FxRate>();
	}

	@Test
	public void current_success () {
		String url = "https://www.lb.lt/webservices/FxRates/FxRates.asmx/getCurrentFxRates?tp=eu";

		InputStream inputStream = get(url);
		List<FxRate> list = parse(inputStream);

		list.forEach(System.out::println);
		assertTrue(list.size() > 0);
	}

	@Test
	public void current_bad_url_return_empty () {
		String url = "https://www.lb.lt/webservices/FxRates/FxRates.asmx/getCurrentFxRates?tp=randomstring";

		InputStream inputStream = get(url);
		List<FxRate> list = parse(inputStream);

		list.forEach(System.out::println);
		assertEquals(0, list.size());
	}

}
