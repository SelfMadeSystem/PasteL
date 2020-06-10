package com.ihl.client.module.hacks.movement;

import com.ihl.client.event.*;
import com.ihl.client.gui.Gui;
import com.ihl.client.gui.ring.RingString;
import com.ihl.client.module.Module;
import com.ihl.client.module.Category;
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
        { //Add values to the AddValue section.
            OptNoS addValue = addBooleanNoS("Add Value", "Adds Values. Enable this to add a value.", false);
            addValue.addStringNoS("Name", "Name of this value", "Custom Value");
            addValue.addChoiceNoS("Condition", "Condition of this value, when it happens", "always", "up", "down", "ground");
            addValue.addIntegerNoS("EveryGround", "Every X amount of ground hits to do this.", 1, 1, 40);
            addValue.addIntegerNoS("EveryTick", "Every X ticks to do this.", 1, 1, 40);
            addValue.addIntegerNoS("TickGround", "Every X ticks after ground.", 1, 1, 40);
        }
        { //Add values to the "Ground" section.
            Option ground = addBoolean("Ground", "Modifications when hitting ground", true);
            ground.addDouble("VClip", "Teleport Up. Default: 0", 0, -2, 2, 0.01);
            ground.addDouble("HClip", "Teleport forwards. Default: 0", 0, -2, 2, 0.01);
            ground.addDouble("Timer", "Speeds up or slows down the game. Default: 1", 1, 0.1, 5, 0.05);
            ground.addDouble("VSet", "Sets the vertical motion. Default: 0.42", 0.42, -2, 2, 0.01);
            ground.addDouble("HAdd", "Adds to the horizontal motion. Default: 0.2", 0.2, -2, 2, 0.01);
            ground.addBoolean("HSet", "Sets the horizontal motion instead of adding to it. Default: false", false);
            ground.addDouble("HMult", "Multiplies the horizontal motion. Default: 0.2", 1, -2, 2, 0.01);
            ground.addDouble("AirSpeed", "Sets the AirSpeed of the player. Default: 0.02", 0.02, -2, 2, 0.01);
        }
        { //Add values to the "Up" section.
            Option ground = addBoolean("Up", "Modifications when going up.", true);
            ground.addDouble("Timer", "Speeds up or slows down the game. Default: 1", 1, 0.1, 5, 0.05);
            ground.addDouble("VAdd", "Adds to the vertical motion. Default: 0", 0, -2, 2, 0.01);
            ground.addDouble("HAdd", "Adds to the horizontal motion. Default: 0.2", 0.2, -2, 2, 0.01);
            ground.addDouble("HMult", "Multiplies the horizontal motion. Default: 0.2", 1, -2, 2, 0.01);
            ground.addDouble("AirSpeed", "Sets the AirSpeed of the player. Default: 0.02", 0.02, -2, 2, 0.01);
        }
        { //Add values to the "Down" section.
            Option ground = addBoolean("Down", "Modifications when coming back down.", true);
            ground.addDouble("Timer", "Speeds up or slows down the game. Default: 1", 1, 0.1, 5, 0.05);
            ground.addDouble("VAdd", "Adds to the vertical motion. Default: 0", 0, -2, 2, 0.01);
            ground.addDouble("HAdd", "Adds to the horizontal motion. Default: 0.2", 0.2, -2, 2, 0.01);
            ground.addDouble("HMult", "Multiplies the horizontal motion. Default: 0.2", 1, -2, 2, 0.01);
            ground.addDouble("AirSpeed", "Sets the AirSpeed of the player. Default: 0.02", 0.02, -2, 2, 0.01);
        }
        addOther("CustomValues", "Other values that you can add/remove");
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
        Option opt = new Option(this, name, "Custom value.", new ValueBoolean(true), Option.Type.BOOLEAN);
        opt.addString("Name", "Name of module.", name);
        opt.addBooleanNoS("Delete", "Enable to delete this module.", false);
        opt.options.put("condition", new Option(this, "Condition", "Condition of this value, when it happens", new ValueChoice(condition, "always", "up", "down", "ground"), Option.Type.CHOICE));
        opt.addInteger("EveryGround", "Every X amount of ground hits to do this.", everyGround, 1, 40);
        opt.addInteger("EveryTick", "Every X ticks to do this.", everyTick, 1, 40);
        opt.addInteger("TickGround", "Every X ticks after ground.", tickGround, 1, 40);
        opt.addDouble("VClip", "Teleports up. Default: 0", 0, -2, 2, 0.01);
        opt.addDouble("HClip", "Teleports forwards. Default: 0", 0, -2, 2, 0.01);
        opt.addDouble("Timer", "Speeds up or slows down the game. Default: 1", 1, 0.1, 5, 0.05);
        opt.addDouble("VAdd", "Adds to the vertical motion. Default: 0", 0, -2, 2, 0.01);
        opt.addBoolean("VSet", "Sets the vertical motion instead of adding to it. Default: false", false);
        opt.addDouble("HAdd", "Adds to the horizontal motion. Default: 0.2", 0.2, -2, 2, 0.01);
        opt.addBoolean("HSet", "Sets the horizontal motion instead of adding to it. Default: false", false);
        opt.addDouble("HMult", "Multiplies the horizontal motion. Default: 0.2", 1, -2, 2, 0.01);
        opt.addDouble("AirSpeed", "Sets the AirSpeed of the player. Default: 0.02", 0.02, -2, 2, 0.01);
        return opt;
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
}
