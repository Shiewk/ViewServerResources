package de.shiewk.viewserverresources.screen.elements;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ManageListWidget<T> extends ScrollableWidget {
    private final List<T> list;
    private final List<ClickableWidget> elements = new ArrayList<>();
    public ManageListWidget(TextRenderer renderer, int i, int j, int k, int l, Text text, List<T> list) {
        super(i, j, k, l, text);
        this.list = list;
        refreshElements(renderer);
    }

    private void refreshElements(TextRenderer renderer){
        elements.clear();
        int yp = 0;
        for (T t : new ObjectArrayList<>(list)) {
            yp += 4;
            final TextWidget tw = new TextWidget(Text.literal(t.toString()), renderer);
            tw.setHeight(20);
            tw.setPosition((width - 28) / 2 - (tw.getWidth() / 2), yp);
            elements.add(tw);
            elements.add(new ButtonWidget.Builder(Text.literal("x"), btn -> {
                btn.active = false;
                list.remove(t);
                refreshElements(renderer);
            }).width(20).position(width - 24, yp).build());
            yp += 20;
        }
    }

    @Override
    protected int getContentsHeight() {
        return list.size() * 24;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 10;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        mouseY += (int) getScrollY();
        for (ClickableWidget element : elements) {
            element.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseY += getScrollY();
        for (ClickableWidget element : elements) {
            if (element.isMouseOver(mouseX, mouseY)){
                return element.mouseClicked(mouseX, mouseY, button);
            }
        }
        return false;
    }

    @Override
    protected void drawBox(DrawContext context, int x, int y, int width, int height) {

    }
}
