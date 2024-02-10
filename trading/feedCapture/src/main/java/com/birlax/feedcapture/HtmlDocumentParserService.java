package com.birlax.feedcapture;

import com.birlax.feedcapture.etlCommonUtils.domain.DataSourceType;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HtmlDocumentParserService {

  int timeoutMillis = 10_000;

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

    final URL url = URI.create(uri).toURL();

    return Jsoup.parse(url, timeoutMillis);
  }
}
