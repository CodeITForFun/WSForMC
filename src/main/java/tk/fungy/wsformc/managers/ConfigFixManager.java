package tk.fungy.wsformc.managers;

import tk.fungy.wsformc.Main;

import java.io.IOException;

public class ConfigFixManager {
    public void autofixConfig() {
        /**
         * WebServerManager.port to 8080 as Integer
         */
        setInt("WebServerManager.port", 8080);
        /**
         * WebServerManager.isRunning to false as Boolean
         */
        setBoolean("WebServerManager.isRunning", false);
        /**
         * WebServerManager.domain to "CHANGE_ME" as String
         */
        setString("WebServerManager.domain", "CHANGE_ME");
        /**
         * WebServerManager.ssl to false as Boolean
         */
        setBoolean("WebServerManager.ssl", false);
        /**
         * WebServerManager.accessLog to true as Boolean
         */
        setBoolean("WebServerManager.accessLog", true);
        /**
         * WebServerManager.threads to 0 as Integer
         */
        setInt("WebServerManager.threads", 0);
        /**
         * No-Permission to "&cYou dont have permissions to do this command" as String
         */
        setString("No-Permission", "&cYou dont have permissions to do this command");
        /**
         * Version to version from pom.xml as String
         */
        setString("Version", Main.getInstance().getDescription().getVersion());


        /**
         * Save the changes
         */
        save();
    }

    private void save() {
        /**
         * Save the config
         */
        try {
            FileManager.config.save(FileManager.configFile);
        } catch (IOException e) {
            Main.getInstance().getLogger().warning("----------------------------------------------------------------");
            Main.getInstance().getLogger().warning("Failed to config fix, please report error bottom in our discord!\n");
            e.printStackTrace();
            Main.getInstance().getLogger().warning("\nEnd of error.");
            Main.getInstance().getLogger().warning("----------------------------------------------------------------");

        }
    }
    private void setInt(String s, int var) {
        if (!FileManager.config.contains(s)) { FileManager.config.set(s, var); }
    }
    private void setString(String s, String var) {
        if (!FileManager.config.contains(s)) { FileManager.config.set(s, var); }
    }
    private void setBoolean(String s, Boolean var) {
        if (!FileManager.config.contains(s)) { FileManager.config.set(s, var); }
    }

}