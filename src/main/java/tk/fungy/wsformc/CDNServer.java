package tk.fungy.wsformc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;

import static tk.fungy.wsformc.WebServer.*;

public class CDNServer extends NanoHTTPD {

    public CDNServer() throws IOException {
        super(port);
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

    public static void main(String[] args) {
        try {
            new CDNServer();
        } catch (IOException e) {
            System.err.println("Couldn't start CDN server:\n" + e);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        Map<String, String> headers = session.getHeaders();
        Map<String, String> parms = session.getParms();

        // Check if the request is for a file
        if (uri.startsWith("/")) {
            // Serve the requested file
            File file = new File(Main.instance.getDataFolder() + "/web/" + uri);
            if (file.exists() && !file.isDirectory()) {
                return serveFile(file, headers);
            } else {
                return newFixedLengthResponse(Status.NOT_FOUND, "text/plain", "File not found");
            }
        } else {
            // Return a "Bad Request" response for anything else
            return newFixedLengthResponse(Status.BAD_REQUEST, "text/plain", "Bad Request");
        }
    }

    private Response serveFile(File file, Map<String, String> headers) {
        String mimeType = getMimeType(file.getName());
        Response response = null;
        try {
            response = newFixedLengthResponse(Status.OK, mimeType, new FileInputStream(file), file.length());
            response.addHeader("Accept-Ranges", "bytes");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            response = newFixedLengthResponse(Status.INTERNAL_ERROR, "text/plain", "INTERNAL ERROR: Serving file failed.");
        }
        return response;
    }

    private String getMimeType(String filename) {
        // Placeholder implementation, replace with actual MIME type detection logic
        return "application/octet-stream";
    }
}
