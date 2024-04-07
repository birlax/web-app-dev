package com.birlax.indiantrader;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Profile("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = IndianTraderAppContext.class)
public abstract class BaseIntegerationTest {

}
