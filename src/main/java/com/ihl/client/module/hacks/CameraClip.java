package com.ihl.client.module.hacks;

import com.ihl.client.event.EventHandler;
import com.ihl.client.module.*;

@EventHandler(events = {})
public class CameraClip extends Module {

    public CameraClip() {
        super("Camera Clip", "Allow third person camera to clip into blocks", Category.RENDER, "NONE");
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }
}
