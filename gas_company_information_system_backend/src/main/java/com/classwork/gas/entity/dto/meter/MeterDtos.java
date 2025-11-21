package com.classwork.gas.entity.dto.meter;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeterDtos {
    @Data
    public static class AddMeterRequest {
        @NotBlank
        private String meterNo;

        private String status;
        private String location;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime installedAt;
    }
}
