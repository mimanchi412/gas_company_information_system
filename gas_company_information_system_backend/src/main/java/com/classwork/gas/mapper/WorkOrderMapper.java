package com.classwork.gas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classwork.gas.entity.WorkOrder;
import com.classwork.gas.entity.enums.WorkOrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    @Select("SELECT * FROM work_orders WHERE status = #{status}")
    List<WorkOrder> findByStatus(@Param("status") WorkOrderStatus status);
}
