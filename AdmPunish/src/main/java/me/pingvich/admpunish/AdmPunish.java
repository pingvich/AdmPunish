package me.pingvich.admpunish;

import org.bukkit.plugin.java.JavaPlugin;

import me.pingvich.admpunish.commands.PunishCommand;
import me.pingvich.admpunish.listeners.PlayerInteractListener;

public final class AdmPunish extends JavaPlugin {

    private static AdmPunish instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (getCommand("punish") != null) {
            getCommand("punish").setExecutor(new PunishCommand(this));
            getCommand("punish").setTabCompleter(new PunishCommand(this));
        }

        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);

        getLogger().info("AdmPunish успешно активирован! Версия майнкрафт: 1.21.+");
    }

    @Override
    public void onDisable() {
        getLogger().info("AdmPunish успешно выключен.");
    }

    public static AdmPunish getInstance() {
        return instance;
    }
}