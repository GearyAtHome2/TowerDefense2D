package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.badlogic.gdx.graphics.Color;

import java.util.*;

public class SpawnerTabs {

    public enum OrderTab {
        ALL,
        NEUTRAL,
        TECH,
        NATURE,
        DARK,
        LIGHT,
        FIRE,
        WATER
    }

    private final List<OrderTab> tabs = new ArrayList<>();
    private final Map<OrderTab, Color> tabColors = new EnumMap<>(OrderTab.class);
    private final Map<OrderTab, List<MobMenuEntry>> tabEntries = new EnumMap<>(OrderTab.class);

    private int activeTab = 0;

    public SpawnerTabs(FriendlySpawner spawner, MobMenuEntry.MobEntryListener listener) {
        buildTabs(spawner, listener);
        populateTabColours();
    }

    /* =========================
       Construction
       ========================= */

    private void buildTabs(FriendlySpawner spawner, MobMenuEntry.MobEntryListener listener) {
        tabs.clear();
        tabEntries.clear();

        tabs.add(OrderTab.ALL);
        for (Mob.Order order : Mob.Order.values()) {
            tabs.add(OrderTab.valueOf(order.name()));
        }

        for (OrderTab tab : tabs) {
            List<MobMenuEntry> entries = new ArrayList<>();

            for (Mob mob : spawner.getSpawnableMobs()) {
                if (tab != OrderTab.ALL && !mob.order.name().equals(tab.name())) continue;

                MobMenuEntry entry = new MobMenuEntry(mob, 0, 0, 60, 60, listener);
                entry.onClick = () -> {};
                entries.add(entry);
            }

            tabEntries.put(tab, entries);
        }
    }

    private void populateTabColours() {
        tabColors.put(OrderTab.ALL,     new Color(0.6f, 0.6f, 0.6f, 1f));
        tabColors.put(OrderTab.NEUTRAL, new Color(0.6f, 0.6f, 0.6f, 1f));
        tabColors.put(OrderTab.TECH,    new Color(0.4f, 0.5f, 0.7f, 1f));
        tabColors.put(OrderTab.NATURE,  new Color(0.2f, 0.7f, 0.2f, 1f));
        tabColors.put(OrderTab.DARK,    new Color(0.1f, 0.1f, 0.1f, 1f));
        tabColors.put(OrderTab.LIGHT,   new Color(0.8f, 0.8f, 0.5f, 1f));
        tabColors.put(OrderTab.FIRE,    new Color(0.9f, 0.2f, 0.1f, 1f));
        tabColors.put(OrderTab.WATER,   new Color(0.2f, 0.4f, 0.9f, 1f));
    }

    /* =========================
       Accessors (NO behaviour)
       ========================= */

    public List<OrderTab> getTabs() {
        return tabs;
    }

    public int getActiveTabIndex() {
        return activeTab;
    }

    public void setActiveTabIndex(int idx) {
        if (idx >= 0 && idx < tabs.size()) {
            activeTab = idx;
        }
    }

    public OrderTab getActiveTab() {
        return tabs.get(activeTab);
    }

    public Color getActiveTabColor() {
        return tabColors.getOrDefault(getActiveTab(), new Color(0.6f, 0.6f, 0.6f, 1f));
    }

    public List<MobMenuEntry> getActiveEntries() {
        return tabEntries.get(getActiveTab());
    }

    public float getActiveEntriesTotalHeight(float gap) {
        List<MobMenuEntry> entries = getActiveEntries();
        float total = 0f;

        for (MobMenuEntry e : entries) {
            total += e.bounds().height + gap;
        }

        if (!entries.isEmpty()) total -= gap;
        return total;
    }

    public Color getTabColor(OrderTab tab) {
        return tabColors.getOrDefault(tab, new Color(0.6f, 0.6f, 0.6f, 1f));
    }

    public Map<OrderTab, Color> getTabColors(){ return tabColors;}
}
