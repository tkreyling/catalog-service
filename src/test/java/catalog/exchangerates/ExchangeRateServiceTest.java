package catalog.exchangerates;

import catalog.Application;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ExchangeRateServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Test
    public void getLatest() throws Exception {
        stubFor(get(urlEqualTo("/latest?symbols=GBP"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("fixer.io.json")
                ));

        ExchangeRateResponse exchangeRateResponse = exchangeRateService.getLatestExchangeRates("GBP");

        assertEquals("EUR", exchangeRateResponse.getBase());
        assertEquals(new BigDecimal("0.8971"), exchangeRateResponse.getRates().get("GBP"));
    }
}