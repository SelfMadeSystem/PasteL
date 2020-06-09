package com.ihl.client.module.hacks.movement;

import com.ihl.client.event.EventHandler;
import com.ihl.client.module.hacks.Category;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.Option;
import com.ihl.client.module.option.ValueDouble;

@EventHandler(events = {})

public class FastClimb extends Module {

    public FastClimb(String name, String desc, Category category, String keybind) {
        super(name, desc, category, keybind);
        options.put("speed", new Option(this, "Speed", "Climbing speed multiplier", new ValueDouble(1.5, new double[] {0.1, 5}, 0.1), Option.Type.NUMBER));
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }
}
