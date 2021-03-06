package catalog.product;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class ProductRequest {
    private String name;

    private BigDecimal price;

    private String currency;

    private Long categoryId;
}
