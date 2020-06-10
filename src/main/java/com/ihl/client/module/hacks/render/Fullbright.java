package com.ihl.client.module.hacks.render;

import com.ihl.client.event.EventHandler;
import com.ihl.client.module.*;
import com.ihl.client.module.Category;

@EventHandler(events = {})

public class Fullbright extends Module {

    public Fullbright() {
        super("Fullbright", "Brighten up the world", Category.RENDER, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }
}
