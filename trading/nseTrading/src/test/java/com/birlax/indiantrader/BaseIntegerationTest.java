package com.birlax.indiantrader;

import com.birlax.indiantrader.spring.IndianTraderAppContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {IndianTraderAppContext.class})
public abstract class BaseIntegerationTest {

}
