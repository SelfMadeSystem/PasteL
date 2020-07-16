package com.ihl.client.module.hacks.combat;

import com.ihl.client.Client;
import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.hacks.combat.aimbases.*;
import com.ihl.client.module.option.Option;
import com.ihl.client.util.*;

import java.util.Arrays;

@EventHandler(events = {EventPlayerUpdate.class, EventRender.class, EventMouseMove.class})
public class AimAssist extends Module {

    public AimAssist() {
        super("AimAssist", "Helps your aim at stuff", Category.COMBAT, "NONE");
        addChoice("Aim When", "Aim when", "always", "mouse");
        Option aW = addChoice("Aim Where", "Aim where", "top", "head", "centre", "feet", "fromTop", "fromBottom", "auto");
        aW.addDouble("Custom", "Value from  top for custom.", 0.4, 0, 3, 0.1);
        addChoice("Mouse Mode", "How it overrides your mouse", "add", "complete");
        addChoice("Priority", "Switch target selection mode", "distance", "health", "direction");
        addDouble("Distance", "Distance to attack entities within", 3.6, 0, 10, 0.1);
        addInteger("Range", "View range to attack entities within", 180, 0, 180);
        addBoolean("Invert yaw", "Enable or Disable if turning the wrong way.", true);
        addBoolean("Invert pitch", "Enable or Disable if turning the wrong way.", false);
        addChoice("Mode Type", "The mode of how it selects values", "list", "random", "write");
        addInteger("Absolute Amount", "The amount of values to make absolute (positive) when recording.", 30, 0, 100);
        addDouble("Predict", "Amount to predict. 0 is none, 1 is motion, 2 double that, and so on.", 1, 0, 10, 0.1);
        addInteger("Turn Time", "Time between stuff", 30, 1, 200);
        addInteger("Yaw Speed", "Speed to aim towards the target", 30, 0, 200);
        addInteger("Yaw Speed Alter", "Speed to aim towards the target", 30, 0, 200);
        addInteger("Pitch Speed", "Speed to aim towards the target", 30, 0, 200);
        addInteger("Pitch Speed Alter", "Speed to aim towards the target", 30, 0, 200);
        addInteger("Maximum overshoot", "Maximum turn the aimassist is allowed to overshoot", 10, 0, 180);

        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void disable() {
        super.disable();
        mc().mouseHelper.overrideMode = 0;
    }

    public void enable() {
        super.enable();
//        if (!HelperUtil.inGame())
//            return;
        //MouseAimBase.updateRotations(aimWhere, custom);
    }

    protected void onEvent(Event event) {
        if (event instanceof EventMouseMove) {
            String mode = STRING("modeType");
            if (mode.equalsIgnoreCase("write")) {
                int absAmount = INTEGER("absAmount");
                if (mc().mouseHelper.trueX != 0 || mc().mouseHelper.trueY != 0) {
                    Filer filer = new Filer("aimAssistValues", Client.NAME);
                    int x = absAmount == 0 ? mc().mouseHelper.trueX : -Math.abs(mc().mouseHelper.trueX);
                    int y = absAmount == 0 ? mc().mouseHelper.trueY : -Math.abs(mc().mouseHelper.trueY);
                    if (absAmount > 0) {
                        if (Math.random() > absAmount / 100f)
                            x *= -1;
                        if (Math.random() > absAmount / 100f)
                            y *= -1;
                    }
                    ChatUtil.send(x + ":" + y);
                    filer.write(x + ":" + y);
                }
                return;
            }
            String aimWhen = STRING("aimwhen");
            String mouseMode = STRING("mousemode");
            String priority = STRING("priority");
            String aimWhere = STRING("aimwhere");
            double ya = DOUBLE("yawSpeedAlter");
            double yaw = DOUBLE("yawSpeed") + (Math.random() * ya) - ya / 2;
            double pa = DOUBLE("pitchSpeedAlter");
            double pitch = DOUBLE("pitchSpeed") + (Math.random() * pa) - pa / 2;
            double distance = DOUBLE("distance");
            double custom = DOUBLE("aimwhere", "custom");
            double predict = DOUBLE("predict");
            int turnTime = INTEGER("turntime");
            int range = INTEGER("range");
            int maxOvershoot = INTEGER("maximumOvershoot");
            boolean invertYaw = BOOLEAN("invertyaw");
            boolean invertPitch = BOOLEAN("invertpitch");

            if (mouseMode.equalsIgnoreCase("add"))
                mc().mouseHelper.overrideMode = 2;
            else if (mouseMode.equalsIgnoreCase("complete"))
                mc().mouseHelper.overrideMode = 1;

            if (aimWhen.equalsIgnoreCase("mouse") && !mc().mouseHelper.moving)
                return;
            if (player().ticksExisted % turnTime == 0) MouseAimBase.updateRotations(aimWhere, custom, predict);
            int[] changeMouse = MouseAimBase.getNextRotations(priority, distance, range, aimWhere, custom, mode, invertYaw, invertPitch, maxOvershoot, predict, yaw, pitch);
            if (TargetUtil.target == null) {
                mc().mouseHelper.overrideMode = 0;
                return;
            }

            mc().mouseHelper.overrideX = changeMouse[0];
            mc().mouseHelper.overrideY = changeMouse[1];

        } else if (event instanceof EventRender) {
            RenderBase.render((EventRender) event);
        }
    }
}
