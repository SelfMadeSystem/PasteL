package com.ihl.client.comparator;

import net.minecraft.entity.*;

import java.util.Comparator;

public class EntityDistanceComparator implements Comparator<Entity> {

    private final EntityLivingBase central;

    public EntityDistanceComparator(EntityLivingBase central) {
        this.central = central;
    }

    @Override
    public int compare(Entity a, Entity b) {
        double c = central.getDistanceToEntity(a);
        double d = central.getDistanceToEntity(b);

        return Double.compare(c, d);
    }

}
