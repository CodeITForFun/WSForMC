package tk.fungy.wsformc;

import fi.iki.elonen.NanoHTTPD;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static tk.fungy.wsformc.FileManager.logsFolder;

public class WebServer extends NanoHTTPD {
    static Integer port = Integer.valueOf(new FileManager().getStringFromConfig("WebServer.port"));
    static boolean secureb = new FileManager().getBooleanFromConfig("WebServer.ssl");
    static String domain = new FileManager().getStringFromConfig("WebServer.domain");
    public WebServer(int port) {
        super(port);
    }
    public WebServer(String hostname, int port) {
        super(hostname, port);
    }
    public static boolean running = Boolean.parseBoolean(new FileManager().getStringFromConfig("WebServer.isRunning"));
    private File logFile;
    public WebServer() {
        super(port);
        logFile = new File(Main.instance.getDataFolder() + "/logs/access.log");
    }
    //TODO:
    //TODO:   FIX TOGGLE V CONFIGU A CHATE ATD..
    //TODO:

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
        } else if (uri.endsWith(".xml")) {
            return "application/xml";
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
            try {
                super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                FileManager.setStringInConfig("WebServer.isRunning", String.valueOf(true));
                if (secureb) {
                    Main.getInstance().getLogger().warning("Running! https://" + domain + ":" + port + "/");
                } else {
                    Main.getInstance().getLogger().warning("Running! http://" + domain + ":" + port + "/");
                }
            } catch (IOException e) {
                Main.getInstance().getLogger().warning("Couldn't start server: " + e.getMessage());
                FileManager.setStringInConfig("WebServer.isRunning", String.valueOf(false));
            }
    }
    public void stop() {
        super.stop();
        FileManager.setStringInConfig("WebServer.isRunning", String.valueOf(false));
        if (!(super.isAlive())) {
            Main.getInstance().getLogger().warning("Webserver has been Stopped!");
        } else {
            Main.getInstance().getLogger().warning("Webserver has not been Stopped! Are you started the WebServer?");
        }
    }
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri().toLowerCase();
        String hostHeader = session.getHeaders().get("host");
        if (hostHeader == null || !hostHeader.contains(domain + ":" + port)) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "DNS_PROBE_POSSIBLE");
        }


        if (uri.endsWith("/")) {
            uri = "/index.html";
        }

        Method method = session.getMethod();
        String mimeType = getMimeType(uri);
        File file = new File(Main.instance.getDataFolder() + "/web/" + uri);
        try {
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
            if (Method.GET.equals(method) && "/".equals(uri)) file = new File(Main.instance.getDataFolder() + "/web/" + "index.html".toLowerCase());
            return newChunkedResponse(Response.Status.OK, mimeType,
                    new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain",
                    "404 File Not Found - " + uri);
        }

    }
}
