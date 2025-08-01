package com.ha1fdan.serverunderdevelopment;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

public class ServerUnderDevelopment extends JavaPlugin implements Listener {
    
    private File allowedUsersFile;
    private FileConfiguration allowedUsersConfig;
    
    @Override
    public void onEnable() {
        // Create plugin folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // Setup allowed_users.yml file
        setupAllowedUsersFile();
        
        // Register event listener
        getServer().getPluginManager().registerEvents(this, this);
        
        // Register command executor
        ServerDevCommand commandHandler = new ServerDevCommand(this);
        getCommand("serverdev").setExecutor(commandHandler);
        getCommand("serverdev").setTabCompleter(commandHandler);
        
        getLogger().info("ServerUnderDevelopment plugin has been enabled!");
        getLogger().info("Only users in allowed_users.yml can join the server.");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("ServerUnderDevelopment plugin has been disabled!");
    }
    
    private void setupAllowedUsersFile() {
        allowedUsersFile = new File(getDataFolder(), "allowed_users.yml");
        
        if (!allowedUsersFile.exists()) {
            try {
                // Create the file
                allowedUsersFile.createNewFile();
                
                // Copy default content from resources or create default content
                InputStream defaultConfig = getResource("allowed_users.yml");
                if (defaultConfig != null) {
                    Files.copy(defaultConfig, allowedUsersFile.toPath());
                    defaultConfig.close();
                } else {
                    // Create default content if resource doesn't exist
                    YamlConfiguration defaultYml = new YamlConfiguration();
                    defaultYml.set("allowed_users", List.of("ExampleUser1", "ExampleUser2"));
                    defaultYml.set("kick_message", "&c&lServer Under Development\n&7Please try again later!");
                    defaultYml.save(allowedUsersFile);
                }
                
                getLogger().info("Created default allowed_users.yml file");
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Could not create allowed_users.yml file", e);
            }
        }
        
        allowedUsersConfig = YamlConfiguration.loadConfiguration(allowedUsersFile);
    }
    
    public void reloadAllowedUsers() {
        allowedUsersConfig = YamlConfiguration.loadConfiguration(allowedUsersFile);
        getLogger().info("Reloaded allowed_users.yml");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String playerName = player.getName();

        // Reload config to get latest changes
        reloadAllowedUsers();

        // Get allowed users list
        List<String> allowedUsers = allowedUsersConfig.getStringList("allowed_users");

        // Check if player is in allowed list
        if (!allowedUsers.contains(playerName)) {
            // Get and format kick message
            final String kickMessage = ChatColor.translateAlternateColorCodes('&',
                    allowedUsersConfig.getString("kick_message",
                            "&c&lServer Under Development\n&7Please try again later!"));

            // Schedule kick for next tick to avoid issues with join event
            getServer().getScheduler().runTask(this, () -> {
                player.kickPlayer(kickMessage);
                getLogger().info("Kicked player " + playerName + " - not in allowed users list");
            });

            // Cancel join message
            event.setJoinMessage(null);
        } else {
            getLogger().info("Allowed user " + playerName + " joined the server");
        }
    }
}