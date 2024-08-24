package de.shiewk.viewserverresources.event;

import de.shiewk.viewserverresources.client.ViewServerResourcesClient;
import de.shiewk.viewserverresources.mixin.AccessorConfirmScreen;
import de.shiewk.viewserverresources.mixin.AccessorConfirmServerResourcePackScreen;
import de.shiewk.viewserverresources.mixin.AccessorConfirmServerResourcePackScreenPack;
import de.shiewk.viewserverresources.screen.ViewResourceURLsScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
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

            adder.add(createButton(Text.translatable(infos.size() == 1 ? "gui.viewserverresources.viewURL" : "gui.viewserverresources.viewURLs"), btn -> viewURLs(client, screen, infos)));
            adder.add(createButton(Text.translatable(infos.size() == 1 ? "gui.viewserverresources.alwaysURL" : "gui.viewserverresources.alwaysURLs"), btn -> whitelistURLsAndAccept(btn, screen, infos)));
            adder.add(createLargeButton(Text.translatable("gui.resourcepackprivacy.alwaysHost", Text.literal(infos.getFirst().url().getHost()).withColor(Color.GREEN.getRGB())), btn -> whitelistHostsAndAccept(btn, screen, infos)), 2);

            gw.refreshPositions();
            SimplePositioningWidget.setPos(gw, 0, 0, scaledWidth, scaledHeight, 0.5F, 0.85F);
            gw.forEachChild(buttons::add);
        }
    }

    private void whitelistURLsAndAccept(ButtonWidget btn, Screen screen, List<PackInfo> infos){
        btn.active = false;
        for (PackInfo info : infos) {
            ViewServerResourcesClient.addWhitelistURL(info.url());
        }
        ViewServerResourcesClient.saveConfig();
        accept(screen);
    }

    private void accept(Screen screen){
        ((AccessorConfirmScreen) screen).getCallback().accept(true);
    }

    private void whitelistHostsAndAccept(ButtonWidget btn, Screen screen, List<PackInfo> infos){
        btn.active = false;
        for (PackInfo info : infos) {
            ViewServerResourcesClient.addWhitelistHost(info.url());
        }
        ViewServerResourcesClient.saveConfig();
        accept(screen);
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
