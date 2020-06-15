package com.ihl.client.module.hacks.combat;

import com.ihl.client.Helper;
import com.ihl.client.event.*;
import com.ihl.client.module.*;
import net.minecraft.network.play.client.C03PacketPlayer;

@EventHandler(events = {EventPlayerAttack.class, EventPacket.class})
public class Criticals extends Module {

    private int crit;

    public Criticals() {
        super("Criticals", "Deal critical hits", Category.COMBAT, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void disable() {
        super.disable();
        crit = 0;
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPlayerAttack) {
            EventPlayerAttack e = (EventPlayerAttack) event;
            if (e.type == Event.Type.PRE) {
                if (crit == 0 && Helper.player().isCollidedVertically) {
                    Helper.player().sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Helper.player().posX, Helper.player().posY + 0.0625101, Helper.player().posZ, false));
                    Helper.player().sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Helper.player().posX, Helper.player().posY, Helper.player().posZ, false));
                    crit = 1;
                }
            }
        } else if (event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            if (e.type == Event.Type.SEND) {
                if (e.packet instanceof C03PacketPlayer) {
                    if (crit > 0) {
                        crit--;
                        e.cancel();
                    }
                }
            }
        }
    }

}