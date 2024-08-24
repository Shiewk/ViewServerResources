package de.shiewk.resourcepackprivacy.event;

import de.shiewk.resourcepackprivacy.ResourcePackPrivacy;
import de.shiewk.resourcepackprivacy.mixin.AccessorConfirmServerResourcePackScreen;
import de.shiewk.resourcepackprivacy.mixin.AccessorConfirmServerResourcePackScreenPack;
import de.shiewk.resourcepackprivacy.mixin.MixinClientCommonNetworkHandler;
import de.shiewk.resourcepackprivacy.screen.ViewResourceURLsScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScreenListener implements ScreenEvents.AfterInit {

    private static final int buttonWidth = 150;

    public record PackInfo(UUID id, URL url, String hash){}

    @Override
    public void afterInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        if (screen instanceof ConfirmScreen && screen.getClass().getEnclosingClass() == ClientCommonNetworkHandler.class){
            GridWidget gw = new GridWidget();
            gw.getMainPositioner().margin(4, 4, 4, 0);
            final GridWidget.Adder adder = gw.createAdder(2);

            final List<ClickableWidget> buttons = Screens.getButtons(screen);

            final List<PackInfo> infos = getPackInfos((AccessorConfirmServerResourcePackScreen) screen);

            while (!buttons.isEmpty()){
                adder.add(buttons.removeFirst());
            }

            adder.add(createButton(Text.translatable(infos.size() == 1 ? "gui.resourcepackprivacy.viewURL" : "gui.resourcepackprivacy.viewURLs"), btn -> viewURLs(client, screen, infos)));
            adder.add(createButton(Text.translatable(infos.size() == 1 ? "gui.resourcepackprivacy.alwaysURL" : "gui.resourcepackprivacy.alwaysURLs"), btn -> btn.active = false));
            adder.add(createLargeButton(Text.translatable("gui.resourcepackprivacy.alwaysHost", Text.literal(infos.getFirst().url().getHost()).withColor(Color.GREEN.getRGB())), btn -> btn.active = false), 2);

            gw.refreshPositions();
            SimplePositioningWidget.setPos(gw, 0, 0, scaledWidth, scaledHeight, 0.5F, 0.875F);
            gw.forEachChild(buttons::add);
        }
    }

    private void viewURLs(MinecraftClient client, Screen screen, List<PackInfo> infos) {
        client.setScreen(new ViewResourceURLsScreen(screen, infos));
    }

    private static @NotNull List<PackInfo> getPackInfos(AccessorConfirmServerResourcePackScreen screen) {
        final List<PackInfo> infos = new ArrayList<>();
        final List<?> packs = screen.getPacks();
        for (Object packObj : packs) {
            AccessorConfirmServerResourcePackScreenPack pack = (AccessorConfirmServerResourcePackScreenPack) packObj;
            infos.add(new PackInfo(pack.getId(), pack.getURL(), pack.getHash()));
        }
        return infos;
    }

    private ButtonWidget createButton(Text m, ButtonWidget.PressAction action){
        return ButtonWidget.builder(m, action).width(buttonWidth).build();
    }

    private ButtonWidget createLargeButton(Text m, ButtonWidget.PressAction action){
        return ButtonWidget.builder(m, action).width(buttonWidth*2+8).build();
    }
}
