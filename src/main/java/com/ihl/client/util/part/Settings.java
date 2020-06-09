package com.ihl.client.util.part;

import com.ihl.client.commands.Command;
import com.ihl.client.commands.CommandOption;
import com.ihl.client.module.option.Option;
import com.ihl.client.module.option.ValueBoolean;
import com.ihl.client.module.option.ValueChoice;
import com.ihl.client.module.option.ValueString;
import joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {

    public static Map<String, Option> options = new HashMap();

    public static void init() {
        options.put("guicolor", new Option(null, "GUI Color", "Color of the GUI", new ValueString("00B3FF"), Option.Type.STRING));
        options.put("arraylist", new Option(null, "Array List", "List of active mods", new ValueBoolean(true), Option.Type.BOOLEAN,
          new Option(null, "Color", "List coloring mode", new ValueChoice(0, "rainbow", "categorical"), Option.Type.CHOICE),
          new Option(null, "Sort", "List ordering mode", new ValueChoice(1, "alphabetical", "length"), Option.Type.CHOICE)));

        for(String key : options.keySet()) {
            List<String> usages = new ArrayList();
            Option option = Option.get(options, key);
            for(String key2 : option.options.keySet()) {
                Option option2 = Option.get(options, key, key2);
                usages.add(String.format("%s %s %s", option.name.replaceAll(" ", ""), option2.name.replaceAll(" ", ""), option2.type == Option.Type.CHOICE ? String.format(option2.type.usage, Strings.join(((ValueChoice) option2.getTValue()).list, "|")) : option2.type.usage).toLowerCase());
            }
            usages.add(String.format("%s %s", option.name.replaceAll(" ", ""), option.type == Option.Type.CHOICE ? String.format(option.type.usage, Strings.join(((ValueChoice) option.getTValue()).list, "|")) : option.type.usage).toLowerCase());

            Command.commands.put(key, new CommandOption(key, usages));
        }
    }

}
