package com.ihl.client.module.hacks.movement;

import com.ihl.client.event.EventHandler;
import com.ihl.client.module.Category;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.Option;
import com.ihl.client.module.option.ValueDouble;

@EventHandler(events = {})

public class FastClimb extends Module {

    public FastClimb() {
        super("FastClimb", "Climb ladders and vines faster", Category.MOVEMENT, "NONE");
        options.put("speed", new Option("Speed", "Climbing speed multiplier", new ValueDouble(1.5, new double[] {0.1, 5}, 0.1), Option.Type.NUMBER));
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }
}
