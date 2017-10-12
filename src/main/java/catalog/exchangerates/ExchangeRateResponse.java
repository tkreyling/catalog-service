package catalog.exchangerates;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExchangeRateResponse {
    private String base;
    private Map<String, BigDecimal> rates;
}
