package com.ihl.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.*;

public class CustomMouser extends MouseHelper {
    public static CustomMouser instance;

    static {
        new CustomMouser();
    }

    private Minecraft mc;

    public CustomMouser() {
        instance = this;
    }

    public float rotationYaw, rotationPitch, prevRotationYaw, prevRotationPitch;
    public boolean toPlayer = false;
    public boolean got, active;

    @Override
    public void grabMouseCursor() {
        got = true;
        this.deltaX = 0;
        this.deltaY = 0;
    }

    @Override
    public void ungrabMouseCursor() {
        got = false;
    }

    @Override
    public void mouseXYChange() {
        //mouseChange(deltaX, deltaY);
    }

    public float[] mouseChange() {
        return mouseChange(deltaX, deltaY);
    }

    public float[] mouseChange(int[] rotation) {
        return mouseChange(rotation[0], rotation[1]);
    }

    public float[] mouseChange(int x, int y) {
        mc = Minecraft.getMinecraft();
        float var13 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float var14 = var13 * var13 * var13 * 8.0F;
        float var15 = (float) x * var14;
        float var16 = (float) y * var14;
        byte var17 = 1;

        setAngles(var15, var16 * (float) var17);
        return new float[] {rotationYaw, rotationPitch};
    }

    public void setAngles(float yaw, float pitch) {
        float var3 = this.rotationPitch;
        float var4 = this.rotationYaw;
        this.rotationYaw = (float) ((double) this.rotationYaw + (double) yaw * 0.15D);
        this.rotationPitch = (float) ((double) this.rotationPitch - (double) pitch * 0.15D);
        this.rotationPitch = MathHelper.clamp_float(this.rotationPitch, -90.0F, 90.0F);
        this.prevRotationPitch += this.rotationPitch - var3;
        this.prevRotationYaw += this.rotationYaw - var4;
        if (active && toPlayer) toPlayer();
        if (active) RUtils.setTargetRotation(this.rotationYaw, this.rotationPitch);
    }

    public void toPlayer() {
        mc.thePlayer.rotationYaw = rotationYaw;
        mc.thePlayer.rotationPitch = rotationPitch;
        mc.thePlayer.prevRotationYaw = prevRotationYaw;
        mc.thePlayer.prevRotationPitch = prevRotationPitch;
    }
}
