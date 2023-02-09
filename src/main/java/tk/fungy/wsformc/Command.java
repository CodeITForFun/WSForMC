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
                case "toggle":
                    sender.sendMessage("Toggling...");
                    if (new FileManager().getBooleanFromConfig("WebServer.isRunning")) {
                        FileManager.setStringInConfig("WebServer.isRunning", String.valueOf(true));
                    } else {
                        FileManager.setStringInConfig("WebServer.isRunning", String.valueOf(false));
                    }
                    WebServer server = new WebServer();
                    server.toggle();
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
                default:
                    sender.sendMessage("Command not found!");
            }
        } else {
            sender.sendMessage(new FileManager().getStringFromConfig("No-Permission"));
        }
        return true;
    }
}
