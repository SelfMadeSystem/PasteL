package com.ihl.client.module.hacks.movement.speeds.ncp;

import com.ihl.client.event.EventPlayerUpdate;
import com.ihl.client.module.hacks.movement.Speed;
import com.ihl.client.module.hacks.movement.speeds.SpeedMode;
import com.ihl.client.util.MUtil;
import net.minecraft.util.MathHelper;

public class NCPYPort extends SpeedMode {
    public NCPYPort() {
        super("NCP", "YPort");
    }

    @Override
    public void onUpdate(EventPlayerUpdate event, Speed speed) {
        if (speed.jumps >= 3 && player().onGround)
            speed.jumps = 0;

        if (player().onGround) {
            player().motionY = speed.jumps <= 1 ? 0.42F : 0.4F;
            float f = player().rotationYaw * 0.017453292F;

            player().motionX -= MathHelper.sin(f) * 0.2F;
            player().motionZ += MathHelper.cos(f) * 0.2F;
        } else if (speed.jumps <= 0)
            player().motionY = -5D;

        MUtil.strafe();
    }
}
