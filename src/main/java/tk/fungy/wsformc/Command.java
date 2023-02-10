package tk.fungy.wsformc;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Command implements CommandExecutor, TabCompleter {
    private static String secured;

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
                                    "/wsm reload [config] / [plugin]\n" +
                                    "/wsm start\n" +
                                    "/wsm status\n" +
                                    "/wsm \n");
                    return true;
                case "start":
                    sender.sendMessage("Starting...");
                    WebServer server = new WebServer();
                    server.start();
                    if (new FileManager().getBooleanFromConfig("WebServer.ssl")) {
                        secured = "https://";
                    } else {
                        secured = "http://";
                    }
                    TextComponent message = new TextComponent(Colors.translate("Accessible via " +
                            secured +
                            new FileManager().getStringFromConfig("WebServer.domain") +
                            ":" +
                            new FileManager().getStringFromConfig("WebServer.port")));
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, secured +
                            new FileManager().getStringFromConfig("WebServer.domain") +
                            ":" +
                            new FileManager().getStringFromConfig("WebServer.port")));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to open Website").create()));
                    player.spigot().sendMessage(message);
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
                    sender.sendMessage("Uptime: null"); //TODO: Add Uptime
                    sender.sendMessage("Enable Log: true\n"); //TODO: Add toggle accesslog
                    sender.sendMessage("Created by FungYY911 for everyone");
                    return true;
                default:
                    sender.sendMessage("Command not found!");
            }
        } else {
            sender.sendMessage(Colors.translate(new FileManager().getStringFromConfig("No-Permission")));
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
                arguments.add("reload");
            }
            return arguments;
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    List<String> arguments = new ArrayList<>();
                    arguments.add("config");
                    arguments.add("plugin");
                    return arguments;
            }

        }
        return null;
    }
}