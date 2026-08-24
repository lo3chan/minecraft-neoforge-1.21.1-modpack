package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.ChatType.Bound;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ChatMessageReceivedCallback {
   EventInvoker<ChatMessageReceivedCallback> EVENT = EventInvoker.lookup(ChatMessageReceivedCallback.class);

   EventResult onChatMessageReceived(MutableValue<Component> var1, @Nullable Bound var2, @Nullable PlayerChatMessage var3, boolean var4);
}
