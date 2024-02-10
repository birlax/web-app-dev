package com.birlax.feedcapture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.findAndRegisterModules();
  }

  @SneakyThrows(JsonProcessingException.class)
  public static String toJson(final Object object) {
    return objectMapper.writeValueAsString(object);
  }

  @SneakyThrows(JsonProcessingException.class)
  public static JsonNode toJsonNode(final String json) {
    return objectMapper.readTree(json);
  }

  public static JsonNode toJsonNode(final Object object) {
    return toJsonNode(toJson(object));
  }

  @SneakyThrows
  public static <T> T convertToClass(final String json, Class<T> valueType) {
    return objectMapper.readValue(json, valueType);
  }

  @SneakyThrows
  public static <T> T convertToTypeReference(final String json, TypeReference<T> valueType) {
    return objectMapper.readValue(json, valueType);
  }

  public static JsonNode convertToJsonNode(final Object object) {
    return objectMapper.convertValue(object, JsonNode.class);
  }
}
