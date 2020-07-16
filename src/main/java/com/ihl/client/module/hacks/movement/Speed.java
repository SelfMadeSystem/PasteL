package com.ihl.client.module.hacks.movement;

import com.ihl.client.event.*;
import com.ihl.client.gui.Gui;
import com.ihl.client.module.*;
import com.ihl.client.module.hacks.movement.speeds.SpeedMode;
import com.ihl.client.module.option.*;
import com.ihl.client.module.option.options.*;
import com.ihl.client.util.MUtil;

import java.util.*;

@EventHandler(events = {EventPlayerMove.class, EventPlayerUpdate.class})
public class Speed extends Module {

    public int jumps;
    public int groundTick;

    public Speed() {
        super("Speed", "Apply a movement multiplier", Category.MOVEMENT, "NONE");
        addChoice("Mode", "Bypass mode for speed.", "Custom", "NCP", "AAC");
        {//AAC Options
            Option aac = addOption(new CustomOption("AACOptions", "Options for mode \"AAC\"", "AAC"));
            aac.addDouble("AACTimer", "Timer speed for \"Timer\"", 5, 1, 20, 0.1);
            aac.addDouble("AACTimerMove", "Movement speed for \"Timer\". 0 to use vanilla. 0.02 for LiquidBounce AACGround2", 0, 0, 0.5, 0.005);
            aac.addBoolean("AACTimerPos", "Sends position packet for \"Timer\"", true);
        }
        {//NCP Options
            //Option ncp = addOption(new CustomOption("NCPOptions", "Options for mode \"NCP\"", "NCP"));
        }
        SpeedMode.init();
        generateOptions();
        { //Add values to the AddValue section.
            Option addValue = addOption(new OptBol("Add Value", "Adds Values. Enable this to add a value.", false) {
                @Override
                public boolean visible() {
                    return module.STRING("mode").equalsIgnoreCase("custom");
                }

                @Override
                public boolean save() {
                    return false;
                }
            });
            addValue.addStringNoS("Name", "Name of this value", "Custom Value");
            addValue.addChoiceNoS("Condition", "Condition of this value, when it happens", "always", "up", "down", "ground");
            addValue.addIntegerNoS("EveryGround", "Every X amount of ground hits to do this.", 1, 1, 40);
            addValue.addIntegerNoS("EveryTick", "Every X ticks to do this.", 1, 1, 40);
            addValue.addIntegerNoS("TickGround", "Every X ticks after ground.", 1, 1, 40);
        }
        // Nomral values.
        Option normalValues = addOption(new CustomOption("NormalValues", "Modification values when going up, down, and on ground"));
        normalValues.addBoolean("Strafe", "Strafes all the time (instantly moves in direction). Default: false", false);
        { //Add values to the "Ground" section.
            Option ground = normalValues.addBoolean("Ground", "Modifications when hitting ground", true);
            ground.addDouble("VClip", "Teleport Up. Default: 0", 0, -2, 2, 0.01);
            ground.addDouble("HClip", "Teleport forwards. Default: 0", 0, -2, 2, 0.01);
            ground.addDouble("Timer", "Speeds up or slows down the game. Default: 1", 1, 0.1, 5, 0.05);
            ground.addDouble("VSet", "Sets the vertical motion. Default: 0.42", 0.42, -2, 2, 0.01);
            ground.addDouble("HAdd", "Adds to the horizontal motion. Default: 0.2", 0.2, -2, 2, 0.01);
            ground.addBoolean("HSet", "Sets the horizontal motion instead of adding to it. Default: false", false);
            ground.addDouble("HMult", "Multiplies the horizontal motion. Default: 1", 1, -2, 2, 0.01);
            ground.addDouble("AirSpeed", "Sets the AirSpeed of the player. Default: 0.02", 0.02, -2, 2, 0.01);
            ground.addBoolean("Strafe", "Strafes when on ground (instantly moves in direction). Default: false", false);
        }
        { //Add values to the "Up" section.
            Option up = normalValues.addBoolean("Up", "Modifications when going up.", true);
            up.addDouble("Timer", "Speeds up or slows down the game. Default: 1", 1, 0.1, 5, 0.05);
            up.addDouble("VAdd", "Adds to the vertical motion. Default: 0", 0, -2, 2, 0.01);
            up.addDouble("VMult", "Multiplies the horizontal motion. Default: 1", 1, -2, 2, 0.01);
            up.addDouble("HAdd", "Adds to the horizontal motion. Default: 0", 0, -2, 2, 0.01);
            up.addDouble("HMult", "Multiplies the horizontal motion. Default: 1", 1, -2, 2, 0.01);
            up.addDouble("AirSpeed", "Sets the AirSpeed of the player. Default: 0.02", 0.02, -2, 2, 0.01);
        }
        { //Add values to the "Down" section.
            Option down = normalValues.addBoolean("Down", "Modifications when coming back down.", true);
            down.addDouble("Timer", "Speeds up or slows down the game. Default: 1", 1, 0.1, 5, 0.05);
            down.addDouble("VAdd", "Adds to the vertical motion. Default: 0", 0, -2, 2, 0.01);
            down.addDouble("VMult", "Multiplies the horizontal motion. Default: 1", 1, -2, 2, 0.01);
            down.addDouble("HAdd", "Adds to the horizontal motion. Default: 0", 0, -2, 2, 0.01);
            down.addDouble("HMult", "Multiplies the horizontal motion. Default: 1", 1, -2, 2, 0.01);
            down.addDouble("AirSpeed", "Sets the AirSpeed of the player. Default: 0.02", 0.02, -2, 2, 0.01);
        }
        addOption(new CustomOption("CustomValues", "Other values that you can add/remove"));
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    private void generateOptions() {
        for (String str : SpeedMode.speeds.keySet()) {
            if (!(Arrays.asList(((ValueChoice) options.get("mode").getTValue()).list)).contains(str)) {
                List<String> l = Arrays.asList(((ValueChoice) options.get("mode").getTValue()).list);
                l.add(str);
                ((ValueChoice) options.get("mode").getTValue()).list = l.toArray(new String[0]);
            }
            List<String> modes = new ArrayList<>(SpeedMode.speeds.get(str).keySet());
            addOptionIfAbsent(new ModeOption(str, modes.toArray(new String[0])));
        }
    }

    public void enable() {
        super.enable();
        jumps = 0;
    }

    public void disable() {
        super.disable();
        if (player() == null)
            return;
        timerSpeed(1f);
        player().speedInAir = 0.02F;
    }

    public void optionChanged(EventOption eventOption) {
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
                        options.get("customvalues").addOption(generateOption(name, condition, ground, tick, tickground));

                    } else if (eventOption.option.name.replaceAll(" ", "").toLowerCase().equalsIgnoreCase("delete")) {
                        options.get("customvalues").removeOption(eventOption.option.parent.name);
                        Gui.prevRing();
                    }
                }
            } else if (eventOption.option.getTValue() instanceof ValueString) {
                if (eventOption.option.name.equalsIgnoreCase("name")) {
                    if (eventOption.option.parent != null && eventOption.option.parent.parent != null) {
                        Option opt = options.get("customvalues").removeOption(eventOption.option.parent.name);
                        opt.name = eventOption.changed;
                        options.get("customvalues").addOption(opt);
                    }
                }
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
        Option opt = new Option(name, "Custom value.", new ValueBoolean(true), Option.Type.BOOLEAN);
        opt.module = this;
        opt.addString("Name", "Name of module.", name);
        opt.addBooleanNoS("Delete", "Enable to delete this module.", false);
        opt.addOption(new Option("Condition", "Condition of this value, when it happens", new ValueChoice(condition, "always", "up", "down", "ground"), Option.Type.CHOICE));
        opt.addInteger("EveryGround", "Every X amount of ground hits to do this.", everyGround, 1, 40);
        opt.addInteger("EveryTick", "Every X ticks to do this.", everyTick, 1, 40);
        opt.addInteger("TickGround", "Every X ticks after ground.", tickGround, 1, 40);
        opt.addDouble("VClip", "Teleports up. Default: 0", 0, -2, 2, 0.01);
        opt.addDouble("HClip", "Teleports forwards. Default: 0", 0, -2, 2, 0.01);
        opt.addDouble("Timer", "Speeds up or slows down the game. Default: 1", 1, 0.1, 5, 0.05);
        opt.addDouble("VAdd", "Adds to the vertical motion. Default: 0", 0, -2, 2, 0.01);
        opt.addBoolean("VSet", "Sets the vertical motion instead of adding to it. Default: false", false);
        opt.addDouble("VMult", "Multiplies the horizontal motion. Default: 1", 1, -2, 2, 0.01);
        opt.addDouble("HAdd", "Adds to the horizontal motion. Default: 0.2", 0.2, -2, 2, 0.01);
        opt.addBoolean("HSet", "Sets the horizontal motion instead of adding to it. Default: false", false);
        opt.addDouble("HMult", "Multiplies the horizontal motion. Default: 1", 1, -2, 2, 0.01);
        opt.addDouble("AirSpeed", "Sets the AirSpeed of the player. Default: 0.02", 0.02, -2, 2, 0.01);
        return opt;
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            EventPlayerUpdate e = (EventPlayerUpdate) event;
            if (e.type == Event.Type.PRE) {
                if (player().onGround) {
                    ++jumps;
                    groundTick = 1;
                } else {
                    groundTick++;
                }
                if (!mc().gameSettings.keyBindJump.getIsKeyPressed() &&
                  (player().moveForward != 0 || player().moveStrafing != 0) &&
                  !player().isOnLadder() && !player().isSneaking()) {
                    String mode = STRING("mode");
                    if (mode.equalsIgnoreCase("Custom")) {

                        if (player().onGround) {
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
                                if (option.BOOLEAN("strafe")) MUtil.strafe();
                            }
                        } else {
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
                        if (BOOLEAN("normalvalues", "strafe")) MUtil.strafe();
                    } else {
                        String type = STRING(mode + "Mode");
                        player().setSprinting(true);
                        timerSpeed(1);
                        if (SpeedMode.speeds.get(mode) != null) {
                            SpeedMode speedMode = SpeedMode.speeds.get(mode).get(type);
                            if (speedMode != null) {
                                speedMode.onUpdate((EventPlayerUpdate) event, this);
                            }
                        }
                    }
                }
                /*switch (STRING("mode")) {
                    case "Custom": {
                    }
                    case "NCP": {
                        String ncpMode = STRING("ncpOptions", "ncpMode");
                        player().setSprinting(true);
                        if (MUtil.isMoving() && !player().isSneaking()) {
                            switch (ncpMode) {
                                case "Hop": { //LiquidBounce Skid :)
                                    timerSpeed(1.0865F);
                                    if (player().onGround) {
                                        player().jump();
                                        player().speedInAir = 0.0223F;
                                    }
                                    MUtil.strafe();
                                    break;
                                }
                                case "YPort": { //LiquidBounce Skid :)
                                    if (jumps >= 4 && player().onGround)
                                        jumps = 0;

                                    if (player().onGround) {
                                        player().motionY = jumps <= 1 ? 0.42F : 0.4F;
                                        float f = player().rotationYaw * 0.017453292F;

                                        player().motionX -= MathHelper.sin(f) * 0.2F;
                                        player().motionZ += MathHelper.cos(f) * 0.2F;
                                    } else if (jumps <= 1)
                                        player().motionY = -5D;

                                    MUtil.strafe();
                                    break;
                                }
                            }
                        } else {
                            MUtil.hmult(0);
                        }
                        break;
                    }
                    case "AAC": {
                        String aacMode = STRING("aacOptions", "aacMode");
                        player().setSprinting(true);

                        if (MUtil.isMoving() && !player().isSneaking()) {
                            switch (aacMode) {// "3.5.0", "3.3.13", "3.6.4", "4.2", "4.2Hop"
                                case "Timer": {// TODO: 2020-06-14 Add Timer to thingy :) kthx
                                    break;
                                }
                                case "3.3.13": { //LiquidBounce Skid :)
                                    if (player().onGround && player().isCollidedVertically) {
                                        // MotionXYZ
                                        float yawRad = player().rotationYaw * 0.017453292F;
                                        player().motionX -= MathHelper.sin(yawRad) * 0.202F;
                                        player().motionZ += MathHelper.cos(yawRad) * 0.202F;
                                        player().motionY = 0.405F;
                                        MUtil.strafe();
                                    } else if (player().fallDistance < 0.31F) {
                                        // Motion XZ
                                        player().jumpMovementFactor = player().moveStrafing == 0F ? 0.027F : 0.021F;
                                        player().motionX *= 1.001;
                                        player().motionZ *= 1.001;

                                        // Motion Y
                                        if (!player().isCollidedHorizontally)
                                            player().motionY -= 0.014999993F;
                                    } else
                                        player().jumpMovementFactor = 0.02F;
                                    break;
                                }
                                case "3.5.0": { //LiquidBounce Skid :)
                                    player().jumpMovementFactor += 0.00208F;
                                    if (player().onGround) {
                                        player().jump();
                                        player().motionX *= 1.0118F;
                                        player().motionZ *= 1.0118F;
                                    } else {
                                        player().motionY -= 0.0147F;

                                        player().motionX *= 1.00138F;
                                        player().motionZ *= 1.00138F;
                                    }
                                    break;
                                }
                                case "3.6.4": { //LiquidBounce Skid :)
                                    player().setSprinting(true);
                                    if (player().onGround) {
                                        if (!mc().gameSettings.keyBindJump.getIsKeyPressed()) {
                                            player().jump();
                                        }
                                        player().motionZ *= 1.01;
                                        player().motionX *= 1.01;
                                    }
                                    player().motionY -= 0.0149;
                                    break;
                                }
                                case "4.2": { //My FusionX Skid :) TODO: add
                                    if (!player().onGround)
                                        return;
                                    //ChatUtils.message(prevYaw + " " + p.yaw + " " + p.sidewaysSpeed + " " + (prevYaw-5<p.yaw && prevYaw+5>p.yaw));
                                    if ((player().prevRotationYaw - 5 < player().rotationYaw && player().prevRotationYaw + 5 > player().rotationPitch) && player().moveStrafing == 0) {
                                        MUtil.strafe(MUtil.getSpeed() + 0.23f);
                                    } else {
                                        MUtil.strafe();
                                    }
                                    break;
                                }
                                case "4.2Hop": { //My FusionX Skid :) TODO: add
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }*/
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

    static class ModeOption extends OptChc {
        String visible;

        public ModeOption(String name, String... values) {
            super(name + "Mode", "The mode for speed mode of " + name + ".", values);
            this.visible = name;
        }

        @Override
        public boolean visible() {
            String mode = module.STRING("mode");
            return mode.equalsIgnoreCase(this.visible);
        }
    }

    static class CustomOption extends OptOtr {
        String visible;

        public CustomOption(String name, String description) {
            this(name, description, "custom");
        }

        public CustomOption(String name, String description, String visible) {
            super(name, description);
            this.visible = visible;
        }

        @Override
        public boolean visible() {
            return module.STRING("mode").equalsIgnoreCase(visible);
        }
    }
}
