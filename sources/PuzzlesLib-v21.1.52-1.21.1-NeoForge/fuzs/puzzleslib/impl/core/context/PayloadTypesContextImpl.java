package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public abstract class PayloadTypesContextImpl implements PayloadTypesContext {
   private static final Map<Class<?>, Type<?>> MESSAGE_TYPES = new IdentityHashMap<>();
   private final AtomicInteger discriminator = new AtomicInteger();
   protected final ResourceLocation channelName;

   protected PayloadTypesContextImpl(String modId) {
      this.channelName = ResourceLocation.fromNamespaceAndPath(modId, "main");
   }

   public static <T extends CustomPacketPayload> Type<T> getPayloadType(Class<T> payloadClazz) {
      Objects.requireNonNull(payloadClazz, "payload class is null");
      Type<T> payloadType = (Type<T>)MESSAGE_TYPES.get(payloadClazz);
      Objects.requireNonNull(payloadType, "payload type is null");
      return payloadType;
   }

   protected final synchronized <T extends CustomPacketPayload> Type<T> registerPayloadType(Class<T> clazz) {
      ResourceLocation resourceLocation = this.channelName.withSuffix("/" + this.discriminator.getAndIncrement());
      Type<T> type = new Type(resourceLocation);
      MESSAGE_TYPES.put(clazz, type);
      return type;
   }

   protected final BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally(String payloadContext) {
      return (throwable, consumer) -> {
         String modName = ModContainer.getDisplayName(this.channelName.getNamespace());
         consumer.accept(Component.literal("Receiving %s from %s failed: %s".formatted(payloadContext, modName, throwable.getMessage())));
      };
   }
}
