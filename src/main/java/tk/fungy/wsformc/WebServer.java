package tk.fungy.wsformc;

import fi.iki.elonen.NanoHTTPD;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class WebServer extends NanoHTTPD {
    private static Integer port = Integer.valueOf(new FileManager().getStringFromConfig("WebServer.port"));
    private static boolean secureb = new FileManager().getBooleanFromConfig("WebServer.ssl");
    private static String domain = new FileManager().getStringFromConfig("WebServer.domain");
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
        logFile = new File(Main.instance.getDataFolder() + "/logs/latest.log");
    }
    //TODO:
    //TODO:   FIX TOGGLE V CONFIGU A CHATE ATD..
    //TODO:
    public void start() {
        running = !running;
        if (running) {
            try {
                start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                FileManager.setStringInConfig("WebServer.isRunning", String.valueOf(true));
                if (secureb) {
                    Main.getInstance().getLogger().warning("Running! https://" + domain + ":" + port + "/");
                } else {
                    Main.getInstance().getLogger().warning("Running! http://" + domain + ":" + port + "/");
                }
            } catch (IOException e) {
                Main.getInstance().getLogger().warning("Couldn't start server: " + e.getMessage());
                FileManager.setStringInConfig("WebServer.isRunning", String.valueOf(false));
                running = false;
            }
        }
    }
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Method method = session.getMethod();

        File file = new File(Main.instance.getDataFolder() + "/web/" + uri);
        try {
            try (FileWriter writer = new FileWriter(logFile, true)) {
                Map<String, String> headers = session.getHeaders();
                String referer = headers.get("referer");
                String agent = headers.get("user-agent");
                String ip = headers.get("remote-addr");
                String timeStamp = new SimpleDateFormat("dd-MM-yyyy ss:mm:HH").format(new Date());
                writer.append(timeStamp + " " + session.getMethod() + " " + session.getUri() + " " + ip + " " + agent + " " + referer + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Method.GET.equals(method) && "/".equals(uri)) file = new File(Main.instance.getDataFolder() + "/web/" + "index.html");
            return newChunkedResponse(Response.Status.OK, "text/html",
                    new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain",
                    "404 File Not Found - " + uri);
        }

    }
}
