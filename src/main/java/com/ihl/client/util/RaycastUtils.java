package com.ihl.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.src.Reflector;
import net.minecraft.util.*;

import java.util.List;

public class RaycastUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static MovingObjectPosition getMouseOver(float partTick, double reach, double blockReach) {
        CustomMouser c = CustomMouser.instance;
        return getMouseOver(partTick, reach, blockReach, c.prevRotationPitch, c.prevRotationYaw, c.rotationPitch, c.rotationYaw);
    }

    public static MovingObjectPosition getMouseOver(float partTick, double reach, double blockReach,
                                                    float prevRotationPitch, float prevRotationYaw, float rotationPitch, float rotationYaw) {
        Entity var2 = mc.getRenderEntity();
        Entity pointedEntity = null;
        MovingObjectPosition objectMouseOver = null;

        if (var2 != null && mc.theWorld != null) {
            mc.mcProfiler.startSection("pick");
            mc.pointedEntity = null;
            double var3 = blockReach;
            objectMouseOver = rayTrace(var3, prevRotationPitch, prevRotationYaw, rotationPitch, rotationYaw);
            double var5 = var3;
            Vec3 var7 = var2.func_174824_e(partTick);

            if (mc.playerController.extendedReach()) {
                var3 = reach * 2;
                var5 = reach * 2;
            } else {
                if (var3 > reach) {
                    var5 = reach;
                }

                var3 = var5;
            }

            if (objectMouseOver != null) {
                var5 = objectMouseOver.hitVec.distanceTo(var7);
            }

            Vec3 var8 = getLook(partTick, prevRotationPitch, prevRotationYaw, rotationPitch, rotationYaw);
            Vec3 var9 = var7.addVector(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3);
            Vec3 var10 = null;
            float var11 = 1.0F;
            List var12 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2, var2.getEntityBoundingBox().addCoord(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3).expand(var11, var11, var11));
            double var13 = var5;

            for (Object o : var12) {
                Entity var16 = (Entity) o;

                if (var16.canBeCollidedWith()) {
                    float var17 = var16.getCollisionBorderSize();
                    AxisAlignedBB var18 = var16.getEntityBoundingBox().expand(var17, var17, var17);
                    MovingObjectPosition var19 = var18.calculateIntercept(var7, var9);

                    if (var18.isVecInside(var7)) {
                        if (0.0D < var13 || var13 == 0.0D) {
                            pointedEntity = var16;
                            var10 = var19 == null ? var7 : var19.hitVec;
                            var13 = 0.0D;
                        }
                    } else if (var19 != null) {
                        double var20 = var7.distanceTo(var19.hitVec);

                        if (var20 < var13 || var13 == 0.0D) {
                            boolean canRiderInteract = false;

                            if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                                canRiderInteract = Reflector.callBoolean(var16, Reflector.ForgeEntity_canRiderInteract);
                            }

                            if (var16 == var2.ridingEntity && !canRiderInteract) {
                                if (var13 == 0.0D) {
                                    pointedEntity = var16;
                                    var10 = var19.hitVec;
                                }
                            } else {
                                pointedEntity = var16;
                                var10 = var19.hitVec;
                                var13 = var20;
                            }
                        }
                    }
                }
            }

            if (pointedEntity != null && (var13 < var5 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, var10);

                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    mc.pointedEntity = pointedEntity;
                }
            }

            mc.mcProfiler.endSection();
        }
        return objectMouseOver;
    }

    public static MovingObjectPosition rayTrace(double distance, float prevRotationPitch, float prevRotationYaw, float rotationPitch, float rotationYaw) {
        Vec3 var4 = mc.thePlayer.func_174824_e(1F);
        Vec3 var5 = getLook(1F, prevRotationPitch, prevRotationYaw, rotationPitch, rotationYaw);
        Vec3 var6 = var4.addVector(var5.xCoord * distance, var5.yCoord * distance, var5.zCoord * distance);
        return mc.theWorld.rayTraceBlocks(var4, var6, false, false, true);
    }

    public static Vec3 getLook(float partialTicksThingy, float prevPitch, float prevYaw, float pitch, float yaw) {
        EntityPlayerSP player = mc.thePlayer;
        if (partialTicksThingy == 1.0F) {
            return player.func_174806_f(pitch, yaw);
        } else {
            float var2 = prevPitch + (pitch - prevPitch) * partialTicksThingy;
            float var3 = prevYaw + (yaw - prevYaw) * partialTicksThingy;
            return player.func_174806_f(var2, var3);
        }
    }
}
