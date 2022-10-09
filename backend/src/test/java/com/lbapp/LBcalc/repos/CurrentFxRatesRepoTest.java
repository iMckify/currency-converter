package com.lbapp.LBcalc.repos;

import com.lbapp.LBcalc.models.CurrentFxRate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DataJpaTest
class CurrentFxRatesRepoTest {
	@Autowired
	private CurrentFxRatesRepo repo;

	private final CurrentFxRate BTC = new CurrentFxRate(1, "BTC", new BigDecimal("6977.0896569209"));

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
		List<CurrentFxRate> expected = List.of(BTC);

		// when
		List<CurrentFxRate> actual = this.repo.findAll();

		// then
		assertEquals(expected, actual);
	}
}
