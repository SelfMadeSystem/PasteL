package com.ihl.client.util;

import com.ihl.client.event.EventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;

public final class RUtils {
    public static boolean lookChanged;
    public static float targetYaw;
    public static float targetPitch;
    public static boolean keepRotation = false;
    public static float[] lastLook = new float[]{0.0F, 0.0F};
    private static int keepLength;
    private static double x = Math.random();
    private static double y = Math.random();
    private static double z = Math.random();

    public RUtils() {
    }

    public static void faceBlockPacket(BlockPos blockPos) {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        if (blockPos != null) {
            double diffX = (double) blockPos.getX() + 0.5D - p.posX;
            double diffY = (double) blockPos.getY() + 0.5D - (p.getEntityBoundingBox().minY + (double) p.getEyeHeight());
            double diffZ = (double) blockPos.getZ() + 0.5D - p.posZ;
            double sqrt = Math.sqrt(diffX * diffX + diffZ * diffZ);
            float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / 3.141592653589793D) - 90.0F;
            float pitch = (float) (-(Math.atan2(diffY, sqrt) * 180.0D / 3.141592653589793D));
            setTargetRotation(p.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - p.rotationYaw), p.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - p.rotationPitch));
        }
    }

    /*public static void faceBow(Entity target, boolean silent, boolean predict, float predictSize) {

        double posX = target.posX + (predict ? (target.posX - target.prevX) * (double)predictSize : 0.0D) - (p.posX + (predict ? p.posX - p.prevX : 0.0D));
        double posY = target.getEntityBoundingBox().minY + (predict ? (target.getEntityBoundingBox().minY - target.prevY) * (double)predictSize : 0.0D) + (double)target.getEyeHeight() - 0.15D - (p.getEntityBoundingBox().minY + (predict ? p.field_70163_u - p.prevY : 0.0D)) - (double)p.getEyeHeight();
        double posZ = target.posZ + (predict ? (target.posZ - target.prevZ) * (double)predictSize : 0.0D) - (p.posZ + (predict ? p.posZ - p.prevZ : 0.0D));
        double sqrt = Math.sqrt(posX * posX + posZ * posZ);
        float velocity = (float)p.getItemInUseCount() / 20.0F;
        velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;
        if (velocity > 1.0F) {
            velocity = 1.0F;
        }

        float yaw = (float)(Math.atan2(posZ, posX) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float)(-Math.toDegrees(Math.atan(((double)(velocity * velocity) - Math.sqrt((double)(velocity * velocity * velocity * velocity) - 0.006000000052154064D * (0.006000000052154064D * sqrt * sqrt + 2.0D * posY * (double)(velocity * velocity)))) / (0.006000000052154064D * sqrt))));
        float[] rotations;
        if (velocity < 0.1F) {
            rotations = getNeededRotations(getCenter(target.getEntityBoundingBox()), true);
            yaw = rotations[0];
            pitch = rotations[1];
        }

        if (silent) {
            setTargetRotation(yaw, pitch);
        } else {
            rotations = limitAngleChange(new float[]{p.rotationYaw, p.rotationPitch}, new float[]{yaw, pitch}, (float)(10 + RandomUtils.getRandom().nextInt(6)));
            if (rotations == null) {
                return;
            }

            p.rotationYaw = rotations[0];
            p.rotationPitch = rotations[1];
        }

    }*/

    /*public static boolean isLookingAtTarget(Entity target, boolean predict){
        boolean looking = false;
        AxisAlignedBB bb = target.getEntityBoundingBox();
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        Vec3 eyesPos = getEyesPos();

        float yaw = MathHelper.wrapAngleTo180_float(p.rotationYaw);
        float pitch = p.rotationPitch;

        Vec3 max = new Vec3(bb.maxX, bb.maxY, bb.maxZ);
        Vec3 min = new Vec3(bb.minX, bb.minY, bb.minZ);

        //ChatUtils.noPXMessage(yaw+" "+getNeededRotations(getCenter(bb), predict)[0]);

        if(getNeededRotations(getCenter(bb), predict)[0]<0 && getNeededRotations(getCenter(bb), predict)[0]>-90) {
            double minDiffX = min.xCoord - eyesPos.xCoord;
            double minDiffY = min.yCoord - eyesPos.yCoord;
            double minDiffZ = max.zCoord - eyesPos.zCoord;
            double minDiffXZ = Math.sqrt(minDiffX * minDiffX + minDiffZ * minDiffZ);
            float minYaw = (float) MathHelper.wrapAngleTo180_float(Math.toDegrees(Math.atan2(minDiffZ, minDiffX)) - 90.0F);
            float minPitch = (float) -Math.toDegrees(Math.atan2(minDiffY, minDiffXZ));

            double maxDiffX = max.xCoord - eyesPos.xCoord;
            double maxDiffY = max.yCoord - eyesPos.yCoord;
            double maxDiffZ = min.zCoord - eyesPos.zCoord;
            double maxDiffXZ = Math.sqrt(maxDiffX * maxDiffX + maxDiffZ * maxDiffZ);
            float maxYaw = (float) MathHelper.wrapAngleTo180_float(Math.toDegrees(Math.atan2(maxDiffZ, maxDiffX)) - 90.0F);
            float maxPitch = (float) -Math.toDegrees(Math.atan2(maxDiffY, maxDiffXZ));

            if (yaw <= minYaw && yaw >= maxYaw && pitch <= minPitch && pitch >= maxPitch)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
                    //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));'
            //ChatUtils.noPXMessage("min max");
        }

        if(getNeededRotations(getCenter(bb), predict)[0]<90 && getNeededRotations(getCenter(bb), predict)[0]>0) {
            double minDiffX = min.xCoord - eyesPos.xCoord;
            double minDiffY = min.yCoord - eyesPos.yCoord;
            double minDiffZ = min.zCoord - eyesPos.zCoord;
            double minDiffXZ = Math.sqrt(minDiffX * minDiffX + minDiffZ * minDiffZ);
            float minYaw = (float) MathHelper.wrapAngleTo180_float(Math.toDegrees(Math.atan2(minDiffZ, minDiffX)) - 90.0F);
            float minPitch = (float) -Math.toDegrees(Math.atan2(minDiffY, minDiffXZ));

            double maxDiffX = max.xCoord - eyesPos.xCoord;
            double maxDiffY = max.yCoord - eyesPos.yCoord;
            double maxDiffZ = max.zCoord - eyesPos.zCoord;
            double maxDiffXZ = Math.sqrt(maxDiffX * maxDiffX + maxDiffZ * maxDiffZ);
            float maxYaw = (float) MathHelper.wrapAngleTo180_float(Math.toDegrees(Math.atan2(maxDiffZ, maxDiffX)) - 90.0F);
            float maxPitch = (float) -Math.toDegrees(Math.atan2(maxDiffY, maxDiffXZ));

            if (yaw <= minYaw && yaw >= maxYaw && pitch <= minPitch && pitch >= maxPitch)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
                    //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));
            //ChatUtils.noPXMessage("min min");
        }

        if(getNeededRotations(getCenter(bb), predict)[0]<180 && getNeededRotations(getCenter(bb), predict)[0]>90) {
            double minDiffX = max.xCoord - eyesPos.xCoord;
            double minDiffY = min.yCoord - eyesPos.yCoord;
            double minDiffZ = min.zCoord - eyesPos.zCoord;
            double minDiffXZ = Math.sqrt(minDiffX * minDiffX + minDiffZ * minDiffZ);
            float minYaw = (float) MathHelper.wrapAngleTo180_float(Math.toDegrees(Math.atan2(minDiffZ, minDiffX)) - 90.0F);
            float minPitch = (float) -Math.toDegrees(Math.atan2(minDiffY, minDiffXZ));

            double maxDiffX = min.xCoord - eyesPos.xCoord;
            double maxDiffY = max.yCoord - eyesPos.yCoord;
            double maxDiffZ = max.zCoord - eyesPos.zCoord;
            double maxDiffXZ = Math.sqrt(maxDiffX * maxDiffX + maxDiffZ * maxDiffZ);
            float maxYaw = (float) MathHelper.wrapAngleTo180_float(Math.toDegrees(Math.atan2(maxDiffZ, maxDiffX)) - 90.0F);
            float maxPitch = (float) -Math.toDegrees(Math.atan2(maxDiffY, maxDiffXZ));

            if (yaw <= minYaw && yaw >= maxYaw && pitch <= minPitch && pitch >= maxPitch)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
                    //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));
            //ChatUtils.noPXMessage("max min");
        }

        if(getNeededRotations(getCenter(bb), predict)[0]<-90 && getNeededRotations(getCenter(bb), predict)[0]>-180) {
            double minDiffX = max.xCoord - eyesPos.xCoord;
            double minDiffY = min.yCoord - eyesPos.yCoord;
            double minDiffZ = max.zCoord - eyesPos.zCoord;
            double minDiffXZ = Math.sqrt(minDiffX * minDiffX + minDiffZ * minDiffZ);
            float minYaw = (float) MathHelper.wrapAngleTo180_float(Math.toDegrees(Math.atan2(minDiffZ, minDiffX)) - 90.0F);
            float minPitch = (float) -Math.toDegrees(Math.atan2(minDiffY, minDiffXZ));

            double maxDiffX = min.xCoord - eyesPos.xCoord;
            double maxDiffY = max.yCoord - eyesPos.yCoord;
            double maxDiffZ = min.zCoord - eyesPos.zCoord;
            double maxDiffXZ = Math.sqrt(maxDiffX * maxDiffX + maxDiffZ * maxDiffZ);
            float maxYaw = (float) MathHelper.wrapAngleTo180_float(Math.toDegrees(Math.atan2(maxDiffZ, maxDiffX)) - 90.0F);
            float maxPitch = (float) -Math.toDegrees(Math.atan2(maxDiffY, maxDiffXZ));

            if (yaw <= minYaw && yaw >= maxYaw && pitch <= minPitch && pitch >= maxPitch)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
                    //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));
            //ChatUtils.noPXMessage("max max");
        }

        // -153 -130
        // -2.24 37

        // 120 112
        // -2.8 7.75

        return looking;
    }*/

    public static boolean isYawLookingAtTarget(Entity target, double predict) {
        float[] rotation = new float[]{Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.rotationPitch};
        return isYawLookingAtTarget(rotation, target, predict);
    }

    public static boolean isPitchLookingAtTarget(Entity target, double predict) {
        float[] rotation = new float[]{Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.rotationPitch};
        return isPitchLookingAtTarget(rotation, target, predict);
    }

    public static boolean isLookingAtTarget(Entity target, double predict) {
        float[] rotation = new float[]{Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.rotationPitch};
        return isPitchLookingAtTarget(rotation, target, predict) && isYawLookingAtTarget(rotation, target, predict);
    }

    public static boolean isLookingAtTarget(float[] rotation, Entity target, double predict) {
        return isPitchLookingAtTarget(rotation, target, predict) && isYawLookingAtTarget(rotation, target, predict);
    }

    public static boolean isPitchLookingAtTarget(float[] rotation, Entity target, double predict) {
        boolean looking = false;
        AxisAlignedBB bb = target.getEntityBoundingBox();
        Vec3 eyesPos = getEyesPos();

        float pitch = rotation[1];

        Vec3 max = new Vec3(bb.maxX, bb.maxY, bb.maxZ);
        Vec3 min = new Vec3(bb.minX, bb.minY, bb.minZ);

        //ChatUtils.noPXMessage(yaw+" "+getNeededRotations(getCenter(bb), predict)[0]);

        if (getNeededRotations(getCenter(bb), predict)[0] < 0 && getNeededRotations(getCenter(bb), predict)[0] > -90) {
            double minDiffX = min.xCoord - eyesPos.xCoord;
            double minDiffY = min.yCoord - eyesPos.yCoord;
            double minDiffZ = max.zCoord - eyesPos.zCoord;
            double minDiffXZ = Math.sqrt(minDiffX * minDiffX + minDiffZ * minDiffZ);
            float minPitch = (float) -Math.toDegrees(Math.atan2(minDiffY, minDiffXZ));

            double maxDiffX = max.xCoord - eyesPos.xCoord;
            double maxDiffY = max.yCoord - eyesPos.yCoord;
            double maxDiffZ = min.zCoord - eyesPos.zCoord;
            double maxDiffXZ = Math.sqrt(maxDiffX * maxDiffX + maxDiffZ * maxDiffZ);
            float maxPitch = (float) -Math.toDegrees(Math.atan2(maxDiffY, maxDiffXZ));

            if (pitch <= minPitch && pitch >= maxPitch)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
            //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));'
            //ChatUtils.noPXMessage("min max");
        }

        if (getNeededRotations(getCenter(bb), predict)[0] < 90 && getNeededRotations(getCenter(bb), predict)[0] > 0) {
            double minDiffX = min.xCoord - eyesPos.xCoord;
            double minDiffY = min.yCoord - eyesPos.yCoord;
            double minDiffZ = min.zCoord - eyesPos.zCoord;
            double minDiffXZ = Math.sqrt(minDiffX * minDiffX + minDiffZ * minDiffZ);
            float minPitch = (float) -Math.toDegrees(Math.atan2(minDiffY, minDiffXZ));

            double maxDiffX = max.xCoord - eyesPos.xCoord;
            double maxDiffY = max.yCoord - eyesPos.yCoord;
            double maxDiffZ = max.zCoord - eyesPos.zCoord;
            double maxDiffXZ = Math.sqrt(maxDiffX * maxDiffX + maxDiffZ * maxDiffZ);
            float maxPitch = (float) -Math.toDegrees(Math.atan2(maxDiffY, maxDiffXZ));

            if (pitch <= minPitch && pitch >= maxPitch)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
            //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));
            //ChatUtils.noPXMessage("min min");
        }

        if (getNeededRotations(getCenter(bb), predict)[0] < 180 && getNeededRotations(getCenter(bb), predict)[0] > 90) {
            double minDiffX = max.xCoord - eyesPos.xCoord;
            double minDiffY = min.yCoord - eyesPos.yCoord;
            double minDiffZ = min.zCoord - eyesPos.zCoord;
            double minDiffXZ = Math.sqrt(minDiffX * minDiffX + minDiffZ * minDiffZ);
            float minPitch = (float) -Math.toDegrees(Math.atan2(minDiffY, minDiffXZ));

            double maxDiffX = min.xCoord - eyesPos.xCoord;
            double maxDiffY = max.yCoord - eyesPos.yCoord;
            double maxDiffZ = max.zCoord - eyesPos.zCoord;
            double maxDiffXZ = Math.sqrt(maxDiffX * maxDiffX + maxDiffZ * maxDiffZ);
            float maxPitch = (float) -Math.toDegrees(Math.atan2(maxDiffY, maxDiffXZ));

            if (pitch <= minPitch && pitch >= maxPitch)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
            //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));
            //ChatUtils.noPXMessage("max min");
        }

        if (getNeededRotations(getCenter(bb), predict)[0] < -90 && getNeededRotations(getCenter(bb), predict)[0] > -180) {
            double minDiffX = max.xCoord - eyesPos.xCoord;
            double minDiffY = min.yCoord - eyesPos.yCoord;
            double minDiffZ = max.zCoord - eyesPos.zCoord;
            double minDiffXZ = Math.sqrt(minDiffX * minDiffX + minDiffZ * minDiffZ);
            float minPitch = (float) -Math.toDegrees(Math.atan2(minDiffY, minDiffXZ));

            double maxDiffX = min.xCoord - eyesPos.xCoord;
            double maxDiffY = max.yCoord - eyesPos.yCoord;
            double maxDiffZ = min.zCoord - eyesPos.zCoord;
            double maxDiffXZ = Math.sqrt(maxDiffX * maxDiffX + maxDiffZ * maxDiffZ);
            float maxPitch = (float) -Math.toDegrees(Math.atan2(maxDiffY, maxDiffXZ));

            if (pitch <= minPitch && pitch >= maxPitch)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
            //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));
            //ChatUtils.noPXMessage("max max");
        }

        // -153 -130
        // -2.24 37

        // 120 112
        // -2.8 7.75

        return looking;
    }

    public static boolean isYawLookingAtTarget(float[] rotation, Entity target, double predict) {
        boolean looking = false;
        AxisAlignedBB bb = target.getEntityBoundingBox();
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        Vec3 eyesPos = getEyesPos();

        float yaw = MathHelper.wrapAngleTo180_float(rotation[0]);

        Vec3 max = new Vec3(bb.maxX, bb.maxY, bb.maxZ);
        Vec3 min = new Vec3(bb.minX, bb.minY, bb.minZ);

        //ChatUtils.noPXMessage(yaw+" "+getNeededRotations(getCenter(bb), predict)[0]);

        if (getNeededRotations(getCenter(bb), predict)[0] < 0 && getNeededRotations(getCenter(bb), predict)[0] > -90) {
            double minDiffX = min.xCoord - eyesPos.xCoord;
            double minDiffZ = max.zCoord - eyesPos.zCoord;
            float minYaw = (float) MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(minDiffZ, minDiffX)) - 90.0F);

            double maxDiffX = max.xCoord - eyesPos.xCoord;
            double maxDiffZ = min.zCoord - eyesPos.zCoord;
            float maxYaw = (float) MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(maxDiffZ, maxDiffX)) - 90.0F);

            if (yaw <= minYaw && yaw >= maxYaw)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
            //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));'
            //ChatUtils.noPXMessage("min max");
        }

        if (getNeededRotations(getCenter(bb), predict)[0] < 90 && getNeededRotations(getCenter(bb), predict)[0] > 0) {
            double minDiffX = min.xCoord - eyesPos.xCoord;
            double minDiffZ = max.zCoord - eyesPos.zCoord;
            float minYaw = (float) MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(minDiffZ, minDiffX)) - 90.0F);

            double maxDiffX = max.xCoord - eyesPos.xCoord;
            double maxDiffZ = min.zCoord - eyesPos.zCoord;
            float maxYaw = (float) MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(maxDiffZ, maxDiffX)) - 90.0F);

            if (yaw <= minYaw && yaw >= maxYaw)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
            //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));
            //ChatUtils.noPXMessage("min min");
        }

        if (getNeededRotations(getCenter(bb), predict)[0] < 180 && getNeededRotations(getCenter(bb), predict)[0] > 90) {
            double minDiffX = min.xCoord - eyesPos.xCoord;
            double minDiffZ = max.zCoord - eyesPos.zCoord;
            float minYaw = (float) MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(minDiffZ, minDiffX)) - 90.0F);

            double maxDiffX = max.xCoord - eyesPos.xCoord;
            double maxDiffZ = min.zCoord - eyesPos.zCoord;
            float maxYaw = (float) MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(maxDiffZ, maxDiffX)) - 90.0F);

            if (yaw <= minYaw && yaw >= maxYaw)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
            //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));
            //ChatUtils.noPXMessage("max min");
        }

        if (getNeededRotations(getCenter(bb), predict)[0] < -90 && getNeededRotations(getCenter(bb), predict)[0] > -180) {
            double minDiffX = min.xCoord - eyesPos.xCoord;
            double minDiffZ = max.zCoord - eyesPos.zCoord;
            float minYaw = (float) MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(minDiffZ, minDiffX)) - 90.0F);

            double maxDiffX = max.xCoord - eyesPos.xCoord;
            double maxDiffZ = min.zCoord - eyesPos.zCoord;
            float maxYaw = (float) MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(maxDiffZ, maxDiffX)) - 90.0F);

            if (yaw <= minYaw && yaw >= maxYaw)
                looking = true;

            //ChatUtils.noPXMessage(minYaw + " " + maxYaw + " " + yaw + "\n" + (yaw <= minYaw) + " " + (yaw >= maxYaw) + "\n"
            //+ maxPitch + " " + minPitch + " " + pitch + "\n" + (pitch <= minPitch) + " " + (pitch >= maxPitch));
            //ChatUtils.noPXMessage("max max");
        }

        // -153 -130
        // -2.24 37

        // 120 112
        // -2.8 7.75

        return looking;
    }

    public static float[] getTargetRotation(Entity entity) {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        return entity != null && p != null ? getNeededRotations(getRandomCenter(entity.getEntityBoundingBox(), false), 1) : null;
    }

    public static float[] getNeededRotations(Vec3 vec, double predict) {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        Vec3 eyesPos = getEyesPos();
        eyesPos.addVector(p.motionX * predict, p.motionY * predict, p.motionZ * predict);

        double diffX = vec.xCoord - eyesPos.xCoord;
        double diffY = vec.yCoord - eyesPos.yCoord;
        double diffZ = vec.zCoord - eyesPos.zCoord;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
    }

    public static Vec3 getCenter(AxisAlignedBB bb) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5D, bb.minY + (bb.maxY - bb.minY) * 0.5D, bb.minZ + (bb.maxZ - bb.minZ) * 0.5D);
    }

    public static Vec3 getTop(AxisAlignedBB bb) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5D, bb.maxY, bb.minZ + (bb.maxZ - bb.minZ) * 0.5D);
    }

    public static Vec3 getBottom(AxisAlignedBB bb) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5D, bb.minY, bb.minZ + (bb.maxZ - bb.minZ) * 0.5D);
    }

    public static Vec3 getHead(Entity target) {
        AxisAlignedBB bb = target.getEntityBoundingBox();
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5D, bb.minY + target.getEyeHeight(), bb.minZ + (bb.maxZ - bb.minZ) * 0.5D);
    }

    public static Vec3 getFromTop(AxisAlignedBB bb, double add) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5D, bb.maxY - add, bb.minZ + (bb.maxZ - bb.minZ) * 0.5D);
    }

    public static Vec3 getFromBottom(AxisAlignedBB bb, double add) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5D, bb.minY + add, bb.minZ + (bb.maxZ - bb.minZ) * 0.5D);
    }

    public static Vec3 getRandomCenter(AxisAlignedBB bb, boolean outBorder) {
        return outBorder ? new Vec3(bb.minX + (bb.maxX - bb.minX) * (x * 0.3D + 1.0D), bb.minY + (bb.maxY - bb.minY) * (y * 0.3D + 1.0D), bb.minZ + (bb.maxZ - bb.minZ) * (z * 0.3D + 1.0D)) : new Vec3(bb.minX + (bb.maxX - bb.minX) * x * 0.8D, bb.minY + (bb.maxY - bb.minY) * y * 0.8D, bb.minZ + (bb.maxZ - bb.minZ) * z * 0.8D);
    }

    public static VecRotation searchCenter(final AxisAlignedBB bb, final boolean outborder, final boolean random, final double predict, final boolean throughWalls) {
        if (outborder) {
            final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * (x * 0.3 + 1.0), bb.minY + (bb.maxY - bb.minY) * (y * 0.3 + 1.0), bb.minZ + (bb.maxZ - bb.minZ) * (z * 0.3 + 1.0));
            return new VecRotation(vec3, toRotation(vec3, predict));
        }

        final Vec3 randomVec = new Vec3(bb.minX + (bb.maxX - bb.minX) * x * 0.8, bb.minY + (bb.maxY - bb.minY) * y * 0.8, bb.minZ + (bb.maxZ - bb.minZ) * z * 0.8);
        final float[] randomRotation = toRotation(randomVec, predict);

        VecRotation vecRotation = null;

        for (double xSearch = 0.15D; xSearch < 0.85D; xSearch += 0.1D) {
            for (double ySearch = 0.15D; ySearch < 1D; ySearch += 0.1D) {
                for (double zSearch = 0.15D; zSearch < 0.85D; zSearch += 0.1D) {
                    final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch, bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
                    final float[] rotation = toRotation(vec3, predict);

                    if (throughWalls || isVisible(vec3)) {
                        final VecRotation currentVec = new VecRotation(vec3, rotation);

                        if (vecRotation == null || (random ? getRotationDifference(currentVec.getRotation(), randomRotation) <
                          getRotationDifference(vecRotation.getRotation(), randomRotation) :
                          getRotationDifference(currentVec.getRotation()[0], currentVec.getRotation()[1]) < getRotationDifference(vecRotation.getRotation()[0], vecRotation.getRotation()[1])))
                            vecRotation = currentVec;
                    }
                }
            }
        }

        return vecRotation;
    }

    public static float[] toRotation(final Vec3 vec, final double predict) {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;

        final Vec3 eyesPos = new Vec3(p.posX, p.getEntityBoundingBox().minY +
          p.getEyeHeight(), p.posZ);

        eyesPos.addVector(p.motionX * predict, p.motionY * predict, p.motionZ * predict);

        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;

        return new float[]{MathHelper.wrapAngleTo180_float(
          (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
        ), MathHelper.wrapAngleTo180_float(
          (float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
        )};
    }

    public static boolean isVisible(final Vec3 vec3) {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        final Vec3 eyesPos = new Vec3(p.posX, p.getEntityBoundingBox().minY + p.getEyeHeight(), p.posZ);

        return Minecraft.getMinecraft().theWorld.rayTraceBlocks(eyesPos, vec3) == null;
    }

    public static double getRotationDifference(Entity entity) {
        float[] rotations = getTargetRotation(entity);
        return rotations == null ? 0.0D : getRotationDifference(rotations[0], rotations[1]);
    }

    public static double getRotationDifference(float yaw, float pitch) {
        return Math.sqrt(Math.pow(Math.abs(angleDifference(lastLook[0] % 360.0F, yaw)), 2.0D) + Math.pow(Math.abs(angleDifference(lastLook[1], pitch)), 2.0D));
    }

    public static double getRotationDifference(final float[] a, final float[] b) {
        return Math.hypot(angleDifference(a[0], b[0]), a[1] - b[1]);
    }

    public static float[] limitAngleChange(float[] current, float[] target, float turnSpeedYaw, float turnSpeedPitch) {
        final float yawDifference = angleDifference(target[0], current[0]);
        final float pitchDifference = angleDifference(target[1], current[1]);

        return new float[]{
          current[0] + (yawDifference > turnSpeedYaw ? turnSpeedYaw : Math.max(yawDifference, -turnSpeedYaw)),
          current[1] + (pitchDifference > turnSpeedPitch ? turnSpeedPitch : Math.max(pitchDifference, -turnSpeedPitch))
        };
    }

    public static float angleDifference(float a, float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    public static Vec3 getEyesPos() {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        return new Vec3(p.posX, p.getEntityBoundingBox().minY + (double) p.getEyeHeight(), p.posZ);
    }

    //public static boolean isFaced(Entity targetEntity, double blockReachDistance) {
    //    return RaycastUtils.raycastEntities(blockReachDistance).contains(targetEntity);
    //}

    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public static void setTargetRotation(float yaw, float pitch) {
        if (!Double.isNaN(yaw) && !Double.isNaN(pitch)) {
            targetYaw = yaw;
            targetPitch = pitch;
            lookChanged = true;
            keepLength = 0;
        }
    }

    public static void reset() {
        lookChanged = false;
        keepLength = 0;
        targetYaw = 0.0F;
        targetPitch = 0.0F;
    }

    public void onUpdate() {
        if (lookChanged) {
            ++keepLength;
            if (keepLength > 15) {
                reset();
            }
        }

        if (RandomUtils.getRandom().nextGaussian() * 100.0D > 80.0D) {
            x = Math.random();
        }

        if (RandomUtils.getRandom().nextGaussian() * 100.0D > 80.0D) {
            y = Math.random();
        }

        if (RandomUtils.getRandom().nextGaussian() * 100.0D > 80.0D) {
            z = Math.random();
        }

    }

    public void onSentPacket(EventPacket event) {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        Packet packet = event.packet;

        if (packet instanceof C03PacketPlayer.C05PacketPlayerLook) {
            if (lookChanged && !keepRotation && (targetYaw != lastLook[0] || targetPitch != lastLook[1])) {
                event.packet = new C03PacketPlayer.C05PacketPlayerLook(targetYaw, targetPitch, p.onGround);
            }
        }
    }

}
