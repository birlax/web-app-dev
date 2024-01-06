package com.birlax.temporalDb.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.birlax.temporalDb.domain.BrokerageCharge;
import com.birlax.temporalDb.mapper.UserMapper;

@Named
public class UserService {

    @Inject
    private UserMapper userMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    public List<BrokerageCharge> getNow() {

        return userMapper.getNow();
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void update(List<BrokerageCharge> brokerageCharges) {
        for (BrokerageCharge brokerageCharge : brokerageCharges) {
            userMapper.update(brokerageCharge);
        }
    }

}
