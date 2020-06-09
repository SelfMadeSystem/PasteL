package com.ihl.client.gui.ring;

import com.ihl.client.gui.Gui;
import com.ihl.client.input.InputUtil;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.Option;
import com.ihl.client.module.option.ValueDouble;
import com.ihl.client.util.ColorUtil;
import com.ihl.client.util.MathUtil;
import com.ihl.client.util.RenderUtil;
import com.ihl.client.util.RenderUtil2D;
import com.ihl.client.util.part.Settings;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class RingSlider extends Ring {

    private Module module;
    private Option option;
    private long timeAtPress;

    private boolean dragging = false;

    public RingSlider(Module module, Option option, List<String> list) {
        super(list);
        this.module = module;
        this.option = option;
    }

    @Override
    public void tick() {
        if (list.isEmpty()) {
            List<String> list = Arrays.asList(module.options.keySet().toArray(new String[0]));
            if (!list.isEmpty()) {
                Gui.changeRing(new RingOption(list, module));
                return;
            }
        }
        super.tick();

        if (dragging) {
            ValueDouble valueDouble = (ValueDouble) option.getTValue();
            double[] limits = valueDouble.limit.clone();
            double valNeg = 0;
            if (limits[0] < 0) {
                valNeg = limits[0];
                limits[1] += -limits[0];
                limits[0] = 0;
            }
            double mang = MathUtil.dirTo(x, y, InputUtil.mouse[0], InputUtil.mouse[1]) + 180 + (((360 / limits[1]) * limits[0]));
            double val = Math.min(Math.max(limits[0], ((limits[1] - limits[0]) / 360) * mang), limits[1]);
            val = MathUtil.roundInc(val + valNeg, valueDouble.step);
            option.setValue(val);
        }
    }

    @Override
    public void mouseReleased(int button) {
        if (button == 0 && timeAtPress + 1000 < System.currentTimeMillis()){
            dragging = false;
        }
    }

    @Override
    public void mouseClicked(int button) {
        if (mouseOver && button == 0) {
            timeAtPress = System.currentTimeMillis();
            dragging = !dragging;
        } else {
            Gui.prevRing();
        }
    }

    @Override
    public void render() {
        super.render();

        RenderUtil2D.string(RenderUtil.fontLarge[1], "" + option.DOUBLE(), x, y, ColorUtil.transparency(white, alpha[1]), 0, 0, false);

        for (int i = 0; i < list.size(); i++) {
            double iX = x + Math.cos(((360f / (list.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            double iY = y + Math.sin(((360f / (list.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            iY -= RenderUtil.fontTiny[0].getHeight() / 2;

            RenderUtil2D.string(RenderUtil.fontSmall[1], StringUtils.capitalize("" + list.get(i)), iX, iY, ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]), 0, 0, false);
        }

        ValueDouble valueDouble = (ValueDouble) option.getTValue();
        double val = (1 / (valueDouble.limit[1] - valueDouble.limit[0])) * ((double) valueDouble.getValue() - valueDouble.limit[0]);
        RenderUtil2D.donutSegFrac(x, y, sizeR + 20, sizeR + 10, 0, 1, val, optionPadding, ColorUtil.transparency(option.color, alpha[1]));
    }

}
