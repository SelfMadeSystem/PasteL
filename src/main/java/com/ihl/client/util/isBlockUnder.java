package com.ihl.client.util;

import net.minecraft.block.*;
import net.minecraft.util.BlockPos;

import static com.ihl.client.Helper.mc;

public class isBlockUnder {
    public static boolean isBlockUnder() {
        for (int i = (int) (mc().thePlayer.posY - 1); i > 0; i--) {
            BlockPos pos = new BlockPos(mc().thePlayer.posX, i, mc().thePlayer.posZ);
            if (!(mc().theWorld.getBlockState(pos).getBlock() instanceof BlockAir)) {
                return true;
            }
        }
        return false;
    }
}
