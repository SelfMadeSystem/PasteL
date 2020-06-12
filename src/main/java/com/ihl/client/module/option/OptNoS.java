package com.ihl.client.module.option;

import com.ihl.client.module.Module;

import java.util.List;

/** Option No Save*/
public class OptNoS extends Option {
    public OptNoS(Module module, String name, String desc, Value value, Type type) {
        super(module, name, desc, value, type);
    }

    public OptNoS(Module module, String name, String desc, Value value, Type type, Option... options) {
        super(module, name, desc, value, type, options);
    }

    public OptNoS(Module module, String name, String desc, Value value, Type type, List<Option> options) {
        super(module, name, desc, value, type, options);
    }

    public OptNoS(Module module, String name, String desc, Value value, Type type, List<Option> options, Option parent) {
        super(module, name, desc, value, type, options, parent);
    }

    @Override
    public boolean save() {
        return false;
    }
}
