package com.ihl.client.event;

import com.ihl.client.gui.ring.Ring;
import com.ihl.client.module.option.Option;

public class EventRing extends Event {
    public Ring ring;
    public Option option;
    public EventRing(Ring ring) {
        super(Type.PRE);
        this.ring = ring;
    }
    public EventRing(Ring ring, Option option) {
        super(Type.PRE);
        this.ring = ring;
        this.option = option;
    }
}
