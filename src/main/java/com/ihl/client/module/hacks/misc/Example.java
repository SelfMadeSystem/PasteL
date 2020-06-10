package com.ihl.client.module.hacks.misc;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;

@EventHandler(events = {EventPlayerUpdate.class})
public class Example extends Module {
    // Don't forget, you have to register it in Module!
    public Example() {
        //super(name, description, category, keybind);
        super("Example", "An example to show developers how to make a module.", Category.MISC, "NONE");
        //options.put(name, option);
        //
        //"this" is just to tell the option which module it's attached to. Will be changed later.
        //new Option(this, name, description, value, type, more options...);
        //new ValueDouble(start, new double[] {min, max}, increments)
        options.put("number", new Option(this, "Number", "Description", new ValueDouble(1, new double[]{0, 10}, 0.5), Option.Type.NUMBER));
        options.put("morestuff", new Option(this, "MoreStuff", "More stuff", new ValueString(""), Option.Type.OTHER,
          new Option(this, "ClickMe", "Prints \"Hello World!\" in chat!", new ValueBoolean(false), Option.Type.BOOLEAN)));
    }

    @Override
    public void optionChanged(EventOption eventOption) {
        if (eventOption.option.name.equalsIgnoreCase("clickme")) {
            eventOption.option.setValueNoTrigger(false); //want to set value without trigger to make sure it doesn't cause stack overflow error.
            ChatUtil.send("Hello World!");
        }
    }

    @Override
    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            double number = options.get("number").DOUBLE(); //WILL remake to just be DOUBLE("number");
            if (number >= 5) {
                ChatUtil.send("Number is above 5!");
            } else {
                ChatUtil.send("Number is below 5!");
                player().motionY = 0;
                MUtil.strafe(0);
            }
        }
    }
}
