package com.birlax.indiantrader;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.birlax.indiantrader.spring.IndianTraderAppContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IndianTraderAppContext.class)
public abstract class BaseIntegerationTest {

}
