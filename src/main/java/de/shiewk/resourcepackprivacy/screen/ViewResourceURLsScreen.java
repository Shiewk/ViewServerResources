package de.shiewk.resourcepackprivacy.screen;

import de.shiewk.resourcepackprivacy.event.ScreenListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;

public class ViewResourceURLsScreen extends Screen {

    private final Screen parent;
    private final List<ScreenListener.PackInfo> infos;
    public ViewResourceURLsScreen(Screen parent, List<ScreenListener.PackInfo> infos) {
        super(Text.translatable("gui.resourcepackprivacy.viewURL"));
        this.parent = parent;
        this.infos = infos;
    }

    @Override
    public void close() {
        assert client != null;
        client.setScreen(parent);
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), btn -> this.close())
                .dimensions(width / 2 - 150, height - 30, 300, 20)
                .build());

        final MultilineTextWidget text = new MultilineTextWidget(getMessage(), textRenderer);
        text.setCentered(true);
        text.setPosition(width / 2 - (text.getWidth() / 2), height / 2 - (text.getHeight() / 2));
        addDrawableChild(text);
    }

    private Text getMessage(){
        MutableText msg = Text.empty();
        for (ScreenListener.PackInfo info : infos) {
            msg = msg.append(Text.literal("\n"+info.url()));
        }
        return Text.translatable(infos.size() == 1 ? "gui.resourcepackprivacy.url" : "gui.resourcepackprivacy.urls", msg).withColor(Color.GREEN.getRGB());
    }
}
