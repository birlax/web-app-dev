package com.birlax.indiantrader.web.domain;

public class SlickGridOption {

    private boolean editable;
    private boolean enableAddRow;
    private boolean enableCellNavigation;
    private boolean asyncEditorLoading;
    private boolean focusable;
    private boolean sortable;
    private boolean enableTextSelectionOnCells;
    private int     rowHeight;

    public SlickGridOption() {
        super();
        this.editable = false;
        this.enableAddRow = false;
        this.enableCellNavigation = true;
        this.asyncEditorLoading = true;
        this.focusable = true;
        this.sortable = true;
        this.enableTextSelectionOnCells = true;
        this.rowHeight = 20;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEnableAddRow() {
        return enableAddRow;
    }

    public void setEnableAddRow(boolean enableAddRow) {
        this.enableAddRow = enableAddRow;
    }

    public boolean isEnableCellNavigation() {
        return enableCellNavigation;
    }

    public void setEnableCellNavigation(boolean enableCellNavigation) {
        this.enableCellNavigation = enableCellNavigation;
    }

    public boolean isAsyncEditorLoading() {
        return asyncEditorLoading;
    }

    public void setAsyncEditorLoading(boolean asyncEditorLoading) {
        this.asyncEditorLoading = asyncEditorLoading;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public boolean isEnableTextSelectionOnCells() {
        return enableTextSelectionOnCells;
    }

    public void setEnableTextSelectionOnCells(boolean enableTextSelectionOnCells) {
        this.enableTextSelectionOnCells = enableTextSelectionOnCells;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }
}
