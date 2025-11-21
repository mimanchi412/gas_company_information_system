package com.classwork.gas.service;

import com.classwork.gas.entity.dto.workorder.WorkOrderDtos;
import com.classwork.gas.entity.WorkOrder;
import com.classwork.gas.entity.enums.WorkOrderStatus;
import com.classwork.gas.entity.enums.WorkOrderCategory;

import java.util.List;

public interface WorkOrderService {
    WorkOrder create(WorkOrderDtos.CreateWorkOrderRequest request);

    WorkOrder assign(Long id, WorkOrderDtos.AssignRequest request);

    WorkOrder updateStatus(Long id, WorkOrderDtos.UpdateStatusRequest request);

    WorkOrder get(Long id);

    List<WorkOrder> list(WorkOrderStatus status, WorkOrderCategory category);
}
