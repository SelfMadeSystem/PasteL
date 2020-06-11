package com.ihl.client.module.hacks.player;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.util.isBlockUnder;


@EventHandler(events = {EventPlayerUpdate.class})
public class Antivoid extends Module {

    public Antivoid() {
        super("Antivoid", "Teleports you back when you fall in to the void.", Category.PLAYER, "NONE");

        addChoice("Mode", "Teleports you back when you fall in to the void.", "Ncp"); //Get using STRING("AString")

    }

    @Override
    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            String mode = STRING("Mode");
            switch (mode) {
                case "Ncp" : {


                    if (mc().thePlayer.fallDistance > 6 && !isBlockUnder.isBlockUnder()) {
                        mc().thePlayer.motionY = 5;
                    }
                    else if (mc().thePlayer.motionY  == 5){
                        mc().thePlayer.motionY = 0;
                    }


                    break;
                }
            }
        }
    }
}
