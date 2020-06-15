package com.ihl.client.module.hacks.movement.speeds;

import com.ihl.client.Helper;
import com.ihl.client.event.EventPlayerUpdate;
import com.ihl.client.module.hacks.movement.Speed;
import com.ihl.client.module.hacks.movement.speeds.aac.*;
import com.ihl.client.module.hacks.movement.speeds.ncp.*;

import java.util.*;

public class SpeedMode extends Helper {
    public static Map<String, Map<String, SpeedMode>> speeds = new LinkedHashMap<>();
    private final String type;
    private final String name;
    public SpeedMode(String type, String name) {
        this.type = type;
        this.name = name;
        speeds.putIfAbsent(type, new HashMap<>());
        speeds.get(type).put(name, this);
    }

    public static void init() {
        // new SpeedModes
        {//NCP
            new NCPHop();
            new NCPYPort();
        }
        {//AAC
            new AACTimer();
            new AAC3313();
            new AAC350();
            new AAC364();
            new AAC42();
            new AAC42Hop();
        }
    }

    public void onUpdate(EventPlayerUpdate event, Speed speed) {
    }
}
