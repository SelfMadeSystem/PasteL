package com.ihl.client.module.hacks.misc;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.option.Option;
import com.ihl.client.util.ChatUtil;

@EventHandler(events = {EventPlayerUpdate.class})
public class Example extends Module {
    // Don't forget, you have to register it in Module!
    public Example() {
        //super(name, description, category, keybind);
        super("Example", "An example to show developers how to make a module.", Category.MISC, "NONE");
        //all gets are case insensitive.
        addBoolean("ABool", "A Bool", false); //Get using BOOLEAN("abool")
        addDouble("ADouble", "A Double", 2, 0, 5, 0.5); //Get using DOUBLE("adouble")
        addInteger("AnInt", "An Int", 5, 0, 10); //Get using INTEGER("anint")
        addChoice("AChoice", "A Choice", "choice1", "choice2", "choice3"); //Get using STRING("AString")
        addString("AString", "A String", "Hello World!"); //Get using STRING("AString")
        Option other = addOther("Other", "O t h e r"); //Has no value.
        other.addBoolean("OtherBool", "An Other Bool", false); //Get using BOOLEAN("other", "otherbool")
        other.addDouble("OtherDouble", "An Other Double", 2, 0, 5, 0.5); //Get using BOOLEAN("other", "otherdouble")
        other.addInteger("OtherInt", "An Other Int", 5, 0, 10); //you get the idea...
        other.addChoice("OtherChoice", "An Other Choice", "choice1", "choice2", "choice3"); //you get the idea...
        other.addString("OtherString", "An Other String", "Hello World!"); //you get the idea...
        Option anOther = other.addOther("OtherOther", "An Other"); //Has no value.
        anOther.addBoolean("YetAnOtherBool", "Yet An Other Bool", false); //Get using BOOLEAN("other", "otherother", "yetanotherbool)
        anOther.addDouble("YetAnOtherDouble", "Yet An Other Double", 2, 0, 5, 0.5); //you get the idea...
        anOther.addInteger("YetAnOtherInt", "Yet An Other Int", 5, 0, 10); //you get the idea...
        anOther.addChoice("YetAnOtherChoice", "Yet An Other Choice", "choice1", "choice2", "choice3"); //you get the idea...
        anOther.addString("YetAnOtherString", "Yet An Other String", "Hello World!"); //you get the idea...
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
