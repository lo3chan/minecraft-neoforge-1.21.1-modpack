package net.mehvahdjukaar.amendments.common.tile;

import com.google.common.base.Preconditions;
import java.util.List;
import net.mehvahdjukaar.amendments.common.LiquidMixer;
import net.mehvahdjukaar.amendments.common.block.DyeCauldronBlock;
import net.mehvahdjukaar.amendments.common.block.LiquidCauldronBlock;
import net.mehvahdjukaar.amendments.common.block.ModCauldronBlock;
import net.mehvahdjukaar.amendments.common.item.DyeBottleItem;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.block.ISoftFluidTankProvider;
import net.mehvahdjukaar.moonlight.api.client.model.IExtraModelDataProvider;
import net.mehvahdjukaar.moonlight.api.client.model.ModelDataKey;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData.Builder;
import net.mehvahdjukaar.moonlight.api.fluids.MLBuiltinSoftFluids;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidTank;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.PotionBottleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public class LiquidCauldronBlockTile extends BlockEntity implements IExtraModelDataProvider, ISoftFluidTankProvider {
   public static final ModelDataKey<ResourceKey<SoftFluid>> FLUID = new ModelDataKey(ResourceKey.class);
   public static final ModelDataKey<Boolean> GLOWING = new ModelDataKey(Boolean.class);
   @Nullable
   private SoftFluidTank fluidTank;
   private boolean hasGlowInk = false;

   public LiquidCauldronBlockTile(BlockPos blockPos, BlockState blockState) {
      super(ModRegistry.LIQUID_CAULDRON_TILE.get(), blockPos, blockState);
   }

   public void addExtraModelData(Builder builder) {
      builder.with(FLUID, (ResourceKey)this.getSoftFluidTank().getFluid().getHolder().unwrapKey().get());
      builder.with(GLOWING, this.hasGlowInk);
   }

   public void setLevel(Level level) {
      super.setLevel(level);
      this.getOrCreateTank(level.registryAccess());
   }

   public SoftFluidTank getSoftFluidTank() {
      return (SoftFluidTank)Preconditions.checkNotNull(this.fluidTank, "Accessing cauldron tank before loadAdditional is was called!");
   }

   private SoftFluidTank getOrCreateTank(Provider registries) {
      if (this.fluidTank == null) {
         this.fluidTank = this.getBlockState().getBlock() instanceof DyeCauldronBlock
            ? this.createCauldronDyeTank(registries)
            : this.createCauldronLiquidTank(registries);
      }

      return this.fluidTank;
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.hasGlowInk = tag.getBoolean("glow_ink");
      this.getOrCreateTank(registries).load(tag, registries);
      if (this.level != null && this.level.isClientSide) {
         this.getSoftFluidTank().refreshTintCache();
         this.requestModelReload();
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      if (this.hasGlowInk) {
         tag.putBoolean("glow_ink", true);
      }

      this.getSoftFluidTank().save(tag, registries);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   public void setChanged() {
      if (this.level != null) {
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         BlockState state = this.getBlockState();
         if (state.getBlock() instanceof ModCauldronBlock cb) {
            state = cb.updateStateOnFluidChange(state, this.level, this.worldPosition, this.getSoftFluidTank().getFluid());
         }

         if (state != this.getBlockState()) {
            this.level.setBlockAndUpdate(this.worldPosition, state);
         }

         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), state, 2);
         super.setChanged();
      }
   }

   public boolean interactWithPlayerItem(Player player, InteractionHand hand, ItemStack stack) {
      if (this.getSoftFluidTank().interactWithPlayer(player, hand, this.level, this.worldPosition)) {
         this.level.gameEvent(player, GameEvent.BLOCK_CHANGE, this.worldPosition);
         this.setChanged();
         this.maybeSendPotionMixMessage(this.getSoftFluidTank().getFluid(), player);
         return true;
      } else {
         return false;
      }
   }

   public void consumeOneLayer() {
      this.getSoftFluidTank().getFluid().shrink(1);
      this.setChanged();
   }

   public void maybeSendPotionMixMessage(SoftFluidStack stack, Player player) {
      if (stack.is(MLBuiltinSoftFluids.POTION)) {
         List<MobEffectInstance> potionEffects = LiquidCauldronBlock.getAllPotionEffects(stack);
         int potionEffectAmount = potionEffects.size();
         if (potionEffectAmount == CommonConfigs.POTION_MIXING_LIMIT.get()) {
            player.displayClientMessage(Component.translatable("message.amendments.cauldron"), true);
         }
      }
   }

   public boolean isGlowing() {
      return this.hasGlowInk;
   }

   public void setGlowing(boolean b) {
      this.hasGlowInk = b;
      this.setChanged();
   }

   private SoftFluidTank createCauldronLiquidTank(Provider ra) {
      return new SoftFluidTank(PlatHelper.getPlatform().isFabric() ? 3 : 4, ra) {
         private boolean canMixPotions() {
            CommonConfigs.MixingMode config = CommonConfigs.POTION_MIXING.get();
            return config == CommonConfigs.MixingMode.ON
               || config == CommonConfigs.MixingMode.ONLY_BOILING
                  && (Boolean)LiquidCauldronBlockTile.this.getBlockState().getValue(LiquidCauldronBlock.BOILING);
         }

         public boolean isFluidCompatible(SoftFluidStack fluidStack) {
            if (fluidStack.is(MLBuiltinSoftFluids.WATER)) {
               return false;
            } else {
               return this.canMixPotions() && fluidStack.is(MLBuiltinSoftFluids.POTION) && fluidStack.is(this.getFluid().getHolder())
                  ? this.fluidStack.getOrDefault((DataComponentType)MoonlightRegistry.BOTTLE_TYPE.get(), PotionBottleType.REGULAR)
                     == fluidStack.getOrDefault((DataComponentType)MoonlightRegistry.BOTTLE_TYPE.get(), PotionBottleType.REGULAR)
                  : super.isFluidCompatible(fluidStack);
            }
         }

         protected void addFluidOntoExisting(SoftFluidStack incoming) {
            if (this.canMixPotions() && incoming.is(MLBuiltinSoftFluids.POTION)) {
               SoftFluidStack newStack = LiquidMixer.mixPotions(this.fluidStack, incoming);
               if (newStack != null) {
                  this.setFluid(newStack);
                  this.needsColorRefresh = true;
               }
            }

            super.addFluidOntoExisting(incoming);
         }
      };
   }

   public SoftFluidTank createCauldronDyeTank(Provider ra) {
      return new SoftFluidTank(3, ra) {
         public boolean isFluidCompatible(SoftFluidStack fluidStack) {
            return fluidStack.is(ModRegistry.DYE_SOFT_FLUID) && fluidStack.is(this.getFluid().getHolder()) ? true : super.isFluidCompatible(fluidStack);
         }

         protected void addFluidOntoExisting(SoftFluidStack fluidStack) {
            if (fluidStack.is(ModRegistry.DYE_SOFT_FLUID)) {
               SoftFluidStack mixed = LiquidMixer.mixDye(this.fluidStack, fluidStack);
               if (mixed != null) {
                  this.setFluid(mixed);
                  this.needsColorRefresh = true;
               }
            }

            super.addFluidOntoExisting(fluidStack);
         }

         @Nullable
         public ItemStack interactWithItem(ItemStack stack, Level world, @Nullable BlockPos pos, boolean simulate) {
            if (stack.getItem() instanceof DyeItem di) {
               if (!simulate) {
                  this.addDyeItem(di, world, pos);
               }

               return ItemStack.EMPTY;
            } else {
               return super.interactWithItem(stack, world, pos, simulate);
            }
         }

         private void addDyeItem(DyeItem dyeItem, Level world, @Nullable BlockPos pos) {
            SoftFluidStack fluid = this.getFluid();
            if (!world.isClientSide()) {
               int count = fluid.getCount();
               if (count == 3) {
                  fluid.setCount(2);
               }

               SoftFluidStack dummyStack = DyeBottleItem.createFluidStack(dyeItem.getDyeColor(), 1, world);
               SoftFluidStack newFluid = LiquidMixer.mixDye(fluid, dummyStack);
               if (newFluid != null) {
                  newFluid.setCount(count);
                  this.setFluid(newFluid);
               }
            }

            if (pos != null) {
               world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
               world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.3F);
            }
         }
      };
   }
}
