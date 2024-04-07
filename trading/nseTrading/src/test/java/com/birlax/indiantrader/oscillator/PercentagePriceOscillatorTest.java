
package com.birlax.indiantrader.oscillator;

import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.patterndetection.oscillator.PercentagePriceOscillator;
import com.birlax.indiantrader.capitalmarket.SecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PercentagePriceOscillatorTest extends BaseIntegerationTest {

    @Autowired
    private PercentagePriceOscillator percentagePriceOscillator;

    @Autowired
    private SecurityService securityService;

    @Test
    public void test() {
        String securitySymbol = "IBVENTURES";
        int lagDuration = 9;
    }

}
