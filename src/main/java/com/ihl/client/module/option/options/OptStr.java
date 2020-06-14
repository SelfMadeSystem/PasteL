package com.ihl.client.module.option.options;

import com.ihl.client.module.option.*;

/**Option String*/
public class OptStr extends Option {
    public OptStr(String name, String description, String defaultValue) {
        super(name, description, new ValueString(defaultValue), Type.STRING);
    }
}
