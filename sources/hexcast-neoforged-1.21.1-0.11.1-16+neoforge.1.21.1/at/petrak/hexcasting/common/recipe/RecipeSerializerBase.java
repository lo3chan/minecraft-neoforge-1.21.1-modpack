package at.petrak.hexcasting.common.recipe;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public abstract class RecipeSerializerBase<T extends Recipe<?>> implements RecipeSerializer<T> {
   @Nullable
   private ResourceLocation registryName;

   public RecipeSerializerBase<T> setRegistryName(ResourceLocation name) {
      this.registryName = name;
      return this;
   }

   @Nullable
   public ResourceLocation getRegistryName() {
      return this.registryName;
   }

   public Class<RecipeSerializer<?>> getRegistryType() {
      return RecipeSerializer.class;
   }
}
