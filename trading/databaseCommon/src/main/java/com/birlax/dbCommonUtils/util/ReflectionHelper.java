/**
 *
 */
package com.birlax.dbCommonUtils.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.dbCommonUtils.service.SingleTemporalDAO;

/**
 * @author birlax
 */
public class ReflectionHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionHelper.class);

    private static final Map<String, String> CAMEL_CASE_TO_SNAKE_CACHE = new ConcurrentHashMap<>();

    /**
     * Single depth flattening.
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> getFlattenedView(Object obj) {
        Class<?> objClass = obj.getClass();

        Field[] fields = objClass.getDeclaredFields();
        Map<String, Object> flatView = new HashMap<>();
        for (Field field : fields) {
            String name = field.getName();
            if (name.contains("this")) {
                continue;
            }
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(obj);
                flatView.put(ReflectionHelper.getLowerCaseSnakeCase(name), value);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                LOGGER.error("Failed to flatten the Object : {}, Try writting your own flatterner.", obj);
                throw new RuntimeException("Error while flattening object", e);
            }
        }
        // System.out.println(flatView);
        return flatView;
    }

    /**
     * Converts Camel case(use in Java Code) to Lower Snake case(style/format used in databases object names).
     *
     * @param columnName
     * @return
     */
    public static String getLowerCaseSnakeCase(String columnName) {
        if (columnName == null || columnName.isEmpty()) {
            return columnName;
        }
        if (CAMEL_CASE_TO_SNAKE_CACHE.get(columnName) != null) {
            return CAMEL_CASE_TO_SNAKE_CACHE.get(columnName);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (char ch : columnName.toCharArray()) {
            if (ch >= 'A' && ch <= 'Z') {
                stringBuilder.append("_");
                stringBuilder.append((char) (ch - 0 + ('a' - 'A')));
            } else {
                stringBuilder.append(ch);
            }
        }
        CAMEL_CASE_TO_SNAKE_CACHE.put(columnName, stringBuilder.toString());
        return CAMEL_CASE_TO_SNAKE_CACHE.get(columnName);
    }

    /**
     * @param flatView
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T extends SingleTemporalDAO> T getDomainObject(Map<String, Object> flatView, Class<?> clazz)
            throws InstantiationException, IllegalAccessException {
        T domain = (T) clazz.newInstance();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            if (name.contains("this")) {
                continue;
            }
            field.setAccessible(true);

            try {
                if (field.getType().isPrimitive() && flatView.get(getLowerCaseSnakeCase(name)) == null) {
                    continue;
                } else {
                    field.set(domain, flatView.get(getLowerCaseSnakeCase(name)));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                LOGGER.error("Failed to instantiate object of class : {} with values : {} failed for field : {}", clazz,
                        flatView, name, e);
                throw new RuntimeException("Failed to instantiate object.", e);
            }
        }
        return domain;
    }
}
