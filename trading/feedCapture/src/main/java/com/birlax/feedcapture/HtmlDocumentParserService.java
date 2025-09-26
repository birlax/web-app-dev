package com.birlax.feedcapture;

import com.birlax.feedcapture.etlCommonUtils.domain.DataSourceType;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HtmlDocumentParserService {

    int timeoutMillis = 10_000;

    public Map<String, String> extractLink(Document doc) {
      
        removeComments(doc);

        doc.select("script,noscript,.hidden,link,style,form,span,img,svg,meta,head,header,iframe,audio,input,button,footer").remove();
        doc.clearAttributes();

        doc.forEachNode(
                n -> {
                    n.removeAttr("onclick");
                    n.removeAttr("title");
                    n.removeAttr("class");
                    n.removeAttr("id");
                    n.removeAttr("target");
                    n.removeAttr("javascript");
                    n.removeAttr("javascript:");
                    n.removeAttr("javascript:;");
                    n.removeAttr("javascript:void(0);");

                    if (!n.hasAttr("href")) {
                        //n.clearAttributes();
                    }
                }
        );

        Elements ft = doc.select("a[href]");

        Map<String, String> map = new HashMap<>();
        ft.stream().forEach(e -> {
            map.putIfAbsent(e.text(), e.attr("href"));
        });

        return map;
    }

    private void removeComments(Node node) {
        node.childNodes().stream().filter(n -> "#comment".equals(n.nodeName())).forEach(Node::remove);
        node.childNodes().forEach(this::removeComments);
    }

    private void removeAllEmptyHtmlElements(Document doc) {
        for (Element element : doc.select("*")) {
            if (!element.hasText() && element.isBlock()) {
                element.remove();
            }
        }
    }


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
