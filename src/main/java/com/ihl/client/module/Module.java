package com.ihl.client.module;

import com.ihl.client.Helper;
import com.ihl.client.commands.*;
import com.ihl.client.event.*;
import com.ihl.client.module.hacks.*;
import com.ihl.client.module.hacks.combat.*;
import com.ihl.client.module.hacks.misc.Debugger;
import com.ihl.client.module.hacks.movement.*;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;
import joptsimple.internal.Strings;
import net.minecraft.util.ResourceLocation;

import java.lang.annotation.Annotation;
import java.util.*;

public class Module extends Helper {

    public static Map<String, Module> modules = new LinkedHashMap();
    protected static int currentId = 1;
    protected static RUtils rUtils;

    public static void init() {
        rUtils = new RUtils();
        new AimAssist();
        new AimBot();
        new AntiBot();
        new AntiCactus("Anti Cactus", "Don't get hurt when standing on a cactus", Category.PLAYER, "NONE");
        //new Aura("Aura", "Kill everything near you", Category.COMBAT, "NONE");
        new AutoClicker();
        new AutoPotion("Auto Potion", "Splash health potions when needed", Category.COMBAT, "NONE");
        new Blink("Blink", "Simulate lag", Category.PLAYER, "NONE");
        new CameraClip("Camera Clip", "Allow third person camera to clip into blocks", Category.RENDER, "NONE");
        new Chams("Chams", "Render entities through blocks", Category.RENDER, "NONE");
        new Commands("Commands", "Enable in-game chat commands", Category.MISC, "NONE");
        new Console("Console", "Enable GUI console for command input", Category.MISC, "NONE");
        new CPS("CPS", "Monitor your clicks-per-second", Category.MISC, "NONE");
        new Criticals("Criticals", "Deal critical hits", Category.COMBAT, "NONE");
        new Damage("Damage", "Force yourself to take damage", Category.PLAYER, "NONE");
        new Debugger();
        new Distance("Distance", "Change the third person camera distance", Category.RENDER, "NONE");
        new ESP("ESP", "Render outlines around entities", Category.RENDER, "NONE");
        new FastClimb("FastClimb", "Climb ladders and vines faster", Category.MOVEMENT, "NONE");
        new FastUse("FastUse", "Finish using items faster", Category.PLAYER, "NONE");
        new Fly();
        new Freecam("Freecam", "Ghost through blocks client-side", Category.MOVEMENT, "NONE");
        new Friends("Friends", "Whitelist friends for combat mods", Category.MISC, "NONE");
        new Fullbright("Fullbright", "Brighten up the world", Category.RENDER, "NONE");
        new Glide("Glide", "Slowly decent to the ground", Category.MOVEMENT, "NONE");
        new GUI("GUI", "Open the radial GUI", Category.RENDER, "RSHIFT");
        new Jesus("Jesus", "Walk on water", Category.MOVEMENT, "NONE");
        new MenuWalk("Menu Walk", "Walk when in a GUI", Category.MOVEMENT, "NONE");
        new Nametags("Nametags", "Render player nametags through blocks", Category.RENDER, "NONE");
        new Noclip("Noclip", "Clip through all blocks", Category.MOVEMENT, "NONE");
        new NoSlow("No Slow", "Prevent slowing down when using items", Category.MOVEMENT, "NONE");
        new NoView("No View", "Prevent the server changing your view direction", Category.PLAYER, "NONE");
        new Plugins("Plugins", "Get a list of server plugins", Category.MISC, "NONE");
        new Phase();
        new Pinger("Pinger", "Spoof a perfect ping of 0", Category.MISC, "NONE");
        new Sneak("Sneak", "Enable server-side sneaking", Category.MOVEMENT, "NONE");
        new Speed();
        new SkinDerp("Skin Derp", "Make your clothing layers toggle", Category.MISC, "NONE");
        new Sprint("Sprint", "Automatically force sprinting", Category.MOVEMENT, "NONE");
        new Step();
        new StorageESP("Storage ESP", "Render outlines around storage blocks", Category.RENDER, "NONE");
        new Swing("Swing", "Reset the swing animation faster", Category.PLAYER, "NONE");
        new Teleport();
        new Tracer("Tracer", "Render lines to entities", Category.RENDER, "NONE");
        new TriggerBot();
        new VClip("VClip", "Clip vertically down through blocks", Category.MOVEMENT, "NONE");
        new Velocity("Velocity", "Change the knockback velocity", Category.COMBAT, "NONE");
    }

    public static Module get(String key) {
        Module module =  modules.get(key);
        if (module == null)
            module = new Module("", "", Category.MISC, "NONE");
        return module;
    }

