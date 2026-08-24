package net.joefoxe.hexerei.container;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipe;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipes;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class WoodcutterContainer extends AbstractContainerMenu {
   private final ContainerLevelAccess access;
   private final DataSlot selectedRecipeIndex = DataSlot.standalone();
   private final Level level;
   private List<RecipeHolder<WoodcutterRecipe>> recipes = Lists.newArrayList();
   private RecipeHolder<WoodcutterRecipe> lastUsedRecipe = null;
   private int recipesSize = 0;
   private ItemStack input = ItemStack.EMPTY;
   private ItemStack lastNonAirInput = ItemStack.EMPTY;
   private ItemStack lastInput = ItemStack.EMPTY;
   long lastSoundTime;
   final Slot inputSlot;
   final Slot resultSlot;
   Runnable slotUpdateListener = () -> {};
   public final Container container = new SimpleContainer(1) {
      public void setChanged() {
         super.setChanged();
         WoodcutterContainer.this.slotsChanged(this);
         WoodcutterContainer.this.slotUpdateListener.run();
      }
   };
   public final ResultContainer resultContainer = new ResultContainer();

   public WoodcutterContainer(int pContainerId, Inventory pPlayerInventory) {
      this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
   }

   public WoodcutterContainer(int pContainerId, Inventory pPlayerInventory, final ContainerLevelAccess pAccess) {
      super((MenuType)ModContainers.WOODCUTTER_CONTAINER.get(), pContainerId);
      this.access = pAccess;
      this.level = pPlayerInventory.player.level();
      this.inputSlot = this.addSlot(new Slot(this.container, 0, 26, 44));
      this.resultSlot = this.addSlot(
         new Slot(this.resultContainer, 1, 142, 56) {
            public boolean mayPlace(ItemStack p_40362_) {
               return false;
            }

            public boolean mayPickup(Player pPlayer) {
               return WoodcutterContainer.this.selectedRecipeIndex.get() != -1
                     && !WoodcutterContainer.this.recipes.isEmpty()
                     && WoodcutterContainer.this.recipes.size() >= WoodcutterContainer.this.selectedRecipeIndex.get()
                     && WoodcutterContainer.this.container.getItem(0).getCount()
                        >= ((WoodcutterRecipe)WoodcutterContainer.this.recipes.get(WoodcutterContainer.this.selectedRecipeIndex.get()).value()).ingredientCount
                  ? super.mayPickup(pPlayer)
                  : false;
            }

            public void onTake(Player p_150672_, ItemStack p_150673_) {
               p_150673_.onCraftedBy(p_150672_.level(), p_150672_, p_150673_.getCount());
               ItemStack itemstack = WoodcutterContainer.this.inputSlot
                  .remove(((WoodcutterRecipe)WoodcutterContainer.this.recipes.get(WoodcutterContainer.this.selectedRecipeIndex.get()).value()).ingredientCount);
               if (!itemstack.isEmpty()) {
                  WoodcutterContainer.this.setupResultSlot();
               }

               pAccess.execute((p_40364_, p_40365_) -> {
                  long l = p_40364_.getGameTime();
                  if (WoodcutterContainer.this.lastSoundTime != l) {
                     p_40364_.playSound(null, p_40365_, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                     WoodcutterContainer.this.lastSoundTime = l;
                  }
               });
               WoodcutterContainer.this.slotsChanged(WoodcutterContainer.this.container);
               super.onTake(p_150672_, p_150673_);
            }
         }
      );

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18 + 4, 84 + i * 18 + 31));
         }
      }

      for (int k = 0; k < 9; k++) {
         this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18 + 4, 173));
      }

      this.addDataSlot(this.selectedRecipeIndex);
      this.selectedRecipeIndex.set(-1);
   }

   public int getSelectedRecipeIndex() {
      return this.selectedRecipeIndex.get();
   }

   public List<RecipeHolder<WoodcutterRecipe>> getRecipes() {
      return this.recipes;
   }

   public int getNumRecipes() {
      return this.recipes.size();
   }

   public boolean hasInputItem() {
      return this.inputSlot.hasItem() && !this.recipes.isEmpty();
   }

   public boolean stillValid(Player pPlayer) {
      return stillValid(this.access, pPlayer, (Block)ModBlocks.WILLOW_WOODCUTTER.get())
         || stillValid(this.access, pPlayer, (Block)ModBlocks.MAHOGANY_WOODCUTTER.get())
         || stillValid(this.access, pPlayer, (Block)ModBlocks.WITCH_HAZEL_WOODCUTTER.get());
   }

   public boolean clickMenuButton(Player pPlayer, int pId) {
      if (this.isValidRecipeIndex(pId)) {
         this.selectedRecipeIndex.set(pId);
         this.setupResultSlot();
      }

      return true;
   }

   private boolean isValidRecipeIndex(int pRecipeIndex) {
      return pRecipeIndex >= 0 && pRecipeIndex < this.recipes.size();
   }

   public void slotsChanged(Container pInventory) {
      ItemStack itemstack = this.inputSlot.getItem();
      this.setupRecipeList(pInventory, itemstack);
      this.input = itemstack.copy();
   }

   private static SingleRecipeInput createRecipeInput(Container container) {
      return new SingleRecipeInput(container.getItem(0));
   }

   private List<RecipeHolder<WoodcutterRecipe>> getRecipesFor(SingleRecipeInput input, Level level) {
      List<RecipeHolder<WoodcutterRecipe>> list = this.level
         .getRecipeManager()
         .getRecipesFor((RecipeType)ModRecipeTypes.WOODCUTTING_TYPE.get(), input, this.level);
      List<RecipeHolder<WoodcutterRecipe>> list2 = WoodcutterRecipes.ALL
         .stream()
         .filter(recipe -> recipe.matches(input, level))
         .map(recipe -> new RecipeHolder(HexereiUtil.getResource("woodcutter"), recipe))
         .toList();
      list.addAll(list2);
      return list;
   }

   private void setupRecipeList(Container pContainer, ItemStack pStack) {
      this.recipes = new ArrayList<>();
      if (!ItemStack.isSameItem(this.input, pStack)) {
         if (!this.input.is(Items.AIR) && !this.input.is(this.lastInput.getItem()) && !this.lastInput.is(Items.AIR)) {
            this.selectedRecipeIndex.set(-1);
         }

         if (!this.input.is(this.lastInput.getItem())) {
            this.lastInput = this.input;
         }
      }

      if (!pStack.isEmpty()) {
         SingleRecipeInput recipeInput = createRecipeInput(this.container);
         this.recipes = this.getRecipesFor(recipeInput, this.level);
         this.recipes = this.recipes
            .stream()
            .filter(craftingRecipe -> this.container.getItem(0).getCount() >= ((WoodcutterRecipe)craftingRecipe.value()).ingredientCount)
            .toList();
      }

      if (this.recipesSize != this.recipes.size() && this.selectedRecipeIndex.get() != -1) {
         for (int i = 0; i < this.recipes.size(); i++) {
            if (this.recipes.get(i) == this.lastUsedRecipe) {
               this.selectedRecipeIndex.set(i);
               break;
            }
         }
      }

      this.recipesSize = this.recipes.size();
      if (!ItemStack.isSameItem(this.input, pStack) || pStack.getCount() != this.input.getCount()) {
         this.resultSlot.set(ItemStack.EMPTY);
         if (this.lastInput.is(Items.AIR)) {
            this.setupResultSlot();
         }
      }
   }

   void setupResultSlot() {
      if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
         RecipeHolder<WoodcutterRecipe> woodcutterrecipe = this.recipes.get(this.selectedRecipeIndex.get());
         this.resultContainer.setRecipeUsed(woodcutterrecipe);
         this.lastUsedRecipe = woodcutterrecipe;
         this.resultSlot.set(((WoodcutterRecipe)woodcutterrecipe.value()).assemble(createRecipeInput(this.container), this.level.registryAccess()));
      } else {
         this.resultSlot.set(ItemStack.EMPTY);
      }

      this.lastNonAirInput = this.input;
      this.broadcastChanges();
   }

   public MenuType<?> getType() {
      return (MenuType<?>)ModContainers.WOODCUTTER_CONTAINER.get();
   }

   public void registerUpdateListener(Runnable pListener) {
      this.slotUpdateListener = pListener;
   }

   public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
      return pSlot.container != this.resultContainer && super.canTakeItemForPickAll(pStack, pSlot);
   }

   public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(pIndex);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         Item item = itemstack1.getItem();
         itemstack = itemstack1.copy();
         if (pIndex == 1) {
            item.onCraftedBy(itemstack1, pPlayer.level(), pPlayer);
            if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (pIndex == 0) {
            if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.getRecipesFor(new SingleRecipeInput(itemstack1), this.level).isEmpty()) {
            if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (pIndex >= 2 && pIndex < 29) {
            if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (pIndex >= 29 && pIndex < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         }

         slot.setChanged();
         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(pPlayer, itemstack1);
         this.broadcastChanges();
      }

      return itemstack;
   }

   public void removed(Player pPlayer) {
      super.removed(pPlayer);
      this.selectedRecipeIndex.set(-1);
      this.resultContainer.removeItemNoUpdate(1);
      this.access.execute((p_40313_, p_40314_) -> this.clearContainer(pPlayer, this.container));
   }
}
