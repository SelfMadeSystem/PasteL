package com.ihl.client.module.hacks.player;

import com.ihl.client.Helper;
import com.ihl.client.event.*;
import com.ihl.client.module.*;
import net.minecraft.network.play.client.C03PacketPlayer;

@EventHandler(events = {EventPlayerMove.class})
public class Damage extends Module {

    public Damage() {
        super("Damage", "Force yourself to take damage", Category.PLAYER, "NONE");
        // TODO: 2020-06-09 Add tons of modes lol & damage amount.
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPlayerMove) {
            EventPlayerMove e = (EventPlayerMove) event;
            if (e.type == Event.Type.PRE) {
                e.x = 0;
                e.z = 0;
                for (int i = 0; i < 40; i++) {
                    Helper.player().sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Helper.player().posX, Helper.player().posY + 0.03, Helper.player().posZ, false));
                    Helper.player().sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Helper.player().posX, Helper.player().posY - 0.05, Helper.player().posZ, false));
                }
                disable();
            }
        }
    }

}
