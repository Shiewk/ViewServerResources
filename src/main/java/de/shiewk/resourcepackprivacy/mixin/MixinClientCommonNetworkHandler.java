package de.shiewk.resourcepackprivacy.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.shiewk.resourcepackprivacy.ResourcePackPrivacy;
import de.shiewk.resourcepackprivacy.client.ResourcePackPrivacyClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URL;
import java.util.UUID;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class MixinClientCommonNetworkHandler {

    @Shadow protected abstract Screen createConfirmServerResourcePackScreen(UUID id, URL url, String hash, boolean required, @Nullable Text prompt);

    @Shadow @Final protected MinecraftClient client;


    @Shadow @Final protected ClientConnection connection;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/common/ResourcePackSendS2CPacket;hash()Ljava/lang/String;"), method = "onResourcePackSend", cancellable = true)
    public void onResourcePackSend(ResourcePackSendS2CPacket packet, CallbackInfo ci, @Local UUID uUID, @Local URL uRL){
        ResourcePackPrivacy.LOGGER.info(packet.url());
        String hash = packet.hash();
        if (ResourcePackPrivacyClient.allowedURL(uRL)){
            this.client.getServerResourcePackProvider().addResourcePack(uUID, uRL, hash);
        } else {
            boolean required = packet.required();
            this.client.setScreen(this.createConfirmServerResourcePackScreen(uUID, uRL, hash, required, packet.prompt().orElse(null)));
            ci.cancel();
        }
    }
}
