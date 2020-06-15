package com.ihl.client.module.hacks.player;

import com.ihl.client.Helper;
import com.ihl.client.event.*;
import com.ihl.client.module.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C03PacketPlayer;

@EventHandler(events = {EventPlayerUpdate.class})

public class FastUse extends Module {

    public FastUse() {
        super("FastUse", "Finish using items faster", Category.PLAYER, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            EventPlayerUpdate e = (EventPlayerUpdate) event;
            if (e.type == Event.Type.PRE) {
                if (Helper.player().isUsingItem()) {
                    if (Helper.player().getItemInUse().getItem() instanceof ItemFood || Helper.player().getItemInUse().getItem() instanceof ItemPotion) {
                        if (Helper.player().getItemInUseDuration() > 14) {
                            for (int i = 0; i < 20; i++) {
                                Helper.player().sendQueue.addToSendQueue(new C03PacketPlayer());
                            }
                            Helper.controller().onStoppedUsingItem(Helper.player());
                        }
                    }/* else if (Helper.player().getItemInUse().getItem() instanceof ItemBow) {
                        if (Helper.player().getItemInUseDuration() > 10) {
                            for(int i = 0; i < 10; i++) {
                                Helper.player().sendQueue.addToSendQueue(new C03PacketPlayer());
                            }
                            Helper.controller().onStoppedUsingItem(Helper.player());
                        }
                    }*/
                }
            }
        }
    }
}
