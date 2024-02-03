/**
 *
 */
package com.birlax.dbCommonUtils.util;

import com.birlax.dbCommonUtils.service.SingleTemporalDAO;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class ReflectionHelper {

    public static Map<String, String> CAMEL_CASE_TO_SNAKE_CACHE = new ConcurrentHashMap<>();

    /**
     * Single depth flattening
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
                flatView.put(getLowerCaseSnakeCase(name), value);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                final String errorMsg =
                        String.format("Failed to flatten the Object : [%s], Try writing your own flatterer", obj);
                throw new RuntimeException(errorMsg, e);
            }
        }
        // System.out.println(flatView);
        return flatView;
    }

    /**
     * Converts Camel case(use in Java Code) to Lower Snake case(style/format used in databases object names).
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
                final String errorMsg =
                        String.format("Failed to instantiate object of class : [%s] with values : [%s] failed for field : [%s]"
                                , clazz, flatView, name);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return domain;
    }
}
