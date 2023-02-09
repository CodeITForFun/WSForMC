package tk.fungy.wsformc;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
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
        Updater.startUpdater();
        getServer().getPluginManager().registerEvents(new Updater(), Main.instance);
        getLogger().info("Loading File Manager.");
        try {
            new FileManager().startup();
        } catch (UnknownHostException e) {
            getLogger().warning("Failed to found IP of this server, please set this manually.");
        }
        getLogger().info("Loading WebServer.");
        if (Boolean.parseBoolean(new FileManager().getStringFromConfig("WebServer.isRunning"))) {
            getLogger().info("Starting WebServer...");
            WebServer server = new WebServer();
            server.start();
        }
        getLogger().info("Loading Commands.");
        instance.getCommand("wsm").setExecutor(new Command());
        instance.getCommand("webserver").setExecutor(new Command());
    }

    @Override
    public void onDisable() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_ss:mm:HH");
        String newFileName = dateFormat.format(new Date()) + ".log";
        File newFile = new File(Main.instance.getDataFolder(), "logs/" + newFileName);
        FileManager.logsFile.renameTo(newFile);
        getLogger().info("Disabling plugin...");
    }
}
