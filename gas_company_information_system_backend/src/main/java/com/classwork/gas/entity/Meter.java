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

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("meters")
public class Meter {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("meter_no")
    private String meterNo;

    @TableField("customer_id")
    private Long customerId;

    private String status;

    private String location;

    @TableField("installed_at")
    private LocalDateTime installedAt;
}
