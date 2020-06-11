package com.ihl.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class MUtil {
    private static EntityPlayerSP player() {
        return Minecraft.getMinecraft().thePlayer;
    }

    public static void moveAllTypes(double vclip, double hclip, float timer, float airSpeed,
                                    double vadd, double vmult, double hadd, double hmult) {
        vclip(vclip);
        hclip(hclip);
        timer(timer);
        airSpeed(airSpeed);
        vadd(vadd);
        vmult(vmult);
        hadd(hadd);
        hmult(hmult);
    }

    public static void timer(float timer) {
        Minecraft.getMinecraft().timer.timerSpeed = timer;
    }

    public static void hmult(double amount) {
        player().motionX *= amount;
        player().motionZ *= amount;
    }

    public static void hadd(double amount) {
        final double yaw = getDirection();
        player().motionX += -Math.sin(yaw) * amount;
        player().motionZ += Math.cos(yaw) * amount;
        /*
        player().motionX += Math.cos(Math.toRadians(player().rotationYaw + 90.0)) * amount;
        player().motionZ += Math.sin(Math.toRadians(player().rotationYaw + 90.0)) * amount;*/
    }

    public static void hclip(double amount) {
        player().setPositionAndUpdate(player().posX += Math.cos(Math.toRadians(player().rotationYaw + 90.0)) * amount, player().posY,
        player().posZ += Math.sin(Math.toRadians(player().rotationYaw + 90.0)) * amount);
    }

    public static void vmult(double amount) {
        player().motionY *= amount;
    }

    public static void vadd(double amount) {
        player().motionY += amount;
    }

    public static void vclip(double amount) {
        player().setPositionAndUpdate(player().posX, player().posY + amount, player().posZ);
    }

    public static void airSpeed(float amount) {
        player().speedInAir = amount;
    }

    public static void vset(double amount) {
        player().motionY = amount;
    }

    //Liquidbounce skid

    public static double getSpeed() {
        return Math.sqrt(player().motionX * player().motionX + player().motionZ * player().motionZ);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static boolean isMoving() {
        return player() != null && (player().movementInput.moveForward != 0F || player().movementInput.moveStrafe != 0F);
    }

    public static boolean hasMotion() {
        return player().motionX != 0D && player().motionZ != 0D && player().motionY != 0D;
    }

    public static void strafe(final double speed) {
        if(!isMoving())
            return;

        final double yaw = getDirection();
        player().motionX = -Math.sin(yaw) * speed;
        player().motionZ = Math.cos(yaw) * speed;
    }

    public static void forward(final double length) {
        final double yaw = Math.toRadians(player().rotationYaw);
        player().setPosition(player().posX + (-Math.sin(yaw) * length), player().posY, player().posZ + (Math.cos(yaw) * length));
    }

    public static double getDirection() {
        float rotationYaw = player().rotationYaw;

        if(player().moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if(player().moveForward < 0F)
            forward = -0.5F;
        else if(player().moveForward > 0F)
            forward = 0.5F;

        if(player().moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if(player().moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }
}
