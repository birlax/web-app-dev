package com.birlax;

import java.io.FileNotFoundException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:com/birlax/spring/webTest1-application-context.xml"})
public abstract class BaseIntegerationTest {

  static {
    String log4jLocation = "classpath:com/birlax/logging/webTest_log4j.xml";
    try {
      Log4jConfigurer.initLogging(log4jLocation);
    } catch (FileNotFoundException ex) {
      System.err.println("Cannot Initialize log4j at location: " + log4jLocation);
    }
  }

}
