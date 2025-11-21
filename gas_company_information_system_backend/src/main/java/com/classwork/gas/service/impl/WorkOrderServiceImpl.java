package com.classwork.gas.service.impl;

import com.classwork.gas.entity.dto.workorder.WorkOrderDtos;
import com.classwork.gas.mapper.CustomerMapper;
import com.classwork.gas.mapper.MeterMapper;
import com.classwork.gas.mapper.WorkOrderMapper;
import com.classwork.gas.entity.Customer;
import com.classwork.gas.entity.Meter;
import com.classwork.gas.entity.WorkOrder;
import com.classwork.gas.entity.enums.WorkOrderStatus;
import com.classwork.gas.entity.enums.WorkOrderCategory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.classwork.gas.service.WorkOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderMapper workOrderMapper;
    private final CustomerMapper customerMapper;
    private final MeterMapper meterMapper;

    public WorkOrderServiceImpl(WorkOrderMapper workOrderMapper,
                                CustomerMapper customerMapper,
                                MeterMapper meterMapper) {
        this.workOrderMapper = workOrderMapper;
        this.customerMapper = customerMapper;
        this.meterMapper = meterMapper;
    }

    @Override
    public WorkOrder create(WorkOrderDtos.CreateWorkOrderRequest request) {
        Customer customer = request.getCustomerId() == null ? null : customerMapper.selectById(request.getCustomerId());
        if (request.getCustomerId() != null && customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "客户不存在");
        }
        Meter meter = request.getMeterId() == null ? null : meterMapper.selectById(request.getMeterId());
        if (request.getMeterId() != null && meter == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "表具不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        WorkOrder workOrder = WorkOrder.builder()
                .customerId(request.getCustomerId())
                .meterId(request.getMeterId())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(WorkOrderStatus.NEW)
                .reportedAt(now)
                .updatedAt(now)
                .build();
        workOrderMapper.insert(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder assign(Long id, WorkOrderDtos.AssignRequest request) {
        WorkOrder workOrder = get(id);
        workOrder.setAssignedTo(request.getAssignedTo());
        workOrder.setStatus(WorkOrderStatus.ASSIGNED);
        workOrder.setUpdatedAt(LocalDateTime.now());
        workOrderMapper.updateById(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder updateStatus(Long id, WorkOrderDtos.UpdateStatusRequest request) {
        WorkOrder workOrder = get(id);
        if (request.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "状态不能为空");
        }
        workOrder.setStatus(request.getStatus());
        if (request.getStatus() == WorkOrderStatus.DONE || request.getStatus() == WorkOrderStatus.CLOSED) {
            workOrder.setCompletedAt(LocalDateTime.now());
        }
        workOrder.setUpdatedAt(LocalDateTime.now());
        workOrderMapper.updateById(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder get(Long id) {
        WorkOrder workOrder = workOrderMapper.selectById(id);
        if (workOrder == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "工单不存在");
        }
        return workOrder;
    }

    @Override
    public List<WorkOrder> list(WorkOrderStatus status, WorkOrderCategory category) {
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(WorkOrder::getStatus, status);
        }
        if (category != null) {
            wrapper.eq(WorkOrder::getCategory, category);
        }
        return workOrderMapper.selectList(wrapper);
    }
}
