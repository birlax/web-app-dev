package com.birlax.feedcapture;

import com.birlax.feedcapture.etlCommonUtils.domain.DataSourceType;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

class HtmlDocumentParserServiceTest extends BaseIntegrationTest {

    @Autowired
    HtmlDocumentParserService htmlDocumentParserService;

    @Test
    public void extractSectors_fromDownloaded_htmlFile() {

        String uri = this.getClass().getResource("/money-control-nse-sector-banks.html").getFile();
        uri = this.getClass().getResource("/sector-msme.html").getFile();

        Document doc = htmlDocumentParserService.getHtmlDocument(uri, DataSourceType.FILE);
        Map<String, String> parsedData = htmlDocumentParserService.extractLink(doc);

        System.out.println(parsedData);
    }

    @Test
    public void extractSectors_directlyFrom_hostedPage() {

        String uri =
                "https://www.moneycontrol.com/india/stockmarket/sector-classification/marketstatistics/nse/capital-goods.html";

        Document doc = htmlDocumentParserService.getHtmlDocument(uri, DataSourceType.URL);
        Map<String, String> parsedData = htmlDocumentParserService.extractLink(doc);

        System.out.println(parsedData);
    }

}
