package com.ihl.client.gui.ring;

import com.ihl.client.gui.Gui;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.Option;
import com.ihl.client.module.option.ValueString;
import com.ihl.client.util.ColorUtil;
import com.ihl.client.util.RenderUtil;
import com.ihl.client.util.RenderUtil2D;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class RingKeybind extends Ring {

    private Module module;
    private Option option;

    public RingKeybind(Module module, Option option, List<String> list) {
        super(list);
        this.module = module;
        this.option = option;
    }

    @Override
    public void tick() {
        if (visibleList.isEmpty()) {
            Gui.prevRing();
        }
        super.tick();
    }

    @Override
    public void keyPress(int k, char c) {
        ValueString valueString = (ValueString) option.getTValue();
        if (k == Keyboard.KEY_DELETE) {
            option.setValue("NONE");
            return;
        }
        option.setValue(Keyboard.getKeyName(k));;
    }

    @Override
    public void mouseClicked(int button) {
        if (!mouseOver || button != 0) {
            Gui.prevRing();
        }
    }

    @Override
    public void render() {
        super.render();

        RenderUtil2D.string(RenderUtil.fontLarge[1], "" + option.STRING(), x, y, ColorUtil.transparency(white, alpha[1]), 0, 0, false);

        for (int i = 0; i < visibleList.size(); i++) {
            double iX = x + Math.cos(((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            double iY = y + Math.sin(((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            iY -= RenderUtil.fontTiny[0].getHeight() / 2;

            RenderUtil2D.string(RenderUtil.fontSmall[1], StringUtils.capitalize("" + visibleList.get(i)), iX, iY, ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]), 0, 0, false);
        }
    }

}
