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
            tw.setPosition((width - 34) / 2 - (tw.getWidth() / 2), yp);
            elements.add(tw);
            elements.add(new ButtonWidget.Builder(Text.literal("x"), btn -> {
                btn.active = false;
                list.remove(t);
                refreshElements(renderer);
            }).width(20).position(width - 30, yp).build());
            yp += 20;
        }
    }

    @Override
    protected int getContentsHeightWithPadding() {
        return list.size() * 24;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 10;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        mouseY += (int) getScrollY();
        context.getMatrices().push();
        context.getMatrices().translate(0, -getScrollY(), 0);
        for (ClickableWidget element : elements) {
            element.render(context, mouseX, mouseY, delta);
        }
        context.getMatrices().pop();
        drawScrollbar(context);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double mouseYScrolled = mouseY + getScrollY();
        for (ClickableWidget element : elements) {
            if (element.isMouseOver(mouseX, mouseYScrolled)){
                return element.mouseClicked(mouseX, mouseYScrolled, button);
            }
        }
        if (super.checkScrollbarDragged(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
