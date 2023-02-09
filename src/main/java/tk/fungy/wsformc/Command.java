package tk.fungy.wsformc;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Command implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Failed to execute command, are connected to server?");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            switch (command.getName()) {
                case "wsm":
                case "webserver":
                    ((Player) sender).performCommand("wsm help");
                    return true;
            }
        }

        if (args.length >= 0 && sender.hasPermission("ws.*")) {
            switch (args[0]) {
                case "help":
                    sender.sendMessage(
                            "Commands: \n" +
                                    "/wsm reload\n" +
                                    "/wsm start\n" +
                                    "/wsm status\n" +
                                    "/wsm \n");
                    return true;
                case "start":
                    sender.sendMessage("Starting...");
                    if (new FileManager().getStringFromConfig("WebServer.type").equals("origin")) {
                    WebServer server = new WebServer();
                    server.start();
                    } else if (new FileManager().getStringFromConfig("WebServer.type").equals("cdn")) {
                        CDNServer cdnServer = null;
                        try {
                            cdnServer = new CDNServer();
                            cdnServer.start();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return true;
                case "reload":
                    switch (args[1]) {
                        case "config":
                            sender.sendMessage("Reloading config...");
                            new FileManager().reloadConfig();
                            sender.sendMessage("Reloaded");
                            return true;
                        case "plugin":
                            sender.sendMessage("Reloading plugin...");
                            Bukkit.getPluginManager().disablePlugin(Main.instance);
                            Bukkit.getPluginManager().enablePlugin(Main.instance);
                            sender.sendMessage("Reloaded");
                            return true;
                    }
                    sender.sendMessage("config, plugin");
                    return true;
                case "type":
                    switch (args[1]) {
                        case "cdn":
                            sender.sendMessage("Setting to cdn...");
                            FileManager.setStringInConfig("WebServer.type", "cdn");
                            sender.sendMessage("Setted");
                            return true;
                        case "origin":
                            sender.sendMessage("Setting to origin...");
                            FileManager.setStringInConfig("WebServer.type", "origin");
                            sender.sendMessage("Setted");
                            return true;
                    }
                    return true;
                case "status":
                    sender.sendMessage("Is Active: " + new FileManager().getBooleanFromConfig("WebServer.isRunning"));
                    sender.sendMessage("Uptime: null");
                    sender.sendMessage("Enable Log: null\n");
                    sender.sendMessage("Created by FungYY911 for everyone");
                    return true;
                default:
                    sender.sendMessage("Command not found!");
            }
        } else {
            sender.sendMessage(new FileManager().getStringFromConfig("No-Permission"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("help");
            if (sender.hasPermission("wsm.tab") || sender.hasPermission("wsm.*")) {
                arguments.add("status");
                arguments.add("start");
            }
            return arguments;
        } else if(args.length == 2) {
            switch(args[0].toLowerCase()) {
                //TODO: ADD LOG AND THEIR TYPES AND TYPE ADD CDN
                case "type":
                    List<String> arguments = new ArrayList<>();
                    arguments.add("cdn");
                    arguments.add("origin");
                    return arguments;
                case "log":
                    List<String> arguments0 = new ArrayList<>();
                    arguments0.add("live");
                    arguments0.add("grep");
                    return arguments0;
            }
        }
        return null;
    }
}