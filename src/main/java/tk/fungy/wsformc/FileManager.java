package tk.fungy.wsformc;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

public class FileManager {

    public static File configFile = new File(Main.instance.getDataFolder(), "config.yml");
    public static File webFolder = new File(Main.instance.getDataFolder(), "web");
    public static YamlConfiguration config;
    public Integer getIntegerFromConfig(Integer in) { config.getString(String.valueOf(in)); return in; }
    public boolean getBooleanFromConfig(boolean b) { return Boolean.parseBoolean(config.getString(String.valueOf(b))); }
    public static void setStringInConfig(String key, String value) {
        config.set(key, value);
        try {
            config.save(configFile);
        } catch (IOException e) {
            Main.getInstance().getLogger().warning("Failed to set string. Executed with this error: \n" + e);
        }
    }


    public void startup() throws UnknownHostException {
        if(!configFile.exists()) {
            Main.instance.saveResource("config.yml", true);
            config = new YamlConfiguration().loadConfiguration(configFile);
            String domain = getStringFromConfig("WebServer.domain");
            if (domain.equalsIgnoreCase("CHANGE_ME")) {
                    FileManager.setStringInConfig("WebServer.domain", "0.0.0.0");
            }

            File cfgFile = new File(Main.instance.getDataFolder(), "config.yml");
            config = YamlConfiguration.loadConfiguration(cfgFile);
        }
        if(!webFolder.exists()) {
            webFolder.mkdir();
        }
    }
    public String getStringFromConfig(String string) {
        if (config == null) {
            config = new YamlConfiguration().loadConfiguration(configFile);
        }
            return config.getString(string);
    }
    public void reloadConfig() {
        File cfgFile = new File(Main.instance.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(cfgFile);
    }
}
