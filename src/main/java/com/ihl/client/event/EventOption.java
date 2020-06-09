package com.ihl.client.event;

import com.ihl.client.module.Module;
import com.ihl.client.module.option.Option;

public class EventOption {
    public Module module;
    public Option option;
    public String changed;
    public EventOption(Module module, Option option, String changed) {
        this.module = module;
        this.option = option;
        this.changed = changed;
    }
}
