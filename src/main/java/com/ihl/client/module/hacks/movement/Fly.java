package com.ihl.client.module.hacks.movement;

import com.ihl.client.Helper;
import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.util.HelperUtil;

@EventHandler(events = {EventPlayerUpdate.class})

public class Fly extends Module {

    public Fly() {
        super("Fly", "Enable creative fly", Category.MOVEMENT, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void enable() {
        super.enable();
        if (!HelperUtil.inGame()) {
            return;
        }

        player().capabilities.isFlying = true;
    }

    public void disable() {
        super.disable();
        if (!HelperUtil.inGame()) {
            return;
        }
        Helper.player().capabilities.isFlying = Helper.player().capabilities.allowFlying;
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            EventPlayerUpdate e = (EventPlayerUpdate) event;
            if (e.type == Event.Type.PRE) {
                Helper.player().capabilities.isFlying = true;
            }
        }
    }
}
