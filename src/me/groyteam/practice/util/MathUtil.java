package me.groyteam.practice.util;

public final class MathUtil
{
    public static String convertTicksToMinutes(final int ticks) {
        final long minute = ticks / 1200L;
        final long second = ticks / 20L - minute * 60L;
        String secondString = new StringBuilder(String.valueOf(Math.round(second))).toString();
        if (second < 10L) {
            secondString = String.valueOf(0) + secondString;
        }
        String minuteString = new StringBuilder(String.valueOf(Math.round(minute))).toString();
        if (minute == 0L) {
            minuteString = "0";
        }
        return String.valueOf(minuteString) + ":" + secondString;
    }

    public static String convertToRomanNumeral(final int number) {
        switch (number) {
            case 1: {
                return "I";
            }
            case 2: {
                return "II";
            }
            default: {
                return null;
            }
        }
    }

    public static double roundToHalves(final double d) {
        return Math.round(d * 2.0) / 2.0;
    }
}
