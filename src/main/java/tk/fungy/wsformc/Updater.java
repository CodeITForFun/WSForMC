package tk.fungy.wsformc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;

import java.net.URL;

public class Updater implements Listener {
    private static final String currentVersion = new FileManager().getStringFromConfig("Version").toString();
    private static String latestVersion;
    public static void startUpdater() {
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
                Bukkit.getLogger().warning(Colors.translate("[WebServer] A new update is available: " + latestVersion + " Your version is: " + currentVersion));
            } else {
                Bukkit.getLogger().warning(Colors.translate("[WebServer] You are using latest version"));
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning(Colors.translate("[WebServer] Failed to check for updates: " + e.getMessage()));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (currentVersion == null) {
            Bukkit.getLogger().warning(Colors.translate("[WebServer] An error occured! Please report console error to our discord! https://codeitfor.fun/discord"));
            return;
        }

        if (player.hasPermission("ws.update") || player.hasPermission("ws.*"))
            if (!(currentVersion.equals(latestVersion.toString()))) player.sendMessage(Colors.translate("&8[&cWebServer&8] &bA new update of Web Server for minecraft is available: &c" + latestVersion + "&b. Your version is: &c" + currentVersion + "&b.\n&bDownload it here: &7https://github.com/CodeITForFun/WSForMC/releases"));
    }
}
