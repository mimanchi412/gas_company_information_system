package com.classwork.gas.entity.dto.customer;

import com.classwork.gas.entity.enums.CustomerType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerDtos {

    @Data
    public static class CreateOrUpdateCustomerRequest {
        @NotBlank
        private String name;

        private CustomerType type;
        private String contactPhone;
        private String email;
        private String address;
    }
}
