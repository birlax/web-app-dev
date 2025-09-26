package com.birlax.feedcapture;

public class HackTestUtils {

    public static String getSQLEncodedString(String val) {
        return "'" + val + "'";
    }

    public static String handleNull(String a[], int index) {
        if (index >= a.length || index < 0 || a[index] == null) return null;
        else return a[index].trim();
    }

}
