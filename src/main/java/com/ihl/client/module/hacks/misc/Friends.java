package com.ihl.client.module.hacks.misc;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.option.*;
import com.ihl.client.util.ChatUtil;
import net.minecraft.entity.EntityLivingBase;

import java.util.*;

@EventHandler(events = {EventPlayerPickBlock.class})
public class Friends extends Module {

    public Friends() {
        super("Friends", "Whitelist friends for combat mods", Category.MISC, "NONE");
        options.put("friends", new Option("Friends", "List of friends", new ValueList(), Option.Type.LIST));
        options.put("middleclick", new Option("Middle Click", "Allow middle clicking players to toggle them", new ValueBoolean(true), Option.Type.BOOLEAN));
        options.put("color", new Option("Color", "Render color override", new ValueString("ffffffff"), Option.Type.STRING));
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public static boolean toggleFriend(String name) {
        Friends friends = (Friends) Module.get("friends");
        List<String> list = new ArrayList();
        list.addAll(Option.get(friends.options, "friends").LIST());

        boolean added;
        name = name.toLowerCase();
        String message;
        if (list.contains(name)) {
            list.remove(name);
            added = false;
            message = String.format("[v]%s [t]removed from [v]%s", name, Option.get(friends.options, "friends").name);
        } else {
            list.add(name);
            added = true;
            message = String.format("[v]%s [t]added to [v]%s", name, Option.get(friends.options, "friends").name);
        }

        ChatUtil.send(message);
        Option.get(friends.options, "friends").setValue(list);
        return added;
    }

    public static boolean isFriend(String name) {
        Friends friends = (Friends) Module.get("friends");
        List<String> list = Option.get(friends.options, "friends").LIST();

        name = name.toLowerCase();
        return friends.active && list.contains(name);
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPlayerPickBlock) {
            EventPlayerPickBlock e = (EventPlayerPickBlock) event;
            if (e.type == Event.Type.PRE) {
                if (e.mop.entityHit != null && e.mop.entityHit instanceof EntityLivingBase && Option.get(options, "middleclick").BOOLEAN()) {
                    toggleFriend(e.mop.entityHit.getName());
                }
            }
        }
    }
}
