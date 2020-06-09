package com.ihl.client.module.hacks.movement;

import com.ihl.client.Helper;
import com.ihl.client.event.*;
import com.ihl.client.module.hacks.Category;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.Option;
import com.ihl.client.module.option.ValueDouble;
import com.ihl.client.module.option.ValueString;
import com.ihl.client.util.HelperUtil;
import net.minecraft.client.entity.EntityPlayerSP;

@EventHandler(events = {EventPlayerMotion.class})
public class Step extends Module {

    public Step() {
        super("Step", "Step up blocks like stairs", Category.MOVEMENT, "NONE");
        options.put("start", new Option(this, "Start", "Values when starting step", new ValueString(""), Option.Type.OTHER, new Option[]{
          new Option(this, "Timer", "Timer modifier. Default: 1", new ValueDouble(1, new double[]{0.1, 5}, 0.01), Option.Type.NUMBER),
          new Option(this, "Clip", "Your initial jump clip. Default: 0", new ValueDouble(0, new double[]{0, 2}, 0.00001), Option.Type.NUMBER),
          new Option(this, "Vertical", "Your initial jump motion. Default: 0.42", new ValueDouble(0.42, new double[]{0, 2}, 0.00001), Option.Type.NUMBER),
        }));
        options.put("during", new Option(this, "During", "Values during step", new ValueString(""), Option.Type.OTHER, new Option[]{
          new Option(this, "Multiplier", "Multiplier for vertical motion. Default: 1", new ValueDouble(1, new double[]{0.1, 10}, 0.00001), Option.Type.NUMBER),
          new Option(this, "Add", "Add for vertical motion. Default: 0", new ValueDouble(0, new double[]{0, 1}, 0.00001), Option.Type.NUMBER),
        }));
        options.put("againStart", new Option(this, "AgainStart", "Values when starting step again in air", new ValueString(""), Option.Type.OTHER, new Option[]{
          new Option(this, "Again", "Distance from last starting point. Default: 0", new ValueDouble(0, new double[]{0, 2}, 0.1), Option.Type.NUMBER),
          new Option(this, "Timer", "Timer modifier. Default: 1", new ValueDouble(1, new double[]{0.1, 5}, 0.01), Option.Type.NUMBER),
          new Option(this, "Clip", "Your initial jump clip. Default: 0", new ValueDouble(0, new double[]{0, 2}, 0.00001), Option.Type.NUMBER),
          new Option(this, "Vertical", "Your initial jump motion. Default: 0", new ValueDouble(0.42, new double[]{0, 2}, 0.00001), Option.Type.NUMBER),
        }));
        options.put("againDuring", new Option(this, "AgainDuring", "Values during step after starting again", new ValueString(""), Option.Type.OTHER, new Option[]{
          new Option(this, "Multiplier", "Multiplier for vertical motion. Default: 1", new ValueDouble(1, new double[]{0.1, 10}, 0.00001), Option.Type.NUMBER),
          new Option(this, "Add", "Add for vertical motion. Default: 0", new ValueDouble(0, new double[]{0, 1}, 0.00001), Option.Type.NUMBER),
        }));
        options.put("done", new Option(this, "Done", "Values when finished step", new ValueString(""), Option.Type.OTHER, new Option[]{
          new Option(this, "Timer", "Timer modifier. Default: 1", new ValueDouble(1, new double[]{0.1, 5}, 0.01), Option.Type.NUMBER),
          new Option(this, "Minus", "Minus for vertical motion. Default: 0", new ValueDouble(0, new double[]{0, 1}, 0.00001), Option.Type.NUMBER),
          new Option(this, "Add", "Add to speed. Default: 0", new ValueDouble(0, new double[]{0, 1}, 0.00001), Option.Type.NUMBER),
        }));
        options.put("after", new Option(this, "After", "Values when after finishing step but before landing", new ValueString(""), Option.Type.OTHER, new Option[]{
          new Option(this, "Timer", "Timer modifier. Default: 1", new ValueDouble(1, new double[]{0.1, 5}, 0.01), Option.Type.NUMBER),
          new Option(this, "Minus", "Minus for vertical motion. Default: 0", new ValueDouble(0, new double[]{0, 1}, 0.00001), Option.Type.NUMBER),
          new Option(this, "Multiplier", "Multiplier for vertical motion. Default: 1", new ValueDouble(1, new double[]{0.1, 10}, 0.00001), Option.Type.NUMBER),
          new Option(this, "Add", "Add to speed. Default: 0", new ValueDouble(0, new double[]{0, 1}, 0.00001), Option.Type.NUMBER),
        }));
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void disable() {
        super.disable();
        if (!HelperUtil.inGame()) {
            return;
        }
        Helper.player().stepHeight = 0.5f;
        mc().timer.timerSpeed = 1f;
    }

    boolean prevCollided;
    boolean stepping;
    boolean again;
    double y;
    byte flag;

    protected void onEvent(Event event) {
        if (event instanceof EventPlayerMotion) {
            EntityPlayerSP p = mc().thePlayer;
            if (p.isCollidedHorizontally) {
                prevCollided = true;
                if (p.onGround && !stepping) {
                    flag = 0;
                    y = p.posY;
                    stepping = true;
                    again = false;
                    Option option = Option.get(options, "start");
                    mc().timer.timerSpeed = (float) option.options.get("timer").DOUBLE();
                    if (mc().timer.timerSpeed < 0.05)
                        mc().timer.timerSpeed = 0.1f;
                    p.setPosition(p.posX, p.posY + option.options.get("clip").DOUBLE(), p.posZ);
                    p.motionY = option.options.get("vertical").DOUBLE();
                } else if (!p.onGround && stepping) {
                    Option againStart = Option.get(options, "againStart");
                    if (!again) {
                        if (p.motionY < 0 && p.posY < y + againStart.options.get("again").DOUBLE()) {
                            again = true;
                            mc().timer.timerSpeed = (float) againStart.options.get("timer").DOUBLE();
                            if (mc().timer.timerSpeed < 0.05)
                                mc().timer.timerSpeed = 0.1f;
                            p.setPosition(p.posX, p.posY + againStart.options.get("clip").DOUBLE(), p.posZ);
                            p.motionY = againStart.options.get("vertical").DOUBLE();
                        } else {
                            Option option = Option.get(options, "during");
                            p.motionY *= option.options.get("multiplier").DOUBLE();
                            p.motionY += option.options.get("add").DOUBLE();
                        }
                    } else {
                        Option option = Option.get(options, "againDuring");
                        p.motionY *= option.options.get("multiplier").DOUBLE();
                        p.motionY += option.options.get("add").DOUBLE();
                    }
                } else if (p.onGround) {
                    flag++;
                    if (flag > 1) {
                        stepping = false;
                    }
                }
            } else {
                if (p.onGround) {
                    stepping = false;
                    mc().timer.timerSpeed = 1f;
                } else {
                    if (stepping) {
                        if (prevCollided) {
                            Option option = Option.get(options, "done");
                            mc().timer.timerSpeed = (float) option.options.get("timer").DOUBLE();
                            if (mc().timer.timerSpeed < 0.05)
                                mc().timer.timerSpeed = 0.1f;
                            p.motionY -= option.options.get("minus").DOUBLE();
                            double axz = option.options.get("add").DOUBLE();
                            player().motionX += Math.cos(Math.toRadians(player().rotationYaw + 90.0)) * axz;
                            player().motionZ += Math.sin(Math.toRadians(player().rotationYaw + 90.0)) * axz;
                        } else {
                            Option option = Option.get(options, "after");
                            mc().timer.timerSpeed = (float) option.options.get("timer").DOUBLE();
                            if (mc().timer.timerSpeed < 0.05)
                                mc().timer.timerSpeed = 0.1f;
                            p.motionY *= option.options.get("multiplier").DOUBLE();
                            p.motionY -= option.options.get("minus").DOUBLE();
                            double axz = option.options.get("add").DOUBLE();
                            player().motionX += Math.cos(Math.toRadians(player().rotationYaw + 90.0)) * axz;
                            player().motionZ += Math.sin(Math.toRadians(player().rotationYaw + 90.0)) * axz;
                        }
                    }
                }
                prevCollided = false;
            }
        }
    }
}
