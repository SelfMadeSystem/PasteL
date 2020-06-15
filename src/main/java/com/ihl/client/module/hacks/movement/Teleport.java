package com.ihl.client.module.hacks.movement;

import com.ihl.client.Helper;
import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.*;

@EventHandler(events = EventPlayerUpdate.class)
public class Teleport extends Module {
    double pX;
    double pY;
    double pZ;
    float pYaw;
    float pPitch;
    private MovingObjectPosition objectPosition;
    private BlockPos endPos;
    public Teleport() {
        super("Teleport", "Just testing stuff", Category.MOVEMENT, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    @Override
    public void enable() {
        super.enable();
        pX = player().posX;
        pY = player().posY;
        pZ = player().posZ;
        pYaw = player().rotationYaw;
        pPitch = player().rotationPitch;
    }

    @Override
    public void disable() {
        super.disable();
        mc().gameSettings.keyBindForward.pressed = false;
    }

    @Override
    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            objectPosition = player().rayTrace(1000, 1.0F);
            endPos = objectPosition.getBlockPos();
            if (endPos == null) {
                toggle();
                return;
            }
            player().capabilities.isFlying = true;
            {
                Helper.player().rotationYaw = EntityUtil.getYawToBlock(endPos);
                mc().gameSettings.keyBindForward.pressed = true;
            }
            if (endPos.add(0, -endPos.getY(), 0).distanceSq(player().getPosition().add(0, -player().getPosition().getY(), 0)) < 2) {
                toggle();
            } else {
                final EntityPlayerSP render = player();
                render.posX = pX;
                render.posY = pY;
                render.posZ = pZ;
                render.rotationYaw = pYaw;
                render.rotationPitch = pPitch;
                Minecraft.getMinecraft().renderEntity = render;
            }
        }
    }
}
