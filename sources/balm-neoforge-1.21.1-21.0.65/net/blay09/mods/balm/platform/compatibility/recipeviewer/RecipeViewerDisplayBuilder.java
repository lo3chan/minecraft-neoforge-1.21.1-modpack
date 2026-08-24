package net.blay09.mods.balm.platform.compatibility.recipeviewer;

import java.util.function.BiConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface RecipeViewerDisplayBuilder<T> {
   RecipeViewerDisplayBuilder<T> size(int var1, int var2);

   default RecipeViewerDisplayBuilder<T> background(ResourceLocation texture) {
      return this.background(texture, 0, 0);
   }

   RecipeViewerDisplayBuilder<T> background(ResourceLocation var1, int var2, int var3);

   default RecipeViewerDisplayBuilder<T> background(ResourceLocation texture, int u, int v, int width, int height) {
      return this.background(texture, u, v, width, height, 256, 256);
   }

   RecipeViewerDisplayBuilder<T> background(ResourceLocation var1, int var2, int var3, int var4, int var5, int var6, int var7);

   default RecipeViewerDisplayBuilder<T> icon(ItemLike itemLike) {
      return this.icon(new ItemStack(itemLike));
   }

   RecipeViewerDisplayBuilder<T> icon(ItemStack var1);

   RecipeViewerDisplayBuilder<T> title(Component var1);

   RecipeViewerDisplayBuilder<T> slots(BiConsumer<T, RecipeViewerDisplaySlotsBuilder> var1);
}
