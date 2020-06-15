package com.ihl.client.module.hacks.combat.aimbases;

import com.ihl.client.Client;
import com.ihl.client.event.*;
import com.ihl.client.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;

import java.util.List;

@EventHandler(events = {EventPlayerUpdate.class, EventRender.class})
public class MouseAimBase {

    private static final Minecraft mc;
    public static int prev = 0;
    public static float[] rotations;

    static {
        mc = Minecraft.getMinecraft();
    }

    public static void disable() {
        mc.mouseHelper.overrideMode = 0;
    }

    public static void enable(String aimWhere, double custom, double predict) {
        if (!HelperUtil.inGame())
            return;

        updateRotations(aimWhere, custom, predict);
    }

    public static void write(double absAmount) {
        if (mc.mouseHelper.tickX != 0 || mc.mouseHelper.tickY != 0) {
            Filer filer = new Filer("aimAssistValues", Client.NAME);
            int x = absAmount == 0 ? mc.mouseHelper.tickX : -Math.abs(mc.mouseHelper.tickX);
            int y = absAmount == 0 ? mc.mouseHelper.tickY : -Math.abs(mc.mouseHelper.tickY);
            if (absAmount > 0) {
                if (Math.random() > absAmount / 100f)
                    x *= -1;
                if (Math.random() > absAmount / 100f)
                    y *= -1;
            }
            ChatUtil.send(x + ":" + y);
            filer.write(x + ":" + y);
        }
    }

    public static int[] getNextRotations(String priority, double distance, double range, String aimWhere, double custom, String mode, boolean invertYaw, boolean invertPitch, int maxOvershoot, double predict) {
        int turnSpeedYaw;
        int turnSpeedPitch;

        {
            Filer filer = new Filer("aimAssistValues", Client.NAME);
            List<String> list = filer.read();
            if (list.size() == 0) {
                ChatUtil.send("Write values first!!!! Go in AimAssist and set mode to \"write\"");
                return new int[]{0, 0};
            }
            //ChatUtil.send(prev + " " + list.size());
            if (list.size() <= prev)
                prev = 0;
            int select = mode.equalsIgnoreCase("list") ? prev : (int) Math.floor(Math.random() * list.size());
            String[] split = list.get(select).split(":");
            turnSpeedYaw = Integer.parseInt(split[0]);
            turnSpeedPitch = Integer.parseInt(split[1]);
            prev++;
        }
        EntityPlayerSP p = mc.thePlayer;
        return getNextRotations(priority, distance, range, aimWhere, custom, invertYaw, invertPitch, maxOvershoot, turnSpeedYaw, turnSpeedPitch, new float[]{p.rotationYaw, p.rotationPitch}, predict);
    }

    public static int[] getNextRotations(String priority, double distance, double range, String aimWhere,
                                         double custom, boolean invertYaw, boolean invertPitch, double maxOvershoot, int turnSpeedYaw, int turnSpeedPitch, float[] from, double predict) {
        TargetUtil.targetEntity(priority, distance, range);
        EntityLivingBase target = TargetUtil.target;
        if (target == null) {
            return new int[]{0, 0};
        }



            /*final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            final float gcd = f * f * f * 1.2F;*/

        //ChatUtil.send(prev+""/*RUtils.angleDifference(mc.thePlayer.rotationYaw, rotations[0]) + " " + RUtils.angleDifference(mc.thePlayer.rotationPitch, rotations[1]) + " " +
        //  turnSpeedYaw + " " + turnSpeedPitch*/);

        if (rotations == null)
            return new int[]{0, 0};

        int[] changeMouse = new int[]{turnSpeedYaw * (rotations[0] < 0 ? -1 : 1),
          turnSpeedPitch * (rotations[1] < 0 ? -1 : 1)};/*new int[]{(int) RUtils.angleDifference(mc.thePlayer.rotationYaw, rotations[0]),
              (int) RUtils.angleDifference(mc.thePlayer.rotationPitch, rotations[1])};*/

        //System.out.println(Arrays.toString(changeMouse) + "|" + mc.gameSettings.mouseSensitivity);

        if (invertYaw)
            changeMouse[0] *= -1;
        if (invertPitch)
            changeMouse[1] *= -1;
        changeMouse[0] *= (double) turnSpeedYaw / 100;
        changeMouse[1] *= (double) turnSpeedPitch / 100;

        float[] fts0 = BasicAimBase.getAimTo(360, aimWhere, custom, from, predict);
        //float[] fts1 = new float[]{RUtils.angleDifference(mc.thePlayer.rotationYaw, fts0[0]), RUtils.angleDifference(mc.thePlayer.rotationPitch, fts0[1])};
        if (Math.abs(fts0[0]) + maxOvershoot < Math.abs(changeMouse[0]))
            changeMouse[0] = (int) (Math.abs(fts0[0]) * (changeMouse[0] > 0 ? 1 : -1));
        if (Math.abs(fts0[1]) + maxOvershoot < Math.abs(changeMouse[1]))
            changeMouse[1] = (int) (Math.abs(fts0[1]) * (changeMouse[1] > 0 ? 1 : -1));

        return changeMouse;

        /*float[] rotations = RUtils.limitAngleChange(new float[]{p.rotationYaw, p.rotationPitch}, to, turnSpeedYaw, turnSpeedPitch);

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float gcd = f * f * f * 1.2F;

        rotations[0] -= rotations[0] % gcd;
        rotations[1] -= rotations[1] % gcd;*/

        //System.out.printf("%s|%s%n", Arrays.toString(to), Arrays.toString(rotations));

        //p.rotationYaw = rotations[0];
        //p.rotationPitch = rotations[1];
    }

    public static void updateRotations(String aimWhere, double custom, double predict) {
        rotations = BasicAimBase.getAimTo(1, aimWhere, custom, predict);
    }
}
