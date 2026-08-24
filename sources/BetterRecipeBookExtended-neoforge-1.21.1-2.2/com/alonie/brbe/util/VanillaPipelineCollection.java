package com.alonie.brbe.util;

import com.alonie.brbe.generic.pins.PipelineCollection;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class VanillaPipelineCollection implements PipelineCollection {
   private final RecipeCollection delegate;

   private VanillaPipelineCollection(RecipeCollection delegate) {
      this.delegate = Objects.requireNonNull(delegate);
   }

   public static VanillaPipelineCollection of(RecipeCollection collection) {
      return new VanillaPipelineCollection(collection);
   }

   public RecipeCollection unwrap() {
      return this.delegate;
   }

   @Override
   public List<RecipeHolder<?>> getRecipes() {
      return this.delegate.getRecipes();
   }

   @Override
   public boolean hasAnyCraftable() {
      return this.delegate.hasCraftable();
   }

   @Override
   public boolean hasAnyPartiallyCraftable() {
      return PartialCraftingUtil.hasPartialMaterials(this.delegate);
   }

   @Override
   public boolean has(ResourceLocation resourceLocation) {
      for (RecipeHolder<?> recipe : this.delegate.getRecipes()) {
         if (recipe.id().equals(resourceLocation)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o instanceof VanillaPipelineCollection that ? this.delegate.equals(that.delegate) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.delegate.hashCode();
   }
}
