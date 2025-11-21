package com.classwork.gas.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.classwork.gas.entity.enums.CustomerType;
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
@TableName("customers")
public class Customer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private CustomerType type;

    @TableField("contact_phone")
    private String contactPhone;

    private String email;

    private String address;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
