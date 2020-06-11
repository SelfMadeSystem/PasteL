package com.ihl.client.util;

import net.minecraft.block.*;
import net.minecraft.util.BlockPos;

import java.util.*;

import static com.ihl.client.Helper.mc;

public class BlockUtils {
    private static final Set<Class<? extends Block>> nonSolids = new HashSet<>();

    static {
        nonSolids.add(BlockAir.class);
        {
            nonSolids.add(BlockBasePressurePlate.class);
            nonSolids.add(BlockPressurePlate.class);
            nonSolids.add(BlockPressurePlateWeighted.class);
        }
        {
            nonSolids.add(BlockBush.class);
            nonSolids.add(BlockCarrot.class);
            nonSolids.add(BlockCrops.class);
            nonSolids.add(BlockDeadBush.class);
            nonSolids.add(BlockDoublePlant.class);
            nonSolids.add(BlockFlower.class);
            nonSolids.add(BlockLilyPad.class);
            nonSolids.add(BlockMushroom.class);
            nonSolids.add(BlockNetherWart.class);
            nonSolids.add(BlockPotato.class);
            nonSolids.add(BlockRedFlower.class);
            nonSolids.add(BlockSapling.class);
            nonSolids.add(BlockTallGrass.class);
            nonSolids.add(BlockYellowFlower.class);
        }
        {
            nonSolids.add(BlockButton.class);
            nonSolids.add(BlockButtonWood.class);
            nonSolids.add(BlockButtonStone.class);
        }
        nonSolids.add(BlockFire.class);
        {
            nonSolids.add(BlockRailBase.class);
            nonSolids.add(BlockRail.class);
            nonSolids.add(BlockRailDetector.class);
            nonSolids.add(BlockRailPowered.class);
        }
        nonSolids.add(BlockTorch.class);
        {
            nonSolids.add(BlockRedstoneDiode.class);
            nonSolids.add(BlockRedstoneComparator.class);
            nonSolids.add(BlockRedstoneRepeater.class);
            nonSolids.add(BlockRedstoneTorch.class);
            nonSolids.add(BlockRedstoneLight.class);
            nonSolids.add(BlockRedstoneWire.class);
        }
        nonSolids.add(BlockTripWire.class);
        nonSolids.add(BlockTripWireHook.class);
        nonSolids.add(BlockVine.class);
    }

    public static boolean isBlockUnder() {
        for (int i = (int) (mc().thePlayer.posY - 1); i > 0; i--) {
            BlockPos pos = new BlockPos(mc().thePlayer.posX, i, mc().thePlayer.posZ);
            if (isNonSolid(mc().theWorld.getBlockState(pos).getBlock())) {
                return true;
            }
        }
        return false;
    }

    public static int blocksUnder() {
        int i1 = 0; //Lazy
        for (int i = (int) (mc().thePlayer.posY - 1); i > 0; i--) {
            BlockPos pos = new BlockPos(mc().thePlayer.posX, i, mc().thePlayer.posZ);
            if (isNonSolid(mc().theWorld.getBlockState(pos).getBlock())) {
                return i1;
            }
            i1++;
        }
        return 256;
    }

    public static boolean isNonSolid(Block block) {
        if (block instanceof BlockAir)
            return true;
        for (Class<? extends Block> nonSolid : nonSolids) {
            if (block.getClass().equals(nonSolid))
                return true;
        }
        return false;
    }
}
