package com.ihl.client.module.hacks.movement;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;

@EventHandler(events = {EventPlayerUpdate.class})
public class Longjump extends Module {
    // Don't forget, you have to register it in Module!
    public Longjump() {
        //super(name, description, category, keybind);
        super("Longjump", "Makes your jump longer.", Category.MOVEMENT, "NONE");
        //all gets are case insensitive.
        addChoice("Mode", "Makes your jump longer.", "Hypixel"); //Get using STRING("AString")

    }


    @Override
    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            String mode = STRING("Mode");
            switch (mode) {
                case "Hypixel" : {
                    if (MUtil.isMoving() && mc().thePlayer.onGround) {
                        MUtil.strafe(0.44);
                        mc().timer.timerSpeed = 1.45F;
                        mc().thePlayer.jump();
                    }
                    else if (mc().thePlayer.motionY < 0.1 && mc().thePlayer.motionY > -0.1) {
                        MUtil.strafe(0.62);
                        mc().timer.timerSpeed = 0.8F;
                    }
                    else if (mc().thePlayer.fallDistance > 0.0 && mc().thePlayer.fallDistance < 0.8) {
                        mc().timer.timerSpeed = 0.9F;
                        MUtil.strafe(0.32);
                    }
                    break;
                }
            }
        }
    }
    @Override
    public void disable() {
        super.disable();
        if (player() == null)
            return;
        mc().timer.timerSpeed = 1f;
        player().speedInAir = 0.02F;
    }
}
