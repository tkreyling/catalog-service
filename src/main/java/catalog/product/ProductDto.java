package catalog.product;

import lombok.Value;

@Value
public class ProductDto {
    private String name;

    private Long categoryId;
}
