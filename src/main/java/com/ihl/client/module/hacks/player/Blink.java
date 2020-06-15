package com.ihl.client.module.hacks.player;

import com.ihl.client.Helper;
import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.util.HelperUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@EventHandler(events = {EventPacket.class})
public class Blink extends Module {

    private final List<Packet> buffer = new CopyOnWriteArrayList();

    public Blink() {
        super("Blink", "Simulate lag", Category.PLAYER, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void disable() {
        super.disable();
        if (!HelperUtil.inGame()) {
            return;
        }
        for (Packet packet : buffer) {
            Helper.mc().getNetHandler().getNetworkManager().sendPacket(packet, null);
        }
        buffer.clear();
    }

    public String getDisplay() {
        return super.getDisplay() + "[" + buffer.size() + "]";
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            if (e.type == Event.Type.SEND) {
                if (e.packet instanceof C03PacketPlayer) {
                    if (Math.abs(Helper.player().posX - Helper.player().lastTickPosX) > 0 ||
                      Math.abs(Helper.player().posY - Helper.player().lastTickPosY) > 0 ||
                      Math.abs(Helper.player().posZ - Helper.player().lastTickPosZ) > 0) {
                        buffer.add(e.packet);
                    }
                    e.cancel();
                }
            }
        }
    }

}
