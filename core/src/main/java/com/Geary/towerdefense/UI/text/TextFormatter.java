package com.Geary.towerdefense.UI.text;

public class TextFormatter {

    public static String formatResourceAmount(double amount) {
        if (amount >= 1_000_000) {
            double value = amount / 1_000_000;
            if (value >= 10) {
                // Floor to nearest whole number
                return String.format("%dM", (int) Math.floor(value));
            } else if (value == (int) value) {
                return String.format("%dM", (int) value);
            } else {
                // Floor to 1 decimal place
                double floored = Math.floor(value * 10) / 10.0;
                return String.format("%.1fM", floored);
            }
        } else if (amount >= 1_000) {
            double value = amount / 1_000;
            if (value >= 10) {
                return String.format("%dK", (int) Math.floor(value));
            } else if (value == (int) value) {
                return String.format("%dK", (int) value);
            } else {
                double floored = Math.floor(value * 10) / 10.0;
                return String.format("%.1fK", floored);
            }
        } else {
            return String.valueOf((int) amount);
        }
    }

}
