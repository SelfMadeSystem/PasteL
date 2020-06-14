package com.ihl.client.gui.ring;

import com.ihl.client.Helper;
import com.ihl.client.event.EventRing;
import com.ihl.client.gui.Gui;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class RingOption extends Ring {

    private final Module module;

    public RingOption(List<String> list, Module module) {
        super(list);
        this.module = module;

        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            Option option = Option.get(module.options, key);
            hasSettings[i] = option.options != null && !option.options.isEmpty();
        }
    }

    @Override
    public void tick() {
        if (visibleList.isEmpty()) {
            Gui.changeRing(new RingModule(Module.category(module.category)));
            return;
        }
        super.tick();
    }

    @Override
    public Ring reset() {
        module.rendeRing(new EventRing(this));
        this.visibleList = new ArrayList<>(this.list);
        for (Object obj : this.list) {
            if (!(module.options.get(String.valueOf(obj))).visible())
                this.visibleList.remove(obj);
        }
        return super.reset();
    }

    @Override
    public void mouseClicked(int button) {
        if (mouseOver && button == 0) {
            String key = (String) visibleList.get(selected);
            Option option = Option.get(module.options, key);
            if (mouseOverSettings) {
                if (option.options != null && !option.options.isEmpty()) {
                    List<String> list = Arrays.asList(option.options.keySet().toArray(new String[0]));
                    if (!list.isEmpty()) {
                        Gui.changeRing(new RingSubOption(list, module, option));
                    }
                }
            } else {
                switch (option.type) {
                    case BOOLEAN:
                        option.setValue(!((boolean) option.getValue()));
                        break;
                    case CHOICE:
                        Gui.changeRing(new RingChoice(module, option, Arrays.asList(((ValueChoice) option.getTValue()).list)));
                        break;
                    case KEYBIND:
                        Gui.changeRing(new RingKeybind(module, option, Arrays.asList(option.name)));
                        break;
                    case LIST:
                        Gui.changeRing(new RingList(module, option, (List<String>) (option.getValue())));
                        break;
                    case NUMBER:
                        Gui.changeRing(new RingSlider(module, option, Arrays.asList(option.name)));
                        break;
                    case OTHER:
                        List<String> list = Arrays.asList(option.options.keySet().toArray(new String[0]));
                        if (!list.isEmpty()) {
                            Gui.changeRing(new RingSubOption(list, module, option));
                        }
                        break;
                    case STRING:
                        Gui.changeRing(new RingString(module, option, Arrays.asList(option.name)));
                        break;
                }
            }
        } else {
            Gui.prevRing();
        }
    }

    @Override
    public void render() {
        super.render();
        double iconSize = 40;

        int notVisible = 0;

        for (int i = 0; i < visibleList.size(); i++) {
            String key = (String) visibleList.get(i);
            Option option = Option.get(module.options, key);

            double iX = x + Math.cos(((360f / (visibleList.size() * 2)) * (((i - notVisible) + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            double iY = y + Math.sin(((360f / (visibleList.size() * 2)) * (((i - notVisible) + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            iY -= RenderUtil.fontTiny[0].getHeight() / 2;


            switch (option.type) {
                case BOOLEAN:
                    if (option.BOOLEAN()) {
                        RenderUtil2D.donutSeg(x, y, sizeR + (settingSlider[i] * settingSliderWidth) + 20, sizeR + (settingSlider[i] * settingSliderWidth) + 10, i, visibleList.size(), optionPadding, ColorUtil.transparency(option.color, alpha[1]));
                    }
                    break;
                case CHOICE:
                    break;
                case KEYBIND:
                    break;
                case LIST:
                    break;
                case NUMBER:
                    ValueDouble valueDouble = (ValueDouble) option.getTValue();
                    double frac = (1 / (valueDouble.limit[1] - valueDouble.limit[0])) * ((double) valueDouble.getValue() - valueDouble.limit[0]);
                    RenderUtil2D.donutSegFrac(x, y, sizeR + (settingSlider[i] * settingSliderWidth) + 20, sizeR + (settingSlider[i] * settingSliderWidth) + 10, i, visibleList.size(), frac, optionPadding, ColorUtil.transparency(option.color, alpha[1]));
                    break;
                case OTHER:
                    break;
                case STRING:
                    break;
            }
            Helper.mc().getTextureManager().bindTexture(option.icon);
            RenderUtil2D.texturedRect(iX - (iconSize / 2), iY - (iconSize / 2), iX + (iconSize / 2), iY + (iconSize / 2), ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]));
            RenderUtil2D.string(RenderUtil.fontTiny[1], option.name, iX, iY + (iconSize / 2), ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]), 0, 0, false);
        }

        if (selected != -1) {
            Option option = Option.get(module.options, (String) visibleList.get((int) Math.floor(selected)));
            RenderUtil2D.string(RenderUtil.fontLarge[1], option.name, x, y, ColorUtil.transparency(white, alpha[0] * alpha[1]), 0, 0, false);
            RenderUtil2D.string(RenderUtil.fontTiny[1], option.desc, x, y + (RenderUtil.fontLarge[1].getHeight() / 2) + 2, ColorUtil.transparency(gray, alpha[0] * alpha[1]), 0, 1, false);
            switch (option.type) {
                case LIST:
                    RenderUtil2D.string(RenderUtil.fontSmall[1], option.LIST().size() + "", x, y + (RenderUtil.fontLarge[1].getHeight() / 2) + RenderUtil.fontTiny[1].getHeight() + 2, ColorUtil.transparency(white, alpha[0] * alpha[1]), 0, 1, false);
                    break;
                default:
                    RenderUtil2D.string(RenderUtil.fontSmall[1], StringUtils.capitalize(option.STRING()), x, y + (RenderUtil.fontLarge[1].getHeight() / 2) + RenderUtil.fontTiny[1].getHeight() + 2, ColorUtil.transparency(white, alpha[0] * alpha[1]), 0, 1, false);
                    break;
            }
        }
    }

}
