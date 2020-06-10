package com.ihl.client.module.hacks.misc;

import com.ihl.client.event.*;
import com.ihl.client.module.Module;
import com.ihl.client.module.Category;
import com.ihl.client.util.ChatUtil;

@EventHandler(events = {EventPlayerUpdate.class})
public class Debugger extends Module {
    public Debugger() {
        super("Debugger", "Debug", Category.MISC, "NONE");
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            ChatUtil.send("XZ: " + Math.sqrt((Math.abs(player().motionX) + Math.abs(player().motionZ)) *
              (Math.abs(player().motionX) + Math.abs(player().motionZ))));
        }
    }
}
