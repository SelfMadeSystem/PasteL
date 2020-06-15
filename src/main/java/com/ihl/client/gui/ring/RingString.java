package com.ihl.client.gui.ring;

import com.ihl.client.gui.Gui;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.Option;
import com.ihl.client.util.*;
import net.minecraft.util.ChatAllowedCharacters;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.util.*;

public class RingString extends Ring {

    private final Module module;
    private final Option option;

    public RingString(Module module, Option option, List<String> list) {
        super(list);
        this.module = module;
        this.option = option;
    }

    @Override
    public void tick() {
        if (visibleList.isEmpty()) {
            List<String> list = Arrays.asList(module.options.keySet().toArray(new String[0]));
            if (!list.isEmpty()) {
                Gui.prevRing();
                return;
            }
        }
        super.tick();
    }

    @Override
    public void keyPress(int k, char c) {
        String text = option.STRING();

        if (k == Keyboard.KEY_BACK) {
            if (text.length() > 0) {
                text = text.substring(0, text.length() - 1);
            }
        } else if (ChatAllowedCharacters.isAllowedCharacter(c)) {
            text += c;
        }

        option.setValue(text);
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

        RenderUtil2D.string(RenderUtil.fontLarge[1], option.STRING() + ((System.currentTimeMillis() % 1000) < 500 ? "_" : "  "), x, y, ColorUtil.transparency(white, alpha[1]), 0, 0, false);

        for (int i = 0; i < visibleList.size(); i++) {
            double iX = x + Math.cos(((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            double iY = y + Math.sin(((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            iY -= RenderUtil.fontTiny[0].getHeight() / 2;

            RenderUtil2D.string(RenderUtil.fontSmall[1], StringUtils.capitalize("" + visibleList.get(i)), iX, iY, ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]), 0, 0, false);
        }
    }

}
