package com.ihl.client.util;

import com.ihl.client.Helper;
import com.ihl.client.comparator.*;
import com.ihl.client.module.hacks.Friends;
import com.ihl.client.module.hacks.combat.AntiBot;
import net.minecraft.entity.*;

import java.util.*;

public class TargetUtil {
    public static EntityLivingBase target;

    public static void targetEntity(String priority, double distance, double range) {
        List<EntityLivingBase> entities = new ArrayList<>();
        for (Object object : Helper.world().getLoadedEntityList()) {
            if (object instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) object;

                if (isLiable(entity, distance, range)) {
                    entities.add(entity);
                }
            }
        }

        switch (priority) {
            case "distance":
                entities.sort(new EntityDistanceComparator(Helper.player()));
                break;
            case "health":
                entities.sort(new EntityHealthComparator());
                break;
            case "direction":
                entities.sort(new EntityCrosshairComparator(Helper.player()));
                break;
        }

        if (!entities.isEmpty()) {
            target = entities.get(0);
        } else {
            target = null;
        }
    }

    private static boolean isLiable(EntityLivingBase entity, double distance, double range) {
        float[] rotationTo = EntityUtil.getRotationToEntity(entity);
        double rotationDifference = EntityUtil.getRotationDifference(rotationTo);

        return (entity != Helper.player() &&
          !entity.getName().equals(Helper.player().getName()) &&
          !Friends.isFriend(entity.getName()) &&
          Helper.player().getDistanceToEntity(entity) < distance &&
          rotationDifference < range) && (
            (!AntiBot.ground || entity.onGround) &&
            (!AntiBot.air || !entity.onGround) &&
            /*(!AntiBot.tab || !entity.tabbystuff) &&*/// TODO: 2020-06-04 make tab thingy
            (!AntiBot.color || !entity.getName().replace("§r", "").contains("§")) &&
              (!AntiBot.hit || AntiBot.hitted.contains(entity.getEntityId()))
          );
    }
}
