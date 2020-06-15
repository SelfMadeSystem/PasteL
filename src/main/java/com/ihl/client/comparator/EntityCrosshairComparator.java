package com.ihl.client.comparator;

import com.ihl.client.util.EntityUtil;
import net.minecraft.entity.*;

import java.util.Comparator;

public class EntityCrosshairComparator implements Comparator<Entity> {

    private final EntityLivingBase central;

    public EntityCrosshairComparator(EntityLivingBase central) {
        this.central = central;
    }

    @Override
    public int compare(Entity a, Entity b) {
        float[] rot = EntityUtil.getRotationToEntity(a);
        double aA = EntityUtil.getRotationDifference(rot);

        float[] prev = EntityUtil.getRotationToEntity(b);
        double bA = EntityUtil.getRotationDifference(prev);

        return Double.compare(aA, bA);
    }

}
