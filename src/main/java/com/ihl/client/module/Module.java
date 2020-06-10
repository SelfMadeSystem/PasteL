package com.ihl.client.module;

import com.ihl.client.Helper;
import com.ihl.client.commands.*;
import com.ihl.client.event.*;
import com.ihl.client.module.hacks.combat.*;
import com.ihl.client.module.hacks.misc.*;
import com.ihl.client.module.hacks.movement.*;
import com.ihl.client.module.hacks.player.*;
import com.ihl.client.module.hacks.render.*;
import com.ihl.client.module.hacks.world.AntiCactus;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;
import joptsimple.internal.Strings;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class Module extends Helper {

    public static Map<String, Module> modules = new LinkedHashMap<>();
    protected static int currentId = 1;
    protected static RUtils rUtils;

    public static void init() {
        rUtils = new RUtils();
        new AimAssist();
        new AimBot();
        new AntiBot();
        new AntiCactus();
        //new Aura("Aura", "Kill everything near you", Category.COMBAT, "NONE");
        new AutoClicker();
        new AutoPotion();
        new Blink();
        new CameraClip();
        new Chams();
        new Commands();
        new Console();
        new CPS();
        new Criticals();
        new Damage();
        new Debugger();
        new Distance();
        new ESP();
        new Example();
        new FastClimb();
        new FastUse();
        new Fly();
        new Freecam();
        new Friends();
        new Fullbright();
        new Glide();
        new GUI();
        new Jesus();
        new MenuWalk();
        new Nametags();
        new Noclip();
        new NoSlow();
        new NoView();
        new Plugins();
        new Phase();
        new Pinger();
        new Sneak();
        new Speed();
        new SkinDerp();
        new Sprint();
        new Step();
        new StorageESP();
        new Swing();
        new Teleport();
        new Tracer();
        new TriggerBot();
        new VClip();
        new Velocity();
    }

    public static Module get(String key) {
        Module module =  modules.get(key);
        if (module == null)
            module = new Module();
        return module;
    }

    public static List<String> enabled() {
        List<String> list = new ArrayList<>();
        for (String s : modules.keySet()) {
            Module module = get(s);
            if (module.active) {
                list.add(s);
            }
        }
        return list;
    }

    public static List<String> category(Category category) {
        List<String> list = new ArrayList<>();
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
            Class<? extends Module> clazz = module.getClass();
            if (clazz.isAnnotationPresent(EventHandler.class)) {
                EventHandler handler = clazz.getAnnotation(EventHandler.class);
                for (Class<? extends Event> type : handler.events()) {
                    if (type == event.getClass()) {
                        module.onEvent(event);
                        break;
                    }
                }
            }
        }
    }

    public static void optionChange(EventOption eventOption) {
        event(eventOption, false);
        for (String key : modules.keySet()) {
            Module module = modules.get(key);
            if (eventOption.module.name.equals(module.name))
                module.optionChanged(eventOption);
        }
    }

    public int id;
    public String name, desc;
    public Category category;
    public int color;
    public boolean active;
    //        name     option   Note: Name also refers to the icon.
    public Map<String, Option> options = new LinkedHashMap<>();
    public ResourceLocation icon;
    //protected Class[] events = new Class[]{};

    private Module() {
        this.name = "";
        this.desc = "";
        this.category = Category.MISC;
    }

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
        List<String> usages = new ArrayList<>();
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
