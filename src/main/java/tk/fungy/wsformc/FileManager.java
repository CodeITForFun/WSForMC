package tk.fungy.wsformc;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {

    public static File configFile = new File(Main.instance.getDataFolder(), "config.yml");
    public static File webFolder = new File(Main.instance.getDataFolder(), "web");
    public static File logsFolder = new File(Main.instance.getDataFolder(), "logs");
    public static File logsFile = new File(Main.instance.getDataFolder(), "logs/access.log");
    public static YamlConfiguration config;
    public static String ipaddr;
    public boolean getBooleanFromConfig(String b) { return Boolean.parseBoolean(config.getString(String.valueOf(b))); }
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
                try {
                    URL url = new URL("http://checkip.amazonaws.com");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    ipaddr = reader.readLine();
                    Main.getInstance().getLogger().info(ipaddr);
                } catch (Exception e) {
                    Main.getInstance().getLogger().info("Failed to get your public ip. Returned error is: " + e.getMessage());
                }
                    ipaddr.replace(" ", "");
                    ipaddr.replace("[STDOUT]", "");
                FileManager.setStringInConfig("WebServer.domain", ipaddr);
            }
            File cfgFile = new File(Main.instance.getDataFolder(), "config.yml");
            config = YamlConfiguration.loadConfiguration(cfgFile);
        }
        if (!logsFolder.exists()) logsFolder.mkdir();
        if(!webFolder.exists()) webFolder.mkdir();
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

    public void removeLogFile() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_ss:mm:HH");
            String newFileName = dateFormat.format(new Date()) + ".log";
            File newFile = new File(Main.instance.getDataFolder(), "logs/" + newFileName);
            logsFile.renameTo(newFile);
    }
}
