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
        /*options.put("number", new Option(this, "Number", "Description", new ValueDouble(1, new double[]{0, 10}, 0.5), Option.Type.NUMBER));
        options.put("morestuff", new Option(this, "MoreStuff", "More stuff", new ValueString(""), Option.Type.OTHER,
          new Option(this, "ClickMe", "Prints \"Hello World!\" in chat!", new ValueBoolean(false), Option.Type.BOOLEAN)));*/
        addBoolean("ABool", "A Bool", false);
        addDouble("ADouble", "A Double", 2, 0, 5, 0.5);
        addInteger("AnInt", "An Int", 5, 0, 10);
        addChoice("AChoice", "A Choice", "choice1", "choice2", "choice3");
        addString("AString", "A String", "Hello World!");
        Option other = addOther("Other", "O t h e r");
        other.addBoolean("OtherBool", "An Other Bool", false);
        other.addDouble("OtherDouble", "An Other Double", 2, 0, 5, 0.5);
        other.addInteger("OtherInt", "An Other Int", 5, 0, 10);
        other.addChoice("OtherChoice", "An Other Choice", "choice1", "choice2", "choice3");
        other.addString("OtherString", "An Other String", "Hello World!");
        Option anOther = other.addOther("OtherOther", "An Other");
        anOther.addBoolean("YetAnOtherBool", "Yet An Other Bool", false);
        anOther.addDouble("YetAnOtherDouble", "Yet An Other Double", 2, 0, 5, 0.5);
        anOther.addInteger("YetAnOtherInt", "Yet An Other Int", 5, 0, 10);
        anOther.addChoice("YetAnOtherChoice", "Yet An Other Choice", "choice1", "choice2", "choice3");
        anOther.addString("YetAnOtherString", "Yet An Other String", "Hello World!");
    }

    @Override
    public void optionChanged(EventOption eventOption) {
        ChatUtil.send("Module:" + eventOption.module.name);
        ChatUtil.send("Option:" + eventOption.option.name);
        ChatUtil.send("Change:" + eventOption.changed);
    }

    @Override
    protected void onEvent(Event event) {
        if (event instanceof EventPlayerUpdate) {
            ChatUtil.send(BOOLEAN("ABool") + " " + DOUBLE("ADouble") + " " + INTEGER("AnInt"));
            ChatUtil.send(BOOLEAN("Other", "OtherBool") + " " + DOUBLE("Other", "OtherDouble") + " " + INTEGER("Other", "OtherInt"));
            ChatUtil.send(BOOLEAN("Other", "OtherOther", "YetAnOtherBool") + " " + DOUBLE("Other", "OtherOther", "YetAnOtherDouble") + " " + INTEGER("Other", "OtherOther", "YetAnOtherInt"));

        }
    }
}
