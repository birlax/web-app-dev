package com.birlax.indiantrader.report;

import com.birlax.indiantrader.patterndetection.indicator.IndicatorResultHolder;
import com.birlax.indiantrader.capitalmarket.PriceVolumnDelivery;
import com.birlax.indiantrader.capitalmarket.Security;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class DailyBuySellReport {

    public void printReport(Security security, LocalDateTime resultDateOnOrAfter, boolean printHeader,
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
