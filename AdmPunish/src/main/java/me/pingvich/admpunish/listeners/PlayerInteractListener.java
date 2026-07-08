package me.pingvich.admpunish.listeners;

import me.pingvich.admpunish.AdmPunish;
import me.pingvich.admpunish.menus.PunishMenu;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener implements Listener {

    private final AdmPunish plugin;

    public PlayerInteractListener(AdmPunish plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player admin = event.getPlayer();

        if (admin.getGameMode() != GameMode.SPECTATOR) return;

        if (!(event.getRightClicked() instanceof Player)) return;

        Player target = (Player) event.getRightClicked();

        if (admin.hasPermission("punish.admin") || admin.hasPermission("punish.*")) {
            event.setCancelled(true);
            PunishMenu.openMainMenu(admin, target);
        } else if (admin.hasPermission("punish.moder")) {
            event.setCancelled(true);
            PunishMenu.openMuteMenu(admin, target);
        }
    }
}