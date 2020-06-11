package com.ihl.client.module.hacks.player;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.util.*;
import net.minecraft.util.Vec3;

import java.util.*;


@EventHandler(events = {EventPlayerUpdate.class})
public class Antivoid extends Module {

    public Antivoid() {
        super("Antivoid", "Teleports you back when you fall in to the void.", Category.PLAYER, "NONE");
        addChoice("Mode", "Teleports you back when you fall in to the void.",
          "TeleportBack", "Flag");
        addBoolean("OnlyVoid", "Only goes back if above void.", true);
        addDouble("FallDistance", "FallDistance to set back", 10, 0, 256, 1);
        addDouble("HClip", "Amount to hClip", 0, -20, 20, 1);
        addDouble("VClip", "Amount to vClip", 0, -20, 20, 1);
        addDouble("VAdd", "Amount to add to vertical motion", 0, -20, 20, 1);
        addDouble("HAdd", "Amount to add to horizontal motion", 0, -20, 20, 1);
    }

    private final List<Vec3> lastGround = new ArrayList<>();

    @Override
    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            if (lastGround.size() <= 4)
                lastGround.add(0, new Vec3(player().posX, player().posY, player().posZ));
            else if (player().onGround)
                lastGround.add(0, new Vec3(player().posX, player().posY, player().posZ));
            if (lastGround.size() > 6)
                lastGround.remove(6);
            String mode = STRING("Mode");
            double fallDistance = DOUBLE("fallDistance");
            boolean onlyVoid = BOOLEAN("onlyVoid");
            if (!player().capabilities.allowFlying && player().fallDistance > fallDistance && (!onlyVoid || !BlockUtils.isBlockUnder())) {
                switch (mode) {
                    case "TeleportBack": {
                        player().setPositionAndUpdate(Math.round(lastGround.get(2).xCoord), lastGround.get(2).yCoord, Math.round(lastGround.get(2).zCoord));
                        break;
                    }
                    case "Flag": {
                        MUtil.forward(DOUBLE("hclip"));
                        MUtil.vclip(DOUBLE("vclip"));
                        MUtil.vadd(DOUBLE("vadd"));
                        MUtil.hadd(DOUBLE("hadd"));
                    }
                }
            }
        }
    }
}
