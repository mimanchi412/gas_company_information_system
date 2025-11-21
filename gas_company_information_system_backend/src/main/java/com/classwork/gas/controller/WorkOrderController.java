package com.classwork.gas.controller;

import com.classwork.gas.entity.ApiResponse;
import com.classwork.gas.entity.dto.workorder.WorkOrderDtos;
import com.classwork.gas.entity.WorkOrder;
import com.classwork.gas.entity.enums.WorkOrderStatus;
import com.classwork.gas.entity.enums.WorkOrderCategory;
import com.classwork.gas.service.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    /**
     * 报修登记/创建工单。
     */
    @PostMapping
    public ApiResponse<WorkOrder> create(@Valid @RequestBody WorkOrderDtos.CreateWorkOrderRequest request) {
        return ApiResponse.ok("工单创建成功", workOrderService.create(request));
    }

    /**
     * 工单派单，指定处理人。
     */
    @PatchMapping("/{id}/assign")
    public ApiResponse<WorkOrder> assign(@PathVariable Long id, @Valid @RequestBody WorkOrderDtos.AssignRequest request) {
        return ApiResponse.ok("派单成功", workOrderService.assign(id, request));
    }

    /**
     * 更新工单状态（处理中/完成/关闭等）。
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<WorkOrder> updateStatus(@PathVariable Long id, @Valid @RequestBody WorkOrderDtos.UpdateStatusRequest request) {
        return ApiResponse.ok("状态更新成功", workOrderService.updateStatus(id, request));
    }

    /**
     * 查询工单列表，支持按状态过滤。
     */
    @GetMapping
    public ApiResponse<List<WorkOrder>> list(@RequestParam(value = "status", required = false) WorkOrderStatus status,
                                             @RequestParam(value = "category", required = false) WorkOrderCategory category) {
        return ApiResponse.ok(workOrderService.list(status, category));
    }

    /**
     * 查看单个工单详情。
     */
    @GetMapping("/{id}")
    public ApiResponse<WorkOrder> get(@PathVariable Long id) {
        return ApiResponse.ok(workOrderService.get(id));
    }
}
