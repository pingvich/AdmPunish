package me.pingvich.admpunish.utils;

import org.bukkit.ChatColor;

public class ColorUtils {
    public static String color(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}