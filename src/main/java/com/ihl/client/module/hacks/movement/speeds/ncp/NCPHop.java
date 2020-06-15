package com.ihl.client.module.hacks.movement.speeds.ncp;

import com.ihl.client.event.EventPlayerUpdate;
import com.ihl.client.module.hacks.movement.Speed;
import com.ihl.client.module.hacks.movement.speeds.SpeedMode;
import com.ihl.client.util.MUtil;

public class NCPHop extends SpeedMode {
    public NCPHop() {
        super("NCP", "Hop");
    }

    @Override
    public void onUpdate(EventPlayerUpdate event, Speed speed) {
        timerSpeed(1.0865F);
        if (player().onGround) {
            player().jump();
            player().speedInAir = 0.0223F;
        }
        MUtil.strafe();
    }
}
