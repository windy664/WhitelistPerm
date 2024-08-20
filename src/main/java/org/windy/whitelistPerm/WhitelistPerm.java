package org.windy.whitelistPerm;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.List;

public final class WhitelistPerm extends JavaPlugin implements Listener {
    FileConfiguration config;
    List<String> commands;
    List<String> uncommands;
    List<String> placeholders;
    List<String> whiteList;
    boolean debug;
    @Override
    public void onEnable() {
        String version = this.getDescription().getVersion();
        String serverName = this.getServer().getName();
        this.getServer().getConsoleSender().sendMessage(Texts.logo);
        this.getServer().getConsoleSender().sendMessage("v"+"§a" + version + "运行环境：§e " + serverName + "\n");
        this.saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);


        config = this.getConfig();
        commands = config.getStringList("placeholder-commands");
        placeholders = config.getStringList("placeholder");
        uncommands = config.getStringList("placeholder-uncommands");
        whiteList = config.getStringList("white-list");
        debug = config.getBoolean("debug");

        if (debug) {
            getLogger().info("Debug模式已启用");
        }
    }

    @Override
    public void onDisable() {
        this.getServer().getConsoleSender().sendMessage(Texts.logo);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (debug) {
            getLogger().info(playerName + " 加入了服务器");
        }


        // Check if the plugin is enabled in the config
        if (!config.getBoolean("enable") || !config.getBoolean("enable-placeholder")) {
            return;
        }

        // Check if the player is in the whitelist

        if (whiteList.contains(playerName)) {
            return; // Player is whitelisted, do nothing
        }

        // Check if the player has any of the required permissions
        List<String> permissions = config.getStringList("perm");
        for (String permission : permissions) {
            if (!player.hasPermission(permission)) {
                if (debug) {
                    getLogger().info("玩家没有有"+permission+"权限");
                }
                return; // Player has required permission, do nothing
            }
        }

        for (String placeholder : placeholders) {
            if (Boolean.parseBoolean(PlaceholderAPI.setPlaceholders(player, placeholder))) {
                try {
                    if (debug) {
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
        if (debug) {
            getLogger().info(playerName + " 被踢出了服务器");
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        String owner = PlaceholderAPI.setPlaceholders(player, "%iridiumSkyblock_current_island_owner%");
        String list = PlaceholderAPI.setPlaceholders(player, "%iridiumSkyblock_current_island_members_online%");

        if (debug) {
            getLogger().info(playerName + " 发生传送事件");
            getLogger().info(whiteList.contains(playerName)+" "+owner.equals("windy")+" "+!list.contains(playerName));
        }




        Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                if (whiteList.contains(playerName) || owner.equals("windy") || !list.contains(playerName)) {
                    if (debug) {
                        getLogger().info(playerName + "命令取消");
                    }
                    return; // Player is whitelisted, do nothing
                }
                // 在这里继续执行其他代码
            }
        }, 60L); // 60 ticks = 3 seconds




        for (String command : commands) {
            String processedCommand = PlaceholderAPI.setPlaceholders(player, command);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
            if (debug) {
                   getLogger().info(playerName + " 已被执行命令: " + processedCommand);
            }
        }
    }
}
