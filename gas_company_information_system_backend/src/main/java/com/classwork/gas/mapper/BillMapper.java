package com.classwork.gas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classwork.gas.entity.Bill;
import com.classwork.gas.entity.enums.BillStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BillMapper extends BaseMapper<Bill> {

    @Select("SELECT * FROM bills WHERE customer_id = #{customerId}")
    List<Bill> findByCustomerId(@Param("customerId") Long customerId);

    @Select("SELECT * FROM bills WHERE status = #{status}")
    List<Bill> findByStatus(@Param("status") BillStatus status);

    @Select("SELECT * FROM bills WHERE status = #{status} AND due_date < #{dueDate}")
    List<Bill> findByStatusAndDueDateBefore(@Param("status") BillStatus status, @Param("dueDate") LocalDate dueDate);
}
