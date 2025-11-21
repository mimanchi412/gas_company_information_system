package com.classwork.gas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classwork.gas.entity.MeterReading;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MeterReadingMapper extends BaseMapper<MeterReading> {

    @Select("SELECT * FROM meter_readings WHERE meter_id = #{meterId} ORDER BY reading_time DESC")
    List<MeterReading> findByMeterId(@Param("meterId") Long meterId);
}
