package com.birlax.first.test;

import javax.annotation.Resource;
import org.junit.Test;
import com.birlax.BaseIntegerationTest;
import hello.GreetingController;

public class TestGreetingController extends BaseIntegerationTest {


  @Resource
  private GreetingController greetingController;

  public void setUp() {

  }


  public void tearDown() {

  }

  @Test
  public void testGreeting() {
    greetingController.greeting();
  }
}
