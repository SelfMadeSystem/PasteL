package com.ihl.client.module.hacks.combat;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.option.*;
import com.ihl.client.util.TimeUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;

@EventHandler(events = {EventPlayerUpdate.class, EventRender.class})
public class TriggerBot extends Module {
    private long delay;
    private long lastSwing;
    public TriggerBot() {
        super("TriggerBot", "Hits stuff", Category.COMBAT, "NONE");

        addOption(new Option("Min", "Min Delay", new ValueDouble(7, new double[]{1, 20}, 1), Option.Type.NUMBER) {
            @Override
            public void setValueNoTrigger(Object value) {
                double max = module.options.get("max").DOUBLE();
                if ((Double) value > max)
                    value = max;
                super.setValueNoTrigger(value);
            }
        });
        addOption(new Option("Max", "Max Delay", new ValueDouble(9, new double[]{1, 20}, 1), Option.Type.NUMBER) {
            @Override
            public void setValueNoTrigger(Object value) {
                double min = module.options.get("min").DOUBLE();
                if ((Double) value < min)
                    value = min;
                super.setValueNoTrigger(value);
            }
        });
    }

    protected void onEvent(Event event) {
        if (event instanceof EventRender) {
            if (mc().objectMouseOver != null && mc().objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY) && System.currentTimeMillis() - lastSwing >= delay) {
                KeyBinding.onTick(mc().gameSettings.keyBindAttack.keyCode); // Minecraft Click handling
                lastSwing = System.currentTimeMillis();
                delay = TimeUtils.randomClickDelay(options.get("min").INTEGER(), options.get("max").INTEGER());
            }
        }
    }
}
