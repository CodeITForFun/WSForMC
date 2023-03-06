package tk.fungy.wsformc;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tk.fungy.wsformc.commands.WSMCommand;
import tk.fungy.wsformc.managers.*;

import java.net.UnknownHostException;

public final class Main extends JavaPlugin {

    public static Main getInstance() {
        return instance;
    }

    private static Main instance;
    public static TimeCounterManager tc = new TimeCounterManager();

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting plugin...");
        getServer().getPluginManager().registerEvents(new UpdateManager(), Main.getInstance());
        getLogger().info("Loading File Manager.");
        try {
            new FileManager().startup();
        } catch (UnknownHostException e) {
            getLogger().warning("Failed to found IP of this server, please set this manually.");
        }
        new ConfigFixManager().autofixConfig();
        new FileManager().removeLogFile();
        getLogger().info("Loading Assets.");
        getInstance().getCommand("wsm").setExecutor(new WSMCommand());
        getInstance().getCommand("webserver").setExecutor(new WSMCommand());
        if (WebServerManager.running) {
            if (WSMCommand.ws == null) WSMCommand.ws = new WebServerManager();
            getLogger().info("Starting Webserver.");
            WSMCommand.ws.start();
        }
        if (FileManager.config == null) {
            try {
                new FileManager().startup();
            } catch (UnknownHostException e) {
                getLogger().warning("Failed to found IP of this server, please set this manually.");
            }
        }
        FileManager.setStringInConfig("Version", getDescription().getVersion());

        Metrics metrics = new Metrics(Main.getInstance(), 17696);

        UpdateManager.startUpdater();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required for Web placeholders.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling plugin...");
    }
}
