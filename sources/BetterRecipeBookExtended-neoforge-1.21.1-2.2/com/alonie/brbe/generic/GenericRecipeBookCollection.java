package com.alonie.brbe.generic;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.generic.pins.Pinnable;
import com.alonie.brbe.generic.pins.PipelineCollection;
import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public abstract class GenericRecipeBookCollection<R extends GenericRecipe, M extends AbstractContainerMenu> implements Pinnable, PipelineCollection {
   protected final RegistryAccess registryAccess;
   protected List<R> recipes;
   protected M menu;

   protected GenericRecipeBookCollection(List<? extends R> list, M menu, RegistryAccess registryAccess) {
      this.menu = menu;
      this.recipes = ImmutableList.copyOf(list);
      this.registryAccess = registryAccess;
   }

   @Override
   public List<R> getRecipes() {
      return this.recipes;
   }

   protected abstract List<R> getDisplayRecipes(boolean var1);

   @Override
   public boolean has(ResourceLocation resourceLocation) {
      for (R recipe : this.getRecipes()) {
         if (recipe.id().equals(resourceLocation)) {
            return true;
         }
      }

      return false;
   }

   public R getFirst() {
      return this.getRecipes().get(0);
   }

   protected abstract boolean atleastOneCraftable(NonNullList<Slot> var1);

   protected boolean atleastOnePartiallyCraftable(NonNullList<Slot> slots) {
      return false;
   }

   public List<R> getPartiallyCraftableRecipes(NonNullList<Slot> slots) {
      return List.of();
   }

   public List<R> getPartiallyCraftableRecipes() {
      return !BetterRecipeBook.ctx().config().partialMarkingEnabled ? List.of() : this.getPartiallyCraftableRecipes(this.menu.slots);
   }

   @Override
   public boolean hasAnyCraftable() {
      return this.atleastOneCraftable(this.menu.slots);
   }

   @Override
   public boolean hasAnyPartiallyCraftable() {
      return this.atleastOnePartiallyCraftable(this.menu.slots);
   }
}
