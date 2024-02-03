/**
 *
 */
package com.birlax.indiantrader.indicator.events;

import java.util.ArrayDeque;
import java.util.Deque;


public class SignalRack {

    private Deque<GenericNotificationEvent> buyNotificationEventsStack;

    private Deque<GenericNotificationEvent> sellNotificationEventsStack;

    public SignalRack() {
        super();
        this.buyNotificationEventsStack = new ArrayDeque<>();
        this.sellNotificationEventsStack = new ArrayDeque<>();
    }

    public void addBuyNotificationEvent(GenericNotificationEvent event) {
        this.buyNotificationEventsStack.offerFirst(event);
    }

    public void addSellNotificationEvent(GenericNotificationEvent event) {
        this.sellNotificationEventsStack.offerFirst(event);
    }

    /**
     * @return the buyNotificationEventsStack
     */
    public Deque<GenericNotificationEvent> getBuyNotificationEventsStack() {
        return this.buyNotificationEventsStack;
    }

    /**
     * @return the sellNotificationEventsStack
     */
    public Deque<GenericNotificationEvent> getSellNotificationEventsStack() {
        return this.sellNotificationEventsStack;
    }

}
