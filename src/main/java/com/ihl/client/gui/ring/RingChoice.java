package com.ihl.client.gui.ring;

import com.ihl.client.gui.Gui;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.Option;
import com.ihl.client.util.ColorUtil;
import com.ihl.client.util.RenderUtil;
import com.ihl.client.util.RenderUtil2D;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class RingChoice extends Ring {

    private Module module;
    private Option option;

    public RingChoice(Module module, Option option, List<String> list) {
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
    public void mouseClicked(int button) {
        if (mouseOver && button == 0) {
            String key = (String) visibleList.get(selected);
            option.setValue(key);
        } else {
            Gui.prevRing();
        }
    }

    @Override
    public void render() {
        super.render();

        for (int i = 0; i < visibleList.size(); i++) {
            double iX = x + Math.cos(((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            double iY = y + Math.sin(((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            iY-= RenderUtil.fontTiny[0].getHeight()/2f;

            RenderUtil2D.string(RenderUtil.fontSmall[1], StringUtils.capitalize(""+ visibleList.get(i)), iX, iY, ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]), 0, 0, false);
        }

        RenderUtil2D.donutSeg(x, y, sizeR + 20, sizeR + 10, visibleList.indexOf(option.CHOICE()), visibleList.size(), optionPadding, ColorUtil.transparency(option.color, alpha[1]));
    }

}
