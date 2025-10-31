package com.jingdyang.dao.mapper;

import com.jingdyang.entity.FundUnitNetValueDO;
import com.jingdyang.entity.ScopedPublicFundDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IndicatorMapper {

    @Select("select * from xxx_table where scope = #{scope}")
    List<ScopedPublicFundDO> getScopePublicFund(String scope);

    @Select("select fundId netValue from nav")
    List<FundUnitNetValueDO> getFundUnitValueNetValue();

}
