package com.classwork.gas.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.classwork.gas.entity.enums.WorkOrderCategory;
import com.classwork.gas.entity.enums.WorkOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("work_orders")
public class WorkOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("customer_id")
    private Long customerId;

    @TableField("meter_id")
    private Long meterId;

    private String title;

    private String description;

    private WorkOrderCategory category;

    private WorkOrderStatus status;

    @TableField("assigned_to")
    private String assignedTo;

    @TableField("reported_at")
    private LocalDateTime reportedAt;

    @TableField("completed_at")
    private LocalDateTime completedAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
