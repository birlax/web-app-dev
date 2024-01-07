package com.birlax.indiantrader;

import java.util.HashMap;
import java.util.Map;

public class ZerodhaBrokerageService {

    private String YOUR_ACC_IS_FROM_STATE = "UP";

    private double INTRADAY_SELL_CHARGE = 0.000250;

    private double STT_CTT_DELIVERTY_BUY_SELL_CHARGE = 0.001_000;

    private double INTRADAY_BROKERAGE_PCT_BY_ZERODHA = 0.000_100; // max 20 rs.

    private double MAX_INTRADAY_BROKERAGE_BY_ZERODHA = 20.000_100; // max 20 rs.

    private double NSE_TXN_CHARGE = 0.00003250; // max 20 rs.

    private double BSE_TXN_CHARGE = 1.5; // max 20 rs.

    private double SEBI_CHARGE = (15.0 / 10_000_000.0);

    private double STAMP_DUTY_CHARGE = 0.000_020;

    private double GST_CHARGE = 0.18;

    public static String INTRADAY_OPENING_TRADE = "INTRADAY_OPENING_TRADE";

    public static String INTRADAY_CLOSING_TRADE = "INTRADAY_CLOSING_TRADE";

    public static String NSE = "NSE";

    public static String BSE = "BSE";

    public static String TOTAL = "Total Charge";

    public static String PNL = "PNL :";

    private Map<String, Double> stateStampDutyCharges = new HashMap<>();

    public void init() {
        stateStampDutyCharges.put(YOUR_ACC_IS_FROM_STATE, STAMP_DUTY_CHARGE);
    }

    public Double computeBrokerage(double quantity, double price, double closePrice, boolean isIntraDay,
            String tradeType, String stateCode, String exchangeCode) {

        Map<String, Double> leg1 = computeBrokerage(quantity, price, isIntraDay,
                ZerodhaBrokerageService.INTRADAY_OPENING_TRADE, YOUR_ACC_IS_FROM_STATE, exchangeCode);

        Map<String, Double> leg2 = computeBrokerage(quantity, closePrice, isIntraDay,
                ZerodhaBrokerageService.INTRADAY_CLOSING_TRADE, YOUR_ACC_IS_FROM_STATE, exchangeCode);

        Map<String, Double> charge = new HashMap<>();
        for (String key : leg1.keySet()) {
            charge.put(key, leg1.get(key) + leg2.get(key));
        }
        charge.put(PNL, ((closePrice - price) * quantity) - charge.get(TOTAL));
        System.out.println(exchangeCode + "==> " + charge);
        return charge.get(PNL);
    }

    public Map<String, Double> computeBrokerage(double quantity, double price, boolean isIntraDay, String tradeType,
            String stateCode, String exchangeCode) {

        double turnover = quantity * price;

        double brokerage = 0.0;

        double sttCtt = 0.0;

        if (isIntraDay) {
            brokerage = Math.min(turnover * INTRADAY_BROKERAGE_PCT_BY_ZERODHA, MAX_INTRADAY_BROKERAGE_BY_ZERODHA);
            if (INTRADAY_CLOSING_TRADE.equals(tradeType)) {
                sttCtt = turnover * INTRADAY_SELL_CHARGE;
            }
        } else {
            sttCtt = turnover * STT_CTT_DELIVERTY_BUY_SELL_CHARGE;
        }
        double txnCharge = 0.0;
        if (NSE.equals(exchangeCode)) {
            txnCharge = turnover * NSE_TXN_CHARGE;
        } else {
            txnCharge = BSE_TXN_CHARGE;
        }

        double gst = (txnCharge + brokerage) * GST_CHARGE;
        double sebiCharge = turnover * SEBI_CHARGE;
        double stampDutyCharge = stateStampDutyCharges.getOrDefault(stateCode, STAMP_DUTY_CHARGE);

        double total = gst + stampDutyCharge + sttCtt + brokerage + sebiCharge + txnCharge;
        Map<String, Double> charges = new HashMap<>();

        charges.put("TURNOVER", turnover);
        charges.put("BROKERAGE Charge", brokerage);
        charges.put("STT/CTT Charge", sttCtt);
        charges.put("TURNOVER/TXN Charge", txnCharge);
        charges.put("GST Charge", gst);
        charges.put("SEBI Charge", sebiCharge);
        charges.put("STAMP-DUTY Charge", stampDutyCharge);
        charges.put(TOTAL, total);
        return charges;

    }

    public static void main(String[] args) {

        ZerodhaBrokerageService s = new ZerodhaBrokerageService();

        double quantity = 1;
        double openPrice = 23200;
        double closePrice = 23650;
        double nseTotal = s.computeBrokerage(quantity, openPrice, closePrice, false,
                ZerodhaBrokerageService.INTRADAY_OPENING_TRADE, "UP", ZerodhaBrokerageService.NSE);

        double bseTotal = s.computeBrokerage(quantity, openPrice, closePrice, false,
                ZerodhaBrokerageService.INTRADAY_OPENING_TRADE, "UP", ZerodhaBrokerageService.BSE);

        if (bseTotal > nseTotal) {
            System.out.println(
                    "Execute on BSE : " + bseTotal + " NSE total : " + nseTotal + "Gain : " + (bseTotal - nseTotal));
        } else {
            System.out.println(
                    "Execute on NSE : " + nseTotal + " BSE total : " + bseTotal + "Gain : " + (nseTotal - bseTotal));
        }
        System.out.println();

    }

}
