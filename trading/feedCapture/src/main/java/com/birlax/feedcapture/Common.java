package com.birlax.feedcapture;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@UtilityClass
public class Common {

  public String CHAR_ENCODING = "UTF-8";

  @SneakyThrows
  public String readFileToString(String path) {
    return FileUtils.readFileToString(ResourceUtils.getFile(path), StandardCharsets.UTF_8);
  }

  @SneakyThrows
  public List<String> readFileToListOfString(String path) {
    return FileUtils.readLines(ResourceUtils.getFile(path), StandardCharsets.UTF_8);
  }
}
