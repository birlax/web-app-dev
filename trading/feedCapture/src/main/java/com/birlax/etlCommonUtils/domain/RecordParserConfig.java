/**
 *
 */
package com.birlax.etlCommonUtils.domain;

import java.util.List;
import java.util.Objects;

/**
 * @author birlax
 */
public class RecordParserConfig {

    // TODO Make this more generic in future.
    private String parserGeneratedUniqueRecordIdFieldName;

    private boolean ignoreParserExceptions = false;;

    private List<RecordFieldConfig> recordsFields;

    public RecordParserConfig(String parserGeneratedUniqueRecordIdFieldName, List<RecordFieldConfig> recordsFields,
            boolean ignoreParserExceptions) {
        super();
        Objects.requireNonNull(parserGeneratedUniqueRecordIdFieldName, "Unique Field Name is Required.");
        Objects.requireNonNull(recordsFields, "Field Configuration is Required.");
        if (recordsFields.isEmpty()) {
            throw new IllegalArgumentException("Field Configuration is Required.");
        }
        this.parserGeneratedUniqueRecordIdFieldName = parserGeneratedUniqueRecordIdFieldName;
        this.ignoreParserExceptions = ignoreParserExceptions;
        this.recordsFields = recordsFields;
    }

    /**
     * @return the recordsFields
     */
    public List<RecordFieldConfig> getRecordsFields() {
        return this.recordsFields;
    }

    /**
     * @param recordsFields
     *            the recordsFields to set
     */
    public void setRecordsFields(List<RecordFieldConfig> recordsFields) {
        this.recordsFields = recordsFields;
    }

    /**
     * @return the parserGeneratedUniqueRecordIdFieldName
     */
    public String getParserGeneratedUniqueRecordIdFieldName() {
        return this.parserGeneratedUniqueRecordIdFieldName;
    }

    /**
     * @return the ignoreParserExceptions
     */
    public boolean isIgnoreParserExceptions() {
        return this.ignoreParserExceptions;
    }

}
