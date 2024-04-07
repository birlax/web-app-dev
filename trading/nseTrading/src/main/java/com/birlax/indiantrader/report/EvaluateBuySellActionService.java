
package com.birlax.indiantrader.report;

import java.util.Deque;
import java.util.List;

import com.birlax.indiantrader.capitalmarket.PriceVolumnDelivery;
import com.birlax.indiantrader.capitalmarket.Security;
import com.birlax.indiantrader.patterndetection.ActionType;
import com.birlax.indiantrader.patterndetection.events.BuySellEvent;
import org.springframework.stereotype.Service;


@Service
public class EvaluateBuySellActionService {

    public void evaluate(Security sec, List<PriceVolumnDelivery> priceVolumnDeliveries, Deque<BuySellEvent> eventsStack,
            boolean shortSellingEnabled) {

        if (eventsStack.isEmpty() || eventsStack.size() <= 2) {
            System.out.println("No action on this script.");
            return;
        }
        BuySellEvent prevEvent = null;
        while (!eventsStack.isEmpty()) {
            // eat all the HOLD Event first, get to a BUY/SELL
            while (!eventsStack.isEmpty() && prevEvent == null) {
                prevEvent = eventsStack.pollLast();
                if (prevEvent.getEventType() == ActionType.HOLD) {
                    prevEvent = null;
                    continue;
                }
                if (!shortSellingEnabled && prevEvent.getEventType() == ActionType.SELL) {
                    prevEvent = null;
                    continue;
                }
            }
            // Get second event
            if (eventsStack.isEmpty()) {
                System.out.println("There were only HOLD Events.");
                return;
            }
            BuySellEvent currentEvent = eventsStack.pollLast();
            while (!eventsStack.isEmpty()) {
                while (!eventsStack.isEmpty() && (prevEvent.getEventType() == currentEvent.getEventType()
                        || currentEvent.getEventType() == ActionType.HOLD)) {
                    currentEvent = eventsStack.pollLast();
                }
                Double txnOpenPrice = priceVolumnDeliveries.get(prevEvent.getIndex()).getOpenPrice();
                Double buyPrice = priceVolumnDeliveries.get(currentEvent.getIndex() - 1).getHighPrice();
                Double txnClosePrice = priceVolumnDeliveries.get(currentEvent.getIndex()).getClosePrice();
                Double txnHighPrice = priceVolumnDeliveries.get(currentEvent.getIndex()).getHighPrice();
                Double pnl = 0.0;
                if (prevEvent.getEventType() == ActionType.SELL) {
                    pnl = (txnOpenPrice - txnClosePrice);
                } else {
                    pnl = (txnClosePrice - txnOpenPrice);
                }
                int days = currentEvent.getIndex() - prevEvent.getIndex();
                System.out.println(sec.getSpn() + "," + sec.getSymbol() + "," + prevEvent.getEventType() + ",IN:Price,"
                        + txnOpenPrice + ",OUT:Price," + txnClosePrice + ",IN:Date,"
                        + priceVolumnDeliveries.get(prevEvent.getIndex()).getTradeDate() + ",OUT:Date,"
                        + priceVolumnDeliveries.get(currentEvent.getIndex()).getTradeDate() + ",Pnl:," + pnl + ",Days:,"
                        + days + ",HighPrice:," + txnHighPrice

                        + ",BUYPrice:," + buyPrice);
                if (shortSellingEnabled) {
                    prevEvent = currentEvent;
                } else {
                    prevEvent = currentEvent = null;
                    break;
                }
            }
        }
    }
}
