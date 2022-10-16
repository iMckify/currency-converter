package com.lbapp.LBcalc.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lbapp.LBcalc.models.FxRateDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.io.InputStream.nullInputStream;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

public class LbApiTest {
	private static List<FxRateDto> fxRates;

	@AfterAll
	public static void printFxRates() {
		if (fxRates != null && !fxRates.isEmpty()) {
			fxRates.forEach(System.out::println);
			System.out.println();
			fxRates.clear();
		}
	}

	@Test
	public void shouldGetAndParseFxRates() {
		// given
		String url = "https://www.lb.lt/webservices/FxRates/FxRates.asmx/getCurrentFxRates?tp=eu";

		// when
		fxRates = getFxRatesFrom(url);

		// then
		assertTrue(fxRates.size() > 0);
		assertTrue(fxRates.stream().allMatch(fxRateDto -> fxRateDto.getCurrencyPairs().size() == 2));
	}

	@Test
	public void shouldReturnEmptyListWhenBadUrl() {
		// given
		String url = "https://www.lb.lt/webservices/FxRates/FxRates.asmx/getCurrentFxRates?tp=randomstring";

		// when
		fxRates = getFxRatesFrom(url);

		// then
		assertTrue(fxRates.isEmpty());
	}

	@Test
	public void shouldReturnEmptyListWhenResponseCannotBeParsedToFxRates() {
		// given
		String url = "https://www.google.com";

		// when
		fxRates = getFxRatesFrom(url);

		// then
		assertTrue(fxRates.isEmpty());
	}

	@Test
	public void shouldFailParsingWhenBlankUrl() {
		// given
		String url = "";

		// when
		NullPointerException exception = assertThrows(NullPointerException.class, () -> getFrom(url));

		// then
		assertEquals("Url can not be blank", exception.getMessage());
	}

	@Test
	public void shouldFailParsingWhenNullInputStream() {
		// given
		InputStream inputStream = null;

		// when
		NullPointerException exception = assertThrows(NullPointerException.class, () -> parseFxRatesFrom(inputStream));

		// then
		assertEquals("InputStream of fx rates can not be null", exception.getMessage());
	}

	@Test
	public void shouldReturnEmptyListWhenEmptyInputStream() {
		// given
		InputStream inputStream = nullInputStream();

		// when
		fxRates = parseFxRatesFrom(inputStream);

		// then
		assertTrue(fxRates.isEmpty());
	}

	@Test
	public void shouldNotFailWhenDeserializing3Pairs() throws JsonProcessingException {
		String json = """
			<FxRates xmlns="http://www.lb.lt/WebServices/FxRates">
				<FxRate>
				    <Tp>EU</Tp>
				    <Dt>2022-10-14</Dt>
				    <CcyAmt>
				      <Ccy>EUR</Ccy>
				      <Amt>1</Amt>
				    </CcyAmt>
				    <CcyAmt>
				      <Ccy>AUD</Ccy>
				      <Amt>1.5493</Amt>
				    </CcyAmt>
				    <CcyAmt>
				      <Ccy>CAD</Ccy>
				      <Amt>1.1111</Amt>
				    </CcyAmt>
				  </FxRate>
				</FxRates>
				""";
		List<FxRateDto> rates = new XmlMapper().readValue(json, new TypeReference<List<FxRateDto>>() {
		});
		assertTrue(rates.get(0).getCurrencyPairs().size() == 3);
	}

	private List<FxRateDto> getFxRatesFrom(String url) {
		InputStream inputStream = getFrom(url);
		return parseFxRatesFrom(inputStream);
	}

	private InputStream getFrom(String url) {
		requireNonNull(url, "Url can not be null");
		requireNonNull(url.isBlank() ? null : url, "Url can not be blank");
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
		} catch (Exception ignored) {}
		return inputStream;
	}

	private List<FxRateDto> parseFxRatesFrom(InputStream inputStream) {
		requireNonNull(inputStream, "InputStream of fx rates can not be null");
		try {
			return new XmlMapper().readValue(inputStream, new TypeReference<List<FxRateDto>>() {
			});
		} catch (Exception ignored) {
		}
		return List.of();
	}
}
