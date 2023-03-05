package tk.fungy.wsformc.managers;

import tk.fungy.wsformc.Main;

import java.io.IOException;

public class ConfigFixManager {
    public void autofixConfig() {
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
    private void setInt(String s, int var) { if (!FileManager.config.contains(s)) { FileManager.config.set(s, var); } save(); }
    private void setString(String s, String var) { if (!FileManager.config.contains(s)) { FileManager.config.set(s, var); } save(); }
    private void setBoolean(String s, boolean var) { if (!FileManager.config.contains(s)) { FileManager.config.set(s, var); } save(); }

}