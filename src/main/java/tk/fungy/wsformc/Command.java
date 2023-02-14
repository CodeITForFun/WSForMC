package tk.fungy.wsformc;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Command implements CommandExecutor, TabCompleter {
    private static String secured;
    public static WebServer ws;
    private static String running;
    private static String timer;
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

        if (args.length >= 0) {
            switch (args[0]) {
                case "help":
                    TextComponent discord = new TextComponent(Colors.translate("&9&lDISCORD SUPPORT&7"));
                    TextComponent spigotmc = new TextComponent(Colors.translate("&e&lSpigotMC"));
                    discord.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/Yxtc7e4naJ"));
                    spigotmc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/web-server-for-minecraft.107949/"));
                    discord.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Redirect").create()));
                    spigotmc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Redirect").create()));

                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &8⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛\n\n"));
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &bWebServer For Minecraft Server\n&8[&cWebServer&8] &r" +
                                    "\n   &a&lWebServer: \n" +
                                    "   &r&2/wsm start    &7Turns on WebServer\n" +
                                    "   &r&2/wsm stop    &7Turns off WebServer\n" +
                                    "   &r&2/wsm update [domain] / [port]    &7Updates variable in config\n" +
                                    "   &b&lOthers: \n" +
                                    "   &r&9/wsm reload [config] / [plugin]    &7Reloads config/plugin\n" +
                                    "   &r&9/wsm status    &7Displays the status of the webserver\n" +
                                    "   &r&9/wsm version    &7Displays the current and latest version\n"));

                    sender.sendMessage("\n    ");
                    BaseComponent[] dDsc15 = new BaseComponent[]{ new TextComponent(Colors.translate("&8[&cWebServer&8] &aHelpful: ")), discord, new TextComponent(Colors.translate("  &7x  ")), spigotmc, new TextComponent(Colors.translate("\n&8[&cWebServer&8] &8")) };
                    sender.spigot().sendMessage(dDsc15);
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &8⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛"));
                    return true;
                case "ver":
                case "version":
                    if (!(sender.hasPermission("ws.ver") || sender.hasPermission("ws.*"))) {
                        sender.sendMessage(Colors.translate(new FileManager().getStringFromConfig("No-Permission")));
                        return true;
                    }
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &bYour version is: &c" + Main.getInstance().getDescription().getVersion() + "&b, Latest: &c" + Updater.latestVersion));
                    return true;
                case "start":
                    if (!(sender.hasPermission("ws.start") || sender.hasPermission("ws.*"))) {
                        sender.sendMessage(Colors.translate(new FileManager().getStringFromConfig("No-Permission")));
                        return true;
                    }

                    if (ws != null) {
                        sender.sendMessage(Colors.translate("&8[&cWebServer&8] &cWebServer is alredy started!"));
                        return true;
                    }
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &7Starting..."));
                    ws = new WebServer();
                    ws.start();
                    if (new FileManager().getBooleanFromConfig("WebServer.ssl")) { secured = "https://"; } else { secured = "http://"; }
                    TextComponent message = new TextComponent(Colors.translate("&8[&cWebServer&8] &bAccessible via &7" +
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
                case "update":
                    if (!(sender.hasPermission("ws.set") || sender.hasPermission("ws.*"))) {
                        sender.sendMessage(Colors.translate(new FileManager().getStringFromConfig("No-Permission")));
                        return true;
                    }
                    if(args.length > 1) {
                        switch (args[1]) {

                            case "domain":

                                if (args.length <= 2) {
                                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &cPlease type your domain or ip."));
                                    return true;
                                }

                                sender.sendMessage(Colors.translate("&8[&cWebServer&8] &7Setting domain..."));
                                FileManager.setStringInConfig("WebServer.domain", args[2]);

                                new FileManager().reloadConfig();

                                sender.sendMessage(Colors.translate("&8[&cWebServer&8] &aNew domain has been set to " + args[2]));
                                sender.sendMessage(Colors.translate("&8[&cWebServer&8] &7Please start and stop your webserver for effect."));
                                return true;

                            case "port":

                                if (args.length <= 2) {
                                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &cPlease type selected port."));
                                    return true;
                                }

                                if (args[2].length() > 10) {
                                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &cYou have too many numbers. Max is 10."));
                                    return true;
                                }

                                if (!(new FileManager().containsDigits(args[2]))) {
                                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &cPlease type only numbers."));
                                    return true;
                                }

                                sender.sendMessage(Colors.translate("&8[&cWebServer&8] &7Setting port..."));

                                FileManager.setIntegerInConfig("WebServer.port", Integer.valueOf(args[2]));

                                sender.sendMessage(Colors.translate("&8[&cWebServer&8] &aNew port has been set to " + args[2]));
                                sender.sendMessage(Colors.translate("&8[&cWebServer&8] &7Please start and stop your webserver for effect."));
                                return true;
                        }
                    } else {
                        sender.sendMessage(Colors.translate("&8[&cWebServer&8] &bPlease choose between domain or port"));
                    }
                    return true;
                case "stop":
                    if (!(sender.hasPermission("ws.stop") || sender.hasPermission("ws.*"))) {
                        sender.sendMessage(Colors.translate(new FileManager().getStringFromConfig("No-Permission")));
                        return true;
                    }

                    if (ws == null) {
                        sender.sendMessage(Colors.translate("&8[&cWebServer&8] &cWebServer was not started yet!"));
                        return true;
                    }
                    ws.stop();
                    FileManager.setStringInConfig("WebServer.isRunning", String.valueOf(false));
                    if (!(ws.isAlive())) {
                        sender.sendMessage(Colors.translate("&8[&cWebServer&8] &aWebserver has been Stopped!"));
                    } else {
                        sender.sendMessage(Colors.translate("&8[&cWebServer&8] &cWebserver has not been Stopped!"));
                    }
                    ws = null;
                    return true;
                case "reload":
                    if (!(sender.hasPermission("ws.reload") || sender.hasPermission("ws.*"))) {
                        sender.sendMessage(Colors.translate(new FileManager().getStringFromConfig("No-Permission")));
                        return true;
                    }
                    if(args.length > 1) {
                        switch (args[1]) {
                            case "config":
                                sender.sendMessage(Colors.translate("&8[&cWebServer&8] &7Reloading config..."));
                                new FileManager().reloadConfig();
                                sender.sendMessage(Colors.translate("&8[&cWebServer&8] &aReloaded"));
                                return true;
                            case "plugin":
                                sender.sendMessage(Colors.translate("&8[&cWebServer&8] &7Reloading plugin..."));
                                Bukkit.getPluginManager().disablePlugin(Main.instance);
                                Bukkit.getPluginManager().enablePlugin(Main.instance);
                                sender.sendMessage(Colors.translate("&8[&cWebServer&8] &aReloaded"));
                                return true;
                        }
                    } else {
                        sender.sendMessage(Colors.translate("&8[&cWebServer&8] &bPlease choose config/plugin"));
                    }
                    return true;

                    //
                    //TODO: Its for future
                    //

                /*case "type":
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
                    return true;*/

                case "status":
                    if (!(Main.tc.running)) timer = "&cOffline";
                    if (Main.tc.running) timer = Main.tc.getTimeCounter();
                    if (new FileManager().getBooleanFromConfig("WebServer.isRunning")) { running = "&aOnline"; } else { running = "&cOffline"; }
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &8⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛"));
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &bStatus: " + running));
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &bBound on: " + new FileManager().getStringFromConfig("WebServer.domain") + ":" + new FileManager().getStringFromConfig("WebServer.port")));
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &bUptime: &7" + timer));
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &bAccesss Log: &aEnabled &8(&7This is in TODO&8)\n")); //TODO: Add toggle accesslog
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &bCreated by FungYY911 for everyone"));
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &8⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛"));
                    return true;
                default:
                    sender.sendMessage(Colors.translate("&8[&cWebServer&8] &cCommand not found!"));
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
            if (sender.hasPermission("ws.tab") || sender.hasPermission("ws.*")) {
                arguments.add("start");
                arguments.add("stop");
                arguments.add("status");
                arguments.add("reload");
                arguments.add("ver");
                arguments.add("update");
            }
            return arguments;
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    List<String> arguments = new ArrayList<>();
                    arguments.add("config");
                    arguments.add("plugin");
                    return arguments;
                case "update":
                    List<String> arg = new ArrayList<>();
                    arg.add("domain");
                    arg.add("port");
                    return arg;
            }
        }
        return null;
    }
}