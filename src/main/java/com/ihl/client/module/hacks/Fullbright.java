package com.ihl.client.module.hacks;

import com.ihl.client.event.EventHandler;
import com.ihl.client.module.*;

@EventHandler(events = {})

public class Fullbright extends Module {

    public Fullbright(String name, String desc, Category category, String keybind) {
        super(name, desc, category, keybind);
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }
}
