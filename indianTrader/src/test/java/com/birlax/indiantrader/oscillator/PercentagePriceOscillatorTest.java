/**
 *
 */
package com.birlax.indiantrader.oscillator;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;

import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.service.SecurityService;

/**
 * @author birlax
 */
public class PercentagePriceOscillatorTest extends BaseIntegerationTest {

    @Inject
    private PercentagePriceOscillator percentagePriceOscillator;

    @Inject
    private SecurityService securityService;

    @Test
    @Ignore
    public void test() {
        String securitySymbol = "IBVENTURES";
        int lagDuration = 9;
    }

}
