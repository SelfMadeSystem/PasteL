package com.ihl.client.module.hacks;

import com.ihl.client.event.EventHandler;
import com.ihl.client.module.*;

@EventHandler(events = {})
public class Console extends Module {

    public Console(String name, String desc, Category category, String keybind) {
        super(name, desc, category, keybind);
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }
}
