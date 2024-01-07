package com.birlax.indiantrader.web.domain;

public class SlickGridColumnOption {

    private String  id;
    private String  name;
    private String  field;
    private int     width;
    private boolean selectable;
    private boolean resizable;
    private String  formatter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    @Override
    public String toString() {
        return "SlickGridColumnOption [id=" + id + ", name=" + name + ", field=" + field + ", width=" + width
                + ", selectable=" + selectable + ", resizable=" + resizable + "]";
    }

    public String getFormatter() {
        return formatter;
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

}
