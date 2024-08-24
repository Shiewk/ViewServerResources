package de.shiewk.viewserverresources.mixin;

import de.shiewk.viewserverresources.client.ViewServerResourcesClient;
import de.shiewk.viewserverresources.event.ChatAnnouncer;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.net.URL;
import java.util.UUID;

@Mixin(ServerResourcePackLoader.class)
public class MixinServerResourcePackLoader {

    @Inject(at = @At("HEAD"), method = "addResourcePack(Ljava/util/UUID;Ljava/net/URL;Ljava/lang/String;)V")
    public void onResourcePackAdd(UUID id, URL url, String hash, CallbackInfo ci){
        if (ViewServerResourcesClient.isBroadcastDownloads()){
            ChatAnnouncer.announce(Text.translatable("gui.resourcepackprivacy.downloading",
                            Text.literal(url.toString()))
                    .withColor(Color.ORANGE.getRGB())
            );
        }
    }
}
