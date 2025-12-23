package com.yigit.ecommerce.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public class UpdateMeRequest {

    @NotBlank
    private String name;

    public UpdateMeRequest() {}

    public UpdateMeRequest(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
