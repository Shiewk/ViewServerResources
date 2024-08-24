package de.shiewk.resourcepackprivacy.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatAnnouncer implements ClientTickEvents.EndTick {

    private static final ObjectArrayList<Text> queue = new ObjectArrayList<>();
    @Override
    public void onEndTick(MinecraftClient client) {
        if (client.player != null && !queue.isEmpty()){
            client.player.sendMessage(queue.removeFirst());
        }
    }

    public static void announce(Text text){
        queue.add(text);
    }
}
