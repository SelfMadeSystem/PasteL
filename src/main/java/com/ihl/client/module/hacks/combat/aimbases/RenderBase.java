package com.ihl.client.module.hacks.combat.aimbases;

import com.ihl.client.event.*;
import com.ihl.client.util.*;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class RenderBase {
    public static void render(EventRender e) {
        if (e.type == Event.Type.PRE) {
            if (TargetUtil.target != null) {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GlStateManager.disableDepth();

                RenderUtil3D.box(TargetUtil.target, 0x80FF0000, 1);

                GL11.glPopAttrib();
            }
        }
    }
}
