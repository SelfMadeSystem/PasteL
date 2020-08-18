package com.ihl.client.module.option;

import com.ihl.client.commands.exceptions.ArgumentException;
import com.ihl.client.event.EventOption;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.options.OptNoS;
import com.ihl.client.util.*;
import joptsimple.internal.Strings;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class Option {

    public Option parent;
    public String name, desc;
    public Module module;
    public Type type;
    public ResourceLocation icon;
    public LHM options = new LHM(module);
    public int color;
    public int weight;
    private Value value;

    public Option(String name, String desc, Value value, Type type) {
        this(name, desc, value, type, new ArrayList<>(), null);
    }

    public Option(String name, String desc, Value value, Type type, Option... options) {
        this(name, desc, value, type, Arrays.asList(options), null);
    }

    public Option(String name, String desc, Value value, Type type, List<Option> options) {
        this(name, desc, value, type, options, null);
    }

    public Option(String name, String desc, Value value, Type type, List<Option> options, Option parent) {
        this.name = name;
        this.desc = desc;
        this.value = value;
        this.type = type;
        //System.out.println(parent);
        this.parent = parent;
        this.value.option = this;
        if (options != null) {
            for (Option option : options) {
                this.options.put(option.name.toLowerCase().replaceAll(" ", ""), option);
            }
        }
        icon = new ResourceLocation("client/icons/option/" + (name.toLowerCase().replaceAll(" ", "")) + ".png");
        color = ColorUtil.rainbow((long) (Math.random() * 10000000000D), 1f).getRGB();
        /*List<Option> p = new ArrayList<>(this.parent);
        p.add(this);
        for (Map.Entry<String, Option> e : this.options.entrySet()) {
            Option o = e.getValue();
            o.setParents(p); // FIXME: 2020-06-12 Set Parents or put in creating new Options :)
        }*/
    }

    public static List<String> getAllS(Map<String, Option> options, String separator) {
        List<String> toReturn = new ArrayList<>();
        for (String st : options.keySet()) {
            //System.out.println("getAllS:" + st + "  " + options.get(st) + "  " + options.get(st).type + "  " + options.get(st).getAll(separator));
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
            case RANGE:
                try {
                    String[] args = arg.split(",", 2);
                    value = new double[]{Double.parseDouble(args[0]), Double.parseDouble(args[1])};
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

    public boolean save() {
        return true;
    }

    /*public void setParents(List<Option> parent) {// TODO: 2020-06-12 idfk
        this.parent = parent;
        for (Map.Entry<String, Option> entry: this.options.entrySet()) {
            List<Option> p = new ArrayList<>(this.parent);
            p.add(this);
            entry.getValue().setParents(p);
        }
    }*/

    public boolean visible() {
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
            //System.out.println(now.getSubOpt());
            subOpts.addAll(now.getSubOpt());
            psubOpts.addAll(now.getSubOpt());
            for (String st : now.options.keySet()) {
                //System.out.println("getAll: " + this.name + "  " + now.name + "  " + now.options.get(st));
                if (now.type != Type.OTHER) {
                    StringBuilder toAdd = new StringBuilder();
                    toAdd.append(now.parent.name).append(separator);
                    //System.out.println(toAdd + st);
                    toReturn.add(toAdd + st);
                }
            }
        }
        //System.out.println(psubOpts);
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

    public double MIN() {
        if (value instanceof ValueRange) {
            return ((double[]) value.getValue())[0];
        }
        return 0;
    }

    public double MAX() {
        if (value instanceof ValueRange) {
            return ((double[]) value.getValue())[1];
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
        return value.stringValue();
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
        return addOption(new Option(name, description, new ValueBoolean(defaultValue), Option.Type.BOOLEAN, new ArrayList<>(), parent));
    }

    public Option addInteger(String name, String description, int defaultValue, int min, int max) {
        return addDouble(name, description, defaultValue, min, max, 1);
    }

    public Option addDouble(String name, String description, double defaultValue, double min, double max, double increments) {
        return addOption(new Option(name, description, new ValueDouble(defaultValue, new double[]{min, max}, increments), Option.Type.NUMBER, new ArrayList<>(), parent));
    }

    public Option addString(String name, String description, String defaultValue) {
        return addOption(new Option(name, description, new ValueString(defaultValue), Option.Type.STRING, new ArrayList<>(), parent));
    }

    public Option addChoice(String name, String description, String... values) {
        return addOption(new Option(name, description, new ValueChoice(0, values), Option.Type.CHOICE, new ArrayList<>(), parent));
    }

    public Option addOther(String name, String description) {
        return addOption(new OptNoS(this.module, name, description, new ValueString(""), Option.Type.OTHER, new ArrayList<>(), parent));
    }

    public OptNoS addBooleanNoS(String name, String description, boolean defaultValue) {
        return (OptNoS) addOption(new OptNoS(this.module, name, description, new ValueBoolean(defaultValue), Type.BOOLEAN, new ArrayList<>(), parent));
    }

    public OptNoS addIntegerNoS(String name, String description, int defaultValue, int min, int max) {
        return addDoubleNoS(name, description, defaultValue, min, max, 1);
    }

    public OptNoS addDoubleNoS(String name, String description, double defaultValue, double min, double max, double increments) {
        return (OptNoS) addOption(new OptNoS(this.module, name, description, new ValueDouble(defaultValue, new double[]{min, max}, increments), OptNoS.Type.NUMBER, new ArrayList<>(), parent));
    }

    public OptNoS addStringNoS(String name, String description, String defaultValue) {
        return (OptNoS) addOption(new OptNoS(this.module, name, description, new ValueString(defaultValue), OptNoS.Type.STRING, new ArrayList<>(), parent));
    }

    public OptNoS addChoiceNoS(String name, String description, String... values) {
        return (OptNoS) addOption(new OptNoS(this.module, name, description, new ValueChoice(0, values), OptNoS.Type.CHOICE, new ArrayList<>(), parent));
    }

    public void addOptions(Option... options) {
        for (Option option : options) {
            addOption(option);
        }
    }

    public Option removeOption(String optionName) {
        return options.remove(optionName.toLowerCase().replaceAll(" ", ""));
    }

    public Option addOption(Option option) {
        option.parent = this;
        option.module = this.module;
        if (options.module == null)
            options.module = this.module;
        options.put(option.name.toLowerCase().replaceAll(" ", ""), option);
        resetOptionMap();
        option.weight = weight++;
        return option;
    }

    public Option addOptionIfAbsent(Option option) {
        option.parent = this;
        option.module = this.module;
        if (options.module == null)
            options.module = this.module;
        options.putIfAbsent(option.name.toLowerCase().replaceAll(" ", ""), option);
        resetOptionMap();
        option.weight = weight++;
        return option;
    }

    public void resetOptionMap() {
        LHM optionMap = new LHM(module);
        for (Map.Entry<String, Option> entry : options.entrySet()) {
            optionMap.put(entry.getValue().name.toLowerCase().replaceAll(" ", ""), entry.getValue());
        }
        options = optionMap;
    }

    //Option Getters.
    // names
    private Object nullObject(String notFound, String... names) {
        new OptionNonExistentException(notFound, names, this.name).printStackTrace();
        return null;
    }

    private static class OptionNonExistentException extends Exception {
        public OptionNonExistentException(String notFound, String[] names, String name) {
            super("No option found (" + notFound + ") getting (" + Arrays.toString(names) + ") module (" + name + ")");
        }
    }

    public Object OBJECT(String... names) {
        for (int i = 0; i < names.length; i++)
            names[i] = names[i].toLowerCase().replaceAll(" ", "").trim();
        Option currentOpt = options.get(names[0]);
        if (currentOpt == null) return nullObject(names[0], names);
        for (int i = 1; i < names.length; i++) {
            currentOpt = currentOpt.options.get(names[i]);
            if (currentOpt == null) return nullObject(names[i], names);
        }
        return currentOpt.getValue();
    }

    public boolean BOOLEAN(String... names) {
        Object obj = OBJECT(names);
        if (obj == null) return false;
        return (boolean) OBJECT(names);
    }

    public int INTEGER(String... names) {
        Object obj = OBJECT(names);
        if (obj == null) return 0;
        return ((Double) OBJECT(names)).intValue();
    }

    public float FLOAT(String... names) {
        Object obj = OBJECT(names);
        if (obj == null) return 0;
        return ((Double) OBJECT(names)).floatValue();
    }

    public double DOUBLE(String... names) {
        Object obj = OBJECT(names);
        if (obj == null) return 0;
        return (double) OBJECT(names);
    }

    public String STRING(String... names) {
        Object obj = OBJECT(names);
        if (obj == null) return "";
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

    public enum Type {
        BOOLEAN("<true|false>"),
        CHOICE("<%s>"),
        KEYBIND("<key>"),
        LIST("<value>"),
        NUMBER("<number>"),
        RANGE("<min,max>"),
        OTHER(""),
        STRING("<text>");

        public String usage;

        Type(String usage) {
            this.usage = usage;
        }
    }
}
