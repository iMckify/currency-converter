package com.lbapp.LBcalc.repos;

import com.lbapp.LBcalc.currency.models.Currency;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DataJpaTest
@ComponentScan({"com.lbapp.LBcalc"})
class CurrencyRepoTest {

	@Autowired
	private CurrencyRepo repo;

	private final Currency BTC = new Currency(1, "BTC", new BigDecimal("6977.0896569209"));

	@BeforeEach
	void seedRepo() {
		repo.save(BTC);
	}

	@AfterEach
	public void destroyAll() {
		repo.deleteAll();
	}

	@Test
	public void shouldFindAll() {
		// given
		List<Currency> expected = List.of(BTC);

		// when
		List<Currency> actual = this.repo.findAll();

		// then
		assertThat(expected, equalTo(actual));
	}
}
