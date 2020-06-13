package com.ihl.client.module.hacks.combat;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.hacks.combat.aimbases.RenderBase;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.MovingObjectPosition;

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
        Option cps = addOther("Click", "Clicks per second and toggle when attack.");
        cps.addBoolean("Look", "Only attacks when looking at entity.", true);
        cps.addOption(new Option(this, "Min", "Min Delay", new ValueDouble(7, new double[]{1, 20}, 1), Option.Type.NUMBER) {
            @Override
            public void setValueNoTrigger(Object value) {
                double max = module.DOUBLE("click", "max");
                if ((Double) value > max)
                    value = max;
                super.setValueNoTrigger(value);
            }
        });
        cps.addOption(new Option(this, "Max", "Max Delay", new ValueDouble(9, new double[]{1, 20}, 1), Option.Type.NUMBER) {
            @Override
            public void setValueNoTrigger(Object value) {
                double min = module.DOUBLE("click", "min");
                if ((Double) value < min)
                    value = min;
                super.setValueNoTrigger(value);
            }
        });
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

    private long delay;
    private long lastSwing;
    private boolean attack;

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
            if (mouseMode.equalsIgnoreCase("silent")) { // TODO: 2020-06-12 Rotation Strafe
                mc().mouseHelper.overrideMode = 0;
                mc().mouseHelper.overrideX = 0;
                mc().mouseHelper.overrideY = 0;
                p.toPlayer = 0;
            } else if (mouseMode.equalsIgnoreCase("add")) {
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

            int[] changeMouse = new int[]{(int) RUtils.angleDifference(p.rotationYaw, rotations[0]),
              (int) RUtils.angleDifference(p.rotationPitch, rotations[1])};

            if (invertYaw)
                changeMouse[0] *= -1;
            if (invertPitch)
                changeMouse[1] *= -1;

            p.deltaX = changeMouse[0] / 5;
            p.deltaY = changeMouse[1] / 5;

            p.mouseChange();

            if (attack) {
                player().swingItem();
                mc().getNetHandler().addToSendQueue(new C02PacketUseEntity(TargetUtil.target, C02PacketUseEntity.Action.ATTACK));
                attack = false;
            }
        } else if (event instanceof EventRender) {
            RenderBase.render((EventRender) event);
            if (System.currentTimeMillis() - lastSwing >= delay) {
                if (!BOOLEAN("click", "look")) {
                    if (TargetUtil.target != null) {
                        attack = true;
                        lastSwing = System.currentTimeMillis();
                        delay = TimeUtils.randomClickDelay(INTEGER("click", "min"), INTEGER("click", "max"));
                    }
                } else {
                    MovingObjectPosition mop = RaycastUtils.getMouseOver(1, distance, 5);
                    if (mop != null && mop.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY)) {
                        if (TargetUtil.target != null) {
                            attack = true;
                            lastSwing = System.currentTimeMillis();
                            delay = TimeUtils.randomClickDelay(INTEGER("click", "min"), INTEGER("click", "max"));
                        }
                    }
                }
            }
        } else if (event instanceof EventPacket) {
            if (event.type.equals(Event.Type.SEND)) {
                Packet packet = ((EventPacket) event).packet;
                if (packet instanceof C03PacketPlayer) {
                    C03PacketPlayer c03 = (C03PacketPlayer) packet;
                    if (c03.getRotating() && STRING("Mouse Mode").equalsIgnoreCase("silent")) {
                        c03.yaw = CustomMouser.instance.rotationYaw;
                        c03.pitch = CustomMouser.instance.rotationPitch;
                        ((EventPacket) event).packet = c03;
                    }
                }
            }
        }
    }
}
