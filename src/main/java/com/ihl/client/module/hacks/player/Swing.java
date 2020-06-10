package com.ihl.client.module.hacks.player;

import com.ihl.client.event.EventHandler;
import com.ihl.client.module.*;
import com.ihl.client.module.Category;

@EventHandler(events = {})
public class Swing extends Module {

    public Swing() {
        super("Swing", "Reset the swing animation faster", Category.PLAYER, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

}