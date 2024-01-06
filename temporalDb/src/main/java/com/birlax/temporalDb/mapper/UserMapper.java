package com.birlax.temporalDb.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.birlax.temporalDb.domain.BrokerageCharge;

@Mapper
public interface UserMapper {

    public List<BrokerageCharge> getNow();

    public void update(BrokerageCharge brokerageCharge);

}
