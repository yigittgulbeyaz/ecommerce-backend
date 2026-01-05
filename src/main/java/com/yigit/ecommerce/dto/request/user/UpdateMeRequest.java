package com.yigit.ecommerce.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateMeRequest(
        @NotBlank String name) {
}
