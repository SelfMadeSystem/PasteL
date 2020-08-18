package com.ihl.client.gui.ring;

import com.ihl.client.Helper;
import com.ihl.client.gui.Gui;
import com.ihl.client.module.*;
import com.ihl.client.module.option.Option;
import com.ihl.client.util.*;

import java.util.*;

public class RingModule extends Ring {

    public RingModule(List<String> list) {
        super(list);
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            Module module = Module.get(key);
            hasSettings[i] = module.options != null && !module.options.isEmpty();
        }
    }

    @Override
    public void tick() {
        if (visibleList.isEmpty()) {
            Gui.changeRing(new RingCategory(Arrays.asList(Category.values())));
            return;
        }
        super.tick();
    }

    @Override
    public void mouseClicked(int button) {
        if (mouseOver && button == 0) {
            String key = (String) visibleList.get(selected);
            Module module = Module.get(key);
            if (mouseOverSettings) {
                List<Option> list = Arrays.asList(module.options.values().toArray(new Option[0]));
                list.sort(Comparator.comparingInt(o -> o.weight));
                if (!list.isEmpty()) {
                    Gui.changeRing(new RingOption(list, module));
                }
            } else {
                module.toggle();
            }
        } else {
            Gui.prevRing();
        }
    }

    @Override
    public void render() {
        super.render();
        double iconSize = 40;

        for (int i = 0; i < visibleList.size(); i++) {
            String key = (String) visibleList.get(i);
            Module module = Module.get(key);

            double ang = ((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180;
            double inc = (sizeR - (width / 2));
            double iX = x + Math.cos(ang) * inc;
            double iY = y + Math.sin(ang) * inc;
            iY -= RenderUtil.fontTiny[0].getHeight() / 2;
            if (module.active) {
                RenderUtil2D.donutSeg(x, y, sizeR + (settingSlider[i] * settingSliderWidth) + 20, sizeR + (settingSlider[i] * settingSliderWidth) + 10, i, visibleList.size(), modulePadding, ColorUtil.transparency(module.color, alpha[1]));
            }

            Helper.mc().getTextureManager().bindTexture(module.icon);
            RenderUtil2D.texturedRect(iX - (iconSize / 2), iY - (iconSize / 2), iX + (iconSize / 2), iY + (iconSize / 2), ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]));
            RenderUtil2D.string(RenderUtil.fontTiny[1], module.name, iX, iY + (iconSize / 2), ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]), 0, 0, false);
        }

        if (selected != -1) {
            Module module = Module.get((String) visibleList.get((int) Math.floor(selected)));
            RenderUtil2D.string(RenderUtil.fontLarge[1], module.name, x, y, ColorUtil.transparency(white, alpha[0] * alpha[1]), 0, 0, false);
            RenderUtil2D.string(RenderUtil.fontTiny[1], module.desc, x, y + (RenderUtil.fontLarge[1].getHeight() / 2) + 2, ColorUtil.transparency(0xFFCCCCCC, alpha[0] * alpha[1]), 0, 1, false);
        }
    }

}
