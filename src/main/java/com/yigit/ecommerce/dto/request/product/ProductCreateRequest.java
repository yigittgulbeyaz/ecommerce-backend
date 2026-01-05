package com.yigit.ecommerce.dto.request.product;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank String name,
        String description,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @NotNull Long categoryId) {
}
