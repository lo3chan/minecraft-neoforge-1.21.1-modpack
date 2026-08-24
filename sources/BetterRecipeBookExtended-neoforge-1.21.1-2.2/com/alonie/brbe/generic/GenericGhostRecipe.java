package com.alonie.brbe.generic;

import com.alonie.brbe.api.BRBBookCategories;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class GenericGhostRecipe<R extends GenericRecipe> {
   @Nullable
   protected Consumer<ItemStack> onGhostUpdate;
   @Nullable
   protected R recipe;
   protected final List<GenericGhostRecipe<R>.GenericGhostIngredient> ingredients = Lists.newArrayList();
   protected float time;
   protected RegistryAccess registryAccess;
   @Nullable
   private BiPredicate<GenericGhostRecipe.GhostRenderType, GenericGhostRecipe<R>.GenericGhostIngredient> renderingPredicate;
   @Nullable
   private ItemStack lastHoveredItem;

   public GenericGhostRecipe(@Nullable Consumer<ItemStack> onGhostUpdate, RegistryAccess registryAccess) {
      this.onGhostUpdate = onGhostUpdate;
      this.registryAccess = registryAccess;
   }

   public void setRenderingPredicate(@Nullable BiPredicate<GenericGhostRecipe.GhostRenderType, GenericGhostRecipe<R>.GenericGhostIngredient> renderingPredicate) {
      this.renderingPredicate = renderingPredicate;
   }

   public <T extends AbstractContainerMenu> void setDefaultRenderingPredicate(T menu) {
      this.setRenderingPredicate((type, ingredient) -> {
         ItemStack slot = ((Slot)menu.slots.get(ingredient.getContainerSlot())).getItem();
         switch (type) {
            case ITEM:
            case BACKGROUND:
            case TOOLTIP:
               return slot.isEmpty();
            default:
               return true;
         }
      });
   }

   public ItemStack getCurrentResult(BRBBookCategories.Category category) {
      if (this.recipe == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemStack = this.recipe.getResult(this.registryAccess, category);
         return itemStack.copy();
      }
   }

   public void clear() {
      this.recipe = null;
      this.ingredients.clear();
      this.time = 0.0F;
   }

   public void addIngredient(int containerSlot, Ingredient ingredient, int i, int j) {
      this.ingredients.add(new GenericGhostRecipe.GenericGhostIngredient(containerSlot, ingredient, i, j));
   }

   public GenericGhostRecipe<R>.GenericGhostIngredient get(int i) {
      return this.ingredients.get(i);
   }

   public int size() {
      return this.ingredients.size();
   }

   @Nullable
   public R getRecipe() {
      return this.recipe;
   }

   public void setRecipe(@Nullable R recipe) {
      this.recipe = recipe;
   }

   public void render(GuiGraphics guiGraphics, Minecraft minecraft, int i, int j, boolean bl, float f, BRBBookCategories.Category category) {
      if (!Screen.hasControlDown()) {
         this.time += f;
         if (this.onGhostUpdate != null && this.recipe != null) {
            this.onGhostUpdate.accept(this.getCurrentResult(category));
         }
      }

      for (int k = 0; k < this.ingredients.size(); k++) {
         GenericGhostRecipe<R>.GenericGhostIngredient ghostIngredient = this.ingredients.get(k);
         boolean shouldRenderBackground = this.renderingPredicate != null
            && this.renderingPredicate.test(GenericGhostRecipe.GhostRenderType.BACKGROUND, ghostIngredient);
         boolean shouldRenderItem = this.renderingPredicate != null && this.renderingPredicate.test(GenericGhostRecipe.GhostRenderType.ITEM, ghostIngredient);
         int l = ghostIngredient.getX() + i;
         int m = ghostIngredient.getY() + j;
         if (shouldRenderBackground) {
            if (k == 0 && bl) {
               guiGraphics.fill(l - 4, m - 4, l + 20, m + 20, 822018048);
            } else {
               guiGraphics.fill(l, m, l + 16, m + 16, 822018048);
            }
         }

         ItemStack itemStack = ghostIngredient.getItem();
         if (shouldRenderItem) {
            guiGraphics.renderFakeItem(itemStack, l, m);
         }

         if (shouldRenderBackground) {
            guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), l, m, l + 16, m + 16, 822083583);
         }

         if (k == 0) {
            guiGraphics.renderItemDecorations(minecraft.font, itemStack, l, m);
         }
      }
   }

   public GenericGhostRecipe<R>.GenericGhostIngredient getBySlot(int i) {
      for (GenericGhostRecipe<R>.GenericGhostIngredient ingredient : this.ingredients) {
         if (ingredient.getContainerSlot() == i) {
            return ingredient;
         }
      }

      return null;
   }

   public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
      ItemStack itemStack = null;

      for (GenericGhostRecipe<R>.GenericGhostIngredient ingredient : this.ingredients) {
         int j = ingredient.getX() + x;
         int k = ingredient.getY() + y;
         if (mouseX >= j
            && mouseY >= k
            && mouseX < j + 16
            && mouseY < k + 16
            && (this.renderingPredicate == null || this.renderingPredicate.test(GenericGhostRecipe.GhostRenderType.TOOLTIP, ingredient))) {
            itemStack = ingredient.getItem();
         }
      }

      this.lastHoveredItem = itemStack;
      if (itemStack != null && Minecraft.getInstance().screen != null) {
         gui.renderComponentTooltip(Minecraft.getInstance().font, Screen.getTooltipFromItem(Minecraft.getInstance(), itemStack), mouseX, mouseY);
      }
   }

   @Nullable
   public ItemStack getLastHoveredItem() {
      return this.lastHoveredItem;
   }

   public class GenericGhostIngredient {
      private final Ingredient ingredient;
      private final int x;
      private final int y;
      private final int containerSlot;

      public GenericGhostIngredient(int containerSlot, Ingredient ingredient, int i, int j) {
         this.containerSlot = containerSlot;
         this.ingredient = ingredient;
         this.x = i;
         this.y = j;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public ItemStack getItem() {
         ItemStack[] itemStacks = this.ingredient.getItems();
         return itemStacks.length == 0 ? ItemStack.EMPTY : itemStacks[Mth.floor(GenericGhostRecipe.this.time / 30.0F) % itemStacks.length];
      }

      public int getContainerSlot() {
         return this.containerSlot;
      }

      public GenericGhostRecipe<R> getOwner() {
         return GenericGhostRecipe.this;
      }
   }

   public static enum GhostRenderType {
      ITEM,
      BACKGROUND,
      TOOLTIP;
   }
}
