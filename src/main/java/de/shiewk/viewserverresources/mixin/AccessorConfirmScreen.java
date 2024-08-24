package de.shiewk.viewserverresources.mixin;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConfirmScreen.class)
public interface AccessorConfirmScreen {

    @Accessor("callback")
    BooleanConsumer getCallback();

}