    public static List<String> enabled() {
        List<String> list = new ArrayList();
        for (String s : modules.keySet()) {
            Module module = get(s);
            if (module.active) {
                list.add(s);
            }
        }
        return list;
    }

    public static List<String> category(Category category) {
        List<String> list = new ArrayList();
        for (String s : modules.keySet()) {
            Module module = get(s);
            if (module.category == category) {
                list.add(s);
            }
        }
        return list;
    }

    public static void tickMods() {
        for (String key : modules.keySet()) {
            Module module = modules.get(key);
            module.tick();
        }
    }

    public static void event(Event event, boolean reverse) {
        if (Helper.player() == null) {
            return;
        }
        if (event instanceof EventPacket)
            rUtils.onSentPacket((EventPacket) event);
        else if (event instanceof EventPlayerUpdate)
            rUtils.onUpdate();
        List<String> enabled = enabled();
        for (int i = reverse ? enabled.size() - 1 : 0; reverse ? i >= 0 : i < enabled.size(); i += reverse ? -1 : 1) {
            Module module = get(enabled.get(i));
            Class clazz = module.getClass();
            if (clazz.isAnnotationPresent(EventHandler.class)) {
                Annotation annotation = clazz.getAnnotation(EventHandler.class);
                EventHandler handler = (EventHandler) annotation;
                for (Class type : handler.events()) {
                    if (type == event.getClass()) {
                        module.onEvent(event);
                        break;
                    }
                }
            }
        }
    }

    public static void optionChange(EventOption eventOption) {
        for (String key : modules.keySet()) {
            Module module = modules.get(key);
            module.optionChanged(eventOption);
        }
    }

    public int id;
    public String name, desc;
    public Category category;
    public int color;
    public boolean active;
    //        name     option   Note: Name also refers to the icon.
    public Map<String, Option> options = new LinkedHashMap();
    public ResourceLocation icon;
    protected Class[] events = new Class[]{};

    public Module(String name, String desc, Category category, String keybind) {
        this.id = currentId++;
        this.name = name;
        this.desc = desc;
        this.category = category;

        options.put("keybind", new Option(this, "Keybind", "Module toggle keybind", new ValueString(keybind), Option.Type.KEYBIND));

        String base = name.toLowerCase().replaceAll(" ", "");
        modules.put(base, this);

        icon = new ResourceLocation("client/icons/module/" + base + ".png");
    }

    protected void initCommands(String base) {
        List<String> usages = new ArrayList();
        usages.add(base);
        for (String key : options.keySet()) {
            Option option = Option.get(options, key);
            for (String key2 : option.options.keySet()) {
                Option option2 = Option.get(options, key, key2);
                usages.add(String.format("%s %s %s %s", name.replaceAll(" ", ""), option.name.replaceAll(" ", ""), option2.name.replaceAll(" ", ""), option2.type == Option.Type.CHOICE ? String.format(option2.type.usage, Strings.join(((ValueChoice) option2.getTValue()).list, "|")) : option2.type.usage).toLowerCase());
            }
            usages.add(String.format("%s %s %s", name.replaceAll(" ", ""), option.name.replaceAll(" ", ""), option.type == Option.Type.CHOICE ? String.format(option.type.usage, Strings.join(((ValueChoice) option.getTValue()).list, "|")) : option.type.usage).toLowerCase());
        }

        Command.commands.put(base, new CommandModule(base, usages));
    }

    public void enable() {
        color = ColorUtil.rainbow((long) (Math.random() * 10000000000D), 1f).getRGB();
        active = true;
    }

    public void disable() {
        active = false;
    }

    public void toggle() {
        if (active) {
            disable();
        } else {
            enable();
        }
    }

    public String getDisplay() {
        return name;
    }

    protected void tick() {
        for (String key : options.keySet()) {
            Option option = Option.get(options, key);
            limitOption(option, true);
            if (!option.options.isEmpty()) {
                for (String subKey : option.options.keySet()) {
                    Option subOption = Option.get(options, key, subKey);
                    limitOption(subOption, true);
                }
            }
        }
    }

    protected void onEvent(Event event) {
    }

    private void limitOption(Option option, boolean... disable) {
        if (option.type == Option.Type.NUMBER) {
            ValueDouble value = (ValueDouble) option.getTValue();
            double val = Math.min(Math.max(value.limit[0], (double) value.getValue()), value.limit[1]);
            val = MathUtil.roundInc(val, value.step);
            if (disable.length > 0 && disable[0])
                option.setValueNoTrigger(val);
            else
                option.setValue(val);
        }
    }

    public void optionChanged(EventOption eventOption) {
    }
}
