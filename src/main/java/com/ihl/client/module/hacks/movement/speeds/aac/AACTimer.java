package com.ihl.client.module.hacks.movement.speeds.aac;

import com.ihl.client.event.EventPlayerUpdate;
import com.ihl.client.module.hacks.movement.Speed;
import com.ihl.client.module.hacks.movement.speeds.SpeedMode;
import com.ihl.client.util.MUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AACTimer extends SpeedMode {
    public AACTimer() {
        super("AAC", "Timer");
    }

    @Override
    public void onUpdate(EventPlayerUpdate event, Speed speed) {
        float timer = speed.FLOAT("AACOptions", "AACTimer");
        float move = speed.FLOAT("AACOptions", "AACTimerMove");
        boolean pos = speed.BOOLEAN("AACOptions", "AACTimerPos");
        timerSpeed(timer);
        if (move > 0) {
            MUtil.strafe(move);
        }
        if (pos)
            mc().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(player().posX, player().posY, player().posZ, true));
    }
}
