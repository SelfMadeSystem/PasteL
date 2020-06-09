package com.ihl.client;

import com.ihl.client.commands.*;
import com.ihl.client.gui.Gui;
import com.ihl.client.input.InputUtil;
import com.ihl.client.module.Module;
import com.ihl.client.util.FileUtil;
import com.ihl.client.util.IconUtil;
import com.ihl.client.util.part.Settings;
import org.lwjgl.opengl.Display;

import java.util.Arrays;

public class Client {

    public static final String NAME = "PasteClient";
    public static final String VERSION = "0.23";

    public static Client instance;

    public Client() {
        instance = this;

        init();
    }

    public void init() {
        Display.setTitle(NAME);
        Display.setIcon(IconUtil.fav());

        Settings.init();

        FileUtil.readData();
        FileUtil.readSettings();

        Command.commands.put("help", new CommandHelp("help", Arrays.asList("help",
                "help [module]",
                "help <module> [option]",
                "help <module> <option> [option]")));

        Command.commands.put("color", new CommandColor("color", Arrays.asList("color")));

        Command.commands.put("login", new CommandLogin("login", Arrays.asList("login <username>",
                "login <username> [password]")));

        Module.init();

        FileUtil.readModules();
    }

    public void tick() {
        InputUtil.tick();
        Gui.tick();
        if (Helper.world() != null && Helper.player() != null) {
            Module.tickMods();
        }
    }

    public void render() {
        Gui.render();
    }

    public void dispose() {
        FileUtil.writeSettings();
        FileUtil.writeModules();
        Gui.dispose();
    }

}
