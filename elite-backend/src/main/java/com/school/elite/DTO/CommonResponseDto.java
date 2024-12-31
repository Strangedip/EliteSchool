package com.school.elite.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponseDto {
    private Integer responseCode;
    private String responseMessage;
    private String errorMessage;
    private Object payload;
}
