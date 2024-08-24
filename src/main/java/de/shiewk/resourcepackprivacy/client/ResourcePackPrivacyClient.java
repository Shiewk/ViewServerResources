package de.shiewk.resourcepackprivacy.client;

import de.shiewk.resourcepackprivacy.event.ChatAnnouncer;
import de.shiewk.resourcepackprivacy.event.ScreenListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class ResourcePackPrivacyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register(new ScreenListener());
        ClientTickEvents.END_CLIENT_TICK.register(new ChatAnnouncer());
    }
}
