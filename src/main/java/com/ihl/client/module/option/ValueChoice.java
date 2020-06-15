package com.ihl.client.module.option;

public class ValueChoice extends Value {

    public String[] list;

    public ValueChoice(int value, String... list) {
        this(list[value], list);
    }

    public ValueChoice(String value, String... list) {
        super(value);
        this.list = list;
    }

}
