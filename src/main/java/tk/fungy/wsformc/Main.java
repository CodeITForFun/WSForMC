package tk.fungy.wsformc;

import com.sun.org.apache.xpath.internal.operations.Bool;
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
        Updater.startUpdater();
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling plugin...");
    }
}
