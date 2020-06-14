package com.ihl.client.module.hacks.combat;

import com.ihl.client.Client;
import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.Category;
import com.ihl.client.module.hacks.combat.aimbases.*;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;

@EventHandler(events = {EventPlayerUpdate.class, EventRender.class})
public class AimAssist extends Module {

    public AimAssist() {
        super("AimAssist", "Helps your aim at stuff", Category.COMBAT, "NONE");
        options.put("aimwhen", new Option("Aim When", "Aim when", new ValueChoice(0, "always", "mouse"), Option.Type.CHOICE));
        options.put("aimwhere", new Option("Aim Where", "Aim where", new ValueChoice(0, "top", "head", "centre", "feet", "fromTop", "fromBottom", "auto"), Option.Type.CHOICE, new Option("Custom", "Value from  top for custom.", new ValueDouble(0.4, new double[]{0, 3}, 0.1), Option.Type.NUMBER)));
        options.put("mousemode", new Option("Mouse Mode", "How it overrides your mouse", new ValueChoice(0, "add", "complete"), Option.Type.CHOICE));
        options.put("priority", new Option("Priority", "Switch target selection mode", new ValueChoice(0, "distance", "health", "direction"), Option.Type.CHOICE));
        options.put("distance", new Option("Distance", "Distance to attack entities within", new ValueDouble(3.6, new double[]{0, 10}, 0.1), Option.Type.NUMBER));
        options.put("range", new Option("Range", "View range to attack entities within", new ValueDouble(180, new double[]{0, 180}, 1), Option.Type.NUMBER));
        options.put("invertyaw", new Option("Invert yaw", "Enable or Disable if turning the wrong way.", new ValueBoolean(true), Option.Type.BOOLEAN));
        options.put("invertpitch", new Option("Invert pitch", "Enable or Disable if turning the wrong way.", new ValueBoolean(false), Option.Type.BOOLEAN));
        options.put("modeType", new Option("Mode Type", "The mode of how it selects values", new ValueChoice(0, "list", "random", "write"), Option.Type.CHOICE));
        options.put("absAmount", new Option("Absolute Amount", "The amount of values to make absolute (positive) when recording.", new ValueDouble(30, new double[]{0, 100}, 1), Option.Type.NUMBER));
        addDouble("Predict", "Amount to predict. 0 is none, 1 is motion, 2 double that, and so on.", 1, 0, 10, 0.1);
        options.put("turntime", new Option("Turn Time", "Time between stuff", new ValueDouble(30, new double[]{1, 200}, 1), Option.Type.NUMBER));
        options.put("turnspeedyaw", new Option("Turn Speed Yaw", "Speed to aim towards the target", new ValueDouble(30, new double[]{0, 200}, 1), Option.Type.NUMBER));
        //options.put("turnspeedyawrandom", new Option("Turn Speed Yaw Random", "Speed alters", new ValueDouble(5, new double[]{0, 180}, 1), Option.Type.NUMBER));
        options.put("turnspeedpitch", new Option("Turn Speed Pitch", "Speed to aim towards the target", new ValueDouble(30, new double[]{0, 200}, 1), Option.Type.NUMBER));
        options.put("maxovershoot", new Option("Maximum overshoot", "Maximum turn the aimassist is allowed to overshoot", new ValueDouble(10, new double[]{0, 180}, 1), Option.Type.NUMBER));
        //options.put("turnspeedpitchrandom", new Option("Turn Speed  PitchRandom", "Speed alters", new ValueDouble(5, new double[]{0, 180}, 1), Option.Type.NUMBER));

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
        if (event instanceof EventPlayerUpdate) {
            String mode = Option.get(options, "modeType").CHOICE();
            if (mode.equalsIgnoreCase("write")) {
                int absAmount = Option.get(options, "absAmount").INTEGER();
                if (mc().mouseHelper.tickX != 0 || mc().mouseHelper.tickY != 0) {
                    Filer filer = new Filer("aimAssistValues", Client.NAME);
                    int x = absAmount == 0 ? mc().mouseHelper.tickX : -Math.abs(mc().mouseHelper.tickX);
                    int y = absAmount == 0 ? mc().mouseHelper.tickY : -Math.abs(mc().mouseHelper.tickY);
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
            String aimWhen = Option.get(options, "aimwhen").CHOICE();
            String mouseMode = Option.get(options, "mousemode").CHOICE();
            String priority = Option.get(options, "priority").CHOICE();
            String aimWhere = Option.get(options, "aimwhere").CHOICE();
            double distance = Option.get(options, "distance").DOUBLE();
            double custom = Option.get(options, "aimwhere", "custom").DOUBLE();
            double predict = DOUBLE("predict");
            int turnTime = Option.get(options, "turntime").INTEGER();
            int range = Option.get(options, "range").INTEGER();
            int maxOvershoot = Option.get(options, "maxovershoot").INTEGER();
            boolean invertYaw = Option.get(options, "invertyaw").BOOLEAN();
            boolean invertPitch = Option.get(options, "invertpitch").BOOLEAN();
            if (mouseMode.equalsIgnoreCase("add"))
                mc().mouseHelper.overrideMode = 2;
            else if (mouseMode.equalsIgnoreCase("complete"))
                mc().mouseHelper.overrideMode = 1;

            if (aimWhen.equalsIgnoreCase("mouse") && !mc().mouseHelper.moving)
                return;
//            TargetUtil.targetEntity(priority, distance, range);
//            EntityLivingBase target = TargetUtil.target;
//            if (target == null) {
//                mc().mouseHelper.overrideMode = 0;
//                return;
//            }
//            if (mouseMode.equalsIgnoreCase("add"))
//                mc().mouseHelper.overrideMode = 2;
//            else if (mouseMode.equalsIgnoreCase("complete"))
//                mc().mouseHelper.overrideMode = 1;
//
//            mc().mouseHelper.overrideX = 0;
//            mc().mouseHelper.overrideY = 0;
//
//            if (aimWhen.equalsIgnoreCase("mouse"))
//                if (!mc().mouseHelper.moving)
//                    return;
//
//            int turnSpeedYaw;
//            int turnSpeedPitch;
//            if (player().ticksExisted % options.get("turntime").INTEGER() == 0)
//                updateRotations();
//            {
//                Filer filer = new Filer("aimAssistValues", Client.NAME);
//                List<String> list = filer.read();
//                if (list.size() == 0) {
//                    ChatUtil.send("Write values first!!!!");
//                    return;
//                }
//                //ChatUtil.send(prev + " " + list.size());
//                if (list.size() <= prev)
//                    prev = 0;
//                int select = mode.equalsIgnoreCase("list") ? prev : (int) Math.floor(Math.random() * list.size());
//                String[] split = list.get(select).split(":");
//                turnSpeedYaw = Integer.parseInt(split[0]);
//                turnSpeedPitch = Integer.parseInt(split[1]);
//                prev++;
//            }
//
//            /*final float f = mc().gameSettings.mouseSensitivity * 0.6F + 0.2F;
//            final float gcd = f * f * f * 1.2F;*/
//
//            //ChatUtil.send(prev+""/*RUtils.angleDifference(mc().thePlayer.rotationYaw, rotations[0]) + " " + RUtils.angleDifference(mc().thePlayer.rotationPitch, rotations[1]) + " " +
//            //  turnSpeedYaw + " " + turnSpeedPitch*/);
//
//            if (rotations == null)
//                return;
//
//            int[] changeMouse = new int[]{turnSpeedYaw * (rotations[0] < 0 ? -1 : 1),
//              turnSpeedPitch * (rotations[1] < 0 ? -1 : 1)};/*new int[]{(int) RUtils.angleDifference(mc().thePlayer.rotationYaw, rotations[0]),
//              (int) RUtils.angleDifference(mc().thePlayer.rotationPitch, rotations[1])};*/
//
//            //System.out.println(Arrays.toString(changeMouse) + "|" + mc().gameSettings.mouseSensitivity);
//
//            if (invertYaw)
//                changeMouse[0] *= -1;
//            if (invertPitch)
//                changeMouse[1] *= -1;
//            changeMouse[0] *= options.get("turnspeedyaw").DOUBLE() / 100;
//            changeMouse[1] *= options.get("turnspeedpitch").DOUBLE() / 100;
//
//            float[] fts0 = getRotations(360);
//            //float[] fts1 = new float[]{RUtils.angleDifference(player().rotationYaw, fts0[0]), RUtils.angleDifference(player().rotationPitch, fts0[1])};
//            if (Math.abs(fts0[0]) + maxOvershoot < Math.abs(changeMouse[0]))
//                changeMouse[0] = (int) (Math.abs(fts0[0]) * (changeMouse[0] > 0 ? 1 : -1));
//            if (Math.abs(fts0[1]) + maxOvershoot < Math.abs(changeMouse[1]))
//                changeMouse[1] = (int) (Math.abs(fts0[1]) * (changeMouse[1] > 0 ? 1 : -1));
            if (player().ticksExisted % turnTime == 0) MouseAimBase.updateRotations(aimWhere, custom, predict);
            int[] changeMouse = MouseAimBase.getNextRotations(priority, distance, range, aimWhere, custom, mode, invertYaw, invertPitch, maxOvershoot, predict);


            mc().mouseHelper.overrideX = changeMouse[0];
            mc().mouseHelper.overrideY = changeMouse[1];

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
