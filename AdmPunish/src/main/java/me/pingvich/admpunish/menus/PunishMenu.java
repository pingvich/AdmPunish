package me.pingvich.admpunish.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.pingvich.admpunish.AdmPunish;
import me.pingvich.admpunish.utils.ColorUtils;

public class PunishMenu implements Listener {

    private static final HashMap<UUID, UUID> mainMenuViewers = new HashMap<>();
    private static final HashMap<UUID, UUID> muteMenuViewers = new HashMap<>();
    private static final HashMap<UUID, UUID> banMenuViewers = new HashMap<>();

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (!(event.getWhoClicked() instanceof Player)) return;
                Player viewer = (Player) event.getWhoClicked();
                UUID viewerId = viewer.getUniqueId();

                // 1. Главное меню
                if (mainMenuViewers.containsKey(viewerId)) {
                    event.setCancelled(true);
                    ItemStack clicked = event.getCurrentItem();
                    if (clicked == null || clicked.getType() == Material.AIR) return;

                    Player target = Bukkit.getPlayer(mainMenuViewers.get(viewerId));
                    if (target == null) {
                        viewer.closeInventory();
                        return;
                    }

                    if (event.getSlot() == 11) {
                        viewer.closeInventory();
                        openMuteMenu(viewer, target);
                    } else if (event.getSlot() == 15) {
                        if (viewer.hasPermission("punish.admin") || viewer.hasPermission("punish.*")) {
                            viewer.closeInventory();
                            openBanMenu(viewer, target);
                        } else {
                            viewer.sendMessage(ColorUtils.color(AdmPunish.getInstance().getConfig().getString("messages.no-permission")));
                            viewer.closeInventory();
                        }
                    }
                    return;
                }

