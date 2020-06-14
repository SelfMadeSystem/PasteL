package com.ihl.client.module.hacks.combat;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.hacks.combat.aimbases.RenderBase;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;
import net.minecraft.entity.*;
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
        addDouble("Predict", "Amount to predict. 0 is none, 1 is motion, 2 double that, and so on.", 1, 0, 10, 0.1);
        addDouble("Max Yaw", "The max amount the yaw's allowed to move", 30, 0, 180, 1);
        addDouble("Min Yaw", "The min amount the yaw's allowed to move", 30, 0, 180, 1);
        addDouble("Max Pitch", "The max amount the pitch's allowed to move", 30, 0, 180, 1);
        addDouble("Min Pitch", "The min amount the pitch's allowed to move", 30, 0, 180, 1);
        addDouble("Turn Speed Yaw", "Speed to aim towards the target", 30, 0, 100, 1);
        addDouble("Turn Speed Yaw Random", "Speed alters", 5, 0, 50, 1);
        addDouble("Turn Speed Pitch", "Speed to aim towards the target", 30, 0, 100, 1);
        addDouble("Turn Speed PitchRandom", "Speed alters", 5, 0, 50, 1);
        Option cps = addOther("Click", "Clicks per second and toggle when attack.");
        cps.addBoolean("Look", "Only attacks when looking at entity.", true);
        cps.addOption(new Option("Min", "Min Delay", new ValueDouble(7, new double[]{1, 20}, 1), Option.Type.NUMBER) {
            @Override
            public void setValueNoTrigger(Object value) {
                double max = module.DOUBLE("click", "max");
                if ((Double) value > max)
                    value = max;
                super.setValueNoTrigger(value);
            }
        });
        cps.addOption(new Option("Max", "Max Delay", new ValueDouble(9, new double[]{1, 20}, 1), Option.Type.NUMBER) {
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
        double predict = DOUBLE("predict");
        int minYaw = INTEGER("minYaw");
        int maxYaw = INTEGER("maxYaw");
        int minPitch = INTEGER("minPitch");
        int maxPitch = INTEGER("maxPitch");
        int range = Option.get(options, "range").INTEGER();
        int turnSpeedYaw = (int)
          ((Math.random() * INTEGER("turnspeedyaw")) - INTEGER("turnspeedyawrandom") / 2);
        int turnSpeedPitch = (int)
          ((Math.random() * INTEGER("turnspeedpitch")) - INTEGER("turnspeedpitchrandom") / 2);
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
            } else if (mouseMode.equalsIgnoreCase("complete")) {
                mc().mouseHelper.overrideMode = 1;
                mc().mouseHelper.overrideX = 0;
                mc().mouseHelper.overrideY = 0;
                p.toPlayer = 1;
            }

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
            float[] rotations = RUtils.limitAngleChange(new float[]{p.rotationYaw, p.rotationPitch}, to, 180, 180);

            int[] changeMouse = new int[]{(int) RUtils.angleDifference(p.rotationYaw, rotations[0]),
              (int) RUtils.angleDifference(p.rotationPitch, rotations[1])};

            if (invertYaw)
                changeMouse[0] *= -1;
            if (invertPitch)
                changeMouse[1] *= -1;
            changeMouse[0] = changeMouse[0] == 0 ? 0 : ((changeMouse[0] < 0 ? -1 : 1) * (Math.abs(changeMouse[0]) < minYaw ? minYaw : (Math.min(Math.abs(changeMouse[0]), maxYaw))));
            changeMouse[1] = changeMouse[1] == 0 ? 0 : ((changeMouse[1] < 0 ? -1 : 1) * (Math.abs(changeMouse[1]) < minPitch ? minPitch : (Math.min(Math.abs(changeMouse[1]), maxPitch))));

            p.deltaX = changeMouse[0] / (51 - turnSpeedYaw);
            p.deltaY = changeMouse[1] / (51 - turnSpeedPitch);

            p.mouseChange();

            if (attack) {
                player().swingItem();
                MovingObjectPosition mop = RaycastUtils.getMouseOver(1, distance, 5);
                Entity hit = (mop == null) ? (target) : ((mop.entityHit == null) ? (target) : (mop.entityHit));
                mc().getNetHandler().addToSendQueue(new C02PacketUseEntity(hit, C02PacketUseEntity.Action.ATTACK));
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
