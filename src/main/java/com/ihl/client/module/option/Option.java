package com.ihl.client.module.option;

import com.ihl.client.commands.exceptions.ArgumentException;
import com.ihl.client.event.EventOption;
import com.ihl.client.module.Module;
import com.ihl.client.util.*;
import joptsimple.internal.Strings;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class Option {

    public enum Type {
        BOOLEAN("<true|false>"),
        CHOICE("<%s>"),
        KEYBIND("<key>"),
        LIST("<value>"),
        NUMBER("<number>"),
        OTHER(""),
        STRING("<text>");

        public String usage;

        Type(String usage) {
            this.usage = usage;
        }
    }

    public static List<String> getAllS(Map<String, Option> options, String separator) {
        List<String> toReturn = new ArrayList<>();
        for (String st : options.keySet()) {
            System.out.println("getAllS:" + st + "  " + options.get(st) + "  " + options.get(st).type + "  " + options.get(st).getAll(separator));
            if (options.get(st) != null)
                toReturn.addAll(options.get(st).getAll(separator));
        }
        return toReturn;
    }

    public static Option get(Map<String, Option> options, List<String> keys) {
        Object[] owo = keys.toArray();
        String[] uwu = new String[owo.length];
        System.arraycopy(owo, 0, uwu, 0, owo.length);
        return get(options, uwu);
    }

    public static Option get(Map<String, Option> options, String key) {
        return options.get(key);
    }

    public static Option get(Map<String, Option> options, String key, String key2) {
        try {
            return options.get(key).options.get(key2);
        } catch (NullPointerException e) {
            throw new NullPointerException("Fucked shit up when getting: " + options + ":" + key + ":" + key2);
        }
    }

    public static Option get(Map<String, Option> options, String... keys) {
        Option toReturn = options.get(keys[0]);
        for (int i = 1; i < keys.length; i++) {
            if (toReturn != null)
                if (toReturn.options != null)
                    if (toReturn.options.get(keys[i]) != null)
                        toReturn = toReturn.options.get(keys[i]);
        }
        return toReturn;
    }

    public static Option get(Option option, String... keys) {
        Option toReturn = option;
        for (int i = 1; i < keys.length; i++) {
            toReturn = toReturn.options.get(keys[i]);
        }
        return toReturn;
    }

    public static void setOptionValue(Option option, String arg, boolean... trigger) throws Exception {
        Object value = null;
        String message = String.format("[v]%s [t]set to [v]%s", option.name, arg);
        switch (option.type) {
            case BOOLEAN:
                try {
                    if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false")) {
                        value = Boolean.parseBoolean(arg);
                    } else {
                        throw new ArgumentException();
                    }
                } catch (Exception e) {
                    throw new ArgumentException();
                }
                break;
            case CHOICE:
                int is = -1;
                String[] list = ((ValueChoice) option.value).list;
                for (int i = 0; i < list.length; i++) {
                    if (list[i].equalsIgnoreCase(arg)) {
                        is = i;
                    }
                }
                if (is != -1) {
                    value = list[is];
                } else {
                    throw new ArgumentException();
                }
                break;
            case KEYBIND:
                value = arg.toUpperCase();
                break;
            case NUMBER:
                try {
                    value = Double.parseDouble(arg);
                } catch (Exception e) {
                    throw new ArgumentException();
                }
                break;
            case OTHER:
                value = "";
                break;
            case STRING:
                value = arg;
                break;
            case LIST:
                List<String> l = new ArrayList();
                l.addAll((List<String>) option.value.getValue());

                if (l.contains(arg)) {
                    l.remove(arg);
                    message = String.format("[v]%s [t]removed from [v]%s", arg, option.name);
                } else {
                    l.add(arg);
                    message = String.format("[v]%s [t]added to [v]%s", arg, option.name);
                }

                value = l;
                break;
        }

        if (value != null) {
            if (trigger.length == 0 || !trigger[0])
                option.setValue(value);
            else
                option.setValueNoTrigger(value);
            ChatUtil.send(message);
        }
    }

    public List<Option> parents;
    public String name, desc;
    public Module module;
    private Value value;
    public Type type;
    public ResourceLocation icon;
    public Map<String, Option> options = new LinkedHashMap<>();
    public int color;

    public Option(Module module, String name, String desc, Value value, Type type) {
        this(module, name, desc, value, type, new ArrayList<>(), new ArrayList<>());
    }

    public Option(Module module, String name, String desc, Value value, Type type, Option... options) {
        this(module, name, desc, value, type, Arrays.asList(options), new ArrayList<>());
    }

    public Option(Module module, String name, String desc, Value value, Type type, List<Option> options) {
        this(module, name, desc, value, type, options, new ArrayList<>());
    }

    public Option(Module module, String name, String desc, Value value, Type type, List<Option> options, List<Option> parents) {
        this.module = module;
        this.name = name;
        this.desc = desc;
        this.value = value;
        this.type = type;
        this.parents = parents;
        this.value.option = this;
        if (options != null) {
            for (Option option : options) {
                this.options.put(option.name.toLowerCase().replaceAll(" ", ""), option);
            }
        }
        icon = new ResourceLocation("client/icons/option/" + (name.toLowerCase().replaceAll(" ", "")) + ".png");
        color = ColorUtil.rainbow((long) (Math.random() * 10000000000D), 1f).getRGB();
        List<Option> p = new ArrayList<>(this.parents);
        p.add(this);
        for (Map.Entry<String, Option> e : this.options.entrySet()) {
            Option o = e.getValue();
            o.parents = p;
        }
    }

    public boolean save() {
        return true;
    }

    public List<String> getAll() {
        return getAll(" ");
    }

    public List<Option> getSubOpt() {
        List<Option> toReturn = new ArrayList<>();
        for (String st : this.options.keySet()) {
            if (this.options.get(st) != null)
                toReturn.add(this.options.get(st));
        }
        return toReturn;
    }

    public List<String> getAll(String separator) {
        List<String> toReturn = new ArrayList<>();
        for (String st : this.options.keySet())
            if (this.options.get(st).type != Type.OTHER)
                toReturn.add(st);

        List<Option> psubOpts = new ArrayList<>(getSubOpt());
        List<Option> subOpts = new ArrayList<>(getSubOpt());
        while (subOpts.size() > 0) {
            Option now = subOpts.get(0);
            subOpts.remove(0);
            System.out.println(now.getSubOpt());
            subOpts.addAll(now.getSubOpt());
            psubOpts.addAll(now.getSubOpt());
            for (String st : now.options.keySet()) {
                System.out.println("getAll: " + this.name + "  " + now.name + "  " + now.options.get(st));
                if (now.type != Type.OTHER) {
                    StringBuilder toAdd = new StringBuilder();
                    for (Option o : now.parents) {
                        toAdd.append(o.name).append(separator);
                    }
                    System.out.println(toAdd + st);
                    toReturn.add(toAdd + st);
                }
            }
        }
        System.out.println(psubOpts);
        return toReturn;
    }

    public boolean BOOLEAN() {
        if (value instanceof ValueBoolean) {
            return (boolean) value.getValue();
        }
        return value.getValue().equals("true");
    }

    public double DOUBLE() {
        if (value instanceof ValueDouble) {
            return (double) value.getValue();
        }
        return 0;
    }

    public int INTEGER() {
        return (int) DOUBLE();
    }

    public String STRING() {
        if (type == Type.LIST) {
            return Strings.join(LIST(), ",");
        }
        return value.getValue().toString();
    }

    public String CHOICE() {
        return STRING();
    }

    public List<String> LIST() {
        return (List<String>) value.getValue();
    }

    // Option Setters
    // name, description, defaultValue
    // Returns created option.
    public Option addBoolean(String name, String description, boolean defaultValue) {
        Option opt = new Option(this.module, name, description, new ValueBoolean(defaultValue), Option.Type.BOOLEAN);
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        return opt;
    }

    public Option addInteger(String name, String description, int defaultValue, int min, int max) {
        return addDouble(name, description, defaultValue, min, max, 1);
    }

    public Option addDouble(String name, String description, double defaultValue, double min, double max, double increments) {
        Option opt = new Option(this.module, name, description, new ValueDouble(defaultValue, new double[]{min, max}, increments), Option.Type.NUMBER);
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        return opt;
    }

    public Option addString(String name, String description, String defaultValue) {
        Option opt = new Option(this.module, name, description, new ValueString(defaultValue), Option.Type.STRING);
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        return opt;
    }

    public Option addChoice(String name, String description, String... values) {
        Option opt = new Option(this.module, name, description, new ValueChoice(0, values), Option.Type.CHOICE);
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        return opt;
    }

    public Option addOther(String name, String description) {
        Option opt = new OptNoS(this.module, name, description, new ValueString(""), Option.Type.OTHER);
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        return opt;
    }
    public OptNoS addBooleanNoS(String name, String description, boolean defaultValue) {
        OptNoS opt = new OptNoS(this.module, name, description, new ValueBoolean(defaultValue), OptNoS.Type.BOOLEAN);
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        return opt;
    }

    public OptNoS addIntegerNoS(String name, String description, int defaultValue, int min, int max) {
        return addDoubleNoS(name, description, defaultValue, min, max, 1);
    }

    public OptNoS addDoubleNoS(String name, String description, double defaultValue, double min, double max, double increments) {
        OptNoS opt = new OptNoS(this.module, name, description, new ValueDouble(defaultValue, new double[]{min, max}, increments), OptNoS.Type.NUMBER);
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        return opt;
    }

    public OptNoS addStringNoS(String name, String description, String defaultValue) {
        OptNoS opt = new OptNoS(this.module, name, description, new ValueString(defaultValue), OptNoS.Type.STRING);
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        return opt;
    }

    public OptNoS addChoiceNoS(String name, String description, String... values) {
        OptNoS opt = new OptNoS(this.module, name, description, new ValueChoice(0, values), OptNoS.Type.CHOICE);
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        return opt;
    }

    public OptNoS addOtherNoS(String name, String description) {
        OptNoS opt = new OptNoS(this.module, name, description, new ValueString(""), OptNoS.Type.OTHER);
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        return opt;
    }

    //Option Getters.
    // names
    public Object OBJECT(String... names) {
        for (int i = 0; i < names.length; i++)
            names[i] = names[i].toLowerCase().replaceAll(" ", "").trim();
        Option currentOpt = options.get(names[0]);
        for (int i = 1; i < names.length; i++) {
            currentOpt = currentOpt.options.get(names[i]);
            if (currentOpt == null)
                throw new NullPointerException("Not value of name:" + names[i]);
        }
        return currentOpt.getValue();
    }
    public boolean BOOLEAN(String... names) {
        return (boolean) OBJECT(names);
    }
    public int INTEGER(String... names) {
        return ((Double) OBJECT(names)).intValue();
    }
    public float FLOAT(String... names) {
        return (float) OBJECT(names);
    }
    public double DOUBLE(String... names) {
        return (double) OBJECT(names);
    }
    public String STRING(String... names) {
        return (String) OBJECT(names);
    }


    /*---------------------------------------------------------------*/
    public Option getOption(String name) {
        return options.get(name);
    }

    public Object getValue(String option) {
        return options.get(option).getValue();
    }

    public Object getValue(String option, String value) {
        return options.get(option).getValue(value);
    }

    public Object getValue() {
        return value.getValue();
    }

    public void setValue(Object value) {
        setValueNoTrigger(value);
        Module.optionChange(new EventOption(this.module, this, String.valueOf(this.getValue())));
    }

    public void setValueNoTrigger(Object value) {
        this.value.setValue(value);
    }

    public Value getTValue() {
        return value;
    }

    public void setTValue(Value value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name + ":" + STRING();
    }
}
