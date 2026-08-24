package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.init.v1.BuiltinItemRenderer;
import fuzs.puzzleslib.api.client.init.v1.ReloadingBuiltInItemRenderer;
import fuzs.puzzleslib.api.client.renderer.v1.special.SpecialModelRenderer;
import fuzs.puzzleslib.impl.client.renderer.SpecialBuiltInItemRenderer;
import java.util.Objects;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface BuiltinModelItemRendererContext {
   void registerItemRenderer(Item var1, BuiltinItemRenderer var2);

   @Deprecated
   default void registerItemRenderer(BuiltinItemRenderer itemRenderer, ItemLike... items) {
      Objects.requireNonNull(items, "items is null");

      for (ItemLike item : items) {
         this.registerItemRenderer(item.asItem(), itemRenderer);
      }
   }

   void registerItemRenderer(Item var1, ReloadingBuiltInItemRenderer var2);

   @Deprecated
   default void registerItemRenderer(ReloadingBuiltInItemRenderer itemRenderer, ItemLike... items) {
      Objects.requireNonNull(items, "items is null");

      for (ItemLike item : items) {
         this.registerItemRenderer(item.asItem(), itemRenderer);
      }
   }

   default void registerItemRenderer(Item item, SpecialModelRenderer.Unbaked<?> specialModelRenderer) {
      this.registerItemRenderer(item, (ReloadingBuiltInItemRenderer)(new SpecialBuiltInItemRenderer<>(specialModelRenderer)));
   }
}
