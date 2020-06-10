package com.ihl.client.module.hacks.render;

import com.ihl.client.Helper;
import com.ihl.client.event.EventHandler;
import com.ihl.client.gui.GuiHandle;
import com.ihl.client.module.*;
import com.ihl.client.module.Category;

@EventHandler(events = {})
public class GUI extends Module {

    public GUI() {
        super("GUI", "Open the radial GUI", Category.RENDER, "RSHIFT");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void enable() {
        super.enable();
        if (!(Helper.mc().currentScreen instanceof GuiHandle)) {
            Helper.mc().displayGuiScreen(new GuiHandle());
        }
        disable();
    }
}
