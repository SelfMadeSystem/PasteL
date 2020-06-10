package com.ihl.client.module.hacks.combat;

import com.ihl.client.Helper;
import com.ihl.client.comparator.*;
import com.ihl.client.event.Event;
import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.hacks.combat.aimbases.MouseAimBase;
import com.ihl.client.module.hacks.misc.Friends;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;

@EventHandler(events = {EventPlayerMotion.class, EventRender.class})
public class Aura extends Module {

    private EntityLivingBase target;
    private float oldYaw, oldPitch, yaw, pitch;
    private final TimerUtil attackTimer = new TimerUtil();

    public Aura(String name, String desc, Category category, String keybind) {
        super(name, desc, category, keybind);
        options.put("invertyaw", new Option(this, "Invert yaw", "Enable or Disable if turning the wrong way.", new ValueBoolean(true), Option.Type.BOOLEAN));
        options.put("invertpitch", new Option(this, "Invert pitch", "Enable or Disable if turning the wrong way.", new ValueBoolean(false), Option.Type.BOOLEAN));
        options.put("mode", new Option(this, "Mode", "Target aim mode", new ValueChoice(2, "directional", "lock", "silent"), Option.Type.CHOICE));
        options.put("priority", new Option(this, "Priority", "Switch target selection mode", new ValueChoice(0, "distance", "health", "direction"), Option.Type.CHOICE));
        options.put("distance", new Option(this, "Distance", "Distance to attack entities within", new ValueDouble(3.6, new double[]{0, 10}, 0.1), Option.Type.NUMBER));
        options.put("range", new Option(this, "Range", "View range to attack entities within", new ValueDouble(180, new double[]{0, 180}, 1), Option.Type.NUMBER));
        options.put("turnspeed", new Option(this, "Turn Speed", "Speed to aim towards the target", new ValueDouble(30, new double[]{0, 180}, 1), Option.Type.NUMBER));
        options.put("delay", new Option(this, "Delay", "Attack delay period (s)", new ValueDouble(0.3, new double[]{0.1, 2}, 0.01), Option.Type.NUMBER));
        options.put("multi", new Option(this, "Multi", "Attack multiple entities simultaneously", new ValueBoolean(true), Option.Type.BOOLEAN));
        options.put("aimwhere", new Option(this, "Aim Where", "Aim where", new ValueChoice(0, "top", "head", "centre", "feet", "fromTop", "fromBottom", "auto"), Option.Type.CHOICE, new Option[]{
          new Option(this, "Custom", "Value from  top for custom.", new ValueDouble(0.4, new double[]{0, 3}, 0.1), Option.Type.NUMBER)
        }));
        options.put("maxovershoot", new Option(this, "Maximum overshoot", "Maximum turn the aimassist is allowed to overshoot", new ValueDouble(0, new double[]{0, 180}, 1), Option.Type.NUMBER));
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void enable() {
        super.enable();
        if (!HelperUtil.inGame()) {
            return;
        }
        CustomMouser.instance.rotationYaw = yaw = Helper.player().rotationYaw;
        CustomMouser.instance.rotationPitch = pitch = Helper.player().rotationPitch;
    }

    protected void onEvent(Event event) {
        String mode = Option.get(options, "mode").CHOICE();
        String priority = Option.get(options, "priority").CHOICE();
        String aimWhere = Option.get(options, "aimwhere").CHOICE();
        double custom = Option.get(options, "aimwhere", "custom").DOUBLE();
        double distance = Option.get(options, "distance").DOUBLE();
        int range = Option.get(options, "range").INTEGER();
        int turnSpeed = Option.get(options, "turnspeed").INTEGER();
        int maxOvershoot = Option.get(options, "maxovershoot").INTEGER();
        double delay = Option.get(options, "delay").DOUBLE();
        boolean multi = Option.get(options, "multi").BOOLEAN();
        boolean invertYaw = Option.get(options, "invertyaw").BOOLEAN();
        boolean invertPitch = Option.get(options, "invertpitch").BOOLEAN();

        if (event instanceof EventPlayerMotion) {
            EventPlayerMotion e = (EventPlayerMotion) event;
            if (e.type == Event.Type.PRE) {
                oldYaw = Helper.player().rotationYaw;
                oldPitch = Helper.player().rotationPitch;
                target = null;

                targetEntity(mode, priority, distance, range, delay);

                if (target != null) {
                    CustomMouser.instance.active = true;
                    CustomMouser.instance.toPlayer = mode.equalsIgnoreCase("lock");
                } else {
                    CustomMouser.instance.active = false;
                }

                /*if (target != null) {
                    float[] rotationTo = BasicAimBase.getAimTo(360, aimWhere, custom, new float[]{(float) yaw, (float) pitch});

                    newYaw = yaw + rotationTo[0];
                    newPitch = pitch + rotationTo[1];
                } else {
                    newYaw = oldYaw;
                    newPitch = oldPitch;
                }*/

                /*double yawDifference = MathUtil.angleDifference(newYaw, yaw);
                double pitchDifference = MathUtil.angleDifference(newPitch, pitch);

                yaw += yawDifference > turnSpeed ? turnSpeed : yawDifference < -turnSpeed ? -turnSpeed : yawDifference;
                pitch += pitchDifference > turnSpeed ? turnSpeed : pitchDifference < -turnSpeed ? -turnSpeed : pitchDifference;*/
                CustomMouser cm = CustomMouser.instance;
                MouseAimBase.updateRotations(aimWhere, custom);
                int[] rot = MouseAimBase.getNextRotations(priority, distance, range, aimWhere, custom, invertYaw, invertPitch, maxOvershoot, turnSpeed, turnSpeed, new float[]{yaw, pitch});
                //ChatUtil.send(Arrays.toString(rot));
                cm.deltaX = rot[0];
                cm.deltaY = rot[1];
                cm.mouseChange();
                yaw = cm.rotationYaw;
                pitch = cm.rotationPitch;
                //cm.toPlayer();
                player().rotationYawHead = yaw;
            } else if (e.type == Event.Type.POST) {
                if (target != null) {
                    if (attackTimer.isTime(delay)) {
                        float[] rotationTo = EntityUtil.getRotationToEntity(target);
                        float[] rotationFrom = new float[]{yaw, pitch};
                        double rotationDifference = RUtils.getRotationDifference(rotationTo, rotationFrom);
                        player().rotationYawHead = yaw;

                        if (rotationDifference < 35) {
                            Helper.player().swingItem();
                            Helper.controller().attackEntity(Helper.player(), target);

                            if (multi) {
                                for (Object object : Helper.world().getLoadedEntityList()) {
                                    if (object instanceof EntityLivingBase && object != Helper.player()) {
                                        EntityLivingBase entity = (EntityLivingBase) object;

                                        if (isLiable(entity, distance, 35)) {
                                            Helper.player().swingItem();
                                            Helper.controller().attackEntity(Helper.player(), entity);
                                        }
                                    }
                                }
                            }

                            attackTimer.reset();
                        }
                    }
                }

                ChatUtil.send(yaw + " " + pitch);
                if (!mode.equalsIgnoreCase("lock")) {
                    Helper.player().rotationYaw = oldYaw;
                    Helper.player().rotationPitch = oldPitch;
                } else {
                    Helper.player().rotationYaw = yaw;
                    Helper.player().rotationPitch = pitch;
                }
            }
        } else if (event instanceof EventRender) {
            EventRender e = (EventRender) event;
            if (e.type == Event.Type.PRE) {
                {
                    Vec3 rotation = RUtils.getVectorForRotation(pitch, yaw);
                    RenderUtil3D.line(0, Helper.player().getEyeHeight(), 0, rotation.xCoord, rotation.yCoord, rotation.zCoord, Color.white.getRGB(), 2);
                }
                if (target != null) {
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GlStateManager.disableDepth();

                    if (!multi) {
                        RenderUtil3D.box(target, 0x80FF0000, 1);
                    } else {
                        float tempYaw = Helper.player().rotationYaw;
                        float tempPitch = Helper.player().rotationPitch;
                        Helper.player().rotationYaw = yaw;
                        Helper.player().rotationPitch = pitch;
                        for (Object object : Helper.world().getLoadedEntityList()) {
                            if (object instanceof EntityLivingBase) {
                                EntityLivingBase entity = (EntityLivingBase) object;

                                if (isLiable(entity, distance, 35)) {
                                    RenderUtil3D.box(entity, 0x80FF0000, 1);
                                }
                            }
                        }
                        Helper.player().rotationYaw = tempYaw;
                        Helper.player().rotationPitch = tempPitch;
                    }

                    GL11.glPopAttrib();
                }
            }
        }
    }

    private void targetEntity(String mode, String priority, double distance, double range, double delay) {
        List<EntityLivingBase> entities = new ArrayList<>();
        for (Object object : Helper.world().getLoadedEntityList()) {
            if (object instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) object;

                if (isLiable(entity, distance, range)) {
                    entities.add(entity);
                }
            }
        }

        switch (priority) {
            case "distance":
                entities.sort(new EntityDistanceComparator(Helper.player()));
                break;
            case "health":
                entities.sort(new EntityHealthComparator());
                break;
            case "direction":
                entities.sort(new EntityCrosshairComparator(Helper.player()));
                break;
        }

        if (!entities.isEmpty()) {
            target = entities.get(0);
        }
    }

    private boolean isLiable(EntityLivingBase entity, double distance, double range) {
        float[] rotationTo = EntityUtil.getRotationToEntity(entity);
        float[] rotationFrom = new float[]{CustomMouser.instance.rotationYaw, CustomMouser.instance.rotationPitch};
        double rotationDifference = RUtils.getRotationDifference(rotationTo, rotationFrom);

        return (entity != Helper.player() &&
          !entity.getName().equals(Helper.player().getName()) &&
          entity.isEntityAlive() &&
          !Friends.isFriend(entity.getName()) &&
          Helper.player().getDistanceToEntity(entity) < distance &&
          rotationDifference < range);
    }
}
