package com.ihl.client.module.hacks.movement.speeds.aac;

import com.ihl.client.event.EventPlayerUpdate;
import com.ihl.client.module.hacks.movement.Speed;
import com.ihl.client.module.hacks.movement.speeds.SpeedMode;
import com.ihl.client.util.MUtil;

public class AAC42 extends SpeedMode {
    float prevYaw;
    float prevPitch;

    public AAC42() {
        super("AAC", "42");
    }

    @Override
    public void onUpdate(EventPlayerUpdate event, Speed speed) {
        if (!player().onGround)
            return;
        //ChatUtils.message(prevYaw + " " + p.yaw + " " + p.sidewaysSpeed + " " + (prevYaw-5<p.yaw && prevYaw+5>p.yaw));
        if ((prevYaw - 5 < player().rotationYaw && prevYaw + 5 > player().rotationYaw) && player().moveStrafing == 0) {
            MUtil.strafe(MUtil.getSpeed() + 0.23f);
        } else {
            MUtil.strafe();
        }
        prevYaw = player().rotationYaw;
        prevPitch = player().rotationPitch;
    }
}
