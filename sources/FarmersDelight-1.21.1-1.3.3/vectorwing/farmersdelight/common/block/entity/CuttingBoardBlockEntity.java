package vectorwing.farmersdelight.common.block.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeManager.CachedCheck;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipeInput;
import vectorwing.farmersdelight.common.registry.ModAdvancements;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.tag.CommonTags;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vectorwing.farmersdelight.common.utility.TextUtils;

@EventBusSubscriber(
   modid = "farmersdelight"
)
public class CuttingBoardBlockEntity extends SyncedBlockEntity implements Clearable {
   private final ItemStackHandler inventory = this.createHandler();
   private final CachedCheck<CuttingBoardRecipeInput, CuttingBoardRecipe> quickCheck;
   private ResourceLocation lastRecipeID;
   private boolean isItemCarvingBoard = false;

   public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntityTypes.CUTTING_BOARD.get(), pos, state);
      this.quickCheck = RecipeManager.createCheck(ModRecipeTypes.CUTTING.get());
   }

   @SubscribeEvent
   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(ItemHandler.BLOCK, ModBlockEntityTypes.CUTTING_BOARD.get(), (be, context) -> be.getInventory());
   }

   public void loadAdditional(CompoundTag compound, Provider registries) {
      super.loadAdditional(compound, registries);
      this.isItemCarvingBoard = compound.getBoolean("IsItemCarved");
      this.inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
   }

   public void saveAdditional(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
      compound.put("Inventory", this.inventory.serializeNBT(registries));
      compound.putBoolean("IsItemCarved", this.isItemCarvingBoard);
   }

   public boolean processStoredItemUsingTool(ItemStack toolStack, @Nullable Player player) {
      if (this.level == null) {
         return false;
      } else if (this.isItemCarvingBoard) {
         return false;
      } else {
         Optional<RecipeHolder<CuttingBoardRecipe>> matchingRecipe = this.getMatchingRecipe(toolStack, player);
         matchingRecipe.ifPresent(
            recipe -> {
               for (ItemStack resultStack : ((CuttingBoardRecipe)recipe.value())
                  .rollResults(
                     this.level.random,
                     ItemUtils.getValidatedEnchantmentLevel(Enchantments.FORTUNE, this.level.registryAccess(), toolStack),
                     new RecipeWrapper(this.inventory)
                  )) {
                  Direction direction = ((Direction)this.getBlockState().getValue(CuttingBoardBlock.FACING)).getCounterClockWise();
                  ItemUtils.spawnItemEntity(
                     this.level,
                     resultStack.copy(),
                     this.worldPosition.getX() + 0.5 + direction.getStepX() * 0.2,
                     this.worldPosition.getY() + 0.2,
                     this.worldPosition.getZ() + 0.5 + direction.getStepZ() * 0.2,
                     direction.getStepX() * 0.2F,
                     0.0,
                     direction.getStepZ() * 0.2F
                  );
               }

               if (!this.level.isClientSide) {
                  toolStack.hurtAndBreak(1, (ServerLevel)this.level, player, item -> {});
                  if (player != null) {
                     player.awardStat(Stats.ITEM_USED.get(toolStack.getItem()));
                  }
               }

               if (this.level instanceof ServerLevel serverLevel) {
                  this.spawnCuttingParticles(serverLevel, this.getBlockPos(), this.getStoredItem());
               }

               this.playProcessingSound(((CuttingBoardRecipe)recipe.value()).getSoundEvent().orElse(null), toolStack, this.getStoredItem());
               this.inventory.extractItem(0, 1, false);
               if (player instanceof ServerPlayer) {
                  ModAdvancements.USE_CUTTING_BOARD.get().trigger((ServerPlayer)player);
                  if (!this.getStoredItem().isEmpty()) {
                     player.displayClientMessage(TextUtils.block("cutting_board.remaining_items", this.getStoredItem().getCount()), true);
                  } else {
                     player.displayClientMessage(Component.empty(), true);
                  }
               }
            }
         );
         return matchingRecipe.isPresent();
      }
   }

   private Optional<RecipeHolder<CuttingBoardRecipe>> getMatchingRecipe(ItemStack toolStack, @Nullable Player player) {
      if (this.level == null) {
         return Optional.empty();
      } else {
         Optional<RecipeHolder<CuttingBoardRecipe>> recipe = this.quickCheck
            .getRecipeFor(new CuttingBoardRecipeInput(this.getStoredItem(), toolStack), this.level);
         if (recipe.isPresent()) {
            if (((CuttingBoardRecipe)recipe.get().value()).getTool().test(toolStack)) {
               return recipe;
            }

            if (player != null) {
               player.displayClientMessage(TextUtils.block("cutting_board.invalid_item"), true);
            }
         } else if (player != null) {
            player.displayClientMessage(TextUtils.block("cutting_board.invalid_tool"), true);
         }

         return Optional.empty();
      }
   }

   public void spawnCuttingParticles(ServerLevel level, BlockPos pos, ItemStack stack) {
      level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 5, 0.1, 0.1, 0.1, 0.05);
   }

   public void playProcessingSound(@Nullable SoundEvent sound, ItemStack tool, ItemStack boardItem) {
      if (sound != null) {
         this.playSound(sound, 1.0F, 1.0F);
      } else if (tool.is(Items.TOOLS_SHEAR)) {
         this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
      } else if (tool.is(CommonTags.Items.TOOLS_KNIFE)) {
         this.playSound(ModSounds.BLOCK_CUTTING_BOARD_KNIFE.get(), 0.8F, 1.0F);
      } else if (boardItem.getItem() instanceof BlockItem blockItem) {
         Block block = blockItem.getBlock();
         SoundType soundType = block.defaultBlockState().getSoundType();
         this.playSound(soundType.getBreakSound(), 1.0F, 0.8F);
      } else {
         this.playSound(SoundEvents.WOOD_BREAK, 1.0F, 0.8F);
      }
   }

   public void playSound(SoundEvent sound, float volume, float pitch) {
      if (this.level != null) {
         this.level
            .playSound(
               null,
               this.worldPosition.getX() + 0.5F,
               this.worldPosition.getY() + 0.5F,
               this.worldPosition.getZ() + 0.5F,
               sound,
               SoundSource.BLOCKS,
               volume,
               pitch
            );
      }
   }

   public boolean canAddItem(ItemStack addedStack) {
      return !this.isItemCarvingBoard && !addedStack.isEmpty()
         ? this.inventory.insertItem(0, addedStack.copy(), true).getCount() != addedStack.getCount()
         : false;
   }

   public ItemStack addItem(ItemStack addedStack) {
      return !this.isItemCarvingBoard ? this.inventory.insertItem(0, addedStack.copy(), false) : addedStack;
   }

   public ItemStack removeItem() {
      this.isItemCarvingBoard = false;
      return this.inventory.extractItem(0, this.getMaxStackSize(), false);
   }

   public boolean carveToolOnBoard(ItemStack toolStack) {
      if ((toolStack.getItem() instanceof TieredItem || toolStack.getItem() instanceof TridentItem || toolStack.getItem() instanceof ShearsItem)
         && this.addItem(toolStack) == ItemStack.EMPTY) {
         this.isItemCarvingBoard = true;
         return true;
      } else {
         return false;
      }
   }

   public IItemHandler getInventory() {
      return this.inventory;
   }

   public ItemStack getStoredItem() {
      return this.inventory.getStackInSlot(0);
   }

   public int getMaxStackSize() {
      return this.inventory.getSlotLimit(0);
   }

   public boolean isEmpty() {
      return this.inventory.getStackInSlot(0).isEmpty();
   }

   public boolean isItemCarvingBoard() {
      return this.isItemCarvingBoard;
   }

   public void setRemoved() {
      super.setRemoved();
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler() {
         protected void onContentsChanged(int slot) {
            CuttingBoardBlockEntity.this.inventoryChanged();
         }
      };
   }

   public void clearContent() {
      ItemUtils.clearItems(this.inventory);
   }
}
