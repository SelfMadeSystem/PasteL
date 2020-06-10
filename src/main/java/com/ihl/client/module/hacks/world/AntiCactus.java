package com.ihl.client.module.hacks.world;

import com.ihl.client.event.EventHandler;
import com.ihl.client.module.*;
import com.ihl.client.module.Category;

@EventHandler(events = {})
public class AntiCactus extends Module {

    public AntiCactus() {
        super("Anti Cactus", "Don't get hurt when standing on a cactus", Category.WORLD, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }
}
