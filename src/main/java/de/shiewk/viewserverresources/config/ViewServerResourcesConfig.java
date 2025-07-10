package de.shiewk.viewserverresources.config;

import java.util.concurrent.CopyOnWriteArrayList;

public class ViewServerResourcesConfig {

    public Whitelists whitelist = new Whitelists();
    public boolean broadcastDownloads = true;

    public static class Whitelists {
        public CopyOnWriteArrayList<String> urls = new CopyOnWriteArrayList<>();
        public CopyOnWriteArrayList<String> hosts = new CopyOnWriteArrayList<>();
    }

}
