package com.birlax.temporalDb;

import java.io.FileNotFoundException;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

import com.birlax.temporalDb.spring.TemporalDBAppContext;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = TemporalDBAppContext.class)
public abstract class BaseIntegerationTest {

    static {
        String log4jLocation = "classpath:com/birlax/temporalDb/logging/temporalDb_log4j.xml";
        try {
            Log4jConfigurer.initLogging(log4jLocation);
        } catch (FileNotFoundException ex) {
            System.err.println("Cannot Initialize log4j at location: " + log4jLocation);
        }
    }

}
