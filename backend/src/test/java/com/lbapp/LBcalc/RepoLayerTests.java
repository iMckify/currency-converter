package com.lbapp.LBcalc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.lbapp.LBcalc.models.CurrentFxRate;
import com.lbapp.LBcalc.repos.CurrentFxRatesRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
@DataJpaTest
class RepoLayerTests {

	static final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

	@Autowired
	private CurrentFxRatesRepo repo;

	@BeforeAll
	static void setUp() {
		logger.setLevel(Level.INFO);
	}

	@BeforeEach
	void mockRepo() {
        repo.save(new CurrentFxRate(1,"BTC",new BigDecimal("6977.0896569209")));
	}

    @AfterEach
    public void destroyAll(){
        repo.deleteAll();
    }

	//===============  REPO  ==============================
	@Test
	public void findAll_result() {
		List<CurrentFxRate> expected = Collections.singletonList(new CurrentFxRate(1,"BTC",new BigDecimal("6977.0896569209")));

		List<CurrentFxRate> actual = this.repo.findAll();

		assertEquals(expected, actual);
	}
}
