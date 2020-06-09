package com.ihl.client.module.hacks.movement;

import com.ihl.client.event.*;
import com.ihl.client.gui.Gui;
import com.ihl.client.gui.ring.RingString;
import com.ihl.client.module.Module;
import com.ihl.client.module.hacks.Category;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;

import java.util.*;
import java.util.stream.Collectors;

@EventHandler(events = {EventPlayerMove.class, EventPlayerUpdate.class})
public class Speed extends Module {

    private int jumps;
    private int groundTick;

    public Speed() {
        super("Speed", "Apply a movement multiplier", Category.MOVEMENT, "NONE");
        //options.put("speed", new Option(this, "Speed", "Movement speed multiplier", new ValueDouble(2, new double[]{0.1, 10}, 0.1), Option.Type.NUMBER));
//        options.put("ncp", new Option(this, "NCP", "Use NCP bypassed speed", new ValueBoolean(true), Option.Type.BOOLEAN, new Option[]{
//                new Option(this, "Mode", "NCP version selector", new ValueChoice(1, new String[]{"normal", "fast"}), Option.Type.CHOICE),
//                new Option(this, "Timer", "NCP speed timer modifier", new ValueDouble(1.15, new double[]{0.1, 10}, 0.01), Option.Type.NUMBER)
//        }));
        //options.put("mode", new Option(this, "Mode", "Bypass mode", new ValueChoice(1, "NCP", "AAC"), Option.Type.CHOICE));
        options.put("addvalue", new OptNoS(this, "Add Value", "Adds Values. Enable this to add a value.", new ValueBoolean(false), Option.Type.BOOLEAN,
          new OptNoS(this, "Name", "Name of this value", new ValueString("Custom Value"), Option.Type.STRING),
          new OptNoS(this, "Condition", "Condition of this value, when it happens", new ValueChoice(0, "always", "up", "down", "ground"), Option.Type.CHOICE),
          new OptNoS(this, "EveryGround", "Every X amount of ground hits to do this.", new ValueDouble(1, new double[]{1, 40}, 1), Option.Type.NUMBER),
          new OptNoS(this, "EveryTick", "Every X ticks to do this.", new ValueDouble(1, new double[]{1, 40}, 1), Option.Type.NUMBER),
          new OptNoS(this, "TickGround", "Every X ticks after ground.", new ValueDouble(1, new double[]{1, 40}, 1), Option.Type.NUMBER)));
        options.put("normalvalues", new OptNoS(this, "Normal Values", "Normal values for the bypasses. Ground, up, down.",
          new ValueString(""), Option.Type.OTHER,
          new Option(this, "Ground", "Modifications when hitting ground", new ValueBoolean(true), Option.Type.BOOLEAN,
            new Option(this, "VClip", "Teleport Up. Default: 0", new ValueDouble(0, new double[]{-2, 2}, 0.01), Option.Type.NUMBER),
            new Option(this, "HClip", "Teleport infront. Default: 0", new ValueDouble(0, new double[]{-2, 2}, 0.01), Option.Type.NUMBER),
            new Option(this, "Timer", "Timer modifier. Default: 1", new ValueDouble(1, new double[]{0.1, 5}, 0.01), Option.Type.NUMBER),
            new Option(this, "VSet", "Vertical Add. Default: 0.42", new ValueDouble(0.42, new double[]{-1, 1}, 0.01), Option.Type.NUMBER),
            new Option(this, "HAdd", "Horizontal Add. Default: 0", new ValueDouble(0, new double[]{-1, 1}, 0.01), Option.Type.NUMBER),
            new Option(this, "HSet", "Sets horizontal instead of adding. Default: false", new ValueBoolean(false), Option.Type.BOOLEAN),
            new Option(this, "HMult", "Horizontal Multiply. Default: 1", new ValueDouble(1, new double[]{-2, 2}, 0.1), Option.Type.NUMBER),
            new Option(this, "Air Speed", "Air speed. Default: 0.02", new ValueDouble(0.02, new double[]{0, 0.5}, 0.001), Option.Type.NUMBER)),
          new Option(this, "Up", "Modifications when going up", new ValueBoolean(true), Option.Type.BOOLEAN,
            new Option(this, "Timer", "Timer modifier. Default: 1", new ValueDouble(1, new double[]{0.1, 5}, 0.01), Option.Type.NUMBER),
            new Option(this, "VAdd", "Vertical Add. Default: 0", new ValueDouble(0, new double[]{-1, 1}, 0.01), Option.Type.NUMBER),
            new Option(this, "VMult", "Vertical Multiply. Default: 1", new ValueDouble(1, new double[]{-2, 2}, 0.01), Option.Type.NUMBER),
            new Option(this, "HAdd", "Horizontal Add. Default: 0", new ValueDouble(0, new double[]{-1, 1}, 0.01), Option.Type.NUMBER),
            new Option(this, "HMult", "Horizontal Multiply. Default: 1", new ValueDouble(1, new double[]{-2, 2}, 0.01), Option.Type.NUMBER),
            new Option(this, "Air Speed", "Air speed. Default: 0.02", new ValueDouble(0.02, new double[]{0, 0.5}, 0.001), Option.Type.NUMBER)),
          new Option(this, "Down", "Modifications when going down", new ValueBoolean(true), Option.Type.BOOLEAN,
            new Option(this, "Timer", "Timer modifier. Default: 1", new ValueDouble(1, new double[]{0.1, 5}, 0.01), Option.Type.NUMBER),
            new Option(this, "VAdd", "Vertical Add. Default: 0", new ValueDouble(0, new double[]{-1, 1}, 0.01), Option.Type.NUMBER),
            new Option(this, "VMult", "Vertical Multiply. Default: 1", new ValueDouble(1, new double[]{-2, 2}, 0.01), Option.Type.NUMBER),
            new Option(this, "HAdd", "Horizontal Add. Default: 0", new ValueDouble(0, new double[]{-1, 1}, 0.01), Option.Type.NUMBER),
            new Option(this, "HMult", "Horizontal Multiply. Default: 1", new ValueDouble(1, new double[]{-2, 2}, 0.01), Option.Type.NUMBER),
            new Option(this, "Air Speed", "Air speed. Default: 0.02", new ValueDouble(0.02, new double[]{0, 0.5}, 0.001), Option.Type.NUMBER))));
        options.put("customvalues", new OptNoS(this, "Custom Values", "Other values that you can add/remove", new ValueString(""), Option.Type.OTHER));
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void enable() {
        super.enable();
        jumps = 0;
    }

    public void disable() {
        super.disable();
        if (player() == null)
            return;
        mc().timer.timerSpeed = 1f;
        player().speedInAir = 0.02F;
    }

    public void optionChanged(EventOption eventOption) {
        System.out.println(eventOption.module.name + "  " + eventOption.option.name + " " +
          eventOption.option.parents.stream().map(option -> option.name).collect(Collectors.toList()) + "  " + eventOption.changed);
        if (eventOption.module.name.equalsIgnoreCase("speed")) {
            if (eventOption.option.getTValue() instanceof ValueBoolean) {
                if (eventOption.changed.equalsIgnoreCase("true")) {
                    if (eventOption.option.name.replaceAll(" ", "").toLowerCase().equalsIgnoreCase("addvalue")) {
                        eventOption.option.setValueNoTrigger(false);
                        Option option = options.get("addvalue");
                        Option cond = option.options.get("condition");
                        String name = (String) option.options.get("name").getValue();
                        int condition = cond.getValue().equals("always") ? 0 :
                          cond.getValue().equals("up") ? 1 :
                            cond.getValue().equals("down") ? 2 : 3 /*ground*/;
                        int ground = option.options.get("everyground").INTEGER();
                        int tick = option.options.get("everytick").INTEGER();
                        int tickground = option.options.get("tickground").INTEGER();
                        options.get("customvalues").options.put(name.toLowerCase().replaceAll(" ", ""), generateOption(name, condition, ground, tick, tickground));

                    } else if (eventOption.option.name.replaceAll(" ", "").toLowerCase().equalsIgnoreCase("delete")) {
                        Option option = options.get("customvalues").options.remove(eventOption.option.parents.get(0).name.toLowerCase().replaceAll(" ", ""));
                        option.name = eventOption.changed;
                        options.get("customvalues").options.put(eventOption.changed.toLowerCase().replaceAll(" ", ""), option);
                        Gui.components.put("ring", new RingString(this, option.options.get("name"), Arrays.asList(option.name)));
                    }
                }
            } else if (eventOption.option.getTValue() instanceof ValueString) {
                Option opt = options.get("customvalues").options.remove(eventOption.option.parents.get(0).name.toLowerCase().replaceAll(" ", ""));
                // TODO: 2020-06-09 change name
                opt.name = eventOption.changed;
                options.get("customvalues").options.put(opt.name.toLowerCase().replaceAll(" ", ""), opt);
            }
        }
    }

    /*public Option generateOption(String name, String condition, String condition1, int amount) {
        int cond = condition.equals("always") ? 0 :
          condition.equals("up") ? 1 :
            condition.equals("down") ? 2 : 3 *//*ground*//*;
        int cond1 = condition1.equals("tick") ? 0 : 1;
        return generateOption(name, cond, cond1, amount);
    }*/
    public Option generateOption(String name) {
        return generateOption(name, 0, 1, 1, 1);
    }

    private Option generateOption(String name, int condition, int everyGround, int everyTick, int tickGround) {
        return new Option(this, name, "Custom value.", new ValueBoolean(true), Option.Type.BOOLEAN,
          new Option(this, "Name", "Name of module.", new ValueString(name), Option.Type.STRING),
          new OptNoS(this, "Delete", "Enable this to delete module.", new ValueBoolean(false), Option.Type.BOOLEAN),
          new Option(this, "Condition", "Condition of this value, when it happens", new ValueChoice(condition, "always", "up", "down", "ground"), Option.Type.CHOICE),
          //new Option(this, "Condition1", "Extra conditions of this value, when it happens", new ValueChoice(condition1, "tick", "ground"), Option.Type.CHOICE,
          new Option(this, "EveryGround", "Every X amount of ground hits to do this.", new ValueDouble(everyGround, new double[]{1, 40}, 1), Option.Type.NUMBER),
          new Option(this, "EveryTick", "Every X ticks to do this.", new ValueDouble(tickGround, new double[]{1, 40}, 1), Option.Type.NUMBER),
          new Option(this, "TickGround", "Every X ticks after ground.", new ValueDouble(everyTick, new double[]{1, 40}, 1), Option.Type.NUMBER),
          new Option(this, "VClip", "Teleport Up. Default: 0", new ValueDouble(0, new double[]{-2, 2}, 0.01), Option.Type.NUMBER),
          new Option(this, "HClip", "Teleport infront. Default: 0", new ValueDouble(0, new double[]{-2, 2}, 0.01), Option.Type.NUMBER),
          new Option(this, "Timer", "Timer modifier. Default: 1", new ValueDouble(1, new double[]{0.1, 5}, 0.01), Option.Type.NUMBER),
          new Option(this, "VAdd", "Vertical Add. Default: 0", new ValueDouble(0, new double[]{-1, 1}, 0.01), Option.Type.NUMBER),
          new Option(this, "VSet", "Sets vertical instead of adding. Default: false", new ValueBoolean(false), Option.Type.BOOLEAN),
          new Option(this, "VMult", "Vertical Multiply. Default: 1", new ValueDouble(1, new double[]{-2, 2}, 0.01), Option.Type.NUMBER),
          new Option(this, "HAdd", "Horizontal Add. Default: 0", new ValueDouble(0, new double[]{-1, 1}, 0.01), Option.Type.NUMBER),
          new Option(this, "HSet", "Sets horizontal instead of adding. Default: 1", new ValueBoolean(false), Option.Type.BOOLEAN),
          new Option(this, "HMult", "Horizontal Multiply. Default: 1", new ValueDouble(1, new double[]{-2, 2}, 0.01), Option.Type.NUMBER),
          new Option(this, "Air Speed", "Air speed. Default: 0.02", new ValueDouble(0.02, new double[]{0, 0.5}, 0.001), Option.Type.NUMBER));
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            EventPlayerUpdate e = (EventPlayerUpdate) event;
            if (e.type == Event.Type.PRE) {
                if (!mc().gameSettings.keyBindJump.getIsKeyPressed() &&
                  (player().moveForward != 0 || player().moveStrafing != 0) &&
                  !player().isOnLadder() && !player().isSneaking()) {
                    if (player().onGround) {
                        ++jumps;
                        groundTick = 1;
                        if (Option.get(options, "normalvalues", "ground").BOOLEAN()) {
                            Option option = options.get("normalvalues").options.get("ground");
                            double vclip = option.DOUBLE("vclip");
                            double hclip = option.DOUBLE("hclip");
                            float timer = (float) option.DOUBLE("timer");
                            float airSpeed = (float) option.DOUBLE("airspeed");
                            double vset = option.DOUBLE("vset");
                            double hadd = option.DOUBLE("hadd");
                            double hmult = option.DOUBLE("hmult");
                            MUtil.vset(vset);
                            MUtil.moveAllTypes(vclip, hclip, timer, airSpeed, 0, 1, hadd, hmult);
                        }
                    } else {
                        groundTick++;
                        if (player().motionY > 0) {
                            if (Option.get(options, "normalvalues", "up").BOOLEAN()) {
                                Option option = options.get("normalvalues").options.get("up");
                                doAction(option, false, false);
                            }
                        }
                        if (player().motionY < 0) {
                            if (Option.get(options, "normalvalues", "down").BOOLEAN()) {
                                Option option = options.get("normalvalues").options.get("down");
                                doAction(option, false, false);
                            }
                        }
                    }
                    for (Map.Entry<String, Option> set : options.get("customvalues").options.entrySet()) {
                        Option option = set.getValue();
                        if (option.BOOLEAN()) {
                            String condition = (String) option.getValue("condition");
                            int everyGround = option.INTEGER("everyground");
                            int everyTick = option.INTEGER("everytick");
                            int tickGround = option.INTEGER("tickground");
                            if (everyGround <= 0) everyGround = 1;
                            if (everyTick <= 0) everyTick = 1;

                            if ((condition.equalsIgnoreCase("always") || (condition.equalsIgnoreCase("up") && player().motionY > 0) ||
                              (condition.equalsIgnoreCase("down") && player().motionY < 0) || (condition.equalsIgnoreCase("ground") && player().onGround)) &&
                              (player().ticksExisted % everyTick == 0) && (jumps % everyGround == 0) && (groundTick % tickGround == 0)) {
                                doAction(option, true, true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void doAction(Option option, boolean clip, boolean sets) {
        double vclip = clip ? option.DOUBLE("vclip") : 0;
        double hclip = clip ? option.DOUBLE("hclip") : 0;
        float timer = (float) option.DOUBLE("timer");
        float airSpeed = (float) option.DOUBLE("airspeed");
        double vadd = option.DOUBLE("vadd");
        double vmult = option.DOUBLE("vmult");
        double hadd = option.DOUBLE("hadd");
        double hmult = option.DOUBLE("hmult");
        if (sets) {
            if (option.BOOLEAN("vset")) {
                MUtil.vset(vadd);
            } else {
                MUtil.vadd(vadd);
            }
            System.out.println(option.BOOLEAN("hset") + " " + hadd);
            if (option.BOOLEAN("hset")) {
                MUtil.strafe(hadd);
            } else {
                MUtil.hadd(hadd);
            }
        } else {
            MUtil.vadd(vadd);
            MUtil.hadd(hadd);
        }
        MUtil.vclip(vclip);
        MUtil.hclip(hclip);
        MUtil.timer(timer);
        MUtil.airSpeed(airSpeed);
        MUtil.vmult(vmult);
        MUtil.hmult(hmult);
        //MUtil.moveAllTypes(vclip, hclip, timer, airSpeed, vadd, vmult, hadd, hmult);
    }

    /*if (event instanceof EventPlayerMove) {
            EventPlayerMove e = (EventPlayerMove) event;
            if (e.type == Event.Type.PRE) {
                if (!ncp) {
                    e.x *= speed;
                    e.z *= speed;
                }
            } else if (e.type == Event.Type.POST) {
                if (ncp) {
                    if (player().onGround && !mc().gameSettings.keyBindJump.getIsKeyPressed() && !player().isCollidedHorizontally && (player().moveForward != 0 || player().moveStrafing != 0) && !player().isSneaking()) {
                        double val = (player().rotationYaw + 90 + (player().moveForward > 0 ? 0 + (player().moveStrafing > 0 ? -45 : player().moveStrafing < 0 ? 45 : 0) : player().moveForward < 0 ? 180 + (player().moveStrafing > 0 ? 45 : player().moveStrafing < 0 ? -45 : 0) : 0 + (player().moveStrafing > 0 ? -90 : player().moveStrafing < 0 ? 90 : 0))) * Math.PI / 180;

                        double x = Math.cos(val) * (ncpMode.equalsIgnoreCase("fast") ? 0.27 : 0.2);
                        double z = Math.sin(val) * (ncpMode.equalsIgnoreCase("fast") ? 0.27 : 0.2);

                        player().motionX += x;
                        player().motionY = ncpMode.equalsIgnoreCase("fast") ? 0.145 : 0.21;
                        player().motionZ += z;

                        mc().timer.timerSpeed = (float) ncpTimer;

                        jumped = true;
                    } else {
                        mc().timer.timerSpeed = 1f;
                    }
                }
            }
        } else if (event instanceof EventPlayerUpdate) {
            EventPlayerUpdate e = (EventPlayerUpdate) event;
            if (e.type == Event.Type.POST) {
                if (ncp) {
                    if (jumped && !player().onGround && !mc().gameSettings.keyBindJump.getIsKeyPressed() && !player().isOnLadder()) {
                        player().motionY = Math.min(ncpMode.equalsIgnoreCase("fast") ? -0.2 : 0, player().motionY);
                        jumped = false;
                    }
                }
            }
        }*/

}
