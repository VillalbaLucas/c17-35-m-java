package com.NoCountry.Patrickscoin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String path;
}
