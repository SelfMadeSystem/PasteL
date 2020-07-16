package com.ihl.client.module.hacks.combat;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.hacks.combat.aimbases.*;
import com.ihl.client.module.option.Option;
import com.ihl.client.util.*;
import net.minecraft.util.MovingObjectPosition;

import java.util.Arrays;

@EventHandler(events = {EventMouseMove.class, EventRender.class})
public class AimTest extends Module {

    public AimTest() {
        super("AimTest", "Aims at stuff", Category.COMBAT, "NONE");
        Option aW = addChoice("Aim Where", "Aim where", "top", "head", "centre", "feet", "fromTop", "fromBottom", "auto");
        aW.addDouble("Custom", "Value from  top for custom.", 0.4, 0, 3, 0.1);
        addChoice("Priority", "Switch target selection mode", "distance", "health", "direction");
        addDouble("Distance", "Distance to attack entities within", 3.6, 0, 10, 0.1);
        addDouble("Range", "View range to attack entities within", 180, 0, 180, 1);
        addBoolean("Invert yaw", "Enable or Disable if turning the wrong way.", true);
        addBoolean("Invert pitch", "Enable or Disable if turning the wrong way.", false);
        addDouble("Yaw", "Yaw", 5, 1, 10, 0.1);
        addDouble("YawR", "YawR", 1, 0, 5, 0.1);
        addDouble("YawD", "YawD", 1, 0, 1, 0.1);
        addDouble("Pitch", "Pitch", 5, 1, 10, 0.1);
        addDouble("PitchR", "PitchR", 1, 0, 5, 0.1);
        addDouble("PitchD", "PitchD", 1, 0, 1, 0.1);

        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void disable() {
        super.disable();
        mc().mouseHelper.overrideMode = 0;
    }

    public void enable() {
        super.enable();
        /*if (!HelperUtil.inGame()) {
            return;
        }*/
    }

    private int noAim;

    protected void onEvent(Event event) {
        if (event instanceof EventMouseMove) {
            String priority = STRING("priority");
            String aimWhere = STRING("aimwhere");
            double distance = DOUBLE("distance");
            double custom = DOUBLE("aimwhere", "custom");
            int range = INTEGER("range");
            int maxOvershoot = INTEGER("maximumOvershoot");
            boolean invertYaw = BOOLEAN("invertyaw");
            boolean invertPitch = BOOLEAN("invertpitch");

            mc().mouseHelper.overrideMode = 4;

            MouseAimBase.updateRotations(aimWhere, custom, 1);
            MouseAimBase.mouseRots = new int[]{180, 180};
            int[] changeMouse = MouseAimBase.getNextRotations(priority, distance, range, aimWhere, custom, invertYaw, invertPitch, maxOvershoot, 180, 180, new float[]{player().rotationYaw, player().rotationPitch}, 1);
            if (TargetUtil.target == null) {
                mc().mouseHelper.overrideMode = 0;
                return;
            }
            double y1 = DOUBLE("yawR");
            double yaw = (DOUBLE("yaw") + y1) - y1/2;
            double p1 = DOUBLE("pitchR");
            double pitch = (DOUBLE("pitch") + p1) - p1/2;

            mc().mouseHelper.cX1 = MathUtil.toSide(changeMouse[0]) * yaw;
            mc().mouseHelper.cX2 = DOUBLE("yawD");
            mc().mouseHelper.cY1 = MathUtil.toSide(changeMouse[1]) * pitch;
            mc().mouseHelper.cY2 = DOUBLE("pitchD");

            if (!mc().objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY)) noAim++;
            else noAim = 0;

            /*float[] rotations = RUtils.limitAngleChange(new float[]{p.rotationYaw, p.rotationPitch}, to, turnSpeedYaw, turnSpeedPitch);

            final float f = mc().gameSettings.mouseSensitivity * 0.6F + 0.2F;
            final float gcd = f * f * f * 1.2F;

            rotations[0] -= rotations[0] % gcd;
            rotations[1] -= rotations[1] % gcd;*/

            //System.out.printf("%s|%s%n", Arrays.toString(to), Arrays.toString(rotations));

            //p.rotationYaw = rotations[0];
            //p.rotationPitch = rotations[1];
        } else if (event instanceof EventRender) {
            RenderBase.render((EventRender) event);
        }
    }
}
