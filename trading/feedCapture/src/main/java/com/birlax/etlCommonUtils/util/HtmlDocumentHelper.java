package com.birlax.etlCommonUtils.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.etlCommonUtils.domain.DataSourceType;

public class HtmlDocumentHelper {

    static Logger LOGGER = LoggerFactory.getLogger(HtmlDocumentHelper.class);

    public static final String charsetName = "UTF-8";

    public static Document getHtmlDocument(String uri, DataSourceType type) throws IOException {
        LOGGER.info("Using url : {} ", uri);
        if (DataSourceType.URL == type) {
            return HtmlDocumentHelper.getHtmlDocumentFromUrl(uri);
        }

        return HtmlDocumentHelper.getHtmlDocumentFromFile(uri);
    }

    private static Document getHtmlDocumentFromFile(String uri) throws IOException {

        File html = new File(uri);
        Document doc = Jsoup.parse(html, charsetName);

        return doc;
    }

    private static Document getHtmlDocumentFromUrl(String uri) throws IOException {
        URL url = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e1) {
            LOGGER.error("Unable to parser URL : {}", uri, e1);
        }
        if (url == null) {
            throw new IllegalArgumentException("Unable to create the URL");
        }

        int timeoutMillis = 10000;
        Document doc = Jsoup.parse(url, timeoutMillis);
        return doc;
    }

}
