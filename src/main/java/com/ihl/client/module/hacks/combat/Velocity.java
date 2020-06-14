package com.ihl.client.module.hacks.combat;

import com.ihl.client.Helper;
import com.ihl.client.event.Event;
import com.ihl.client.event.EventHandler;
import com.ihl.client.event.EventPacket;
import com.ihl.client.module.Category;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.Option;
import com.ihl.client.module.option.ValueDouble;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@EventHandler(events = {EventPacket.class})
public class Velocity extends Module {

    public Velocity() {
        super("Velocity", "Change the knockback velocity", Category.COMBAT, "NONE");
        options.put("multiplier", new Option("Multiplier", "Knockback multiplier", new ValueDouble(0, new double[]{0, 5}, 0.1), Option.Type.NUMBER));
        // TODO: 2020-06-09 Make it a lot more like my custom wurst's velocity but better.
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    protected void onEvent(Event event) {
        double multiplier = Option.get(options, "multiplier").DOUBLE();

        if (event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            if (e.type == Event.Type.RECEIVE) {
                if (e.packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.packet;
                    if (Helper.world().getEntityByID(packet.func_149412_c()) == Helper.player()) {
                        packet.x *= multiplier;
                        packet.y *= multiplier;
                        packet.z *= multiplier;
                        if (multiplier == 0) {
                            e.cancel();
                        }
                    }
                }
            }
        }
    }

}
