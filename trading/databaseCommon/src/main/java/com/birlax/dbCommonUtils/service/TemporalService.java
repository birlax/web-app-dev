/**
 *
 */
package com.birlax.dbCommonUtils.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Marker service for all Temporal operation.
 *
 * @author birlax
 */
public interface TemporalService {

    /**
     * @param records
     *            these form the where clause condition or search criterion, all values are AND'ed to get rows.
     *            <br>
     *            if value provided for a field is null IS NULL will be used
     * @param searchByColumns
     *            if null or empty all columns of the underlying table will be returned.
     * @return
     */
    public <T extends SingleTemporalDAO> List<Map<String, Object>> searchRecords(List<T> records,
            Set<String> searchByColumns);

    /**
     * @param records
     * @param searchByColumns
     * @return
     */
    public <T extends SingleTemporalDAO> List<T> searchRecords(List<T> records, Set<String> searchByColumns,
            Class<?> clazz);

    /**
     * @param records
     * @param searchByColumns
     *            columns to use in filter/search
     * @param retrieveColumns
     *            columns to retriev from table
     * @param effectiveDateColName
     *            column in the table on which startDate & endDate conditions will be applied
     * @param startDate
     *            inclusive of this date, records with effectiveDateColName >= startDate
     * @param endDate
     *            exclusive of this date, records with effectiveDateColName < endDate
     * @param clazz
     * @return
     */
    public <T extends SingleTemporalDAO> List<T> searchRecordsForDateRange(List<T> records, Set<String> searchByColumns,
            Set<String> retrieveColumns, String effectiveDateColName, LocalDate startDate, LocalDate endDate, Class<?> clazz);

    /**
     * @param records
     * @param searchByColumns
     * @param retrieveColumns
     * @param effectiveDateColName
     * @param startDate
     * @param endDate
     * @return
     */
    public <T extends SingleTemporalDAO> List<Map<String, Object>> searchRecordsForDateRange(List<T> records,
            Set<String> searchByColumns, Set<String> retrieveColumns, String effectiveDateColName, LocalDate startDate,
            LocalDate endDate);

    /**
     * @param records
     * @return
     */
    public <T extends SingleTemporalDAO> List<Map<String, Object>> deleteRecords(List<T> records);

    /**
     * @param records
     * @return
     */
    public <T extends SingleTemporalDAO> List<Map<String, Object>> insertRecords(List<T> records);

    /**
     * @param records
     * @return
     */
    public <T extends SingleTemporalDAO> List<Map<String, Object>> mergeRecords(List<T> records);

    /**
     * @param clazz
     * @return
     */
    public <T extends SingleTemporalDAO> List<T> getAllRecords(Set<String> retrieveColumns, Class<?> clazz);
}
