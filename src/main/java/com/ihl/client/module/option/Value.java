package com.ihl.client.module.option;

public class Value {

    public Option option;
    protected Object value;

    public Value(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String stringValue() {
        return value.toString();
    }
}
