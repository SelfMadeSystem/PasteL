package com.ihl.client.module.hacks.combat;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.hacks.combat.aimbases.RenderBase;
import com.ihl.client.module.option.Option;
import com.ihl.client.util.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;

@EventHandler(events = {EventPlayerUpdate.class, EventRender.class, EventPacket.class})
public class Aura extends Module {

    public Aura() {
        super("Aura", "Automatically hits stuff around you.", Category.COMBAT, "NONE");
        Option aW = addChoice("Aim Where", "Aim where", "top", "head", "centre", "feet", "fromTop", "fromBottom", "auto");
        aW.addDouble("Custom", "Value from  top for custom.", 0.4, 0, 3, 0.1);
        addChoice("Mouse Mode", "How it overrides your mouse", "silent", "add", "complete");
        addChoice("Priority", "Switch target selection mode", "distance", "health", "direction");
        addDouble("Distance", "Distance to attack entities within", 3.6, 0, 10, 0.1);
        addDouble("Range", "View range to attack entities within", 180, 0, 180, 1);
        addBoolean("Invert yaw", "Enable or Disable if turning the wrong way.", true);
        addBoolean("Invert pitch", "Enable or Disable if turning the wrong way.", false);
        addDouble("Turn Speed Yaw", "Speed to aim towards the target", 30, 0, 180, 1);
        addDouble("Turn Speed Yaw Random", "Speed alters", 5, 0, 180, 1);
        addDouble("Turn Speed Pitch", "Speed to aim towards the target", 30, 0, 180, 1);
        addDouble("Turn Speed  PitchRandom", "Speed alters", 5, 0, 180, 1);
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void disable() {
        super.disable();
        mc().mouseHelper.overrideMode = 0;
        CustomMouser p = CustomMouser.instance;
        p.active = false;
        p.deltaX = 0;
        p.deltaY = 0;
    }

    public void enable() {
        super.enable();
        if (HelperUtil.inGame()) {
            CustomMouser.instance.fromPlayer();
        }
    }

    protected void onEvent(Event event) {
        String aimWhere = Option.get(options, "aimwhere").CHOICE();
        String mouseMode = Option.get(options, "mousemode").CHOICE();
        //String mode = Option.get(options, "mode").CHOICE();
        String priority = Option.get(options, "priority").CHOICE();
        double distance = Option.get(options, "distance").DOUBLE();
        double custom = Option.get(options, "aimwhere", "custom").DOUBLE();
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
            CustomMouser p = CustomMouser.instance;
            if (target == null) {
                p.toPlayer = 0;
                p.active = false;
                p.deltaX = 0;
                p.deltaY = 0;
                mc().mouseHelper.overrideMode = 0;
                mc().mouseHelper.overrideX = 0;
                mc().mouseHelper.overrideY = 0;
                return;
            }
            p.active = true;
            if (mouseMode.equalsIgnoreCase("silent"))
                p.toPlayer = 0;
            else if (mouseMode.equalsIgnoreCase("add")) {
                p.toPlayer = 2;
                p.fromPlayer();
            } else if (mouseMode.equalsIgnoreCase("complete"))
                p.toPlayer = 1;

            float[] to;
            switch (aimWhere) {
                case "top":
                    to = RUtils.getNeededRotations(RUtils.getTop(target.getEntityBoundingBox()), true);
                    break;
                case "head":
                    to = RUtils.getNeededRotations(RUtils.getHead(target), true);
                    break;
                case "center":
                    to = RUtils.getNeededRotations(RUtils.getCenter(target.getEntityBoundingBox()), true);
                    break;
                case "feet":
                    to = RUtils.getNeededRotations(RUtils.getBottom(target.getEntityBoundingBox()), true);
                    break;
                case "fromTop":
                    to = RUtils.getNeededRotations(RUtils.getFromTop(target.getEntityBoundingBox(), custom), true);
                    break;
                case "auto":
                    to = RUtils.getNeededRotations(RUtils.searchCenter(target.getEntityBoundingBox(), false, false, true, false).getVec(), true);
                    break;
                default:
                    to = RUtils.getNeededRotations(RUtils.getFromBottom(target.getEntityBoundingBox(), custom), true);
            }
            float[] rotations = RUtils.limitAngleChange(new float[]{p.rotationYaw, p.rotationPitch}, to, turnSpeedYaw, turnSpeedPitch);

            /*final float f = mc().gameSettings.mouseSensitivity * 0.6F + 0.2F;
            final float gcd = f * f * f * 1.2F;*/

            int[] changeMouse = new int[]{(int) RUtils.angleDifference(p.rotationYaw, rotations[0]),
              (int) RUtils.angleDifference(p.rotationPitch, rotations[1])};

            //System.out.println(Arrays.toString(changeMouse) + "|" + mc().gameSettings.mouseSensitivity);

            if (invertYaw)
                changeMouse[0] *= -1;
            if (invertPitch)
                changeMouse[1] *= -1;

            p.deltaX = changeMouse[0] / 5;
            p.deltaY = changeMouse[1] / 5;

            p.mouseChange();

            //ChatUtil.send(p.deltaX + "  " + p.deltaY + "  " + p.toPlayer + " " + p.active);

            /*
            todo:
            if (cps is finished thingy) {
              if (mop.entity thingy lol) {
                hit() kthx;
              }
            }
             */

            MovingObjectPosition mop = RaycastUtils.getMouseOver(1, distance, 5);

            ChatUtil.send(String.valueOf(mop));

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
        } else if (event instanceof EventPacket) {
            if (event.type.equals(Event.Type.SEND)) {
                Packet packet = ((EventPacket) event).packet;
                if (packet instanceof C03PacketPlayer) {
                    C03PacketPlayer c03 = (C03PacketPlayer) packet;
                    if (c03.getRotating()) {
                        c03.yaw = CustomMouser.instance.rotationYaw;
                        c03.pitch = CustomMouser.instance.rotationPitch;
                        ((EventPacket) event).packet = c03;
                    }
                }
            }
        }
    }
}
