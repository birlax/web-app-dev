package com.birlax.indiantrader.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.birlax.indiantrader.web.domain.SlickGridColumnOption;

@Mapper
public interface GridDefinitionMapper {

    public List<SlickGridColumnOption> getGridColumnOptionsByName(String gridName);
}
