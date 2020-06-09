package com.ihl.client.gui.ring;

public class PrevRing {
    public PrevRing prevInstance = null;
    public Ring prevRing;

    public PrevRing(PrevRing prevInstance, Ring prevRing) {
        this.prevInstance = prevInstance;
        this.prevRing = prevRing;
    }

    public PrevRing(Ring prevRing) {
        this.prevRing = prevRing;
    }
}
