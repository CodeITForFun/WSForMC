package tk.fungy.wsformc;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.UnknownHostException;

public final class Main extends JavaPlugin {

    public static Main getInstance() {
        return instance;
    }

    public static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting plugin...");
        getServer().getPluginManager().registerEvents(new Updater(), Main.instance);
        getLogger().info("Loading File Manager.");
        try {
            new FileManager().startup();
        } catch (UnknownHostException e) {
            getLogger().warning("Failed to found IP of this server, please set this manually.");
        }
        new FileManager().removeLogFile();
        getLogger().info("Loading Assets.");
        instance.getCommand("wsm").setExecutor(new Command());
        instance.getCommand("webserver").setExecutor(new Command());
        if (WebServer.running) {
            if (Command.ws == null) Command.ws = new WebServer();
            getLogger().info("Starting Webserbver.");
            Command.ws.start();
        }
        if (FileManager.config == null) {
            try {
                new FileManager().startup();
            } catch (UnknownHostException e) {
                getLogger().warning("Failed to found IP of this server, please set this manually.");
            }
        }
        FileManager.setStringInConfig("Version", getDescription().getVersion());
        if (!(new FileManager().getBooleanFromConfig("Version"))) {
            new FileManager().setBooleanInConfig("Updates", true);
        }

        int pluginId = 17696;
        Metrics metrics = new Metrics(Main.instance, pluginId);

        Updater.startUpdater();
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling plugin...");
    }
}
