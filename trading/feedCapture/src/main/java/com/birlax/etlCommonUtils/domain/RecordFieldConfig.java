/**
 *
 */
package com.birlax.etlCommonUtils.domain;

/**
 * @author birlax
 */
public class RecordFieldConfig {

    private int index;

    private String fieldName;

    private FieldDataType fieldDataType;

    private String validationRegex;

    // Used for date parsing.
    private String specialParsingFormat;

    public RecordFieldConfig(int index, String fieldName, FieldDataType fieldDataType, String validationRegex,
            String specialParsingFormat) {
        super();
        this.index = index;
        this.fieldName = fieldName;
        this.fieldDataType = fieldDataType;
        this.validationRegex = validationRegex;
        this.specialParsingFormat = specialParsingFormat;
        if (this.fieldDataType == FieldDataType.DATE
                && (this.specialParsingFormat == null || this.specialParsingFormat.isEmpty())) {
            throw new IllegalArgumentException(
                    "Special Parsing Format is Required for field type : " + this.fieldDataType);
        }
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * @return the fieldDataType
     */
    public FieldDataType getFieldDataType() {
        return this.fieldDataType;
    }

    /**
     * @return the validationRegex
     */
    public String getValidationRegex() {
        return this.validationRegex;
    }

    /**
     * @param validationRegex
     *            the validationRegex to set
     */
    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }

    /**
     * @return the specialParsingFormat
     */
    public String getSpecialParsingFormat() {
        return this.specialParsingFormat;
    }

    /**
     * @param index
     *            the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @param fieldName
     *            the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @param fieldDataType
     *            the fieldDataType to set
     */
    public void setFieldDataType(FieldDataType fieldDataType) {
        this.fieldDataType = fieldDataType;
    }

}
