package com.ihl.client.module.hacks.combat;

import com.ihl.client.Helper;
import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.Vec3;

import java.util.*;
import java.util.stream.Collectors;

@EventHandler(events = {EventPacket.class, EventPlayerUpdate.class})
public class Velocity extends Module {

    public Velocity() {
        super("Velocity", "Change the knockback velocity", Category.COMBAT, "NONE");
        addOption(new Option("Test", "Ok", new ValueRange(1, 4, 0, 10, 0.1), Option.Type.RANGE));
        addOption(new DOpt("Delay", "Delay for said mode in ticks", 0, 0, 20, 1, "delayed"));
        addOption(new DOpt("Repeat", "Repeat for said mode in ticks", 0, 0, 20, 1, "delayed"));
        addOption(new COpt("Strafe Strafe", "Completely strafes", false, "strafe"));
        addOption(new COpt("Strafe Add", "Amount to add to your motion", 0, 0, 0.2, 0.001, "strafe"));
        addOption(new COpt("HClip Amount", "How much should we HClip",
          0, 0, 2, 0.005, "HClip"));
        addInteger("KBDelay", "Delay for the kb multiplier to take effect.", 1, 0, 20);
        addInteger("KBRepeat", "Repeat for the kb multiplier to take effect again.", 1, 0, 20);
        addInteger("MaxTicks", "Max amount of ticks modifiers should last.", 1, 0, 40);
        addBoolean("StopOnGround", "Stops if on ground.", true);
        addDouble("Vertical", "Vertical knockback multiplier", 1, -2, 2, 0.1);
        addDouble("Horizontal", "Horizontal knockback multiplier", 1, -2, 2, 0.1);
        addChoice("Mode", "Mode for velocity", "Normal", "Strafe", "Teleport", "HClip");
        // TODO: 2020-06-09 Make it a lot more like my custom wurst's velocity but better.
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    private static class COpt extends Option {
        List<String> modes;
        public COpt(String name, String desc, double def, double min, double max, double inc, String... modes) {
            super(name, desc, new ValueDouble(def, new double[]{min, max}, inc), Type.NUMBER);
            this.modes = Arrays.stream(modes).map(String::toLowerCase).collect(Collectors.toList());
        }

        public COpt(String name, String desc, boolean def, String... modes) {
            super(name, desc, new ValueBoolean(def), Type.BOOLEAN);
            this.modes = Arrays.stream(modes).map(String::toLowerCase).collect(Collectors.toList());
        }

        @Override
        public boolean visible() {
            return this.modes.contains(module.STRING("mode").toLowerCase());
        }
    }

    private static class DOpt extends Option {
        List<String> modes;
        public DOpt(String name, String desc, double def, double min, double max, double inc, String... modes) {
            super(name, desc, new ValueDouble(def, new double[]{min, max}, inc), Type.NUMBER);
            this.modes = Arrays.stream(modes).map(String::toLowerCase).collect(Collectors.toList());
        }

        @Override
        public boolean visible() {
            return !this.modes.contains(module.STRING("mode").toLowerCase());
        }
    }

    private Vec3 teleportLocation;
    private int ticks;

    protected void onEvent(Event event) {
        double vertical = DOUBLE("vertical");
        double horizontal = DOUBLE("horizontal");
        int kbDelay = INTEGER("kbDelay");
        int kbRepeat = INTEGER("kbRepeat");
        int maxTicks = INTEGER("maxTicks");
        boolean stopOnGround = BOOLEAN("stopOnGround");
        String mode = STRING("mode").toLowerCase();
        if (event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            if (e.type == Event.Type.RECEIVE) {
                if (e.packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.packet;
                    if (Helper.world().getEntityByID(packet.func_149412_c()) == Helper.player()) {
                        ticks = 0;
                        if (kbDelay == 0) {
                            packet.x *= horizontal;
                            packet.y *= vertical;
                            packet.z *= horizontal;
                            if (vertical == 0 && horizontal == 0) {
                                e.cancel();
                            }
                        }
                        teleportLocation = new Vec3(player().posX, player().posY, player().posZ);
                    }
                }
            }
        } else if (event instanceof EventPlayerUpdate) {
            if (stopOnGround && player().isCollidedVertically && player().motionY < 0)
                ticks = maxTicks+1;
            if (ticks < maxTicks) {
                if (ticks > 0 && kbRepeat != 0 && (ticks + kbDelay) % kbRepeat == 0) {
                    player().motionX *= horizontal;
                    player().motionY *= vertical;
                    player().motionZ *= horizontal;
                }
                int delay = INTEGER("delay");
                int repeat = INTEGER("repeat");
                if (ticks > 0 && repeat != 0 && (ticks + delay) % repeat == 0) {
                    switch (mode) {
                        case "strafe": {
                            double add = DOUBLE("strafeAdd");
                            boolean strafe = BOOLEAN("strafe");
                            if (strafe) MUtil.strafe();
                            MUtil.hadd(add);
                            break;
                        }
                        case "teleport": {
                            player().setPosition(teleportLocation.xCoord, teleportLocation.yCoord, teleportLocation.zCoord);
                            break;
                        }
                        case "hclip": {
                            double amount = DOUBLE("hClipAmount");
                            MUtil.hclip(amount);
                            break;
                        }
                    }
                }
                ticks++;
            }
        }
    }
}
