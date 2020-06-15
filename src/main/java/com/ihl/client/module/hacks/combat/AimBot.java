package com.ihl.client.module.hacks.combat;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.hacks.combat.aimbases.RenderBase;
import com.ihl.client.module.option.Option;
import com.ihl.client.util.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;

@EventHandler(events = {EventPlayerUpdate.class, EventRender.class})
public class AimBot extends Module {

    public AimBot() {
        super("AimBot", "Aims at stuff", Category.COMBAT, "NONE");
        addChoice("Aim When", "Aim when", "always", "mouse");
        Option aW = addChoice("Aim Where", "Aim where", "top", "head", "centre", "feet", "fromTop", "fromBottom", "auto");
        aW.addDouble("Custom", "Value from  top for custom.", 0.4, 0, 3, 0.1);
        addChoice("Mouse Mode", "How it overrides your mouse", "silent", "add", "complete");
        addChoice("Priority", "Switch target selection mode", "distance", "health", "direction");
        addDouble("Distance", "Distance to attack entities within", 3.6, 0, 10, 0.1);
        addDouble("Range", "View range to attack entities within", 180, 0, 180, 1);
        addBoolean("Invert yaw", "Enable or Disable if turning the wrong way.", true);
        addBoolean("Invert pitch", "Enable or Disable if turning the wrong way.", false);
        addDouble("Predict", "Amount to predict. 0 is none, 1 is motion, 2 double that, and so on.", 1, 0, 10, 0.1);
        addDouble("Turn Speed Yaw", "Speed to aim towards the target", 30, 0, 180, 1);
        addDouble("Turn Speed Yaw Random", "Speed alters", 5, 0, 180, 1);
        addDouble("Turn Speed Pitch", "Speed to aim towards the target", 30, 0, 180, 1);
        addDouble("Turn Speed  PitchRandom", "Speed alters", 5, 0, 180, 1);
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

    protected void onEvent(Event event) {
        String aimWhen = Option.get(options, "aimwhen").CHOICE();
        String aimWhere = Option.get(options, "aimwhere").CHOICE();
        String mouseMode = Option.get(options, "mousemode").CHOICE();
        //String mode = Option.get(options, "mode").CHOICE();
        String priority = Option.get(options, "priority").CHOICE();
        double distance = Option.get(options, "distance").DOUBLE();
        double custom = Option.get(options, "aimwhere", "custom").DOUBLE();
        double predict = DOUBLE("predict");
        int range = Option.get(options, "range").INTEGER();
        int turnSpeedYaw = (int)
          ((Math.random() * Option.get(options, "turnspeedyaw").INTEGER()) - Option.get(options, "turnspeedyawrandom").INTEGER() / 2);
        int turnSpeedPitch = (int)
          ((Math.random() * Option.get(options, "turnspeedpitch").INTEGER()) - Option.get(options, "turnspeedpitchrandom").INTEGER() / 2);
        boolean invertYaw = Option.get(options, "invertyaw").BOOLEAN();
        boolean invertPitch = Option.get(options, "invertpitch").BOOLEAN();

        if (event instanceof EventPlayerUpdate) {
            TargetUtil.targetEntity(priority, distance, range);
            EntityLivingBase target = TargetUtil.target;
            EntityPlayerSP p = mc().thePlayer;
            if (target == null) {
                mc().mouseHelper.overrideMode = 0;
                return;
            }
            if (mouseMode.equalsIgnoreCase("add"))
                mc().mouseHelper.overrideMode = 2;
            else if (mouseMode.equalsIgnoreCase("complete"))
                mc().mouseHelper.overrideMode = 1;

            mc().mouseHelper.overrideX = 0;
            mc().mouseHelper.overrideY = 0;

            if (aimWhen.equalsIgnoreCase("mouse"))
                if (!mc().mouseHelper.moving)
                    return;

            float[] to;
            switch (aimWhere) {
                case "top":
                    to = RUtils.getNeededRotations(RUtils.getTop(target.getEntityBoundingBox()), predict);
                    break;
                case "head":
                    to = RUtils.getNeededRotations(RUtils.getHead(target), predict);
                    break;
                case "center":
                    to = RUtils.getNeededRotations(RUtils.getCenter(target.getEntityBoundingBox()), predict);
                    break;
                case "feet":
                    to = RUtils.getNeededRotations(RUtils.getBottom(target.getEntityBoundingBox()), predict);
                    break;
                case "fromTop":
                    to = RUtils.getNeededRotations(RUtils.getFromTop(target.getEntityBoundingBox(), custom), predict);
                    break;
                case "auto":
                    to = RUtils.getNeededRotations(RUtils.searchCenter(target.getEntityBoundingBox(), false, false, predict, false).getVec(), predict);
                    break;
                default:
                    to = RUtils.getNeededRotations(RUtils.getFromBottom(target.getEntityBoundingBox(), custom), predict);
            }
            float[] rotations = RUtils.limitAngleChange(new float[]{p.rotationYaw, p.rotationPitch}, to, turnSpeedYaw, turnSpeedPitch);

            /*final float f = mc().gameSettings.mouseSensitivity * 0.6F + 0.2F;
            final float gcd = f * f * f * 1.2F;*/

            int[] changeMouse = new int[]{(int) RUtils.angleDifference(mc().thePlayer.rotationYaw, rotations[0]),
              (int) RUtils.angleDifference(mc().thePlayer.rotationPitch, rotations[1])};

            //System.out.println(Arrays.toString(changeMouse) + "|" + mc().gameSettings.mouseSensitivity);

            if (invertYaw)
                changeMouse[0] *= -1;
            if (invertPitch)
                changeMouse[1] *= -1;

            mc().mouseHelper.overrideX = changeMouse[0] / 5D;
            mc().mouseHelper.overrideY = changeMouse[1] / 5D;

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
