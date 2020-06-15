package com.ihl.client.module.hacks.movement.speeds.aac;

import com.ihl.client.event.EventPlayerUpdate;
import com.ihl.client.module.hacks.movement.Speed;
import com.ihl.client.module.hacks.movement.speeds.SpeedMode;
import com.ihl.client.util.MUtil;
import net.minecraft.util.MathHelper;

public class AAC3313 extends SpeedMode {
    public AAC3313() {
        super("AAC", "3313");
    }

    @Override
    public void onUpdate(EventPlayerUpdate event, Speed speed) {
        if (player().onGround && player().isCollidedVertically) {
            // MotionXYZ
            float yawRad = player().rotationYaw * 0.017453292F;
            player().motionX -= MathHelper.sin(yawRad) * 0.202F;
            player().motionZ += MathHelper.cos(yawRad) * 0.202F;
            player().motionY = 0.405F;
            MUtil.strafe();
        } else if (player().fallDistance < 0.31F) {
            // Motion XZ
            player().jumpMovementFactor = player().moveStrafing == 0F ? 0.027F : 0.021F;
            player().motionX *= 1.001;
            player().motionZ *= 1.001;

            // Motion Y
            if (!player().isCollidedHorizontally)
                player().motionY -= 0.014999993F;
        } else
            player().jumpMovementFactor = 0.02F;
    }
}
