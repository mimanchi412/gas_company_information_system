package com.classwork.gas.entity.dto.workorder;

import com.classwork.gas.entity.enums.WorkOrderCategory;
import com.classwork.gas.entity.enums.WorkOrderStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkOrderDtos {

    @Data
    public static class CreateWorkOrderRequest {
        private Long customerId;
        private Long meterId;

        @NotBlank
        private String title;

        private String description;
        private WorkOrderCategory category;
    }

    @Data
    public static class AssignRequest {
        @NotBlank
        private String assignedTo;
    }

    @Data
    public static class UpdateStatusRequest {
        private WorkOrderStatus status;
    }
}
