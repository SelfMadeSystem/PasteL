package com.ihl.client.commands;

import com.ihl.client.commands.exceptions.*;
import com.ihl.client.util.ChatUtil;

import java.util.*;

public abstract class Command {

    public static Map<String, Command> commands = new HashMap();
    public String base;
    public List<String> usages = new ArrayList();
    public Command(String base, List<String> usages) {
        this.base = base;
        if (usages != null && !usages.isEmpty()) {
            this.usages.addAll(usages);
        }
    }

    public static String[] dropFirst(String[] args) {
        String[] var1 = new String[args.length - 1];
        System.arraycopy(args, 1, var1, 0, args.length - 1);
        return var1;
    }

    public static void run(String base, String[] args) {
        Command command = Command.commands.get(base);
        if (command != null) {
            try {
                command.execute(args);
            } catch (ArgumentException ex) {
                ChatUtil.send("[e]Invalid arguments");
            } catch (SyntaxException ex) {
                ChatUtil.send("[e]Invalid syntax");
            } catch (CommandException ex) {
                ChatUtil.send("[e]An error occurred. Please file a bug report.");
            }
        } else {
            ChatUtil.send("[e]Unknown command");
        }
    }

    public abstract void execute(String[] args) throws CommandException;

}
