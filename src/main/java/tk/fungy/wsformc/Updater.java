package tk.fungy.wsformc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Updater implements Listener {
    private static String currentVersion;
    private static String latestVersion;
    public static void startUpdater() {
        currentVersion = new FileManager().getStringFromConfig("Version");
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                checkForUpdates();
            }
        }, 0L, 20 * 60 * 60 * 24); // Schedule the task to run every 24 hours
    }
    private static void checkForUpdates() {
        try {
            URL url = new URL("https://api.github.com/repos/CodeITForFun/WSForMC/releases"); // Replace with the URL to your GitHub releases
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JsonArray releases = new JsonParser().parse(result.toString()).getAsJsonArray();
            JsonObject latestRelease = releases.get(0).getAsJsonObject();
            latestVersion = latestRelease.get("tag_name").getAsString().replace("v", "");

            if (!currentVersion.equals(latestVersion)) {
                Bukkit.getLogger().warning("[WebServerForMinecraft] A new update is available: " + latestVersion);
            } else {
                Bukkit.getLogger().warning("[WebServerForMinecraft] You are using latest version");
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[WebServerForMinecraft] Failed to check for updates: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("ws.update") || player.hasPermission("ws.*"))
            if (!(currentVersion.equals(latestVersion)))
                player.sendMessage(Colors.translate("&bA new update of Web Server for minecraft is available: &c" + latestVersion + "\n&bYour version is: &c" + currentVersion + "\n&bDownload it here: &7https://github.com/CodeITForFun/WSForMC/releases"));
    }
}