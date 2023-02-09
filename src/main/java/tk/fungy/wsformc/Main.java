package tk.fungy.wsformc;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    public static Main getInstance() {
        return instance;
    }

    public static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting plugin...");
        getLogger().info("Loading File Manager.");
        try {
            new FileManager().startup();
        } catch (UnknownHostException e) {
            getLogger().warning("Failed to found IP of this server, please set this manually.");
        }
        getLogger().info("Loading WebServer.");
        if (Boolean.parseBoolean(new FileManager().getStringFromConfig("WebServer.isRunning")) == true) {
            WebServer server = new WebServer();
            server.start();
        }
        getLogger().info("Loading Commands.");
        instance.getCommand("wsm").setExecutor(new Command());
        instance.getCommand("webserver").setExecutor(new Command());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
