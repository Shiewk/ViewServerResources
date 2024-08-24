package de.shiewk.viewserverresources.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import de.shiewk.viewserverresources.ViewServerResourcesMod;
import de.shiewk.viewserverresources.event.ChatAnnouncer;
import de.shiewk.viewserverresources.event.ScreenListener;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.util.List;

public class ViewServerResourcesClient implements ClientModInitializer {

    private static final ObjectArrayList<String> whitelistedURLs = new ObjectArrayList<>();
    private static final ObjectArrayList<String> whitelistedHosts = new ObjectArrayList<>();
    private static boolean broadcastDownloads = true;
    private static File whitelistFile;
    private static final Gson gson = new Gson();

    public static boolean allowedURL(URL uRL) {
        if (whitelistedURLs.contains(uRL.toString())){
            ViewServerResourcesMod.LOGGER.info("URL {} is whitelisted", uRL);
            return true;
        } else if (whitelistedHosts.contains(uRL.getHost())){
            ViewServerResourcesMod.LOGGER.info("Host {} is whitelisted", uRL.getHost());
            return true;
        }
        return false;
    }

    public static void addWhitelistURL(URL url){
        final String urls = url.toString();
        ViewServerResourcesMod.LOGGER.info("Whitelist url {}", urls);
        if (!whitelistedURLs.contains(urls)){
            whitelistedURLs.add(urls);
        }
    }

    public static void addWhitelistHost(URL url){
        final String h = url.getHost();
        ViewServerResourcesMod.LOGGER.info("Whitelist host {}", h);
        if (!whitelistedHosts.contains(h)){
            whitelistedHosts.add(h);
        }
    }

    public static void loadConfig(){
        ViewServerResourcesMod.LOGGER.info("Loading config");
        try (FileReader fr = new FileReader(whitelistFile)){
            final JsonObject cfg = gson.fromJson(fr, JsonObject.class);
            final JsonObject whitelist = cfg.get("whitelist").getAsJsonObject();
            final JsonArray whitelistHosts = whitelist.getAsJsonArray("hosts");
            whitelistedHosts.clear();
            for (JsonElement whitelistHost : whitelistHosts) {
                whitelistedHosts.add(whitelistHost.getAsString());
            }
            final JsonArray whitelistURLs = whitelist.getAsJsonArray("urls");
            whitelistedURLs.clear();
            for (JsonElement whitelistURL : whitelistURLs) {
                whitelistedURLs.add(whitelistURL.getAsString());
            }
            final JsonElement bdl = cfg.get("broadcastDownloads");
            broadcastDownloads = bdl == null || !bdl.isJsonPrimitive() || bdl.getAsBoolean();
        } catch (FileNotFoundException e) {
            ViewServerResourcesMod.LOGGER.warn("Config file not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveConfig() {
        ViewServerResourcesMod.LOGGER.info("Saving config");
        try (FileWriter fw = new FileWriter(whitelistFile)) {
            final JsonObject cfg = getConfigObject();

            try (JsonWriter jsonWriter = new JsonWriter(fw)) {
                Streams.write(cfg, jsonWriter);
            }

        } catch (IOException e) {
            ViewServerResourcesMod.logThrowable(e);
        }
    }

    private static @NotNull JsonObject getConfigObject() {
        JsonObject cfg = new JsonObject();
        JsonObject whitelist = new JsonObject();

        JsonArray hosts = new JsonArray();
        for (String whitelistedHost : whitelistedHosts) {
            hosts.add(whitelistedHost);
        }
        whitelist.add("hosts", hosts);

        JsonArray urls = new JsonArray();
        for (String whitelistedURL : whitelistedURLs) {
            urls.add(whitelistedURL);
        }
        whitelist.add("urls", urls);

        cfg.add("whitelist", whitelist);
        cfg.addProperty("broadcastDownloads", broadcastDownloads);
        return cfg;
    }

    public static List<String> getWhitelistedURLs() {
        return whitelistedURLs;
    }

    public static List<String> getWhitelistedHosts() {
        return whitelistedHosts;
    }

    public static boolean isBroadcastDownloads() {
        return broadcastDownloads;
    }

    public static void setBroadcastDownloads(boolean broadcastDownloads) {
        ViewServerResourcesClient.broadcastDownloads = broadcastDownloads;
    }

    @Override
    public void onInitializeClient() {
        whitelistFile = new File(MinecraftClient.getInstance().runDirectory.getPath() + "/viewserverresources.json");
        ScreenEvents.AFTER_INIT.register(new ScreenListener());
        ClientTickEvents.END_CLIENT_TICK.register(new ChatAnnouncer());
        loadConfig();
    }
}
