package com.classwork.gas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classwork.gas.entity.Meter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MeterMapper extends BaseMapper<Meter> {

    @Select("SELECT * FROM meters WHERE meter_no = #{meterNo} LIMIT 1")
    Meter findByMeterNo(@Param("meterNo") String meterNo);

    @Select("SELECT COUNT(1) FROM meters WHERE meter_no = #{meterNo}")
    Long countByMeterNo(@Param("meterNo") String meterNo);

    @Select("SELECT * FROM meters WHERE customer_id = #{customerId}")
    List<Meter> findByCustomerId(@Param("customerId") Long customerId);
}
