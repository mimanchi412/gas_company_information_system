package com.classwork.gas.entity.dto.billing;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BillingDtos {

    @Data
    public static class MeterReadingRequest {
        @NotBlank
        private String meterNo;

        @NotNull
        @DecimalMin("0")
        private BigDecimal readingValue;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime readingTime;

        private String periodLabel;
    }

    @Data
    public static class CreateBillRequest {
        @NotNull
        private Long customerId;
        private Long meterId;
        private Long readingId;

        @NotNull
        @DecimalMin("0")
        private BigDecimal amount;

        @NotNull
        private LocalDate dueDate;
    }

    @Data
    public static class PayBillRequest {
        @NotNull
        @DecimalMin("0")
        private BigDecimal amount;

        private String method;
        private String remark;
    }
}
