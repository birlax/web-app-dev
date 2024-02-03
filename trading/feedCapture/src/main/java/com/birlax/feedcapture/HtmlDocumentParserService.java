package com.birlax.feedcapture;

import com.birlax.feedcapture.etlCommonUtils.domain.DataSourceType;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HtmlDocumentParserService {

  public Document getHtmlDocument(String uri, DataSourceType type) {
    log.info("Using url : {} ", uri);
    if (DataSourceType.URL == type) {
      return getHtmlDocumentFromUrl(uri);
    }
    return getHtmlDocumentFromFile(uri);
  }

  @SneakyThrows
  private Document getHtmlDocumentFromFile(String uri) {
    File html = new File(uri);
    return Jsoup.parse(html, Common.CHAR_ENCODING);
  }

  @SneakyThrows
  private Document getHtmlDocumentFromUrl(String uri) {
    URL url = null;
    try {
      url = new URL(uri);
    } catch (MalformedURLException e1) {
      log.error("Unable to parser URL : {}", uri, e1);
    }
    if (url == null) {
      throw new IllegalArgumentException("Unable to create the URL");
    }
    int timeoutMillis = 10_000;
    return Jsoup.parse(url, timeoutMillis);
  }
}
