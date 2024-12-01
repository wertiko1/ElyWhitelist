package me.wertiko.elyWhitelist;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

public class WhitelistHttpServer {
    private final Whitelist whitelist;
    private final int port;
    private ElyWhitelist plugin;
    private final String validToken;
    private HttpServer server;

    public WhitelistHttpServer(int port, @NotNull Whitelist whitelist, @NotNull ElyWhitelist plugin) {
        this.port = port;
        this.whitelist = whitelist;
        this.plugin = plugin;
        validToken = plugin.config.getString("token", "XXXXX");
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/whitelist", new WhitelistHandler());
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private class WhitelistHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String token = exchange.getRequestHeaders().getFirst("Authorization");

                if (token == null || !token.equals(validToken)) {
                    String response = "Unauthorized: Invalid token";
                    exchange.sendResponseHeaders(401, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }

                StringBuilder body = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        body.append(line);
                    }
                }

                JsonObject json;
                try {
                    json = JsonParser.parseString(body.toString()).getAsJsonObject();
                } catch (Exception e) {
                    String response = "Invalid JSON";
                    exchange.sendResponseHeaders(400, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }

                String playerName = json.get("player").getAsString();
                String response;
                if (whitelist.addPlayer(playerName)) {
                    response = "Player added: " + playerName;
                    exchange.sendResponseHeaders(200, response.length());
                } else {
                    response = "Player already whitelisted: " + playerName;
                    exchange.sendResponseHeaders(409, response.length());
                }

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }
}
