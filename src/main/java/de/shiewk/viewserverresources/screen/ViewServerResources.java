package de.shiewk.viewserverresources.screen;

import de.shiewk.viewserverresources.client.ViewServerResourcesClient;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ViewServerResources extends Screen {
    private final Screen parent;
    private static final int buttonWidth = 192;
    private boolean cfgDirty = false;
    public ViewServerResources(Screen parent) {
        super(Text.translatable("gui.viewserverresources.config"));
        this.parent = parent;
    }

    @Override
    public void close() {
        if (cfgDirty){
            ViewServerResourcesClient.saveConfig();
        }
        assert client != null;
        client.setScreen(parent);
    }

    @Override
    protected void init() {
        {
            final TextWidget tw = new TextWidget(Text.translatable("viewserverresources.settings"), textRenderer);
            tw.setPosition(width / 2 - tw.getWidth() / 2, 10);
            addDrawableChild(tw);
        }
        {
            final GridWidget gw = new GridWidget();
            gw.getMainPositioner().margin(4, 4, 4, 0);
            final GridWidget.Adder adder = gw.createAdder(2);
            adder.add(createToggleableLargeButton(
                    ViewServerResourcesClient.isBroadcastDownloads(),
                    bl -> Text.translatable("viewserverresources.settings.broadcast", Text.translatable(bl ? "gui.yes" : "gui.no")).withColor(bl ? new Color(100, 255, 100).getRGB() : new Color(255, 100, 100).getRGB()),
                    bl -> {
                        ViewServerResourcesClient.setBroadcastDownloads(bl);
                        cfgDirty = true;
                    }
            ), 2);
            adder.add(createButton(Text.translatable("viewserverresources.settings.whitelistedURLs"), btn -> {
                btn.active = false;
                assert client != null;
                cfgDirty = true;
                client.setScreen(new ManageListScreen<>(Text.translatable("viewserverresources.settings.whitelistedURLs"), this, ViewServerResourcesClient.getWhitelistedURLs()));
            }));
            adder.add(createButton(Text.translatable("viewserverresources.settings.whitelistedHosts"), btn -> {
                btn.active = false;
                assert client != null;
                cfgDirty = true;
                client.setScreen(new ManageListScreen<>(Text.translatable("viewserverresources.settings.whitelistedHosts"), this, ViewServerResourcesClient.getWhitelistedHosts()));
            }));
            gw.refreshPositions();
            SimplePositioningWidget.setPos(gw, 0, 0, this.width, this.height, 0.5F, 0.5f);
            gw.forEachChild(this::addDrawableChild);
        }
    }

    private ButtonWidget createButton(Text m, ButtonWidget.PressAction action){
        return new ButtonWidget.Builder(m, action).width(buttonWidth).build();
    }

    private ButtonWidget createToggleableLargeButton(boolean state, Boolean2ObjectFunction<Text> function, BooleanConsumer onToggle){
        AtomicBoolean bl = new AtomicBoolean(state);
        return new ButtonWidget.Builder(function.get(state), btn -> {
            bl.set(!bl.get());
            onToggle.accept(bl.get());
            btn.setMessage(function.apply(bl.get()));
        }).width(buttonWidth * 2 + 8).build();
    }
}
