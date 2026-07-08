# AdmPunish 🛡️

A lightweight and highly customizable Minecraft punishment GUI plugin built for **Paper 1.21+**. It allows server administrators and moderators to quickly issue mutes, bans, and IP-bans through an intuitive graphical interface using configurable rules.

## ✨ Features

* **Dynamic GUI Sizes:** Automatically calculates inventory rows based on the number of rules specified in the config.
* **Smart Rule Parsing:** Supports hierarchical rule numbering with dots (e.g., `2.1`, `4.3.1`) seamlessly.
* **Moderator Action Guard:** Automatically checks permissions if a ban/ipban command is placed inside the mute menu to prevent lower-tier staff from abusing higher-tier punishments.
* **Simplified Configuration:** No tedious item-by-item material or slot layout setup. Just list your rules and commands—the plugin handles the rest!

---

## 🛠️ Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/punish <player>` | Opens the main punishment GUI for the target player | *Staff only* |
| `/punish reload` | Reloads the plugin configuration | `punish.admin` / `punish.*` |

### Detailed Permissions:
* **`punish.admin`** or **`punish.*`** — Full access. Required to open the Ban Menu and execute any ban/ipban commands from the GUI.
* **Moderator Level** (No admin permission) — Can only open the Mute Menu and execute standard mute commands.

---

## 📦 Configuration (`config.yml`)

The plugin utilizes a hyper-simplified configuration structure where you only need to manage the text messages and the punishment reasons mapped to their respective console commands:

```yaml
messages:
  no-permission: "&c▶ &fУ вас &cнет прав &fдля использования этой команды!"
  only-players: "&c▶ &fЭту команду могут использовать только игроки!"
  player-not-found: "&c▶ &fИгрок %target% &cне найден &fна сервере!"
  reload: "&a▶ &fКонфигурация плагина &aуспешно &fперезагружена!"
  usage: "&c▶ &fИспользуйте: /punish [reload/игрок]"

gui:
  title: "&fНаказание: &7%target%"
  mute-item-name: "&aМут"
  ban-item-name: "&cБан"

mutes:
  title: "&fМут игрока: &7%target%"
  items:
    "2.1": "mute %target% 1h 2.1"
    "2.2": "mute %target% 2h 2.2"
    "2.3": "mute %target% 15m 2.3"

bans:
  title: "&fБан игрока: &7%target%"
  items:
    "4.1": "ban %target% 3d 4.1"
    "4.2": "ban %target% 7d 4.2"
    "4.3.1": "ipban %target% 30d 4.3.1"
