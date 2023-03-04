package tk.fungy.wsformc.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import tk.fungy.wsformc.Main;

import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {

    public static File configFile = new File(Main.getInstance().getDataFolder(), "config.yml");
    public static File webFolder = new File(Main.getInstance().getDataFolder(), "/web");
    public static File logsFolder = new File(Main.getInstance().getDataFolder(), "/logs");
    public static File logsFile = new File(Main.getInstance().getDataFolder(), "/logs/access.log");
    public static YamlConfiguration config;
    public static String ipaddr;
    public static void setStringInConfig(String key, String value) {
        if (config == null) { config = new YamlConfiguration().loadConfiguration(configFile); }
        config.set(key, value);
        try {
            config.save(configFile);
        } catch (IOException e) {
            Main.getInstance().getLogger().warning("Failed to set string. Executed with this error: \n" + e);
        }
    }
    public static void setIntegerInConfig(String key, Integer value) {
        if (config == null) { config = new YamlConfiguration().loadConfiguration(configFile); }
        config.set(key, value);
        try {
            config.save(configFile);
        } catch (IOException e) {
            Main.getInstance().getLogger().warning("Failed to set string. Executed with this error: \n" + e);
        }
    }
    public boolean containsDigits(String StringInput) {
        if (config == null) { config = new YamlConfiguration().loadConfiguration(configFile); }
        boolean containsDigits = StringInput.matches("[0-9]+");

        if(containsDigits) {
            return true;
        } else {
            return false;
        }
    }
    public static void setBooleanInConfig(String key, Boolean value) {
        if (config == null) { config = new YamlConfiguration().loadConfiguration(configFile); }
        config.set(key, value);
        try {
            config.save(configFile);
        } catch (IOException e) {
            Main.getInstance().getLogger().warning("Failed to set boolean. Executed with this error: \n" + e);
        }
    }


    public void startup() throws UnknownHostException {
        if(!configFile.exists()) {
            Main.getInstance().saveResource("config.yml", true);
            config = new YamlConfiguration().loadConfiguration(configFile);
            //new ConfigFixManager().autofixConfig();
            String domain = getStringFromConfig("WebServerManager.domain");
            if (domain.equalsIgnoreCase("CHANGE_ME")) {
                try {
                    URL url = new URL("http://checkip.amazonaws.com");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    ipaddr = reader.readLine();
                    Main.getInstance().getLogger().info("IP of device: " + ipaddr);
                } catch (Exception e) {
                    Main.getInstance().getLogger().info("Failed to get your public ip. Returned error is: " + e.getMessage());
                }
                    ipaddr.replace(" ", "");
                    ipaddr.replace("[STDOUT]", "");
                FileManager.setStringInConfig("WebServerManager.domain", ipaddr);
            }
            File cfgFile = new File(Main.getInstance().getDataFolder(), "config.yml");
            config = YamlConfiguration.loadConfiguration(cfgFile);
        }
        if (!logsFolder.exists()) logsFolder.mkdir();
        if(!webFolder.exists()) webFolder.mkdir();
        saveMyResource("index.html", "/web/", false);
    }

    public void saveMyResource(String resourceName, String destination, boolean replace) {
        // Get the plugin's data folder
        File dataFolder = Main.getInstance().getDataFolder();

        // Create the destination file
        File destFile = new File(dataFolder + destination, resourceName);

        // If the file already exists and 'replace' is false, return early
        if (destFile.exists() && !replace) {
            return;
        }

        // Open the resource file
        InputStream is = Main.getInstance().getResource(resourceName);

        // If the resource file is not found, throw an exception
        if (is == null) {
            throw new IllegalArgumentException("Resource not found: " + resourceName);
        }

        try {
            // Create the parent directories if they don't exist
            destFile.getParentFile().mkdirs();

            // Open the destination file for writing
            OutputStream os = new FileOutputStream(destFile);

            // Copy the contents of the resource to the destination file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            // Close the input and output streams
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getBooleanFromConfig(String b) {
        if (config == null) {
            config = new YamlConfiguration().loadConfiguration(configFile);
        } return config.getBoolean(b);
    }

    public String getStringFromConfig(String string) {
        if (config == null) {
            config = new YamlConfiguration().loadConfiguration(configFile);
        } return config.getString(string);
    }
    public Integer getIntegerFromConfig(String integer) {
        if (config == null) {
            config = new YamlConfiguration().loadConfiguration(configFile);
        } return config.getInt(integer);
    }
    public boolean checkBooleanFromConfig(String key, Boolean string) {
        if (config == null) {
            config = new YamlConfiguration().loadConfiguration(configFile);
        } if (config.get(key) != null) {
            return true;
        } else {
            config.set(key, string);
            return false;
        }
    }
    public boolean checkStringFromConfig(String key, String string) {
        if (config == null) {
            config = new YamlConfiguration().loadConfiguration(configFile);
        } if (config.get(key) != null) {
            return true;
        } else {
            config.set(key, string);
            return false;
        }
    }
    public boolean checkIntFromConfig(String key, Integer string) {
        if (config == null) {
            config = new YamlConfiguration().loadConfiguration(configFile);
        } if (config.get(key) != null) {
            return true;
        } else {
            config.set(key, string);
            return false;
        }
    }
    public void reloadConfig() {
        File cfgFile = new File(Main.getInstance().getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(cfgFile);
    }

    public void removeLogFile() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_ss:mm:HH");
            String newFileName = dateFormat.format(new Date()) + ".log";
            File newFile = new File(Main.getInstance().getDataFolder(), "logs/" + newFileName);
            logsFile.renameTo(newFile);
    }
}
