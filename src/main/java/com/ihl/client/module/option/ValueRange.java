package com.ihl.client.module.option;

public class ValueRange extends Value {

    public double[] limit;
    public double step;

    public ValueRange(double minVal, double maxVal, double min, double max, double step) {
        super(new double[]{minVal, maxVal});
        this.limit = new double[]{min, max};
        this.step = step;
    }

    @Override
    public String stringValue() {
        double[] d = (double[]) value;
        return d[0] + "," + d[1];
    }
}
