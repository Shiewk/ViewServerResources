package de.shiewk.viewserverresources.screen;

import de.shiewk.viewserverresources.screen.elements.ManageListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class ManageListScreen<T> extends Screen {
    private final Screen parent;
    private final List<T> list;
    public ManageListScreen(Text title, Screen parent, List<T> list) {
        super(title);
        this.parent = parent;
        this.list = list;
    }

    @Override
    public void close() {
        assert client != null;
        client.setScreen(parent);
    }

    @Override
    protected void init() {
        addDrawableChild(new ManageListWidget<>(textRenderer, 0, 0, width, height, title, list));
    }
}
