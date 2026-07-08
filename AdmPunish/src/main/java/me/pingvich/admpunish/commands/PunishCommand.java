package me.pingvich.admpunish.commands;

import me.pingvich.admpunish.AdmPunish;
import me.pingvich.admpunish.menus.PunishMenu;
import me.pingvich.admpunish.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class PunishCommand implements CommandExecutor, TabCompleter {

    private final AdmPunish plugin;

    public PunishCommand(AdmPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.usage")));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("punish.admin") && !sender.hasPermission("punish.*")) {
                sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.no-permission")));
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.reload")));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.only-players")));
            return true;
        }

        Player adminOrModer = (Player) sender;
        
        if (!adminOrModer.hasPermission("punish.moder") && 
            !adminOrModer.hasPermission("punish.admin") && 
            !adminOrModer.hasPermission("punish.*")) {
            adminOrModer.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.no-permission")));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            adminOrModer.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.player-not-found").replace("%target%", targetName)));
            return true;
        }

        if (adminOrModer.hasPermission("punish.admin") || adminOrModer.hasPermission("punish.*")) {
            PunishMenu.openMainMenu(adminOrModer, target);
        } else {
            PunishMenu.openMuteMenu(adminOrModer, target);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("punish.admin") || sender.hasPermission("punish.*")) {
                if ("reload".startsWith(args[0].toLowerCase())) {
                    completions.add("reload");
                }
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        return completions;
    }
}