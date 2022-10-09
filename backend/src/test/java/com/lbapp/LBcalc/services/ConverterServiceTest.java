package com.lbapp.LBcalc.services;

import com.lbapp.LBcalc.models.CurrentFxRate;
import com.lbapp.LBcalc.repos.CurrentFxRatesRepo;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.valueOf;
import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;


@ExtendWith(MockitoExtension.class)
class ConverterServiceTest {
	private final Map<String, BigDecimal> CURRENCIES = Map.of(
			"EUR", ONE,
			"USD", valueOf(1.0521),
			"AUD", valueOf(1.5254),
			"GBP", valueOf(1.126695),
			"BTC", valueOf(6977.0896569209),
			"ETH", valueOf(685.29447470022),
			"FKE", valueOf(0.025)
	);

	@Mock
	private CurrentFxRatesRepo repo;

	private ConverterService converter;

	@BeforeEach
	void mockRepo() {
		converter = new ConverterService(repo);

		List<CurrentFxRate> data = CURRENCIES
				.entrySet()
				.stream()
				.map(this::createCurrentFxRateFrom)
				.toList();
		data.forEach(currentFxRate -> {
			lenient().when(repo.findBySymbolContains(currentFxRate.getSymbol())).thenReturn(of(currentFxRate));
		});

		lenient().when(repo.findAll()).thenReturn(data);
	}

	@AfterEach
	void printNewLine() {
		System.out.println();
	}

	@Test
	public void shouldConvertEUR2USD() {
		BigDecimal quantity = ONE;
		String from = "EUR";
		String to = "USD";

		BigDecimal expected = valueOf(1.0521);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldConvertUSD2EUR() {
		BigDecimal quantity = valueOf(1.0521);
		String from = "USD";
		String to = "EUR";

		BigDecimal expected = ONE;

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldConvertUSD2AUD() {
		BigDecimal quantity = valueOf(97);
		String from = "USD";
		String to = "AUD";

		BigDecimal expected = valueOf(140.6366314989069);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldConvertBTC2ETH() {
		BigDecimal quantity = valueOf(0.25);
		String from = "BTC";
		String to = "ETH";

		BigDecimal expected = valueOf(0.0245551694329534);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldConvertBTC2USD() {
		BigDecimal quantity = valueOf(0.000625);
		String from = "BTC";
		String to = "USD";

		BigDecimal expected = valueOf(0.0000000942459581);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldConvertBTC2GBP() {
		BigDecimal quantity = valueOf(0.0875);
		String from = "BTC";
		String to = "GBP";

		BigDecimal expected = valueOf(0.000014129933446);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldConvertBTC2FKE() {

		BigDecimal quantity = valueOf(0.33333);
		String from = "BTC";
		String to = "FKE";

		BigDecimal expected = valueOf(0.0000011943733576);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldConvertUSD2GBP() {
		BigDecimal quantity = valueOf(62510);
		String from = "USD";
		String to = "GBP";

		BigDecimal expected = valueOf(66942.0249500998);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldConvertUSD2BTC() {
		BigDecimal quantity = valueOf(6265690);
		String from = "USD";
		String to = "BTC";

		BigDecimal expected = valueOf(41551450330.26586);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldConvertUSD2ETH() {
		BigDecimal quantity = valueOf(7250);
		String from = "USD";
		String to = "ETH";

		BigDecimal expected = valueOf(4722350.481490918);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldConvertUSD2FKE() {
		BigDecimal quantity = valueOf(68230);
		String from = "USD";
		String to = "FKE";

		BigDecimal expected = valueOf(1621.28124702975);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldFailConvertWhenCurrencyNotFound() {
		BigDecimal quantity = valueOf(68230);
		String from = "bad";
		String to = "FKE";

		NullPointerException thrown = assertThrows(
				NullPointerException.class,
				() -> converter.convertAPI(from, to, quantity)
		);

		assertEquals("Symbol can not be null", thrown.getMessage());
	}

	@Test
	public void shouldFailConvertWhenInputCurrencyBlank() {
		BigDecimal quantity = valueOf(68230);
		String from = "";
		String to = "FKE";

		NullPointerException thrown = assertThrows(
				NullPointerException.class,
				() -> converter.convertAPI(from, to, quantity)
		);

		assertEquals("Symbol can not be blank", thrown.getMessage());
	}

	@Test
	public void shouldFailConvertWhenOutputCurrencyBlank() {
		BigDecimal quantity = valueOf(68230);
		String from = "FKE";
		String to = "";

		NullPointerException thrown = assertThrows(
				NullPointerException.class,
				() -> converter.convertAPI(from, to, quantity)
		);

		assertEquals("Symbol can not be blank", thrown.getMessage());
	}

	@Test
	public void shouldFailConvertWhenInputCurrencyNull() {
		BigDecimal quantity = valueOf(68230);
		String from = "FKE";
		String to = null;

		NullPointerException thrown = assertThrows(
				NullPointerException.class,
				() -> converter.convertAPI(from, to, quantity)
		);

		assertEquals("Symbol can not be null", thrown.getMessage());
	}

	@Test
	public void shouldFailConvertWhenOutputCurrencyNull() {
		BigDecimal quantity = valueOf(68230);
		String from = "FKE";
		String to = null;

		NullPointerException thrown = assertThrows(
				NullPointerException.class,
				() -> converter.convertAPI(from, to, quantity)
		);

		assertEquals("Symbol can not be null", thrown.getMessage());
	}

	@Test
	public void shouldConvertWhenQuantityZero() {
		BigDecimal quantity = valueOf(0);
		String from = "USD";
		String to = "BTC";

		BigDecimal expected = valueOf(0);

		BigDecimal actual = converter.convertAPI(from, to, quantity);

		assertThat(expected, Matchers.comparesEqualTo(actual));
	}

	@Test
	public void shouldConvertWhenQuantityNull() {
		BigDecimal quantity = null;
		String from = "USD";
		String to = "BTC";

		NullPointerException thrown = assertThrows(
				NullPointerException.class,
				() -> converter.convertAPI(from, to, quantity)
		);

		assertEquals("Amount can not be null", thrown.getMessage());
	}

	private CurrentFxRate createCurrentFxRateFrom(Map.Entry<String, BigDecimal> entry) {
		return new CurrentFxRate(null, entry.getKey(), entry.getValue());
	}
}
