package vectorwing.farmersdelight.common.block.entity;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.RecipeManager.CachedCheck;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import vectorwing.farmersdelight.common.block.SkilletBlock;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModParticleTypes;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vectorwing.farmersdelight.common.utility.TextUtils;

public class SkilletBlockEntity extends SyncedBlockEntity implements HeatableBlockEntity, Clearable {
   private final ItemStackHandler inventory = this.createHandler();
   private int cookingTime;
   private int cookingTimeTotal;
   private ItemStack skilletStack = new ItemStack((ItemLike)ModItems.SKILLET.get());
   private int fireAspectLevel;
   private final CachedCheck<SingleRecipeInput, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

   public SkilletBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntityTypes.SKILLET.get(), pos, state);
   }

   public static void cookingTick(Level level, BlockPos pos, BlockState state, SkilletBlockEntity skillet) {
      boolean isHeated = skillet.isHeated(level, pos);
      if ((Boolean)state.getValue(SkilletBlock.WATERLOGGED)) {
         if (ItemUtils.doesInventoryHaveItems(skillet.inventory)) {
            ItemUtils.dropItems(level, pos, skillet.inventory);
            skillet.inventoryChanged();
         }
      } else if (isHeated) {
         ItemStack cookingStack = skillet.getStoredStack();
         if (cookingStack.isEmpty()) {
            skillet.cookingTime = 0;
         } else {
            skillet.cookAndOutputItems(cookingStack, level);
         }
      } else if (skillet.cookingTime > 0) {
         skillet.cookingTime = Mth.clamp(skillet.cookingTime - 2, 0, skillet.cookingTimeTotal);
      }
   }

   public static void animationTick(Level level, BlockPos pos, BlockState state, SkilletBlockEntity skillet) {
      if (skillet.isHeated(level, pos) && skillet.hasStoredStack()) {
         RandomSource random = level.random;
         if (random.nextFloat() < 0.2F) {
            double x = pos.getX() + 0.5 + (random.nextDouble() * 0.4 - 0.2);
            double y = pos.getY() + 0.1;
            double z = pos.getZ() + 0.5 + (random.nextDouble() * 0.4 - 0.2);
            double motionY = random.nextBoolean() ? 0.015 : 0.005;
            level.addParticle((ParticleOptions)ModParticleTypes.STEAM.get(), x, y, z, 0.0, motionY, 0.0);
         }

         if (skillet.fireAspectLevel > 0 && random.nextFloat() < skillet.fireAspectLevel * 0.05F) {
            double x = pos.getX() + 0.5 + (random.nextDouble() * 0.4 - 0.2);
            double y = pos.getY() + 0.1;
            double z = pos.getZ() + 0.5 + (random.nextDouble() * 0.4 - 0.2);
            double motionX = level.random.nextFloat() - 0.5F;
            double motionY = level.random.nextFloat() * 0.5F + 0.2F;
            double motionZ = level.random.nextFloat() - 0.5F;
            level.addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, motionX, motionY, motionZ);
         }
      }
   }

   private void cookAndOutputItems(ItemStack cookingStack, Level level) {
      this.cookingTime++;
      if (this.cookingTime >= this.cookingTimeTotal) {
         Optional<RecipeHolder<CampfireCookingRecipe>> recipe = this.getMatchingRecipe(cookingStack);
         if (recipe.isPresent()) {
            ItemStack resultStack = ((CampfireCookingRecipe)recipe.get().value()).assemble(new SingleRecipeInput(cookingStack), level.registryAccess());
            Direction direction = ((Direction)this.getBlockState().getValue(SkilletBlock.FACING)).getClockWise();
            ItemUtils.spawnItemEntity(
               level,
               resultStack.copy(),
               this.worldPosition.getX() + 0.5,
               this.worldPosition.getY() + 0.3,
               this.worldPosition.getZ() + 0.5,
               direction.getStepX() * 0.08F,
               0.25,
               direction.getStepZ() * 0.08F
            );
            this.cookingTime = 0;
            this.inventory.extractItem(0, 1, false);
         }
      }
   }

   public boolean isCooking() {
      return this.isHeated() && this.hasStoredStack();
   }

   public boolean isHeated() {
      return this.level != null ? this.isHeated(this.level, this.worldPosition) : false;
   }

   private Optional<RecipeHolder<CampfireCookingRecipe>> getMatchingRecipe(ItemStack stack) {
      return this.level == null ? Optional.empty() : this.quickCheck.getRecipeFor(new SingleRecipeInput(stack), this.level);
   }

   public void loadAdditional(CompoundTag compound, Provider registries) {
      super.loadAdditional(compound, registries);
      this.inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
      this.cookingTime = compound.getInt("CookTime");
      this.cookingTimeTotal = compound.getInt("CookTimeTotal");
      this.skilletStack = ItemStack.parseOptional(registries, compound.getCompound("Skillet"));
      this.fireAspectLevel = ItemUtils.getValidatedEnchantmentLevel(Enchantments.FIRE_ASPECT, registries, this.skilletStack);
   }

   public void saveAdditional(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
      compound.put("Inventory", this.inventory.serializeNBT(registries));
      compound.putInt("CookTime", this.cookingTime);
      compound.putInt("CookTimeTotal", this.cookingTimeTotal);
      if (!this.skilletStack.isEmpty()) {
         compound.put("Skillet", this.skilletStack.save(registries));
      }
   }

   public ItemStack getSkilletAsItem() {
      return this.skilletStack;
   }

   public void setSkilletItem(ItemStack stack) {
      this.skilletStack = stack.copy();
      this.fireAspectLevel = ItemUtils.getValidatedEnchantmentLevel(Enchantments.FIRE_ASPECT, this.level.registryAccess(), stack);
      this.inventoryChanged();
   }

   public ItemStack addItemToCook(ItemStack addedStack, Player player) {
      Optional<RecipeHolder<CampfireCookingRecipe>> recipe = this.getMatchingRecipe(addedStack);
      if (recipe.isPresent() && this.getStoredStack().isEmpty()) {
         if ((Boolean)this.getBlockState().getValue(SkilletBlock.WATERLOGGED)) {
            player.displayClientMessage(TextUtils.block("skillet.underwater"), true);
            return addedStack;
         }

         boolean wasEmpty = this.getStoredStack().isEmpty();
         ItemStack remainderStack = this.inventory.insertItem(0, addedStack.copy(), false);
         if (!ItemStack.matches(remainderStack, addedStack)) {
            this.cookingTimeTotal = SkilletBlock.getSkilletCookingTime(((CampfireCookingRecipe)recipe.get().value()).getCookingTime(), this.fireAspectLevel);
            this.cookingTime = 0;
            if (wasEmpty && this.level != null && this.isHeated(this.level, this.worldPosition)) {
               this.level
                  .playSound(
                     null,
                     this.worldPosition.getX() + 0.5F,
                     this.worldPosition.getY() + 0.5F,
                     this.worldPosition.getZ() + 0.5F,
                     ModSounds.BLOCK_SKILLET_ADD_FOOD.get(),
                     SoundSource.BLOCKS,
                     0.8F,
                     1.0F
                  );
            }

            return remainderStack;
         }
      } else {
         player.displayClientMessage(TextUtils.block("skillet.invalid_item"), true);
      }

      return addedStack;
   }

   public ItemStack removeItem() {
      return this.inventory.extractItem(0, this.getStoredStack().getMaxStackSize(), false);
   }

   public IItemHandler getInventory() {
      return this.inventory;
   }

   public ItemStack getStoredStack() {
      return this.inventory.getStackInSlot(0);
   }

   public boolean hasStoredStack() {
      return !this.getStoredStack().isEmpty();
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler() {
         protected void onContentsChanged(int slot) {
            SkilletBlockEntity.this.inventoryChanged();
         }
      };
   }

   public void setRemoved() {
      super.setRemoved();
   }

   public void clearContent() {
      ItemUtils.clearItems(this.inventory);
   }
}
