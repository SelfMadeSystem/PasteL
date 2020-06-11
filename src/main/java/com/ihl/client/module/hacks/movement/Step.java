package com.ihl.client.module.hacks.movement;

import com.ihl.client.Helper;
import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.option.Option;
import com.ihl.client.util.HelperUtil;
import net.minecraft.client.entity.EntityPlayerSP;

@EventHandler(events = {EventPlayerMotion.class})
public class Step extends Module {

    public Step() {
        super("Step", "Step up blocks like stairs", Category.MOVEMENT, "NONE");
        Option start = addOther("Start", "Values when starting step");
        start.addDouble("Timer", "Timer modifier. Default: 1", 1, 0.1, 5, 0.01);
        start.addDouble("Clip", "Your initial jump clip. Default: 0", 0, 0, 2, 0.00001);
        start.addDouble("Vertical", "Your initial jump motion. Default: 0.42", 0.42, 0, 2, 0.00001);
        Option during = addOther("During", "Values during step");
        during.addDouble("Multiplier", "Multiplier for vertical motion. Default: 1", 1, 0.1, 10, 0.00001);
        during.addDouble("Add", "Add for vertical motion. Default: 0", 0, 0, 1, 0.00001);
        Option againStart = addOther("AgainStart", "Values when starting step again in air");
        againStart.addDouble("Again", "Distance from last starting point. Default: 0", 0, 0, 2, 0.1);
        againStart.addDouble("Timer", "Timer modifier. Default: 1", 1, 0.1, 5, 0.01);
        againStart.addDouble("Clip", "Your initial jump clip. Default: 0", 0, 0, 2, 0.00001);
        againStart.addDouble("Vertical", "Your initial jump motion. Default: 0", 0.42, 0, 2, 0.00001);
        Option againDuring = addOther("AgainDuring", "Values during step after starting again");
        againDuring.addDouble("Multiplier", "Multiplier for vertical motion. Default: 1", 1, 0.1, 10, 0.00001);
        againDuring.addDouble("Add", "Add for vertical motion. Default: 0", 0, 0, 1, 0.00001);
        Option done = addOther("Done", "Values when finished step");
        done.addDouble("Timer", "Timer modifier. Default: 1", 1, 0.1, 5, 0.01);
        done.addDouble("Minus", "Minus for vertical motion. Default: 0", 0, 0, 1, 0.00001);
        done.addDouble("Add", "Add to speed. Default: 0", 0, 0, 1, 0.00001);
        Option after = addOther("After", "Values when after finishing step but before landing");
        after.addDouble("Timer", "Timer modifier. Default: 1", 1, 0.1, 5, 0.01);
        after.addDouble("Minus", "Minus for vertical motion. Default: 0", 0, 0, 1, 0.00001);
        after.addDouble("Multiplier", "Multiplier for vertical motion. Default: 1", 1, 0.1, 10, 0.00001);
        after.addDouble("Add", "Add to speed. Default: 0", 0, 0, 1, 0.00001);
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
