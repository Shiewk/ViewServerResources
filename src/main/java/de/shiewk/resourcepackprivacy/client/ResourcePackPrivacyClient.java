package de.shiewk.resourcepackprivacy.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import de.shiewk.resourcepackprivacy.ResourcePackPrivacy;
import de.shiewk.resourcepackprivacy.event.ChatAnnouncer;
import de.shiewk.resourcepackprivacy.event.ScreenListener;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.net.URL;

public class ResourcePackPrivacyClient implements ClientModInitializer {

    private static final ObjectArrayList<String> whitelistedURLs = new ObjectArrayList<>();
    private static final ObjectArrayList<String> whitelistedHosts = new ObjectArrayList<>();
    private static File whitelistFile;
    private static final Gson gson = new Gson();

    public static boolean allowedURL(URL uRL) {
        if (whitelistedURLs.contains(uRL.toString())){
            ResourcePackPrivacy.LOGGER.info("URL {} is whitelisted", uRL);
            return true;
        } else if (whitelistedHosts.contains(uRL.getHost())){
            ResourcePackPrivacy.LOGGER.info("Host {} is whitelisted", uRL.getHost());
            return true;
        }
        return false;
    }

    public static void addWhitelistURL(URL url){
        final String urls = url.toString();
        ResourcePackPrivacy.LOGGER.info("Whitelist url {}", urls);
        if (!whitelistedURLs.contains(urls)){
            whitelistedURLs.add(urls);
        }
    }

    public static void addWhitelistHost(URL url){
        final String h = url.getHost();
        ResourcePackPrivacy.LOGGER.info("Whitelist host {}", h);
        if (!whitelistedHosts.contains(h)){
            whitelistedHosts.add(h);
        }
    }

    public static void loadConfig(){
        ResourcePackPrivacy.LOGGER.info("Loading config");
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
        } catch (FileNotFoundException e) {
            ResourcePackPrivacy.LOGGER.warn("Config file not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveConfig() {
        ResourcePackPrivacy.LOGGER.info("Saving config");
        try (FileWriter fw = new FileWriter(whitelistFile)) {
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

            try (JsonWriter jsonWriter = new JsonWriter(fw)) {
                Streams.write(cfg, jsonWriter);
            }
        } catch (IOException e) {
            ResourcePackPrivacy.logThrowable(e);
        }
    }

    @Override
    public void onInitializeClient() {
        whitelistFile = new File(MinecraftClient.getInstance().runDirectory.getPath() + "/resourcepackprivacy.json");
        ScreenEvents.AFTER_INIT.register(new ScreenListener());
        ClientTickEvents.END_CLIENT_TICK.register(new ChatAnnouncer());
        loadConfig();
    }
}
