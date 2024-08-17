package org.windy.whitelistPerm;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.List;

public final class WhitelistPerm extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        String version = this.getDescription().getVersion();
        String serverName = this.getServer().getName();
        this.getServer().getConsoleSender().sendMessage(Texts.logo);
        this.getServer().getConsoleSender().sendMessage("v"+"§a" + version + "运行环境：§e " + serverName + "\n");
        this.saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        this.getServer().getConsoleSender().sendMessage(Texts.logo);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FileConfiguration config = this.getConfig();
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (config.getBoolean("debug")) {
            getLogger().info(playerName + " 加入了服务器");
        }


        // Check if the plugin is enabled in the config
        if (!config.getBoolean("enable") || !config.getBoolean("enable-placeholder")) {
            return;
        }



        // Check if the player is in the whitelist
        List<String> whiteList = config.getStringList("white-list");
        if (whiteList.contains(playerName)) {
            return; // Player is whitelisted, do nothing
        }

        // Check if the player has any of the required permissions
        List<String> permissions = config.getStringList("perm");
        for (String permission : permissions) {
            if (!player.hasPermission(permission)) {
                if (config.getBoolean("debug")) {
                    getLogger().info("玩家没有有"+permission+"权限");
                }
                return; // Player has required permission, do nothing
            }
        }

        List<String> placeholders = config.getStringList("placeholder");
        for (String placeholder : placeholders) {
            if (Boolean.parseBoolean(PlaceholderAPI.setPlaceholders(player, placeholder))) {
                try {
                    if (config.getBoolean("debug")) {
                        getLogger().info("玩家有" + placeholder + "变量为true");
                    }
                } catch (Exception e) {
                    getLogger().warning("无法获取配置项的布尔值，异常信息: " + e.getMessage());
                }
                return;
            }
        }


        // If the player does not have the required permissions, kick them with the configured message
        String kickMessage = ChatColor.translateAlternateColorCodes('&', config.getString("message", "你没有权限"));
        player.kickPlayer(kickMessage);
        if (config.getBoolean("debug")) {
            getLogger().info(playerName + " 被踢出了服务器");
        }
    }
}
