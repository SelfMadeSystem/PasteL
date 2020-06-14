package com.ihl.client.module.option.options;

import com.ihl.client.module.option.*;

/**Option Boolean*/
public class OptBol extends Option {
    public OptBol(String name, String description, boolean defaultValue) {
        super(name, description, new ValueBoolean(defaultValue), Type.BOOLEAN);
    }
}
