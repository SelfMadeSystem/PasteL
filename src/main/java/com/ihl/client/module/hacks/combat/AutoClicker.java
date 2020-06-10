package com.ihl.client.module.hacks.combat;

import com.ihl.client.event.*;
import com.ihl.client.module.Module;
import com.ihl.client.module.Category;
import com.ihl.client.module.option.*;
import com.ihl.client.util.TimeUtils;
import net.minecraft.client.settings.KeyBinding;

@EventHandler(events = {EventPlayerUpdate.class, EventRender.class})
public class AutoClicker extends Module {

    private long rightDelay, rightLastSwing, leftDelay, leftLastSwing;

    public AutoClicker() {
        super("AutoClicker", "Clicks", Category.COMBAT, "NONE");
        options.put("left", new Option(this, "Left", "Left", new ValueBoolean(true), Option.Type.BOOLEAN,
          new Option(this, "Min", "Min Delay", new ValueDouble(7, new double[]{0, 20}, 1), Option.Type.NUMBER) {
              @Override
              public void setValueNoTrigger(Object value) {
                  double max = module.options.get("left").DOUBLE("max");
                  if ((Double) value > max)
                      value = max;
                  super.setValueNoTrigger(value);
              }
          },
          new Option(this, "Max", "Max Delay", new ValueDouble(7, new double[]{0, 20}, 1), Option.Type.NUMBER) {
              @Override
              public void setValueNoTrigger(Object value) {
                  double min =  module.options.get("left").DOUBLE("min");
                  if ((Double) value < min)
                      value = min;
                  super.setValueNoTrigger(value);
              }
          }));
        options.put("right", new Option(this, "Right", "Right", new ValueBoolean(true), Option.Type.BOOLEAN,
          new Option(this, "Min", "Min Delay", new ValueDouble(7, new double[]{0, 20}, 1), Option.Type.NUMBER) {
              @Override
              public void setValueNoTrigger(Object value) {
                  double max = module.options.get("right").DOUBLE("max");
                  if ((Double) value > max)
                      value = max;
                  super.setValueNoTrigger(value);
              }
          },
          new Option(this, "Max", "Max Delay", new ValueDouble(7, new double[]{0, 20}, 1), Option.Type.NUMBER) {
              @Override
              public void setValueNoTrigger(Object value) {
                  double min =  module.options.get("right").DOUBLE("min");
                  if ((Double) value < min)
                      value = min;
                  super.setValueNoTrigger(value);
              }
          }));
    }

    @Override
    public void enable() {
        super.enable();
        rightDelay = TimeUtils.randomClickDelay(Option.get(options, "right", "min").INTEGER(),
          Option.get(options, "right", "max").INTEGER());
        leftDelay = TimeUtils.randomClickDelay(Option.get(options, "left", "min").INTEGER(),
          Option.get(options, "left", "max").INTEGER());
    }

    protected void onEvent(Event event) {
        if (event instanceof EventRender) {
            // Left click
            if (mc().gameSettings.keyBindAttack.getIsKeyPressed() && Option.get(options, "left").BOOLEAN() &&
              System.currentTimeMillis() - leftLastSwing >= leftDelay) {
                KeyBinding.onTick(mc().gameSettings.keyBindAttack.keyCode); // Minecraft Click Handling

                leftLastSwing = System.currentTimeMillis();
                leftDelay = TimeUtils.randomClickDelay(Option.get(options, "left", "min").INTEGER(),
                  Option.get(options, "left", "max").INTEGER());
            }

            // Right click
            if (mc().gameSettings.keyBindUseItem.getIsKeyPressed() && !mc().thePlayer.isUsingItem() &&
              Option.get(options, "right").BOOLEAN() &&
              System.currentTimeMillis() - rightLastSwing >= rightDelay) {
                KeyBinding.onTick(mc().gameSettings.keyBindUseItem.keyCode); // Minecraft Click Handling

                rightLastSwing = System.currentTimeMillis();
                rightDelay = TimeUtils.randomClickDelay(Option.get(options, "right", "min").INTEGER(),
                  Option.get(options, "right", "max").INTEGER());
            }
        }
    }

    /*protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            if (Option.get(options, "left").BOOLEAN()) {
                if (mc().gameSettings.keyBindAttack.pressed &&
                  Option.get(options, "left").BOOLEAN()) {
                    lTime++;
                    if (lTime > lDelay) {
                        lTime = 0;
                        lDelay = (int) ((Math.random() * Option.get(options, "left", "delay").INTEGER()) -
                          Option.get(options, "left", "random").INTEGER() / 2);
                        //mc().gameSettings.keyBindAttack.unpressKey();
                        //mc().gameSettings.keyBindAttack.pressed = true;
                        if (!Option.get(options, "left", "block").BOOLEAN() ||
                          !mc().objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.BLOCK))
                            KeyBinding.onTick(mc().gameSettings.keyBindAttack.keyCode);
                    }
                }
                if (mc().gameSettings.keyBindUseItem.pressed &&
                  Option.get(options, "right").BOOLEAN()) {
                    //System.out.println(rTime + "|" + rDelay);
                    rTime++;
                    if (rTime > rDelay) {
                        rTime = 0;
                        rDelay = (int) ((Math.random() * Option.get(options, "right", "delay").INTEGER()) -
                          Option.get(options, "right", "random").INTEGER() / 2);
                        KeyBinding.onTick(mc().gameSettings.keyBindUseItem.keyCode);
                    }
                }
            }
        }
    }*/
}
