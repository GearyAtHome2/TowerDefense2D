package com.Geary.towerdefense.pathGeneration;

public final class ZoneUtil {

    private ZoneUtil() {}

    public static boolean isInHomeZone(int x, int y, int zoneSize) {
        return x < zoneSize && y < zoneSize;
    }

    public static boolean isInEndZone(int x, int y, int gridW, int gridH, int zoneSize) {
        return x >= gridW - zoneSize && y >= gridH - zoneSize;
    }
}
