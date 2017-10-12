package catalog.exchangerates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate;
    private final String host;

    public ExchangeRateService(RestTemplate restTemplate, @Value("${fixer.io.host}") String host) {
        this.restTemplate = restTemplate;
        this.host = host;
    }

    public ExchangeRateResponse getLatestExchangeRates(String symbol) {
        return restTemplate.getForObject(
                "http://" + host + "/latest?symbols={symbol}",
                ExchangeRateResponse.class,
                symbol
        );
    }
}
