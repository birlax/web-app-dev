/**
 *
 */
package com.birlax.indiantrader.service.backtest;

import jakarta.inject.Named;
import java.util.ArrayDeque;
import java.util.Deque;

import com.birlax.indiantrader.indicator.events.BuySellEvent;
import com.birlax.indiantrader.indicator.events.BuySellEvent.ActionType;
import com.birlax.indiantrader.indicator.events.GenericNotificationEvent;
import com.birlax.indiantrader.indicator.events.SignalRack;

/**
 * @author birlax
 */
@Named
public class BuySellActionGeneratorService {

    public Deque<BuySellEvent> generateBuySellActions(SignalRack signals) {

        Deque<GenericNotificationEvent> buyNotifications = signals.getBuyNotificationEventsStack();
        Deque<GenericNotificationEvent> sellNotifications = signals.getSellNotificationEventsStack();

        // generate buy-Action on 2 buy notification.
        // generate sell-Action on first sell notification.

        Deque<BuySellEvent> buySellActions = new ArrayDeque<>();

        GenericNotificationEvent prevBuy = null;
        GenericNotificationEvent currentBuy = null;

        while (!buyNotifications.isEmpty()) {

            /*
             * while (prevBuy == null && !buyNotifications.isEmpty()) {
             * prevBuy = buyNotifications.pollLast();
             * }
             */
            while (currentBuy == null && !buyNotifications.isEmpty()) {
                currentBuy = buyNotifications.pollLast();
            }
            /*
             * if (prevBuy != null && currentBuy != null && prevBuy.getEventType() == currentBuy.getEventType()) {
             * prevBuy = currentBuy;
             * currentBuy = null;
             * continue;
             * }
             */
            // If both the buy notification signals are generated within 12 periods/days/candles.
            // if (prevBuy != null && currentBuy != null && currentBuy.getIndex() - prevBuy.getIndex() <= 12000) {

            // if (direction != null && direction[currentBuy.getIndex()] != null
            // && direction[currentBuy.getIndex()] == Direction.DOWNWARD) {
            // direction of long SMA price has to be up
            buySellActions.addFirst(new BuySellEvent(currentBuy.getIndex(), currentBuy.getDate(),
                    currentBuy.getSignalType(), currentBuy.getCautionType(), -1, ActionType.BUY, "Buy-"));
            // }

            // }
            ////// Look for sell signal now ////
            GenericNotificationEvent currentSell = null;
            while (currentBuy != null && currentSell == null && !sellNotifications.isEmpty()) {

                if (currentBuy.getIndex() > sellNotifications.peekLast().getIndex()) {
                    currentSell = null;
                    sellNotifications.pollLast();
                } else {
                    currentSell = sellNotifications.pollLast();
                }
            }

            if (currentSell != null) {
                buySellActions.addFirst(new BuySellEvent(currentSell.getIndex(), currentSell.getDate(),
                        currentSell.getSignalType(), currentSell.getCautionType(), -1, ActionType.SELL, "Sell-"));
            }
            prevBuy = null;
            currentBuy = null;
        }
        return buySellActions;
    }
}
