package tk.fungy.wsformc;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Failed to execute command, are connected to server?");
            return true;
        }

        Player player = (Player) sender;

        if(args.length == 0) {
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
                            "/wsm blabla\n" +
                            "/wsm reload\n" +
                            "/wsm stop\n" +
                            "/wsm start\n" +
                            "/wsm status\n" +
                            "/wsm \n");
                    return true;
                case "start":
                    sender.sendMessage("Starting...");
                    WebServer server = new WebServer();
                    server.start();
                    return true;
                case "stop":
                    sender.sendMessage("Stopping...");
                    WebServer wsm = new WebServer();
                    wsm.stopServer();
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
}
