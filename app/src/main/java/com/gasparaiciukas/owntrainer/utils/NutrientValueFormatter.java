package com.gasparaiciukas.owntrainer.utils;

import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class NutrientValueFormatter extends ValueFormatter {
    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {
        pieEntry.setLabel("");
        return super.getPieLabel(value, pieEntry);
    }

    @Override
    public String getFormattedValue(float value) {
        DecimalFormat df = new DecimalFormat("#");
        String floatToPercent = df.format(value);
        if (value == 0) return "";
        else return floatToPercent + "%";
    }
}
