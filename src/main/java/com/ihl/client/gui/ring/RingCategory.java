package com.ihl.client.gui.ring;

import com.ihl.client.Helper;
import com.ihl.client.gui.Gui;
import com.ihl.client.module.Category;
import com.ihl.client.module.Module;
import com.ihl.client.util.ColorUtil;
import com.ihl.client.util.RenderUtil;
import com.ihl.client.util.RenderUtil2D;

import java.util.List;

public class RingCategory extends Ring {

    public RingCategory(List<Category> list) {
        super(list);
    }

    @Override
    public void mouseClicked(int button) {
        if (mouseOver && button == 0) {
            Category category = (Category) visibleList.get(selected);
            List<String> list = Module.category(category);
            if (!list.isEmpty()) {
                Gui.changeRing(new RingModule(list));
            }
        }
    }

    @Override
    public void render() {
        super.render();
        double iconSize = 40;

        for (int i = 0; i < visibleList.size(); i++) {
            Category category = (Category) visibleList.get(i);

            double rot = ((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2));
            double rad = Math.PI / 180;
            double ang = (rot * rad) % 360;
            double inc = (sizeR - (width / 2));
            double iX = x + Math.cos(ang) * inc;
            double iY = y + Math.sin(ang) * inc;

            Helper.mc().getTextureManager().bindTexture(category.icon);
            RenderUtil2D.texturedRect(iX - (iconSize / 2), iY - (iconSize / 2), iX + (iconSize / 2), iY + (iconSize / 2), ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]));

            RenderUtil2D.string(RenderUtil.fontTiny[1], category.display, iX, iY + (iconSize / 2), ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]), 0, 0, false);
        }

        if (selected != -1) {
            Category category = (Category) visibleList.get((int) Math.floor(selected));
            RenderUtil2D.string(RenderUtil.fontLarge[1], category.display, x, y, ColorUtil.transparency(white, alpha[0] * alpha[1]), 0, 0, true);
        }
    }
}
