package com.ihl.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.Timer;

public class Helper {

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    public static EntityPlayerSP player() {
        return mc().thePlayer;
    }

    public static Timer timer() {
        return mc().timer;
    }

    public static float timerSpeed() {
        return timer().timerSpeed;
    }

    public static void timerSpeed(float f) {
        timer().timerSpeed = f;
    }

    public static WorldClient world() {
        return mc().theWorld;
    }

    public static PlayerControllerMP controller() {
        return mc().playerController;
    }

    public static ScaledResolution scaled() {
        return new ScaledResolution(mc(), mc().displayWidth, mc().displayHeight);
    }

}
