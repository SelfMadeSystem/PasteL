package com.ihl.client.module.option;

import java.util.*;

public class ValueList extends Value {

    public ValueList(String... list) {
        this(Arrays.asList(list));
    }

    public ValueList(List<String> list) {
        super(list);
    }

}
