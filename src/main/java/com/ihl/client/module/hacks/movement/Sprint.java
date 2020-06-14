package com.ihl.client.module.hacks.movement;

import com.ihl.client.Helper;
import com.ihl.client.event.Event;
import com.ihl.client.event.EventHandler;
import com.ihl.client.event.EventPlayerLiving;
import com.ihl.client.module.Category;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.Option;
import com.ihl.client.module.option.ValueBoolean;

@EventHandler(events = {EventPlayerLiving.class})
public class Sprint extends Module {

    public Sprint() {
        super("Sprint", "Automatically force sprinting", Category.MOVEMENT, "NONE");
        options.put("directional", new Option("Directional", "Sprint in all directions", new ValueBoolean(true), Option.Type.BOOLEAN));
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    protected void onEvent(Event event) {
        boolean canSprint = !Helper.player().isCollidedHorizontally && !Helper.player().isSneaking() && (Helper.player().moveForward != 0 || Helper.player().moveStrafing != 0);
        boolean directional = Option.get(options, "directional").BOOLEAN();

        if (event instanceof EventPlayerLiving) {
            EventPlayerLiving e = (EventPlayerLiving) event;
            if (e.type == Event.Type.PRE) {
                if (canSprint) {
                    if ((!directional && Helper.player().moveForward > 0) || directional) {
                        Helper.player().setSprinting(true);
                    }
                }
            }
        }
    }

}
