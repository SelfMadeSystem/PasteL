package net.minecraft.util;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class MouseHelper {
    /**
     * 0 none
     * 1 complete
     * 2 add override values to delta
     * 3 multiply override values to delta
     */
    public int overrideMode = 0;
    public boolean moving = false;
    public int deltaX, deltaY, tickX, tickY, trueX, trueY;
    public double overrideY, overrideX;
    private static final String __OBFID = "CL_00000648";

    /**
     * Grabs the mouse cursor it doesn't move and isn't seen.
     */
    public void grabMouseCursor() {
        Mouse.setGrabbed(true);
        this.deltaX = 0;
        this.deltaY = 0;
    }

    /**
     * Ungrabs the mouse cursor so it can be moved and set it to the center of the screen
     */
    public void ungrabMouseCursor() {
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        Mouse.setGrabbed(false);
    }

    public void mouseXYChange() {
        this.trueX = Mouse.getDX();
        this.trueY = Mouse.getDY();
        int x, y;
        if (overrideMode == 1) {
            x = (int) overrideX;
            y = (int) overrideY;
        } else if (overrideMode == 2) {
            x = (int) (overrideX + trueX);
            y = (int) (overrideY + trueY);
        } else if (overrideMode == 3) {
            x = (int) (overrideX * trueX);
            y = (int) (overrideY * trueY);
        } else {
            x = trueX;
            y = trueY;
        }
        this.tickX += x;
        this.tickY += y;
        this.deltaX = x;
        this.deltaY = y;
        this.moving = trueX != 0 || trueY != 0;
    }
}
