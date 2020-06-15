package com.ihl.client.module.hacks.movement.speeds.aac;

import com.ihl.client.event.EventPlayerUpdate;
import com.ihl.client.module.hacks.movement.Speed;
import com.ihl.client.module.hacks.movement.speeds.SpeedMode;

public class AAC350 extends SpeedMode {
    public AAC350() {
        super("AAC", "350");
    }

    @Override
    public void onUpdate(EventPlayerUpdate event, Speed speed) {
        player().jumpMovementFactor += 0.00208F;
        if (player().onGround) {
            player().jump();
            player().motionX *= 1.0118F;
            player().motionZ *= 1.0118F;
        } else {
            player().motionY -= 0.0147F;

            player().motionX *= 1.00138F;
            player().motionZ *= 1.00138F;
        }
    }
}
