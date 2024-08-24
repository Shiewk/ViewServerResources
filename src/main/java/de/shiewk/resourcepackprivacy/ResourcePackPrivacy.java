package de.shiewk.resourcepackprivacy;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ResourcePackPrivacy implements ModInitializer {

    public static final String MOD_ID = "resourcepackprivacy";
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourcePackPrivacy.class);

    public static void logThrowable(IOException e) {
        LOGGER.error(e.toString());
        for (StackTraceElement element : e.getStackTrace()) {
            LOGGER.error(element.toString());
        }
    }

    @Override
    public void onInitialize() {
    }
}
