package com.ihl.client.module.hacks.misc;

import com.ihl.client.event.EventHandler;
import com.ihl.client.module.*;
import com.ihl.client.module.Category;

@EventHandler(events = {})
public class Console extends Module {

    public Console() {
        super("Console", "Enable GUI console for command input", Category.MISC, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }
}
