package com.birlax.feedcapture;

import com.birlax.dbCommonUtils.industryClassification.IndustrySector;
import com.birlax.feedcapture.BaseIntegrationTest;
import com.birlax.feedcapture.HtmlDocumentParserService;
import com.birlax.feedcapture.etlCommonUtils.domain.DataSourceType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HtmlDocumentParserServiceTest extends BaseIntegrationTest {

  @Autowired HtmlDocumentParserService htmlDocumentParserService;

  @BeforeEach
  void setUp() {}

  @AfterEach
  void tearDown() {}

  @Test
  void getHtmlDocument() throws IOException {

    Document document =
        htmlDocumentParserService.getHtmlDocument(
            "https://www.nseindia.com/get-quotes/equity?symbol=POWERGRID", DataSourceType.URL);

    Assertions.assertEquals("html", document.documentType().name());
  }

  @Test
  public void extractSectors() throws IOException {

    String uri = "/home/birlax/Desktop/Downloads_Win/sectors.html";
    Document doc = htmlDocumentParserService.getHtmlDocument(uri, DataSourceType.URL);
    Elements tables = doc.getElementsByClass("news_advice_sector");
    List<IndustrySector> sectors = new ArrayList<>();
    for (Element e : tables) {
      Elements c2 = e.getElementsByTag("a");
      int i = 0;
      for (Element c3 : c2) {
        String sectorName = c3.text();
        String a[] = sectorName.split("-");
        // if(!a[0].trim().toUpperCase().equals("TEXTILES")) {
        // continue;
        // }
        IndustrySector sector =
            IndustrySector.builder()
                .sectorId(++i)
                .sectorNameMajor(a[0].trim().toUpperCase())
                .build();
        if (a.length == 2) {
          sector.setSectorNameMinor(a[1].trim().toUpperCase());
        }
        sectors.add(sector);
      }
    }
  }

  public void testMonyControll(String[] args) throws IOException {

    Map<String, String> mcCodeToURLMap = new HashMap<>();
    // mcCodeToURLMap.put("AAR", "/home/birlax/crawler/AAR.html");
    // mcCodeToURLMap.put("AI45", "/home/birlax/crawler/AI45.html");

    mcCodeToURLMap.put(
        "API04",
        "http://www.moneycontrol.com/india/stockpricequote/paintsvarnishes/americanpaintsindia/API04");
    mcCodeToURLMap.put(
        "AR03",
        "http://www.moneycontrol.com/india/stockpricequote/pharmaceuticals/americanremedies/AR03");
    mcCodeToURLMap.put(
        "ATL02",
        "http://www.moneycontrol.com/india/stockpricequote/textilesspinningcottonblended/amethitextiles/ATL02");
    mcCodeToURLMap.put(
        "ACC01",
        "http://www.moneycontrol.com/india/stockpricequote/chemicals/amexcarbonateschemicals/ACC01");

    List<Map<String, String>> dataMaps = new ArrayList<>();

    for (Map.Entry<String, String> e : mcCodeToURLMap.entrySet()) {
      Map<String, String> parsedData = new HashMap<>();
      parsedData.put("MC_CODE", e.getKey());
      doForThisHtml(e.getValue(), parsedData, DataSourceType.URL);
      dataMaps.add(parsedData);
    }
    System.out.println(dataMaps);
    ///
    // "https://www.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=FINCABLES&segmentLink=3&symbolCount=1&series=ALL&dateRange=15days&fromDate=&toDate=&dataType=PRICEVOLUMEDELIVERABLE"
  }

  public Map<String, String> doForThisHtml(
      String uri, Map<String, String> parsedData, DataSourceType type) throws IOException {
    Document doc = htmlDocumentParserService.getHtmlDocument(uri, type);
    Elements tables = doc.getElementsByClass("PB10");
    for (Element e : tables) {
      Elements c = e.getElementsByClass("gry10");
      for (Element c1 : c) {
        if (c1.text().contains("SECTOR")) {
          String data = c1.text();
          for (String key : data.split("\\|")) {
            key = key.trim();
            String a[] = key.split(":");
            parsedData.put(handleNull(a, 0), handleNull(a, 1));
          }
        }
      }
    }

    return parsedData;
  }

  public String handleNull(String a[], int index) {
    if (index >= a.length || index < 0 || a[index] == null) return null;
    else return a[index].trim();
  }

  public void htmlParser(String[] args) {

    File html = new File("/home/birlax/A.html");

    String charsetName = "UTF-8";
    try {
      Document doc = Jsoup.parse(html, charsetName);
      Elements tables = doc.getElementsByTag("table");
      for (Element rows : tables) {
        Elements row = rows.getElementsByTag("td");
        for (Element column : row) {
          Elements data = column.getElementsByTag("a");
          System.out.println("");
          for (Element e : data) {
            System.out.print(
                "insert INTO crawler.money_controll (char_name,title,name,mc_segment_name,mc_code_id,url) values (");
            System.out.print(getSQLEncodedString("A") + ",");
            System.out.print(getSQLEncodedString(e.attr("title")) + ",");
            System.out.print(getSQLEncodedString(e.text()) + ",");

            String url = e.attr("href");
            // System.out.println(url);
            String[] dp = url.split("/");

            System.out.print(getSQLEncodedString(dp[5]) + ",");
            System.out.print(getSQLEncodedString(dp[7]) + ",");
            System.out.print(getSQLEncodedString(url) + ");");
          }
          // System.out.println(data);
        }
      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void parserFile(String[] args) {

    File file = new File("/home/birlax/als.csv");

    String str = null;
    String charsetName = "UTF-8";

    try (FileInputStream fis = new FileInputStream(file)) {
      // byte[] data = new byte[(int) file.length()];
      // fis.read(data);
      // str = new String(data, "UTF-8");
      BufferedReader fr = new BufferedReader(new InputStreamReader(fis, charsetName));
      String line = fr.readLine();
      while (line != null) {

        System.out.println("");

        String[] dp = line.split(",");
        // System.out.println(Arrays.toString(dp));
        System.out.print(
            "INSERT INTO sec.trade_master (event_date, open_price, high_price, low_price, close_price, volumn) VALUES (");

        for (int i = 0; i < dp.length; i++) {
          if (i != dp.length - 1) System.out.print(getSQLEncodedString(dp[i]) + ",");
          else System.out.print(getSQLEncodedString(dp[i]) + ");");
        }

        line = fr.readLine();
      }

    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // System.out.println(data);
  }

  public String getSQLEncodedString(String val) {
    return "'" + val + "'";
  }
}
