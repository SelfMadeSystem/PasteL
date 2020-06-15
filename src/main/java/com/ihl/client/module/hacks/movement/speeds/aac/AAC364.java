package com.ihl.client.module.hacks.movement.speeds.aac;

import com.ihl.client.event.EventPlayerUpdate;
import com.ihl.client.module.hacks.movement.Speed;
import com.ihl.client.module.hacks.movement.speeds.SpeedMode;

public class AAC364 extends SpeedMode {
    public AAC364() {
        super("AAC", "364");
    }

    @Override
    public void onUpdate(EventPlayerUpdate event, Speed speed) {
        player().setSprinting(true);
        if (player().onGround) {
            if (!mc().gameSettings.keyBindJump.getIsKeyPressed()) {
                player().jump();
            }
            player().motionZ *= 1.01;
            player().motionX *= 1.01;
        }
        player().motionY -= 0.0149;
    }
}
