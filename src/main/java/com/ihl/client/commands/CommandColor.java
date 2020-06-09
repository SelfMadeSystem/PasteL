package com.ihl.client.commands;

import com.ihl.client.commands.exceptions.CommandException;
import com.ihl.client.gui.ring.Ring;
import com.ihl.client.util.ChatUtil;

import java.util.List;

public class CommandColor extends Command {

    public CommandColor(String base, List<String> usages) {
        super(base, usages);
    }

    public void execute(String[] args) throws CommandException {
        try {
            if (args[0].equalsIgnoreCase("border")) {
                Ring.borderColor = Integer.parseInt(args[1], 16);
            } else if (args[0].equalsIgnoreCase("center")) {
                Ring.centerColor = Integer.parseInt(args[1], 16);
            } else if (args[0].equalsIgnoreCase("white")) {
                Ring.white = Integer.parseInt(args[1], 16);
            } else if (args[0].equalsIgnoreCase("gray")) {
                Ring.gray = Integer.parseInt(args[1], 16);
            } else if (args[0].equalsIgnoreCase("guicolor")) {
                Ring.guicolor = Integer.parseInt(args[1], 16);
            }
        } catch (NumberFormatException e) {
            ChatUtil.send("lol");
        }
    }
}
