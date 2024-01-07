/**
 *
 */
package com.birlax.indiantrader.indicator.events;

import java.time.LocalDate;
import java.util.Date;

import com.birlax.indiantrader.domain.IndicatorCautionType;
import com.birlax.indiantrader.domain.IndicatorEventType;
import com.birlax.indiantrader.domain.IndicatorSignalType;

/**
 * @author birlax
 */
public class GenericNotificationEvent {

    private int index;

    private LocalDate date;

    private IndicatorSignalType signalType;

    private IndicatorCautionType cautionType;

    private IndicatorEventType eventType;

    private String comments;

    public GenericNotificationEvent(int index, LocalDate date, IndicatorSignalType signalType,
            IndicatorCautionType cautionType, IndicatorEventType eventType, String comments) {
        super();
        this.index = index;
        this.date = date;
        this.signalType = signalType;
        this.cautionType = cautionType;
        this.eventType = eventType;
        this.comments = comments;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * @param index
     *            the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @return the signalType
     */
    public IndicatorSignalType getSignalType() {
        return this.signalType;
    }

    /**
     * @param signalType
     *            the signalType to set
     */
    public void setSignalType(IndicatorSignalType signalType) {
        this.signalType = signalType;
    }

    /**
     * @return the cautionType
     */
    public IndicatorCautionType getCautionType() {
        return this.cautionType;
    }

    /**
     * @param cautionType
     *            the cautionType to set
     */
    public void setCautionType(IndicatorCautionType cautionType) {
        this.cautionType = cautionType;
    }

    /**
     * @return the eventType
     */
    public IndicatorEventType getEventType() {
        return this.eventType;
    }

    /**
     * @param eventType
     *            the eventType to set
     */
    public void setEventType(IndicatorEventType eventType) {
        this.eventType = eventType;
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return this.comments;
    }

    /**
     * @param comments
     *            the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GenericNotificationEvent [index=" + this.index + ", date=" + this.date + ", signalType="
                + this.signalType + ", cautionType=" + this.cautionType + ", eventType=" + this.eventType
                + ", comments=" + this.comments + "]";
    }

}
