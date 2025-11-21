package com.classwork.gas.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("payments")
public class Payment {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("bill_id")
    private Long billId;

    private BigDecimal amount;

    private String method;

    @TableField("paid_at")
    private LocalDateTime paidAt;

    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
