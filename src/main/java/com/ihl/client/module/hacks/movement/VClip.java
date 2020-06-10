package com.ihl.client.module.hacks.movement;

import com.ihl.client.Helper;
import com.ihl.client.event.Event;
import com.ihl.client.event.EventHandler;
import com.ihl.client.event.EventPlayerUpdate;
import com.ihl.client.module.Category;
import com.ihl.client.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

@EventHandler(events = {EventPlayerUpdate.class})
public class VClip extends Module {

    public VClip() {
        super("VClip", "Clip vertically down through blocks", Category.MOVEMENT, "NONE");
        // TODO: 2020-06-09 1: lol no 2: make `vclip` & `hclip` a command and make this just `clip`
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            EventPlayerUpdate e = (EventPlayerUpdate) event;
            if (e.type == Event.Type.POST) {
                for (int i = 0; i < 100; i++) {
                    Helper.player().sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Helper.player().posX, Helper.player().posY - 0.05, Helper.player().posZ, true));
                }
                Helper.player().sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Helper.player().posX, Helper.player().posY + 1.34, Helper.player().posZ, true));
                for (int i = 0; i < 81; i++) {
                    Helper.player().sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Helper.player().posX, Helper.player().posY - 0.09, Helper.player().posZ, true));
                }
                Helper.player().setPosition(Helper.player().posX, Helper.player().posY - 0.05, Helper.player().posZ);
                disable();
            }
        }
    }

}
