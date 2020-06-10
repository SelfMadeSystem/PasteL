package com.ihl.client.module.hacks;

import com.ihl.client.Helper;
import com.ihl.client.event.Event;
import com.ihl.client.event.EventHandler;
import com.ihl.client.event.EventPacket;
import com.ihl.client.module.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@EventHandler(events = {EventPacket.class})
public class NoView extends Module {

    public NoView() {
        super("No View", "Prevent the server changing your view direction", Category.PLAYER, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            if (e.type == Event.Type.RECEIVE) {
                if (e.packet instanceof S08PacketPlayerPosLook) {
                    S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.packet;
                    packet.yaw = Helper.player().rotationYaw;
                    packet.pitch = Helper.player().rotationPitch;
                }
            }
        }
    }

}
