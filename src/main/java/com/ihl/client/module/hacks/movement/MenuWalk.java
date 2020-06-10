package com.ihl.client.module.hacks.movement;

import com.ihl.client.Helper;
import com.ihl.client.event.EventHandler;
import com.ihl.client.gui.GuiHandle;
import com.ihl.client.module.Category;
import com.ihl.client.module.Module;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

@EventHandler(events = {})
public class MenuWalk extends Module {

    public MenuWalk() {
        super("Menu Walk", "Walk when in a GUI", Category.MOVEMENT, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    protected void tick() {
        super.tick();
        if (active && Helper.mc().currentScreen != null && !(Helper.mc().currentScreen instanceof GuiChat) && !(Helper.mc().currentScreen instanceof GuiHandle && Module.get("console").active) && Helper.player() != null) {
            KeyBinding[] keybinds = new KeyBinding[] {
                    Helper.mc().gameSettings.keyBindBack,
                    Helper.mc().gameSettings.keyBindForward,
                    Helper.mc().gameSettings.keyBindJump,
                    Helper.mc().gameSettings.keyBindLeft,
                    Helper.mc().gameSettings.keyBindPlayerList,
                    Helper.mc().gameSettings.keyBindRight,
                    Helper.mc().gameSettings.keyBindSneak,
                    Helper.mc().gameSettings.keyBindSprint,
                    Helper.mc().gameSettings.ofKeyBindZoom,
            };

            for(KeyBinding keybind : keybinds) {
                int code = keybind.getKeyCode();
                if (code >= 0) {
                    KeyBinding.setKeyBindState(code, Keyboard.isKeyDown(code));
                }
            }
        }
    }
}
