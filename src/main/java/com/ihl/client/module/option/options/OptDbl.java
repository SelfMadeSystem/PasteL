package com.ihl.client.module.option.options;

import com.ihl.client.module.option.*;

/**
 * Option Double
 */
public class OptDbl extends Option {
    public OptDbl(String name, String description, double defaultValue, double min, double max, double step) {
        super(name, description, new ValueDouble(defaultValue, min, max, step), Type.NUMBER);
    }
}
