package com.ha1fdan.serverunderdevelopment;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerDevCommand implements CommandExecutor, TabCompleter {
    
    private final ServerUnderDevelopment plugin;
    
    public ServerDevCommand(ServerUnderDevelopment plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("serverdev.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadAllowedUsers();
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded allowed_users.yml!");
                break;
                
            case "info":
                sendInfoMessage(sender);
                break;
                
            case "help":
            default:
                sendHelpMessage(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== ServerUnderDevelopment Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/serverdev reload" + ChatColor.WHITE + " - Reload the allowed_users.yml file");
        sender.sendMessage(ChatColor.YELLOW + "/serverdev info" + ChatColor.WHITE + " - Show plugin information");
        sender.sendMessage(ChatColor.YELLOW + "/serverdev help" + ChatColor.WHITE + " - Show this help message");
    }
    
    private void sendInfoMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== ServerUnderDevelopment Info ===");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Author: " + ChatColor.WHITE + plugin.getDescription().getAuthors());
        sender.sendMessage(ChatColor.YELLOW + "Description: " + ChatColor.WHITE + plugin.getDescription().getDescription());
        sender.sendMessage(ChatColor.GRAY + "Config file: plugins/ServerUnderDevelopment/allowed_users.yml");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("serverdev.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            List<String> completions = Arrays.asList("reload", "info", "help");
            List<String> result = new ArrayList<>();
            
            for (String completion : completions) {
                if (completion.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(completion);
                }
            }
            
            return result;
        }
        
        return new ArrayList<>();
    }
}