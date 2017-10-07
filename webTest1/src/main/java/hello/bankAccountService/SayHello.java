package hello.bankAccountService;

import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hello.Greeting;

@RestController
@RequestMapping("/sayHello")
public class SayHello {

  Logger log = LoggerFactory.getLogger(SayHello.class);

  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();

  @RequestMapping(value = "/greeting")
  public Greeting greeting() {
    log.info("done logging.");
    return new Greeting(counter.incrementAndGet(), String.format(template, "Briejsh"));
  }
}
