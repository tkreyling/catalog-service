package catalog.exchangerates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate;
    private final String host;

    public ExchangeRateService(RestTemplate restTemplate, @Value("${fixer.io.host}") String host) {
        this.restTemplate = restTemplate;
        this.host = host;
    }

    public BigDecimal getLatestExchangeRateToEuro(String currency) {
        if ("EUR".equals(currency)) return BigDecimal.ONE;

        ExchangeRateResponse exchangeRateResponse = restTemplate.getForObject(
                "http://" + host + "/latest?base={currency}&symbol=EUR",
                ExchangeRateResponse.class,
                currency
        );
        return exchangeRateResponse.getRates().get("EUR");
    }
}