                // 2. Меню мутов
                if (muteMenuViewers.containsKey(viewerId)) {
                    event.setCancelled(true);
                    ItemStack clicked = event.getCurrentItem();
                    if (clicked == null || clicked.getType() == Material.AIR) return;

                    Player target = Bukkit.getPlayer(muteMenuViewers.get(viewerId));
                    if (target == null) {
                        viewer.closeInventory();
                        return;
                    }

                    ConfigurationSection section = AdmPunish.getInstance().getConfig().getConfigurationSection("mutes.items");
                    if (section != null) {
                        // Исправлено: собираем ключи с поддержкой точек
                        List<String> keys = new ArrayList<>();
                        for (String key : section.getKeys(true)) {
                            if (section.isString(key)) {
                                keys.add(key);
                            }
                        }

                        int slot = event.getSlot();
                        if (slot >= 0 && slot < keys.size()) {
                            String rule = keys.get(slot);
                            String cmd = section.getString(rule);

                            if (cmd != null && (cmd.toLowerCase().startsWith("ban ") || cmd.toLowerCase().startsWith("ipban "))) {
                                if (!viewer.hasPermission("punish.admin") && !viewer.hasPermission("punish.*")) {
                                    viewer.sendMessage(ColorUtils.color(AdmPunish.getInstance().getConfig().getString("messages.no-permission")));
                                    viewer.closeInventory();
                                    return;
                                }
                            }

                            if (cmd != null) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%target%", target.getName()));
                            }
                            viewer.closeInventory();
                        }
                    }
                    return;
                }

                // 3. Меню банов
                if (banMenuViewers.containsKey(viewerId)) {
                    event.setCancelled(true);
                    ItemStack clicked = event.getCurrentItem();
                    if (clicked == null || clicked.getType() == Material.AIR) return;

                    if (!viewer.hasPermission("punish.admin") && !viewer.hasPermission("punish.*")) {
                        viewer.sendMessage(ColorUtils.color(AdmPunish.getInstance().getConfig().getString("messages.no-permission")));
                        viewer.closeInventory();
                        return;
                    }

                    Player target = Bukkit.getPlayer(banMenuViewers.get(viewerId));
                    if (target == null) {
                        viewer.closeInventory();
                        return;
                    }

                    ConfigurationSection section = AdmPunish.getInstance().getConfig().getConfigurationSection("bans.items");
                    if (section != null) {
                        // Исправлено: собираем ключи с поддержкой точек
                        List<String> keys = new ArrayList<>();
                        for (String key : section.getKeys(true)) {
                            if (section.isString(key)) {
                                keys.add(key);
                            }
                        }

                        int slot = event.getSlot();
                        if (slot >= 0 && slot < keys.size()) {
                            String rule = keys.get(slot);
                            String cmd = section.getString(rule);
                            if (cmd != null) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%target%", target.getName()));
                            }
                            viewer.closeInventory();
                        }
                    }
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                UUID id = event.getPlayer().getUniqueId();
                mainMenuViewers.remove(id);
                muteMenuViewers.remove(id);
                banMenuViewers.remove(id);
            }
        }, AdmPunish.getInstance());
    }

    public static void openMainMenu(Player admin, Player target) {
        String title = ColorUtils.color(AdmPunish.getInstance().getConfig().getString("gui.title").replace("%target%", target.getName()));
        Inventory gui = Bukkit.createInventory(null, 27, title);

        ItemStack muteItem = new ItemStack(Material.FEATHER);
        ItemMeta muteMeta = muteItem.getItemMeta();
        if (muteMeta != null) {
            muteMeta.setDisplayName(ColorUtils.color(AdmPunish.getInstance().getConfig().getString("gui.mute-item-name", "&aМут")));
            List<String> lore = new ArrayList<>();
            lore.add(ColorUtils.color("&7Нажмите, чтобы открыть"));
            lore.add(ColorUtils.color("&7список доступных мутов."));
            muteMeta.setLore(lore);
            muteItem.setItemMeta(muteMeta);
        }

        ItemStack banItem = new ItemStack(Material.BARRIER);
        ItemMeta banMeta = banItem.getItemMeta();
        if (banMeta != null) {
            banMeta.setDisplayName(ColorUtils.color(AdmPunish.getInstance().getConfig().getString("gui.ban-item-name", "&cБан")));
            List<String> lore = new ArrayList<>();
            lore.add(ColorUtils.color("&7Нажмите, чтобы открыть"));
            lore.add(ColorUtils.color("&7список доступных банов."));
            banMeta.setLore(lore);
            banItem.setItemMeta(banMeta);
        }

        gui.setItem(11, muteItem);
        gui.setItem(15, banItem);

        mainMenuViewers.put(admin.getUniqueId(), target.getUniqueId());
        admin.openInventory(gui);
    }

    public static void openMuteMenu(Player moderator, Player target) {
        ConfigurationSection section = AdmPunish.getInstance().getConfig().getConfigurationSection("mutes.items");
        if (section == null) return;

        // Исправлено: глубокое чтение ключей с точками
        List<String> rules = new ArrayList<>();
        for (String key : section.getKeys(true)) {
            if (section.isString(key)) {
                rules.add(key);
            }
        }

        int size = calculateSize(rules.size());
        String title = ColorUtils.color(AdmPunish.getInstance().getConfig().getString("mutes.title").replace("%target%", target.getName()));
        Inventory gui = Bukkit.createInventory(null, size, title);

        for (int i = 0; i < rules.size(); i++) {
            String rule = rules.get(i);
            gui.setItem(i, createAutoItem(Material.PAPER, rule));
        }

        muteMenuViewers.put(moderator.getUniqueId(), target.getUniqueId());
        moderator.openInventory(gui);
    }

    public static void openBanMenu(Player admin, Player target) {
        ConfigurationSection section = AdmPunish.getInstance().getConfig().getConfigurationSection("bans.items");
        if (section == null) return;

        // Исправлено: глубокое чтение ключей с точками
        List<String> rules = new ArrayList<>();
        for (String key : section.getKeys(true)) {
            if (section.isString(key)) {
                rules.add(key);
            }
        }

        int size = calculateSize(rules.size());
        String title = ColorUtils.color(AdmPunish.getInstance().getConfig().getString("bans.title").replace("%target%", target.getName()));
        Inventory gui = Bukkit.createInventory(null, size, title);

        for (int i = 0; i < rules.size(); i++) {
            String rule = rules.get(i);
            gui.setItem(i, createAutoItem(Material.BARRIER, rule));
        }

        banMenuViewers.put(admin.getUniqueId(), target.getUniqueId());
        admin.openInventory(gui);
    }

    private static int calculateSize(int count) {
        if (count <= 0) return 9;
        int quotient = (int) Math.ceil((double) count / 9);
        return Math.min(quotient * 9, 54);
    }

    private static ItemStack createAutoItem(Material material, String rule) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtils.color("&cПункт правила " + rule));
            List<String> lore = new ArrayList<>();
            lore.add(ColorUtils.color("&7Нажмите, чтобы выдать наказание"));
            lore.add(ColorUtils.color("&7согласно данному пункту."));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}