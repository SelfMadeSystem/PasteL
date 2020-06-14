package com.ihl.client.module.hacks.player;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.option.options.*;
import com.ihl.client.util.*;
import net.minecraft.util.Vec3;

import java.util.*;


@EventHandler(events = {EventPlayerUpdate.class})
public class Antivoid extends Module {

    public Antivoid() {
        super("Antivoid", "Teleports you back when you fall in to the void.", Category.PLAYER, "NONE");
        addChoice("Mode", "Teleports you back when you fall in to the void.",
          "TeleportBack", "Flag", "Hypixel");
        addOption(new OptBol("OnlyVoid", "Only goes back if above void.",true){
            @Override
            public boolean visible() {
                String st = module.STRING("mode");
                return st.equalsIgnoreCase("TeleportBack") || st.equalsIgnoreCase("Flag");
            }
        });
        addDouble("FallDistance", "FallDistance to set back", 10, 0, 256, 1);
        addOption(new CustomOption("HClip", "Amount to hClip"));
        addOption(new CustomOption("VClip", "Amount to vClip"));
        addOption(new CustomOption("HAdd", "Amount to hAdd"));
        addOption(new CustomOption("VAdd", "Amount to vAdd"));
    }

    class CustomOption extends OptDbl {
        public CustomOption(String name, String description) {
            super(name, description, 0, -20, 20, 1);
        }
        @Override
        public boolean visible() {
            String st = module.STRING("mode");
            return st.equalsIgnoreCase("Flag");
        }
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
            if (!player().capabilities.allowFlying && !player().capabilities.isCreativeMode && player().fallDistance > fallDistance && (!onlyVoid || !BlockUtils.isBlockUnder())) {
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
                        break;
                    }
                    case "Hypixel": {
                        if (player().fallDistance > fallDistance && !BlockUtils.isBlockUnder() && player().motionY < -0.1) {
                            MUtil.vset(10);
                        } else if (player().motionY > 9) {
                            MUtil.vset(5);
                        }
                    }
                }
            }
        }
    }
}
