/**
 *
 */
package com.birlax.indiantrader.oscillator;

import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.service.SecurityService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author birlax
 */
public class PercentagePriceOscillatorTest extends BaseIntegerationTest {

    @Inject
    private PercentagePriceOscillator percentagePriceOscillator;

    @Inject
    private SecurityService securityService;

    @Test
    @Disabled
    public void test() {
        String securitySymbol = "IBVENTURES";
        int lagDuration = 9;
    }

}
