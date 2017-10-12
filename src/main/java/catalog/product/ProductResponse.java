package catalog.product;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class ProductResponse {
    private long id;

    private String name;

    private BigDecimal price;

    private String currency;

    private BigDecimal priceInEuro;

    private Long categoryId;
}
