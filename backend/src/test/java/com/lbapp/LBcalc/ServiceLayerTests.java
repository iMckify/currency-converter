package com.lbapp.LBcalc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.lbapp.LBcalc.models.CurrentFxRate;
import com.lbapp.LBcalc.repos.CurrentFxRatesRepo;
import com.lbapp.LBcalc.services.ConverterService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;


@ExtendWith(MockitoExtension.class)
class ServiceLayerTests {

	static final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

	@Mock
	private CurrentFxRatesRepo repo;

	private ConverterService converter;

	@BeforeAll
	static void setUp() {
		logger.setLevel(Level.INFO);
	}

	@BeforeEach
	void mockRepo() {
		converter = new ConverterService(repo);

		LinkedHashMap<String, BigDecimal> currencies = new LinkedHashMap<String, BigDecimal>() {{
			put("EUR", new BigDecimal("1"));
			put("USD", new BigDecimal("1.0521"));
			put("AUD", new BigDecimal("1.5254"));
			put("GBP", new BigDecimal("1.126695"));
			put("BTC", new BigDecimal("6977.0896569209"));
			put("ETH", new BigDecimal("685.29447470022"));
			put("FKE", new BigDecimal("0.025"));
		}};

		List<CurrentFxRate> data = new ArrayList<>();
		for (Map.Entry<String, BigDecimal> entry : currencies.entrySet()) {
			CurrentFxRate currentFxRate = new CurrentFxRate(null, entry.getKey(), entry.getValue());
			data.add(currentFxRate);
			lenient().when(repo.findBySymbolContains(entry.getKey())).thenReturn(Optional.of(currentFxRate));
		}
		lenient().when(repo.findAll()).thenReturn(data);
	}

	@AfterEach
	void newLine() {
		System.out.println();
	}

	//===============  SERVICE  ==============================
	@Test
	public void convert_EUR2USD_result() {
		BigDecimal quantity = BigDecimal.valueOf(1);
		String from = "EUR";
		String to = "USD";

		BigDecimal expected = BigDecimal.valueOf(0.809552722000000000);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}

	@Test
	public void convert_USD2EUR_result() {
		BigDecimal quantity = BigDecimal.valueOf(0.809552722);
		String from = "USD";
		String to = "EUR";

		BigDecimal expected = BigDecimal.valueOf(1);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}

	@Test
	public void convert_USD2AUD_result() {
		BigDecimal quantity = BigDecimal.valueOf(97);
		String from = "USD";
		String to = "AUD";

		BigDecimal expected = BigDecimal.valueOf(140.6366314989069000);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}

	@Test
	public void convert_BTC2ETH_result() {
		BigDecimal quantity = BigDecimal.valueOf(0.25);
		String from = "BTC";
		String to = "ETH";

		BigDecimal expected = BigDecimal.valueOf(0.0245551694329534);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}

	@Test
	public void convert_BTC2USD_result() {
		BigDecimal quantity = BigDecimal.valueOf(0.000625);
		String from = "BTC";
		String to = "USD";

		BigDecimal expected = BigDecimal.valueOf(0.0000000725188403);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}

	@Test
	public void convert_BTC2GBP_result() {
		BigDecimal quantity = BigDecimal.valueOf(0.0875);
		String from = "BTC";
		String to = "GBP";

		BigDecimal expected = BigDecimal.valueOf(0.0000141299334460);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}

	@Test
	public void convert_BTC2FKE_result() {

		BigDecimal quantity = BigDecimal.valueOf(0.33333);
		String from = "BTC";
		String to = "FKE";

		BigDecimal expected = BigDecimal.valueOf(0.0000011943733576);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}

	@Test
	public void convert_USD2GBP_result() {
		BigDecimal quantity = BigDecimal.valueOf(62510);
		String from = "USD";
		String to = "GBP";

		BigDecimal expected = BigDecimal.valueOf(86998.292434868744719011);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}

	@Test
	public void convert_USD2BTC_result() {
		BigDecimal quantity = BigDecimal.valueOf(6265690);
		String from = "USD";
		String to = "BTC";

		BigDecimal expected = BigDecimal.valueOf(54000535980.499999999999999397);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}

	@Test
	public void convert_USD2ETH_result() {
		BigDecimal quantity = BigDecimal.valueOf(7250);
		String from = "USD";
		String to = "ETH";

		BigDecimal expected = BigDecimal.valueOf(6137197.499999999999999890);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}

	@Test
	public void convert_USD2FKE_result() {
		BigDecimal quantity = BigDecimal.valueOf(68230);
		String from = "USD";
		String to = "FKE";

		BigDecimal expected = BigDecimal.valueOf(2107.027687815001000000);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}

	@Test
	public void convert_bad2FKE_ex() {
		BigDecimal quantity = BigDecimal.valueOf(68230);
		String from = "bad";
		String to = "FKE";

		IllegalArgumentException thrown = assertThrows(
				IllegalArgumentException.class,
				() -> converter.convertAPI(from, to, quantity),
				"Bad symbol"
		);

		assertEquals("Bad symbol", thrown.getMessage());
	}

	@Test
	public void convert_null2FKE_ex() {
		BigDecimal quantity = BigDecimal.valueOf(68230);
		String from = null;
		String to = "FKE";

		IllegalArgumentException thrown = assertThrows(
				IllegalArgumentException.class,
				() -> converter.convertAPI(from, to, quantity),
				"Bad symbol"
		);

		assertEquals("Bad symbol", thrown.getMessage());
	}

	@Test
	public void convert_zeroQuantity_result() {
		BigDecimal quantity = BigDecimal.valueOf(0);
		String from = "USD";
		String to = "BTC";

		BigDecimal expected = BigDecimal.valueOf(0);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected,  Matchers.comparesEqualTo(actual));
	}
}
