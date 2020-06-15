package com.ihl.client.module.option.options;

import com.ihl.client.module.option.*;

/**
 * Option Choice
 */
public class OptChc extends Option {
    public OptChc(String name, String description, String... values) {
        super(name, description, new ValueChoice(0, values), Type.CHOICE);
    }

    public OptChc(String name, String description, int defaultValue, String... values) {
        super(name, description, new ValueChoice(defaultValue, values), Type.CHOICE);
    }
}
