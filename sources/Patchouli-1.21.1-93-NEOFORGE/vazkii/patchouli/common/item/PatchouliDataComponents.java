package vazkii.patchouli.common.item;

import java.util.function.BiConsumer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

public class PatchouliDataComponents {
   public static final ResourceLocation COMPONENT_ID = ResourceLocation.fromNamespaceAndPath("patchouli", "book");
   public static final DataComponentType<ResourceLocation> BOOK = DataComponentType.builder()
      .persistent(ResourceLocation.CODEC)
      .networkSynchronized(ResourceLocation.STREAM_CODEC)
      .build();

   public static void submitDataComponentRegistrations(BiConsumer<ResourceLocation, DataComponentType<?>> consumer) {
      consumer.accept(COMPONENT_ID, BOOK);
   }
}
