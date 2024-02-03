/**
 *
 */
package com.birlax.indiantrader.indicator.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.indiantrader.domain.Direction;
import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorResultHolder;
import com.birlax.indiantrader.domain.PriceVolumnDelivery;
import com.birlax.indiantrader.domain.Security;
import com.birlax.indiantrader.domain.SmoothCurve;


public class IndicatorUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(IndicatorUtil.class);

    public static enum PriceType {
        OPENING, CLOSING, HIGH, LOW, HIGH_LOW_AVG, HIGH_LOW_CLOSE_AVG, TYPICAL_PRICE, OPEN_HIGH_LOW_CLOSE_AVG, VWAP, NO_TOTAL_TRADE, TOTAL_TRADED_QTY, RAW_MONEY_FLOW
    }

    public static Double getPrice(PriceVolumnDelivery priceVolumnDelivery, String dataFieldName) {

        PriceType priceType = PriceType.valueOf(dataFieldName);
        return getNamedFieldData(priceVolumnDelivery, priceType);
    }

    private static Double getNamedFieldData(PriceVolumnDelivery priceVolumnDelivery, PriceType priceType) {

        // By default always requestedPrice= closePrice.
        Double requestedPrice = priceVolumnDelivery.getClosePrice();

        switch (priceType) {
            case OPENING:
                requestedPrice = priceVolumnDelivery.getOpenPrice();
                break;
            case CLOSING:
                requestedPrice = priceVolumnDelivery.getClosePrice();
                break;
            case HIGH:
                requestedPrice = priceVolumnDelivery.getHighPrice();
                break;
            case LOW:
                requestedPrice = priceVolumnDelivery.getLowPrice();
                break;
            case VWAP:
                requestedPrice = priceVolumnDelivery.getAveragePrice();
                break;
            case HIGH_LOW_AVG:
                requestedPrice = (priceVolumnDelivery.getHighPrice() + priceVolumnDelivery.getLowPrice()) / 2.0;
                break;
            case TYPICAL_PRICE:
            case HIGH_LOW_CLOSE_AVG:
                requestedPrice = (priceVolumnDelivery.getHighPrice() + priceVolumnDelivery.getLowPrice()
                        + priceVolumnDelivery.getClosePrice()) / 3.0;
                break;

            case OPEN_HIGH_LOW_CLOSE_AVG:
                requestedPrice = (priceVolumnDelivery.getOpenPrice() + priceVolumnDelivery.getHighPrice()
                        + priceVolumnDelivery.getLowPrice() + priceVolumnDelivery.getClosePrice()) / 4.0;
                break;
            case NO_TOTAL_TRADE:
                requestedPrice = priceVolumnDelivery.getNoOfTrades();
                break;
            case TOTAL_TRADED_QTY:
                requestedPrice = priceVolumnDelivery.getTotalTradedQuantity();
                break;
            case RAW_MONEY_FLOW:
                requestedPrice = getNamedFieldData(priceVolumnDelivery, PriceType.TYPICAL_PRICE)
                        * getNamedFieldData(priceVolumnDelivery, PriceType.TOTAL_TRADED_QTY);
                break;
            default:
                priceVolumnDelivery.getClosePrice();
                break;
        }
        return requestedPrice;
    }

    public static Double[] getListOfDoubleOfSizeWithNulls(int size) {
        Double[] list = new Double[size];
        Arrays.fill(list, null);
        return list;
    }

    public static Direction[] getDirectionByLookBack(Double[] data, int lookBack, double threshold,
            int climbingDaysVSDecliningDaysThreshold) {

        if (lookBack <= 1) {
            lookBack = 2;
            LOGGER.warn("Bad value for threshold : lookBack <= One : Will use this : {}", lookBack);
        }
        if (climbingDaysVSDecliningDaysThreshold <= 0 || climbingDaysVSDecliningDaysThreshold >= lookBack) {
            climbingDaysVSDecliningDaysThreshold = lookBack - 1;
            LOGGER.debug(
                    "Bad value for threshold : climbingDaysVSDecliningDaysThreshold >= lookBack : Will use this : {}",
                    climbingDaysVSDecliningDaysThreshold);
        }

        Direction[] directions = new Direction[data.length];

        Arrays.fill(directions, Direction.UNKNOWN);
        // leave the index zero
        int index = 1;
        // UPS has 1 if value at index > value at index-1
        int ups[] = new int[data.length];
        // DOWNS has 1 if value at index < value at index-1
        int downs[] = new int[data.length];
        // points to first index in array for which that point & it's previous are both not nulls.
        int startFromIndex = 0;
        double positiveThreshold = threshold;
        double negativeThreshold = -1.0 * positiveThreshold;
        for (; index < data.length; index++) {
            if (data[index] == null || data[index - 1] == null) {
                directions[index] = Direction.UNKNOWN;
                startFromIndex = index;
                continue;
            }
            if (data[index] - data[index - 1] > positiveThreshold) {
                ups[index]++;
            }
            if (data[index] - data[index - 1] < negativeThreshold) {
                downs[index]++;
            }
        }
        // System.out.println(Arrays.toString(ups));
        // System.out.println(Arrays.toString(downs));
        int totalUp = 0;
        int totalDown = 0;
        index = startFromIndex;
        // compute the direction for first window of size lookBack
        for (; index < startFromIndex + lookBack && index < ups.length; index++) {
            if (ups[index] > 0) {
                totalUp++;
            } else if (downs[index] > 0) {
                totalDown++;
            } else {
                directions[index] = Direction.UNKNOWN;
            }
        }
        int UPS = totalUp;
        int DOWNS = totalDown;

        directions[index] = getDirectionBasedOnValues(UPS, DOWNS, climbingDaysVSDecliningDaysThreshold);
        // dont miss this.
        index++;
        for (; index < ups.length; index++) {

            UPS = totalUp + ups[index] - ups[index - lookBack];
            DOWNS = totalDown + downs[index] - downs[index - lookBack];

            directions[index] = getDirectionBasedOnValues(UPS, DOWNS, climbingDaysVSDecliningDaysThreshold);

            totalUp = UPS;
            totalDown = DOWNS;
        }
        return directions;
    }

    private static Direction getDirectionBasedOnValues(int UPS, int DOWNS, int climbingDaysVSDecliningDaysThreshold) {
        Direction direction = null;
        if (DOWNS > UPS && (DOWNS - UPS >= climbingDaysVSDecliningDaysThreshold)) {
            direction = Direction.DOWNWARD;
        } else if (UPS > DOWNS && (UPS - DOWNS >= climbingDaysVSDecliningDaysThreshold)) {
            direction = Direction.UPWARD;
        } else {
            direction = Direction.SIDEWAYS;
        }
        // this is special case as we have used >= and <= in both DOWN/UP
        if (UPS == DOWNS) {
            direction = Direction.SIDEWAYS;
        }
        return direction;
    }

    public static SmoothCurve[] getSmoothCurveByDirection(Direction[] directions, int windowSize, int firstHalfCount,
            int middleHalfCount, int lastHalfCount) {

        if (windowSize >= 26 || (middleHalfCount + firstHalfCount) > windowSize) {
            throw new IllegalArgumentException("Too big a window. Or Up/Down + Middle counts greater than Window.");
        }

        SmoothCurve[] smoothCurves = new SmoothCurve[directions.length];

        Arrays.fill(smoothCurves, SmoothCurve.UNKNOWN);

        // divide the line as up-side-down or down-side-up, if only ups and downs the line else curve
        int currentCountHolder = 0;
        for (int i = 0; i < directions.length - windowSize; i++) {
            if (directions[i] == Direction.UNKNOWN) {
                continue;
            }
            currentCountHolder = 0;
            int countsOfUpsDownsSideDownsUps[] = new int[3];
            Direction[] windowDirs = new Direction[3];
            int j = i;
            Direction prevDir = directions[j];
            for (; j < i + windowSize; j++) {
                if (directions[j] == prevDir) {
                    countsOfUpsDownsSideDownsUps[currentCountHolder]++;
                    windowDirs[currentCountHolder] = directions[j];
                } else {
                    // Only types of count are allowed so index can't go above 2
                    if (currentCountHolder < 2) {
                        currentCountHolder++;
                    }
                    windowDirs[currentCountHolder] = directions[j];
                    countsOfUpsDownsSideDownsUps[currentCountHolder]++;
                }
                prevDir = directions[j];
            }
            // put values now
            if (windowDirs[0] == Direction.UPWARD && windowDirs[1] == Direction.UPWARD
                    && windowDirs[2] == Direction.UPWARD) {
                smoothCurves[i] = SmoothCurve.UPWARD_LINE;
            }
            if (windowDirs[0] == Direction.DOWNWARD && windowDirs[1] == Direction.DOWNWARD
                    && windowDirs[2] == Direction.DOWNWARD) {
                smoothCurves[i] = SmoothCurve.DOWNWARD_LINE;
            }
            if (countsOfUpsDownsSideDownsUps[0] >= firstHalfCount && countsOfUpsDownsSideDownsUps[1] >= middleHalfCount
                    && countsOfUpsDownsSideDownsUps[2] >= lastHalfCount) {

                if (windowDirs[0] == Direction.DOWNWARD && windowDirs[1] == Direction.SIDEWAYS
                        && windowDirs[2] == Direction.UPWARD) {
                    // put counts too
                    smoothCurves[i] = SmoothCurve.UPRIGHT_V;
                }
                if (windowDirs[0] == Direction.UPWARD && windowDirs[1] == Direction.SIDEWAYS
                        && windowDirs[2] == Direction.DOWNWARD) {
                    // put counts too
                    smoothCurves[i] = SmoothCurve.UPSIDE_DOWN_V;
                }

                if (windowDirs[0] == Direction.SIDEWAYS && windowDirs[1] == Direction.SIDEWAYS
                        && windowDirs[2] == Direction.DOWNWARD) {
                    // put counts too
                    smoothCurves[i] = SmoothCurve.SEVEN_MARK;
                }
                if (windowDirs[0] == Direction.SIDEWAYS && windowDirs[1] == Direction.SIDEWAYS
                        && windowDirs[2] == Direction.UPWARD) {
                    // put counts too
                    smoothCurves[i] = SmoothCurve.UPSIDE_TICK_MARK;
                }
                if (windowDirs[0] == Direction.UPWARD && windowDirs[1] == Direction.UPWARD
                        && windowDirs[2] == Direction.SIDEWAYS) {
                    // put counts too
                    smoothCurves[i] = SmoothCurve.UPSTAIR_ESCALATOR;
                }
                if (windowDirs[0] == Direction.DOWNWARD && windowDirs[1] == Direction.DOWNWARD
                        && windowDirs[2] == Direction.SIDEWAYS) {
                    // put counts too
                    smoothCurves[i] = SmoothCurve.DOWNSTAIR_ESCALATOR;
                }

            } else {
                smoothCurves[i] = SmoothCurve.UNKNOWN;
            }
        }
        return smoothCurves;

    }

    public static Double[] transform(List<PriceVolumnDelivery> priceVolumnDeliveries, IndicatorResultHolder holder,
            IndicatorComputationOptions option) {

        Double[] referenceDataLine = new Double[priceVolumnDeliveries.size()];

        for (int index = 0; index < priceVolumnDeliveries.size(); index++) {

            referenceDataLine[index] = IndicatorUtil.getPrice(priceVolumnDeliveries.get(index), option.getPriceType());

        }
        holder.addResult(option.getNameForComputation(option.getPriceType().toString()), referenceDataLine);
        return referenceDataLine;
    }

    public static Comparator<Double> getDoubleComparator() {
        Comparator<Double> cmp = (Double av, Double bv) -> {
            if (av == null && bv == null) {
                return 0;
            }
            if (av != null && bv == null) {
                return -1;
            }
            if (av == null && bv != null) {
                return 1;
            }
            double a = av.doubleValue();
            double b = bv.doubleValue();
            if (a < b) {
                return -1;
            }
            if (a > b) {
                return 1;
            }
            return 0;
        };
        return cmp;
    }

    /**
     * Return longest running up or down days as of that point.
     *
     * @param data
     * @return
     */
    public static Double[] getLongRunningUpDownDays(Double[] data) {

        Double[] upDowns = new Double[data.length];
        // Arrays.fill(upDowns, null);

        for (int index = 1; index < data.length; index++) {
            if (data[index] == null) {
                upDowns[index] = 0.0;
                continue;
            }
            if (data[index] >= 0 && upDowns[index - 1] == null) {
                upDowns[index - 1] = 1.0;

            }
            if (data[index] < 0 && upDowns[index - 1] == null) {
                upDowns[index - 1] = -1.0;

            }

            if (data[index] >= 0) {
                upDowns[index] = upDowns[index - 1] > 0 ? upDowns[index - 1] + 1.0 : 1.0;
            }
            if (data[index] < 0) {
                upDowns[index] = upDowns[index - 1] < 0 ? upDowns[index - 1] - 1.0 : -1.0;
            }
        }
        return upDowns;
    }

    public static Double[] getDiffWithPreviousIndex(Double[] data) {
        Double[] change = new Double[data.length];
        if (data.length <= 1) {
            return change;
        }
        for (int i = 1; i < data.length; i++) {
            if (data[i - 1] == null || data[i] == null) {
                continue;
            }
            change[i] = data[i] - data[i - 1];
        }
        return change;
    }

    public static double getRelativeStrengthIndex(double averageGains, double averageLosses) {

        if (averageLosses == 0.0) {
            return 100.0;
        }
        double relativeStrength = averageGains / averageLosses;

        double val = 100.0 - (100.0 / (1 + relativeStrength));
        val = ((int) (val * 10000)) / 10000.0;
        return val;
    }

    public static void printReport(Security security, LocalDate resultDateOnOrAfter, boolean printHeader,
            List<PriceVolumnDelivery> priceVolumnDeliveries, IndicatorResultHolder holder) {

        String header = "Index,Spn,Security,Series,TradeDate,OpeningPrice,HighPrice,LowPrice,ClosingPrice,AveragePrice,TotalTradedQty,";

        List<String> results = holder.getAllResultNames();

        header += results;
        if (printHeader) {
            System.out.println(header);
        }

        for (int i = 0; i < priceVolumnDeliveries.size(); i++) {
            if (resultDateOnOrAfter.isBefore(priceVolumnDeliveries.get(i).getTradeDate())) {
                String data = i + "," + priceVolumnDeliveries.get(i).getSpn() + "," + security.getSymbol() + ","
                        + priceVolumnDeliveries.get(i).getSeries() + "," + priceVolumnDeliveries.get(i).getTradeDate()
                        + "," + priceVolumnDeliveries.get(i).getOpenPrice() + ","
                        + priceVolumnDeliveries.get(i).getHighPrice() + "," + priceVolumnDeliveries.get(i).getLowPrice()
                        + "," + priceVolumnDeliveries.get(i).getClosePrice() + ","
                        + priceVolumnDeliveries.get(i).getAveragePrice() + ","
                        + priceVolumnDeliveries.get(i).getTotalTradedQuantity() + ",";

                for (String res : results) {
                    data += holder.getResultList(res)[i] + ",";
                }
                System.out.println(data);
            }
        }
    }
}
