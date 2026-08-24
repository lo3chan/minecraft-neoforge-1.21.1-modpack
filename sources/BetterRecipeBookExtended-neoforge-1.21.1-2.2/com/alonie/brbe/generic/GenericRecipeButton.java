package com.alonie.brbe.generic;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.mixins.accessors.KeyMappingAccessor;
import com.alonie.brbe.util.BRBTextures;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;

public class GenericRecipeButton<C extends GenericRecipeBookCollection<R, M>, R extends GenericRecipe, M extends AbstractContainerMenu> extends AbstractWidget {
   private final Supplier<Boolean> filteringSupplier;
   protected C collection;
   protected M menu;
   protected float time;
   protected int currentIndex;
   protected RegistryAccess registryAccess;
   protected BRBBookCategories.Category category;

   public GenericRecipeButton(RegistryAccess registryAccess, Supplier<Boolean> filteringSupplier) {
      super(0, 0, 25, 25, CommonComponents.EMPTY);
      this.registryAccess = registryAccess;
      this.filteringSupplier = filteringSupplier;
   }

   public void showCollection(C collection, M smithingMenu, BRBBookCategories.Category category) {
      this.collection = collection;
      this.menu = smithingMenu;
      this.category = category;
   }

   public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
      if (this.collection != null) {
         if (!Screen.hasControlDown()) {
            this.time += delta;
         }

         List<R> list = this.getOrderedRecipes();
         if (!list.isEmpty()) {
            this.currentIndex = Mth.floor(this.time / 30.0F) % list.size();
            R current = this.getCurrentDisplayedRecipe();
            boolean isPartial = current != null && this.collection.getPartiallyCraftableRecipes().stream().anyMatch(r -> r.id().equals(current.id()));
            boolean effectiveCraftable = this.collection.atleastOneCraftable(this.menu.slots) || isPartial;
            ResourceLocation outlineTexture = effectiveCraftable
               ? BRBTextures.RECIPE_BOOK_BUTTON_SLOT_CRAFTABLE_SPRITE
               : BRBTextures.RECIPE_BOOK_BUTTON_SLOT_UNCRAFTABLE_SPRITE;
            gui.blitSprite(outlineTexture, this.getX(), this.getY(), this.width, this.height);
            if (isPartial) {
               gui.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, 1627337523);
            }

            ItemStack result = this.getCurrentDisplayedRecipe().getResult(this.registryAccess, this.category);
            int offset = 4;
            gui.renderFakeItem(result, this.getX() + offset, this.getY() + offset);
            if (BetterRecipeBook.pinnedRecipeManager.has(this.collection)) {
               gui.blitSprite(BRBTextures.RECIPE_BOOK_PIN_SPRITE, this.getX() + this.getWidth() - 4, this.getY() - 4, 32, 32);
            }
         }
      }
   }

   public R getCurrentDisplayedRecipe() {
      List<R> list = this.getOrderedRecipes();
      return list.get(this.currentIndex);
   }

   public boolean isOnlyOption() {
      return this.getOrderedRecipes().size() == 1;
   }

   public List<R> getOrderedRecipes() {
      C coll = this.getCollection();
      if (coll == null) {
         return List.of();
      } else {
         List<R> list = coll.getDisplayRecipes(true);
         if (!this.filteringSupplier.get()) {
            list.addAll(this.collection.getDisplayRecipes(false));
         } else {
            list.addAll(this.collection.getPartiallyCraftableRecipes());
         }

         return list;
      }
   }

   public C getCollection() {
      return this.collection;
   }

   public void updateWidgetNarration(NarrationElementOutput builder) {
   }

   public int getWidth() {
      return 25;
   }

   protected boolean isValidClickButton(int i) {
      return i == 0 || i == 1;
   }

   public List<Component> getTooltipText() {
      List<Component> list = Lists.newArrayList();
      TooltipContext tipCtx = TooltipContext.of(this.registryAccess);
      list.addAll(
         this.getCurrentDisplayedRecipe()
            .getResult(this.registryAccess, this.category)
            .getTooltipLines(tipCtx, Minecraft.getInstance().player, TooltipFlag.NORMAL)
      );
      this.addPinTooltip(list);
      return list;
   }

   public void addPinTooltip(List<Component> list) {
      list.add(Component.empty());
      if (BetterRecipeBook.pinnedRecipeManager.has(this.collection)) {
         list.add(Component.translatable("brb.gui.pin.remove", new Object[]{((KeyMappingAccessor)BetterRecipeBook.PIN_MAPPING).getKey().getDisplayName()}));
      } else {
         list.add(Component.translatable("brb.gui.pin.add", new Object[]{((KeyMappingAccessor)BetterRecipeBook.PIN_MAPPING).getKey().getDisplayName()}));
      }
   }
}
