package de.shiewk.resourcepackprivacy.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.shiewk.resourcepackprivacy.screen.ResourcePackPrivacyConfigScreen;

public class ModMenuConfig implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ResourcePackPrivacyConfigScreen::new;
    }
}
