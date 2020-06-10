package com.ihl.client.module.hacks;

import com.ihl.client.event.EventHandler;
import com.ihl.client.module.*;

@EventHandler(events = {})

public class Fullbright extends Module {

    public Fullbright() {
        super("Fullbright", "Brighten up the world", Category.RENDER, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }
}
