package com.alonie.brbe.brewingstand;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.api.BRBBookSettings;
import com.alonie.brbe.generic.GenericRecipeBookComponent;
import com.alonie.brbe.generic.GenericRecipePage;
import com.alonie.brbe.interfaces.IPinningComponent;
import com.alonie.brbe.loaders.PotionLoader;
import com.alonie.brbe.mixins.accessors.BrewingStandMenuAccessor;
import com.alonie.brbe.util.BRBHelper;
import com.alonie.brbe.util.ClientInventoryUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BrewingRecipeBookComponent
   extends GenericRecipeBookComponent<BrewingStandMenu, BrewingRecipeCollection, BrewableResult>
   implements IPinningComponent<BrewingRecipeCollection> {
   private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("brb.gui.togglePotions.brewable");

   public void init(
      int parentWidth,
      int parentHeight,
      Minecraft client,
      boolean narrow,
      BrewingStandMenu menu,
      Consumer<ItemStack> onGhostRecipeUpdate,
      RegistryAccess registryAccess
   ) {
      this.recipesPage = new GenericRecipePage<>(
         registryAccess, () -> new BrewableRecipeButton(registryAccess, () -> BRBBookSettings.isFiltering(this.getRecipeBookType()))
      );
      super.init(parentWidth, parentHeight, client, narrow, menu, onGhostRecipeUpdate, registryAccess);
      this.ghostRecipe
         .setRenderingPredicate(
            (type, ingredient) -> {
               ItemStack slot = ((Slot)menu.slots.get(ingredient.getContainerSlot())).getItem();
               switch (type) {
                  case ITEM:
                  case BACKGROUND:
                     ItemStack ghost = ingredient.getContainerSlot() == BrewingStandMenuAccessor.getBOTTLE_SLOT_START()
                        ? ingredient.getOwner().getBySlot(1).getItem()
                        : ingredient.getItem();
                     if (ingredient.getContainerSlot() >= BrewingStandMenuAccessor.getBOTTLE_SLOT_START()
                        && ingredient.getContainerSlot() <= BrewingStandMenuAccessor.getBOTTLE_SLOT_END()) {
                        if (!(slot.getItem() instanceof PotionItem)) {
                           return true;
                        }

                        PotionContents slotPotion = (PotionContents)slot.get(DataComponents.POTION_CONTENTS);
                        PotionContents ghostPotion = (PotionContents)ghost.get(DataComponents.POTION_CONTENTS);
                        return !Objects.equals(slotPotion, ghostPotion);
                     }

                     return !slot.is(ghost.getItem());
                  case TOOLTIP:
                     return slot.isEmpty();
                  default:
                     return true;
               }
            }
         );
   }

   public ItemStack getInputStack(BrewableResult result) {
      Potion inputPotion = PlatformPotionUtil.getFrom(result.recipe);
      Ingredient ingredient = PlatformPotionUtil.getIngredient(result.recipe);
      ItemStack inputStack;
      if (this.selectedTab.getCategory() == BetterRecipeBook.BREWING_SPLASH_POTION) {
         inputStack = new ItemStack(Items.SPLASH_POTION);
      } else if (this.selectedTab.getCategory() == BetterRecipeBook.BREWING_LINGERING_POTION) {
         inputStack = new ItemStack(Items.LINGERING_POTION);
      } else {
         inputStack = new ItemStack(Items.POTION);
      }

      inputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder(inputPotion)));
      return inputStack;
   }

   public void setupGhostRecipe(BrewableResult result, List<Slot> slots) {
      this.ghostRecipe
         .addIngredient(
            BrewingStandMenuAccessor.getINGREDIENT_SLOT(),
            Ingredient.of(new ItemStack[]{PlatformPotionUtil.getIngredient(result.recipe).getItems()[0]}),
            slots.get(BrewingStandMenuAccessor.getINGREDIENT_SLOT()).x,
            slots.get(BrewingStandMenuAccessor.getINGREDIENT_SLOT()).y
         );

      assert this.selectedTab != null;

      ItemStack inputStack = result.inputAsItemStack(this.selectedTab.getCategory());

      for (int i = BrewingStandMenuAccessor.getBOTTLE_SLOT_START(); i <= BrewingStandMenuAccessor.getBOTTLE_SLOT_END(); i++) {
         this.ghostRecipe.addIngredient(i, Ingredient.of(new ItemStack[]{inputStack}), slots.get(i).x, slots.get(i).y);
      }
   }

   @Override
   protected List<BrewingRecipeCollection> getCollectionsForCategory() {
      List<BrewingRecipeCollection> results = new ArrayList<>();
      BRBBookCategories.Category category = this.selectedTab.getCategory();

      for (BrewableResult potion : PotionLoader.POTIONS) {
         results.add(new BrewingRecipeCollection(List.of(potion), this.menu, this.registryAccess, category));
      }

      return results;
   }

   @Override
   public Component getRecipeFilterName() {
      return ONLY_CRAFTABLES_TOOLTIP;
   }

   @Override
   public BRBHelper.Book getRecipeBookType() {
      return BetterRecipeBook.BREWING;
   }

   @Override
   public void handlePlaceRecipe() {
      BrewableResult result = this.recipesPage.getCurrentClickedRecipe();
      if (result != null) {
         this.ghostRecipe.clear();
         if (!result.hasMaterials(this.selectedTab.getCategory(), this.menu.slots)) {
            this.setupGhostRecipe(result, this.menu.slots);
         } else {
            ItemStack inputStack = this.getInputStack(result);
            Ingredient ingredient = PlatformPotionUtil.getIngredient(result.recipe);
            int slotIndex = 0;
            int usedInputSlots = 0;

            for (Slot slot : this.menu.slots) {
               ItemStack itemStack = slot.getItem();
               if (ItemStack.isSameItemSameComponents(inputStack, itemStack)) {
                  if (usedInputSlots <= 2) {
                     ClientInventoryUtil.moveItemToSlot(this.menu, slotIndex, this.menu.getSlot(usedInputSlots).index);
                     usedInputSlots++;
                  }
               } else if (ingredient.getItems()[0].getItem().equals(slot.getItem().getItem())) {
                  ClientInventoryUtil.moveItemToSlot(this.menu, slotIndex, this.menu.getSlot(3).index);
               }

               slotIndex++;
            }

            this.updateCollections(false);
         }
      }
   }

   public void recipesUpdated() {
      this.updateCollections(false);
   }
}
