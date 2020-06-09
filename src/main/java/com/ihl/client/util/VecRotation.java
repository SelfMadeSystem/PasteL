package com.ihl.client.util;

import net.minecraft.util.Vec3;

public class VecRotation {
    Vec3 vec;
    float[] rotation;

    public VecRotation(Vec3 vec, float[] rotation) {
        this.vec = vec;
        this.rotation = rotation;
    }

    public Vec3 getVec() {
        return vec;
    }

    public void setVec(Vec3 vec) {
        this.vec = vec;
    }

    public float[] getRotation() {
        return rotation;
    }

    public void setRotation(float[] rotation) {
        this.rotation = rotation;
    }
}
