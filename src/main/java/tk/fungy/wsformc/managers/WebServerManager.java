package tk.fungy.wsformc.managers;

import fi.iki.elonen.NanoHTTPD;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import tk.fungy.wsformc.Main;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tk.fungy.wsformc.managers.FileManager.logsFolder;

public class WebServerManager extends NanoHTTPD {
    //static Integer port = Integer.valueOf(new FileManager().getStringFromConfig("WebServerManager.port"));
    //static String domain = new FileManager().getStringFromConfig("WebServerManager.domain");
    public WebServerManager(int port) {
        super(port);
    }
    public WebServerManager(String hostname, int port) {
        super(hostname, port);
    }
    public static boolean running = new FileManager().getBooleanFromConfig("WebServer.isRunning");
    private File logFile;
    public WebServerManager() {
        super(Integer.valueOf(new FileManager().getStringFromConfig("WebServer.port")));
        logFile = new File(Main.getInstance().getDataFolder() + "/logs/access.log");
    }

    private String getMimeType(String uri) {
        if (uri.endsWith(".css")) {
            return "text/css";
        } else if (uri.endsWith(".scss")) {
            return "text/x-scss";
        } else if (uri.endsWith(".js")) {
            return "application/javascript";
        } else if (uri.endsWith(".txt")) {
            return "text/plain";
        } else if (uri.endsWith(".html") || uri.endsWith(".htm")) {
            return "text/html";
        } else if (uri.endsWith(".jpeg") || uri.endsWith(".jpg")) {
            return "image/jpeg";
        } else if (uri.endsWith(".png")) {
            return "image/png";
        } else if (uri.endsWith(".gif")) {
            return "image/gif";
        } else if (uri.endsWith(".mp4")) {
            return "video/mp4";
        } else if (uri.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (uri.endsWith(".pdf")) {
            return "application/pdf";
        } else if (uri.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (uri.endsWith(".xml")) {
            return "image/svg+xml";
        } else if (uri.endsWith(".json")) {
            return "application/json";
        } else if (uri.endsWith(".ico")) {
            return "image/vnd.microsoft.icon";
        } else if (uri.endsWith(".ttf")) {
            return "font/ttf";
        } else if (uri.endsWith(".weba")) {
            return "audio/webm";
        } else if (uri.endsWith(".webm")) {
            return "video/webm";
        } else if (uri.endsWith(".webp")) {
            return "image/webp";
        } else if (uri.endsWith(".xhtml")) {
            return "application/xhtml+xml";
        } else if (uri.endsWith(".aac")) {
            return "audio/aac";
        } else if (uri.endsWith(".jsonld")) {
            return "application/ld+json";
        } else if (uri.endsWith(".css.map")) {
            return "application/json";
        } else if (uri.endsWith(".js.map")) {
            return "application/json";
        } else {
            return "application/octet-stream";
        }
    }


    public void start() {
        int maxThreads = new FileManager().getIntegerFromConfig("WebServer.threads"); // maximum number of threads
        if (maxThreads == 0) {
            try {
                super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            } catch (IOException e) {
                Main.getInstance().getLogger().warning("Couldn't start server: " + e.getMessage());
                FileManager.setBooleanInConfig("WebServer.isRunning", false);
            }
        } else {
            ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);
            executorService.submit(() -> {
                try {
                    super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                } catch (IOException e) {
                    Main.getInstance().getLogger().warning("Couldn't start server: " + e.getMessage());
                    FileManager.setBooleanInConfig("WebServer.isRunning", false);
                }
            });
        }
        FileManager.setBooleanInConfig("WebServer.isRunning", true);
        if (new FileManager().getBooleanFromConfig("WebServerManager.ssl")) {
            Main.getInstance().getLogger().warning("Running! https://" + new FileManager().getStringFromConfig("WebServer.domain") + ":" + Integer.valueOf(new FileManager().getStringFromConfig("WebServer.port")) + "/");
        } else {
            Main.getInstance().getLogger().warning("Running! http://" + new FileManager().getStringFromConfig("WebServer.domain") + ":" + Integer.valueOf(new FileManager().getStringFromConfig("WebServer.port")) + "/");
        }
        Main.tc.reset();
        Main.tc.start();
    }
    public void stop() {
        super.stop();
        Main.tc.stop();
        FileManager.setBooleanInConfig("WebServer.isRunning", false);
        if (!(super.isAlive())) {
            Main.getInstance().getLogger().warning("Webserver has been Stopped!");
        } else {
            Main.getInstance().getLogger().warning("Webserver has not been Stopped! Are you started the WebServerManager?");
        }
    }
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri().toLowerCase();
        String hostHeader = session.getHeaders().get("host");
        if (hostHeader == null || !hostHeader.contains(new FileManager().getStringFromConfig("WebServer.domain"))) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Permanent Redirect to " + new FileManager().getStringFromConfig("WebServerManager.domain") + ":" + Integer.valueOf(new FileManager().getStringFromConfig("WebServerManager.port")));
        }

        if (uri.endsWith("/")) { uri = "/index.html"; }

        Method method = session.getMethod();
        String mimeType = getMimeType(uri);
        File file = new File(Main.getInstance().getDataFolder() + "/web/" + uri);
        try {
            if (new FileManager().getBooleanFromConfig("WebServer.accessLog")) {
                try (FileWriter writer = new FileWriter(logFile, true)) {
                    Map<String, String> headers = session.getHeaders();
                    String referer = headers.get("referer");
                    String agent = headers.get("user-agent");
                    String ip = headers.get("remote-addr");
                    String timeStamp = new SimpleDateFormat("dd-MM-yyyy ss:mm:HH").format(new Date());
                    writer.append(timeStamp + " " + session.getMethod() + " " + session.getUri() + " " + ip + " " + agent + " " + referer + "\n");
                } catch (NoSuchFileException e) {
                    if (!logsFolder.exists()) logsFolder.mkdir();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Method.GET.equals(method) && "/".equals(uri)) file = new File(Main.getInstance().getDataFolder() + "/web/" + "index.html".toLowerCase());

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {

                StringBuilder fileContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(Main.getInstance().getDataFolder() + "/web/" + uri.toLowerCase()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }
                } catch (IOException e) {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 File Not Found - " + uri);
                }

                String placeholderRegex = "%(.*?)%";

                Pattern pattern = Pattern.compile(placeholderRegex);
                Matcher matcher = pattern.matcher(fileContent);
                StringBuffer modifiedContentBuffer = new StringBuffer();

                while (matcher.find()) {
                    String placeholder = matcher.group(1);
                    String resolvedValue = PlaceholderAPI.setPlaceholders(null, "%" + placeholder + "%");
                    matcher.appendReplacement(modifiedContentBuffer, Matcher.quoteReplacement(resolvedValue));
                }

                matcher.appendTail(modifiedContentBuffer);

                String modifiedContent = modifiedContentBuffer.toString();
                Response response = newFixedLengthResponse(Response.Status.OK, mimeType, modifiedContent);
                return response;
            } else {
                return newChunkedResponse(Response.Status.OK, mimeType, new FileInputStream(file));
            }

        } catch (FileNotFoundException e) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain",
                    "404 File Not Found - " + uri);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    private String getQueryValue(String query, String paramName) {
        String[] params = query.split("&");
        for (String param : params) {
            String[] parts = param.split("=");
            if (parts.length == 2 && parts[0].equals(paramName)) {
                return parts[1];
            }
        }
        return null;
    }
}
