package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferError.Type;
import net.joefoxe.hexerei.container.MixingCauldronContainer;
import net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.RecipeToServer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class MixingCauldronTransferInfo implements IRecipeTransferHandler<MixingCauldronContainer, MixingCauldronRecipe> {
   private final IRecipeTransferHandlerHelper transferHandlerHelper;

   public MixingCauldronTransferInfo(IRecipeTransferHandlerHelper transferHandlerHelper) {
      this.transferHandlerHelper = transferHandlerHelper;
   }

   public Class<MixingCauldronContainer> getContainerClass() {
      return MixingCauldronContainer.class;
   }

   public Optional<MenuType<MixingCauldronContainer>> getMenuType() {
      return Optional.empty();
   }

   public RecipeType<MixingCauldronRecipe> getRecipeType() {
      return new RecipeType(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class);
   }

   @Nullable
   public IRecipeTransferError transferRecipe(
      final MixingCauldronContainer container,
      MixingCauldronRecipe recipe,
      IRecipeSlotsView recipeSlots,
      Player pPlayer,
      boolean pMaxTransfer,
      boolean pDoTransfer
   ) {
      List<IRecipeSlotView> stacks = recipeSlots.getSlotViews();
      NonNullList<ItemStack> convertedInput = NonNullList.withSize(stacks.size() - 1, ItemStack.EMPTY);
      MixingCauldronTile inventory = (MixingCauldronTile)container.tileEntity;
      final List<Boolean> itemMatchesSlot = new ArrayList<>();

      for (int i = 0; i < 8; i++) {
         itemMatchesSlot.add(i, false);
      }

      boolean flag = false;

      for (int j = 0; j < 8; j++) {
         Ingredient ingredient = (Ingredient)recipe.getIngredients().get(j);

         for (int i = 0; i < 8; i++) {
            if (ingredient.test((ItemStack)inventory.items.get(i)) && !itemMatchesSlot.get(j)) {
               itemMatchesSlot.set(j, true);
            }
         }

         for (int ix = 0; ix < 36; ix++) {
            if (ingredient.test((ItemStack)pPlayer.getInventory().items.get(ix)) && !itemMatchesSlot.get(j)) {
               itemMatchesSlot.set(j, true);
            }
         }
      }

      boolean allItemsMissing = true;

      for (int ixx = 0; ixx < 8; ixx++) {
         if (itemMatchesSlot.get(ixx)) {
            allItemsMissing = false;
            break;
         }
      }

      int check = 0;
      if (!allItemsMissing) {
         check = checkRecipe(stacks, inventory, pPlayer);
      }

      if (!pDoTransfer && check == 0) {
         return new IRecipeTransferError() {
            public Type getType() {
               return Type.USER_FACING;
            }

            public void showError(GuiGraphics guiGraphics, int mouseX, int mouseY, IRecipeSlotsView recipeSlotsView, int recipeX, int recipeY) {
               for (int ixxx = 0; ixxx < itemMatchesSlot.size(); ixxx++) {
                  if (!itemMatchesSlot.get(ixxx)) {
                     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                     Slot slot = (Slot)container.slots.get(ixxx + 37);
                     guiGraphics.pose().pushPose();
                     guiGraphics.pose().translate(0.0F, 0.0F, 1000.0F);
                     guiGraphics.fill(recipeX + slot.x - 22, recipeY + slot.y + 1, recipeX + slot.x + 16 - 22, recipeY + slot.y + 1 + 16, 1727987712);
                     guiGraphics.pose().popPose();
                  }
               }

               super.showError(guiGraphics, mouseX, mouseY, recipeSlotsView, recipeX, recipeY);
            }
         };
      } else if (!pDoTransfer && check == 1) {
         return new IRecipeTransferError() {
            public Type getType() {
               return Type.COSMETIC;
            }

            public void showError(GuiGraphics guiGraphics, int mouseX, int mouseY, IRecipeSlotsView recipeSlotsView, int recipeX, int recipeY) {
               for (int ixxx = 0; ixxx < itemMatchesSlot.size(); ixxx++) {
                  boolean bool = itemMatchesSlot.get(ixxx);
                  if (!bool) {
                     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                     Slot slot = (Slot)container.slots.get(ixxx + 37);
                     guiGraphics.pose().pushPose();
                     guiGraphics.pose().translate(0.0F, 0.0F, 1000.0F);
                     guiGraphics.fill(recipeX + slot.x - 22, recipeY + slot.y + 1, recipeX + slot.x + 16 - 22, recipeY + slot.y + 1 + 16, 1727987712);
                     guiGraphics.pose().popPose();
                  }
               }

               super.showError(guiGraphics, mouseX, mouseY, recipeSlotsView, recipeX, recipeY);
            }
         };
      } else {
         return pDoTransfer && !transferRecipe(stacks, inventory, pPlayer) ? () -> Type.USER_FACING : null;
      }
   }

   public static int checkRecipe(List<IRecipeSlotView> guiIngredients, MixingCauldronTile blockEntity, Player player) {
      List<ItemStack> items = new ArrayList<>();

      for (int i = 0; i < 10; i++) {
         items.add(ItemStack.EMPTY);
      }

      int j = 0;

      for (IRecipeSlotView slotView : guiIngredients) {
         Optional<ItemStack> stack = slotView.getAllIngredients()
            .filter(t -> t.getType() == VanillaTypes.ITEM_STACK)
            .map(t -> (ItemStack)t.getIngredient())
            .findFirst();
         if (stack.isPresent() && j != 9) {
            items.set(j - 1, stack.get());
         }

         j++;
      }

      NonNullList<ItemStack> inv = blockEntity.items;
      List<Boolean> itemMatchesSlot = new ArrayList<>();

      for (int i = 0; i < 7; i++) {
         itemMatchesSlot.add(i, false);
      }

      boolean matchesAtleastOne = false;

      for (ItemStack stack : player.getInventory().items) {
         for (int i = 0; i < 7; i++) {
            if (!itemMatchesSlot.get(i) && ItemStack.isSameItem(stack, items.get(i))) {
               itemMatchesSlot.set(i, true);
               matchesAtleastOne = true;
            }
         }
      }

      boolean matchesItems = true;

      for (boolean bool : itemMatchesSlot) {
         if (!bool) {
            matchesItems = false;
            break;
         }
      }

      return matchesItems ? 2 : (matchesAtleastOne ? 1 : 0);
   }

   public static boolean transferRecipe(List<IRecipeSlotView> IRecipeSlotViews, MixingCauldronTile blockEntity, Player player) {
      List<ItemStack> items = new ArrayList<>();

      for (int i = 0; i < 10; i++) {
         items.add(ItemStack.EMPTY);
      }

      int j = 0;

      for (IRecipeSlotView slotView : IRecipeSlotViews) {
         Optional<ItemStack> stack = slotView.getAllIngredients()
            .filter(t -> t.getType() == VanillaTypes.ITEM_STACK)
            .map(t -> (ItemStack)t.getIngredient())
            .findFirst();
         if (stack.isPresent() && j != 9) {
            items.set(j - 1, stack.get());
         }

         j++;
      }

      boolean matchesItems = false;
      NonNullList<ItemStack> inv = blockEntity.items;

      for (ItemStack stack : player.getInventory().items) {
         if (matchesItems) {
            break;
         }

         for (ItemStack stack2 : items) {
            if (ItemStack.isSameItem(stack, stack2)) {
               matchesItems = true;
               break;
            }
         }
      }

      if (matchesItems) {
         HexereiPacketHandler.sendToServer(new RecipeToServer(items, blockEntity.getBlockPos(), player.getUUID()));
      }

      return matchesItems;
   }
}
