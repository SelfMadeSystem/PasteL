package com.ihl.client.module.hacks.combat;

import com.ihl.client.event.*;
import com.ihl.client.module.Module;
import com.ihl.client.module.hacks.Category;
import com.ihl.client.module.option.*;
import com.ihl.client.util.TimeUtils;
import net.minecraft.client.settings.KeyBinding;

@EventHandler(events = {EventPlayerUpdate.class, EventRender.class})
public class TriggerBot extends Module {
    public TriggerBot() {
        super("TriggerBot", "Hits stuff", Category.COMBAT, "NONE");

        options.put("min", new Option(this, "Min", "Min Delay", new ValueDouble(7, new double[]{0, 20}, 1), Option.Type.NUMBER) {
            @Override
            public void setValueNoTrigger(Object value) {
                double max = module.options.get("max").DOUBLE();
                if ((Double) value > max)
                    value = max;
                super.setValueNoTrigger(value);
            }
        });
        options.put("max", new Option(this, "Max", "Max Delay", new ValueDouble(7, new double[]{0, 20}, 1), Option.Type.NUMBER) {
            @Override
            public void setValueNoTrigger(Object value) {
                double min = module.options.get("min").DOUBLE();
                if ((Double) value < min)
                    value = min;
                super.setValueNoTrigger(value);
            }
        });
    }


    private long delay;
    private long lastSwing;

    protected void onEvent(Event event) {
        if (event instanceof EventRender) {
            if (mc().objectMouseOver != null && System.currentTimeMillis() - lastSwing >= delay) {
                KeyBinding.onTick(mc().gameSettings.keyBindAttack.keyCode); // Minecraft Click handling
                lastSwing = System.currentTimeMillis();
                delay = TimeUtils.randomClickDelay(options.get("min").INTEGER(), options.get("max").INTEGER());
            }
        }
    }
}
