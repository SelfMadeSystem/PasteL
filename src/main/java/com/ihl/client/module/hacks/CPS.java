package com.ihl.client.module.hacks;

import com.ihl.client.event.*;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;

import java.util.*;

@EventHandler(events = {EventMouse.class, EventRender.class})
public class CPS extends Module {

    public static double cps;
    private final Set<Long> cpsList = new HashSet<>();

    public CPS() {
        super("CPS", "Monitor your clicks-per-second", Category.MISC, "NONE");
        options.put("floatlimit", new Option(this, "Float Limit", "Decimal places to see.", new ValueDouble(1, new double[]{0, 16}, 1), Option.Type.NUMBER));
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void enable() {
        super.enable();
    }

    public void disable() {
        super.disable();
    }

    protected void tick() {
    }

    protected void onEvent(Event event) {
        if (event instanceof EventMouse) {
            EventMouse e = (EventMouse) event;
            if (e.type == Event.Type.CLICKL) {
                cpsList.add(System.currentTimeMillis());
            }
        } else if (event instanceof EventRender) {
            cps = 0;
            Set<Long> longs = new HashSet<>();
            cpsList.forEach(l -> {
                if ((1000 - (System.currentTimeMillis() - l)) <= 0)
                    longs.add(l);
                else
                    cps += (1000 - (System.currentTimeMillis() - l)) / 1000D;
            });
            longs.forEach(cpsList::remove);

            cps = MathUtil.round(cps, (options.get("floatlimit").INTEGER()));

            RenderUtil2D.string(RenderUtil.fontSmall[0], String.format("Current CPS: [v]%s", cps), 10, 10, 0xFFFFFFFF, 1, 0, true);
        }
    }
}
