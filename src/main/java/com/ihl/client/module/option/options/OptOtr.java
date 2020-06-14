package com.ihl.client.module.option.options;

import com.ihl.client.module.option.*;

public class OptOtr extends Option {
    public OptOtr(String name, String desc) {
        super(name, desc, new ValueString(""), Type.OTHER);
    }
}
