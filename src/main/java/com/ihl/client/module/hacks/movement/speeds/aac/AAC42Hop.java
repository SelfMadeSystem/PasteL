package com.ihl.client.module.hacks.movement.speeds.aac;

import com.ihl.client.event.EventPlayerUpdate;
import com.ihl.client.module.hacks.movement.Speed;
import com.ihl.client.module.hacks.movement.speeds.SpeedMode;
import com.ihl.client.util.MUtil;

public class AAC42Hop extends SpeedMode {
    public AAC42Hop() {
        super("AAC", "42Hop");
    }

    @Override
    public void onUpdate(EventPlayerUpdate event, Speed speed) {
        if (player().onGround) {
            MUtil.hadd(0.21);
            player().motionY = 0.42;
        } else {
            MUtil.hadd(0.0205);
            player().speedInAir = 0.028F;
            if (player().motionY < 0) {
                timerSpeed(0.9F);
            } else {
                timerSpeed(1.3F);
            }
        }
    }
}
