package com.ihl.client.gui.ring;

import com.ihl.client.gui.Gui;
import com.ihl.client.input.InputUtil;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class RingSlider extends Ring {

    private final Module module;
    private final Option option;
    private long timeAtPress;

    private boolean dragging = false;
    private double prevM = 0;

    public RingSlider(Module module, Option option, List<String> list) {
        super(list);
        this.module = module;
        this.option = option;
    }

    @Override
    public void tick() {
        if (visibleList.isEmpty()) {
            List<Option> list = Arrays.asList(module.options.values().toArray(new Option[0]));
            list.sort(Comparator.comparingInt(o -> o.weight));
            if (!list.isEmpty()) {
                Gui.changeRing(new RingOption(list, module));
                return;
            }
        }
        super.tick();

        if (dragging) {
            Value value = option.getTValue();
            double m = (MathUtil.dirTo(x, y, InputUtil.mouse[0], InputUtil.mouse[1]) + 180) / 360;
            boolean saveM = true;
            if ((prevM > 0.8) && (m < 0.2)) {
                saveM = false;
                m = 1;
            } else if ((prevM < 0.2) && (m > 0.8)) {
                saveM = false;
                m = 0;
            }
            if (value instanceof ValueDouble) {
                ValueDouble valueDouble = (ValueDouble) value;
                double[] limits = valueDouble.limit.clone();
                double valNeg = 0;
                if (limits[0] < 0) {
                    valNeg = limits[0];
                    limits[1] += -limits[0];
                    limits[0] = 0;
                } //MathUtil.dirTo(x, y, InputUtil.mouse[0], InputUtil.mouse[1]) + 180 + (((360 / limits[1]) * limits[0]))
                double mang = m * 360 + (((360 / limits[1]) * limits[0]));
                double val = Math.min(Math.max(limits[0], ((limits[1] - limits[0]) / 360) * mang), limits[1]);
                val = MathUtil.roundInc(val + valNeg, valueDouble.step);
                option.setValue(val);
            } else if (value instanceof ValueRange) {
                ValueRange valueDouble = (ValueRange) value;
                double[] limits = valueDouble.limit.clone();
                double valNeg = 0;
                if (limits[0] < 0) {
                    valNeg = limits[0];
                    limits[1] += -limits[0];
                    limits[0] = 0;
                } //MathUtil.dirTo(x, y, InputUtil.mouse[0], InputUtil.mouse[1]) + 180 + (((360 / limits[1]) * limits[0]))
                double mang = m * 360 + (((360 / limits[1]) * limits[0]));
                double val = Math.min(Math.max(limits[0], ((limits[1] - limits[0]) / 360) * mang), limits[1]);
                val = MathUtil.roundInc(val + valNeg, valueDouble.step);
                double[] doubles = (double[]) valueDouble.getValue();
                double mid = (doubles[0] + doubles[1]) / 2;
                if (val < mid) doubles[0] = val;
                else doubles[1] = val;
                option.setValue(doubles);
            }
            if (saveM)
            prevM = m;
        }
    }

    @Override
    public void mouseReleased(int button) {
        if (button == 0 && timeAtPress + 1000 < System.currentTimeMillis()) {
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
        String text = "";
        Value value = option.getTValue();
        if (value instanceof ValueDouble) {
            text = String.valueOf(option.DOUBLE());
        } else if (value instanceof ValueRange) {
            text = option.MIN() + " " + option.MAX();
        }
        RenderUtil2D.string(RenderUtil.fontLarge[1], text , x, y, ColorUtil.transparency(white, alpha[1]), 0, 0, false);

        for (int i = 0; i < visibleList.size(); i++) {
            double iX = x + Math.cos(((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            double iY = y + Math.sin(((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            iY -= RenderUtil.fontTiny[0].getHeight() / 2F;

            RenderUtil2D.string(RenderUtil.fontSmall[1], StringUtils.capitalize("" + visibleList.get(i)), iX, iY, ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]), 0, 0, false);
        }
        if (value instanceof ValueDouble) {
            ValueDouble valueDouble = (ValueDouble) option.getTValue();
            double val = (1 / (valueDouble.limit[1] - valueDouble.limit[0])) * ((double) valueDouble.getValue() - valueDouble.limit[0]);
            RenderUtil2D.donutSegFrac(x, y, sizeR + 20, sizeR + 10, 0, 1, val, optionPadding, ColorUtil.transparency(option.color, alpha[1]));
        } else if (value instanceof ValueRange) {
            ValueRange valueRange = (ValueRange) option.getTValue();
            double[] val = (double[]) valueRange.getValue();
            double min = (1 / (valueRange.limit[1] - valueRange.limit[0])) * (val[0] - valueRange.limit[0]);
            double max = (1 / (valueRange.limit[1] - valueRange.limit[0])) * (val[1] - valueRange.limit[0]);
            RenderUtil2D.donutSegFracTest(x, y, sizeR + 20, sizeR + 10, min, max, ColorUtil.transparency(option.color, alpha[1]));
        }
    }

}
