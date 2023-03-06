package tk.fungy.wsformc.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import tk.fungy.wsformc.Main;

import java.io.IOException;

import static tk.fungy.wsformc.managers.FileManager.config;
import static tk.fungy.wsformc.managers.FileManager.configFile;

public class ConfigFixManager {
    public void autofixConfig() {
        /**
         * if config is null, load it
         */
        if (config == null) {
            config = new YamlConfiguration().loadConfiguration(configFile);
        }
        /**
         * WebServer.port to 8080 as Integer
         */
        setInt("WebServer.port", 8080);
        /**
         * WebServer.isRunning to false as Boolean
         */
        setBoolean("WebServer.isRunning", false);
        /**
         * WebServer.domain to "CHANGE_ME" as String
         */
        setString("WebServer.domain", "CHANGE_ME");
        /**
         * WebServer.ssl to false as Boolean
         */
        setBoolean("WebServer.ssl", false);
        /**
         * WebServer.accessLog to true as Boolean
         */
        setBoolean("WebServer.accessLog", true);
        /**
         * WebServer.threads to 0 as Integer
         */
        setInt("WebServer.threads", 0);
        /**
         * No-Permission to "&cYou dont have permissions to do this command" as String
         */
        setString("No-Permission", "&cYou dont have permissions to do this command");
        /**
         * Version to version from pom.xml as String
         */
        setString("Version", Main.getInstance().getDescription().getVersion());
        save();

        //Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "restart");
    }

    private void save() {
        /**
         * Save the config
         */
        try {
            config.save(configFile);
        } catch (IOException e) {
            Main.getInstance().getLogger().warning("[WebServer] ----------------------------------------------------------------");
            Main.getInstance().getLogger().warning("[WebServer] Failed to save config, please report bottom error in our discord!\n");
            e.printStackTrace();
            Main.getInstance().getLogger().warning("\n[WebServer] End of error.");
            Main.getInstance().getLogger().warning("[WebServer] ----------------------------------------------------------------");
        }
    }
    private void setInt(String s, int var) { if (!config.contains(s)) { config.set(s, var); } save(); }
    private void setString(String s, String var) { if (!config.contains(s)) { config.set(s, var); } save(); }
    private void setBoolean(String s, boolean var) { if (!config.contains(s)) { config.set(s, var); } save(); }

}