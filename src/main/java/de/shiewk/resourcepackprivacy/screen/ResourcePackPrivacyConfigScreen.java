package de.shiewk.resourcepackprivacy.screen;

import de.shiewk.resourcepackprivacy.client.ResourcePackPrivacyClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

public class ResourcePackPrivacyConfigScreen extends Screen {
    private final Screen parent;
    private static final int buttonWidth = 192;
    public ResourcePackPrivacyConfigScreen(Screen parent) {
        super(Text.translatable("gui.resourcepackprivacy.config"));
        this.parent = parent;
    }

    @Override
    public void close() {
        assert client != null;
        client.setScreen(parent);
    }

    @Override
    protected void init() {
        {
            final TextWidget tw = new TextWidget(Text.translatable("resourcepackprivacy.settings"), textRenderer);
            tw.setPosition(width / 2 - tw.getWidth() / 2, 10);
            addDrawableChild(tw);
        }
        {
            final GridWidget gw = new GridWidget();
            gw.getMainPositioner().margin(4, 4, 4, 0);
            final GridWidget.Adder adder = gw.createAdder(2);
            adder.add(createButton(Text.translatable("resourcepackprivacy.settings.whitelistedURLs"), btn -> {
                btn.active = false;
                assert client != null;
                client.setScreen(new ManageListScreen<>(Text.translatable("resourcepackprivacy.settings.whitelistedURLs"), this, ResourcePackPrivacyClient.getWhitelistedURLs()));
            }));
            adder.add(createButton(Text.translatable("resourcepackprivacy.settings.whitelistedHosts"), btn -> {
                btn.active = false;
                assert client != null;
                client.setScreen(new ManageListScreen<>(Text.translatable("resourcepackprivacy.settings.whitelistedHosts"), this, ResourcePackPrivacyClient.getWhitelistedHosts()));
            }));
            gw.refreshPositions();
            SimplePositioningWidget.setPos(gw, 0, 0, this.width, this.height, 0.5F, 0.5f);
            gw.forEachChild(this::addDrawableChild);
        }
    }

    private ButtonWidget createButton(Text m, ButtonWidget.PressAction action){
        return new ButtonWidget.Builder(m, action).width(buttonWidth).build();
    }
}
