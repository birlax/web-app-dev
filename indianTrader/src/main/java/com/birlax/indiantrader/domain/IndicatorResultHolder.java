/**
 *
 */
package com.birlax.indiantrader.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author birlax
 */
public class IndicatorResultHolder {

    private Map<String, Double[]> holder;

    public IndicatorResultHolder() {
        super();
        this.holder = new HashMap<>();
    }

    /* *//**
          * @return the holder
          *//*
             * @Deprecated
             * public Map<Date, Double> getResult(String name) {
             * Map<Date, Double> data = this.holder.get(name);
             * if (data == null) {
             * throw new IllegalArgumentException("Invalid result- name, Allowed : " + this.holder.keySet());
             * }
             * return data;
             * }
             */
    public Double[] getResultList(String name) {
        Double[] data = this.holder.get(name);
        if (data == null) {
            System.out.println("Error: " + name);
            throw new IllegalArgumentException(
                    "Invalid result- name, Allowed : " + this.holder.keySet() + " : Requested : " + name);
            // return Collections.emptyList();
        }
        return data;
    }

    public List<String> getAllResultNames() {
        return new ArrayList<>(this.holder.keySet());
    }

    public void addResult(String name, Double data[]) {
        this.holder.put(name, data);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "IndicatorResultHolder [indicatorTolistHolder=" + this.holder + "]";
    }

}
