package com.ihl.client.module.hacks.player;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import net.minecraft.network.play.client.C03PacketPlayer;

@EventHandler(events = {EventPlayerUpdate.class})
public class Nofall extends Module {
    public Nofall() {
        super("Nofall", "Lets you take no falldamage.", Category.PLAYER, "NONE");

        addChoice("Mode", "Lets you take no falldamage.", "Spoof"); //Get using STRING("AString")

    }

    @Override
    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            String mode = STRING("Mode");
            switch (mode) {
                case "Spoof" : {
                    if (mc().thePlayer.fallDistance > 2.5) {
                        mc().thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                        //^^kind of bypasses hypixel sometimes get laggbaked by ncp
                    }

                    break;
                }
            }
        }
    }
}
