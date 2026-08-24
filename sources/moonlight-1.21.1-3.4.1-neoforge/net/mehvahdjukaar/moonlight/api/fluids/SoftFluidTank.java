package net.mehvahdjukaar.moonlight.api.fluids;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.fluids.platform.SoftFluidTankImpl;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoftFluidTank {
   public static final StreamCodec<RegistryFriendlyByteBuf, SoftFluidTank> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, SoftFluidTank>() {
      public SoftFluidTank decode(RegistryFriendlyByteBuf object) {
         int capacity = (Integer)ByteBufCodecs.INT.decode(object);
         SoftFluidStack stack = (SoftFluidStack)SoftFluidStack.STREAM_CODEC.decode(object);
         return SoftFluidTank.create(stack, capacity, (HolderGetter<SoftFluid>)object.registryAccess().lookup(SoftFluidRegistry.KEY).get());
      }

      public void encode(RegistryFriendlyByteBuf object, SoftFluidTank object2) {
         ByteBufCodecs.INT.encode(object, object2.getCapacity());
         SoftFluidStack.STREAM_CODEC.encode(object, object2.getFluid());
      }
   };
   private static final Codec<Pair<SoftFluidStack, Integer>> CONTENT_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(SoftFluidStack.CODEC.fieldOf("fluid").forGetter(Pair::getFirst), Codec.INT.fieldOf("capacity").forGetter(Pair::getSecond))
         .apply(instance, Pair::of)
   );
   public static final Codec<SoftFluidTank> CODEC = new Codec<SoftFluidTank>() {
      public <T> DataResult<Pair<SoftFluidTank, T>> decode(DynamicOps<T> ops, T input) {
         if (ops instanceof RegistryOps<?> registryOps) {
            Optional<HolderGetter<SoftFluid>> reg = registryOps.getter(SoftFluidRegistry.KEY);
            if (reg.isEmpty()) {
               return DataResult.error(() -> "Failed to find registry from registry lookup!");
            } else {
               DataResult<Pair<Pair<SoftFluidStack, Integer>, T>> content = SoftFluidTank.CONTENT_CODEC.decode(ops, input);
               Pair<SoftFluidStack, Integer> c = (Pair<SoftFluidStack, Integer>)((Pair)content.getOrThrow()).getFirst();
               return content.map(p -> Pair.of(SoftFluidTank.create((SoftFluidStack)c.getFirst(), (Integer)c.getSecond(), reg.get()), p.getSecond()));
            }
         } else {
            return DataResult.error(() -> "Registry ops required!");
         }
      }

      public <T> DataResult<T> encode(SoftFluidTank input, DynamicOps<T> ops, T prefix) {
         return SoftFluidTank.CONTENT_CODEC.encode(Pair.of(input.getFluid(), input.getCapacity()), ops, prefix);
      }
   };
   public static final int BOTTLE_COUNT = 1;
   public static final int BOWL_COUNT = 2;
   public static final int BUCKET_COUNT = 4;
   private final HolderGetter<SoftFluid> fluidReg;
   protected final int capacity;
   @NotNull
   protected SoftFluidStack fluidStack;
   protected int stillTintCache = 0;
   protected int flowingTintCache = 0;
   protected int particleTintCache = 0;
   protected boolean needsColorRefresh = true;

   protected SoftFluidTank(int capacity, Provider registries) {
      this(capacity, registries.lookupOrThrow(SoftFluidRegistry.KEY));
   }

   protected SoftFluidTank(int capacity, HolderGetter<SoftFluid> fluidReg) {
      this.capacity = capacity;
      this.fluidReg = fluidReg;
      this.fluidStack = SoftFluidStack.empty(fluidReg);
   }

   @Deprecated(
      forRemoval = true
   )
   protected SoftFluidTank(int capacity) {
      this(capacity, Utils.hackyGetRegistryAccess());
   }

   public static SoftFluidTank create(int capacity, Provider registries) {
      return create(capacity, registries.lookupOrThrow(SoftFluidRegistry.KEY));
   }

   @Deprecated(
      forRemoval = true
   )
   public static SoftFluidTank create(int capacity) {
      return create(capacity, SoftFluidRegistry.get(Utils.hackyGetRegistryAccess()).asLookup());
   }

   @Deprecated(
      forRemoval = true
   )
   public static SoftFluidTank create(SoftFluidStack stack, int capacity) {
      return create(stack, capacity, SoftFluidRegistry.get(Utils.hackyGetRegistryAccess()).asLookup());
   }

   public static SoftFluidTank create(SoftFluidStack stack, int capacity, HolderGetter<SoftFluid> fluidReg) {
      SoftFluidTank tank = create(capacity, fluidReg);
      tank.setFluid(stack);
      return tank;
   }

   public SoftFluidTank makeCopy() {
      SoftFluidTank tank = create(this.capacity, this.fluidReg);
      tank.copyContent(this);
      return tank;
   }

   public boolean interactWithPlayer(Player player, InteractionHand hand, @Nullable Level world, @Nullable BlockPos pos) {
      ItemStack handStack = player.getItemInHand(hand);
      ItemStack returnStack = this.interactWithItem(handStack, world, pos, false);
      if (returnStack != null) {
         Utils.swapItem(player, hand, returnStack);
         if (!handStack.isEmpty()) {
            player.awardStat(Stats.ITEM_USED.get(handStack.getItem()));
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public ItemStack interactWithItem(ItemStack stack, Level world, @Nullable BlockPos pos, boolean simulate) {
      InteractionResultHolder<ItemStack> fillResult = this.fillItem(stack, world, pos, simulate);
      if (fillResult.getResult().consumesAction()) {
         return (ItemStack)fillResult.getObject();
      } else {
         InteractionResultHolder<ItemStack> drainResult = this.drainItem(stack, world, pos, simulate);
         return drainResult.getResult().consumesAction() ? (ItemStack)drainResult.getObject() : null;
      }
   }

   public InteractionResultHolder<ItemStack> drainItem(ItemStack filledContainerStack, @Nullable Level world, @Nullable BlockPos pos, boolean simulate) {
      return this.drainItem(filledContainerStack, world, pos, simulate, true);
   }

   public InteractionResultHolder<ItemStack> drainItem(ItemStack filledContainer, Level level, @Nullable BlockPos pos, boolean simulate, boolean playSound) {
      Pair<SoftFluidStack, FluidContainerList.Category> extracted = SoftFluidStack.fromItem(filledContainer, level.registryAccess());
      if (extracted == null) {
         return InteractionResultHolder.pass(ItemStack.EMPTY);
      } else {
         SoftFluidStack fluidStack = (SoftFluidStack)extracted.getFirst();
         if (this.addFluid(fluidStack, true) == fluidStack.getCount()) {
            FluidContainerList.Category category = (FluidContainerList.Category)extracted.getSecond();
            ItemStack emptyContainer = category.getEmptyContainer().getDefaultInstance();
            if (!simulate) {
               this.addFluid(fluidStack, false);
               SoundEvent sound = category.getEmptySound();
               if (sound != null && pos != null) {
                  level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
               }
            }

            return InteractionResultHolder.sidedSuccess(emptyContainer, level.isClientSide);
         } else {
            return InteractionResultHolder.pass(ItemStack.EMPTY);
         }
      }
   }

   public InteractionResultHolder<ItemStack> fillItem(ItemStack emptyContainer, @Nullable Level world, @Nullable BlockPos pos, boolean simulate) {
      return this.fillItem(emptyContainer, world, pos, simulate, true);
   }

   public InteractionResultHolder<ItemStack> fillItem(ItemStack emptyContainer, Level level, @Nullable BlockPos pos, boolean simulate, boolean playSound) {
      Pair<ItemStack, FluidContainerList.Category> pair = this.fluidStack.splitToItem(emptyContainer);
      if (pair != null) {
         FluidContainerList.Category category = (FluidContainerList.Category)pair.getSecond();
         SoundEvent sound = category.getEmptySound();
         if (sound != null && pos != null) {
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         return InteractionResultHolder.sidedSuccess((ItemStack)pair.getFirst(), level.isClientSide);
      } else {
         return InteractionResultHolder.pass(ItemStack.EMPTY);
      }
   }

   protected void addFluidOntoExisting(SoftFluidStack stack) {
      this.fluidStack.grow(stack.getCount());
   }

   @Nullable
   public InteractionResultHolder<ItemStack> fillBottle(Level world, BlockPos pos) {
      return this.fillItem(Items.GLASS_BOTTLE.getDefaultInstance(), world, pos, false);
   }

   @Nullable
   public InteractionResultHolder<ItemStack> fillBucket(Level world, BlockPos pos) {
      return this.fillItem(Items.BUCKET.getDefaultInstance(), world, pos, false);
   }

   @Nullable
   public InteractionResultHolder<ItemStack> fillBowl(Level world, BlockPos pos) {
      return this.fillItem(Items.BOWL.getDefaultInstance(), world, pos, false);
   }

   public boolean isFluidCompatible(SoftFluidStack fluidStack) {
      return this.fluidStack.isSameFluidSameComponents(fluidStack) || this.isEmpty();
   }

   public int addFluid(SoftFluidStack stack, boolean simulate) {
      if (!this.isFluidCompatible(stack)) {
         return 0;
      } else {
         int space = this.getSpace();
         if (space == 0) {
            return 0;
         } else {
            int amount = Math.min(space, stack.getCount());
            if (simulate) {
               return amount;
            } else {
               SoftFluidStack toAdd = stack.split(amount);
               if (this.isEmpty()) {
                  this.setFluid(toAdd);
               } else {
                  this.addFluidOntoExisting(toAdd);
               }

               return amount;
            }
         }
      }
   }

   public SoftFluidStack removeFluid(int amount, boolean simulate) {
      if (this.isEmpty()) {
         return SoftFluidStack.empty(this.fluidReg);
      } else {
         int toRemove = Math.min(amount, this.fluidStack.getCount());
         SoftFluidStack stack = this.fluidStack.copyWithCount(toRemove);
         if (!simulate) {
            this.fluidStack.shrink(toRemove);
         }

         return stack;
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public boolean transferFluid(SoftFluidTank destination) {
      return this.transferFluid(destination, 1);
   }

   @Deprecated(
      forRemoval = true
   )
   public boolean transferFluid(SoftFluidTank destination, int amount) {
      if (this.isEmpty()) {
         return false;
      } else {
         SoftFluidStack removed = this.removeFluid(amount, false);
         if (destination.addFluid(removed, true) == removed.getCount()) {
            destination.addFluid(removed, false);
            return true;
         } else {
            return false;
         }
      }
   }

   public int getSpace() {
      return Math.max(0, this.capacity - this.fluidStack.getCount());
   }

   public int getFluidCount() {
      return this.fluidStack.getCount();
   }

   public boolean isFull() {
      return this.fluidStack.getCount() == this.capacity;
   }

   public boolean isEmpty() {
      return this.fluidStack.isEmpty();
   }

   public float getHeight(float maxHeight) {
      return maxHeight * this.fluidStack.getCount() / this.capacity;
   }

   public int getComparatorOutput() {
      float f = (float)this.fluidStack.getCount() / this.capacity;
      return Mth.floor(f * 14.0F) + 1;
   }

   public SoftFluidStack getFluid() {
      return this.fluidStack;
   }

   public SoftFluid getFluidValue() {
      return (SoftFluid)this.fluidStack.getHolder().value();
   }

   public void setFluid(SoftFluidStack fluid) {
      this.fluidStack = fluid;
      this.refreshTintCache();
   }

   public void refreshTintCache() {
      this.stillTintCache = 0;
      this.needsColorRefresh = true;
   }

   private void fillCount() {
      this.fluidStack.setCount(this.capacity);
   }

   public void clear() {
      this.setFluid(SoftFluidStack.empty(this.fluidReg));
   }

   public void copyContent(SoftFluidTank other) {
      SoftFluidStack stack = other.getFluid();
      this.setFluid(stack.copyWithCount(Math.min(this.capacity, stack.getCount())));
   }

   public int getCapacity() {
      return this.capacity;
   }

   public void capCapacity() {
      this.fluidStack.setCount(Mth.clamp(this.fluidStack.getCount(), 0, this.capacity));
   }

   private void cacheColors(@Nullable BlockAndTintGetter world, @Nullable BlockPos pos) {
      this.stillTintCache = this.fluidStack.getStillColor(world, pos);
      this.flowingTintCache = this.fluidStack.getFlowingColor(world, pos);
      this.particleTintCache = this.fluidStack.getParticleColor(world, pos);
      this.needsColorRefresh = false;
   }

   public int getCachedStillColor(@Nullable BlockAndTintGetter world, @Nullable BlockPos pos) {
      if (this.needsColorRefresh) {
         this.cacheColors(world, pos);
      }

      return this.stillTintCache;
   }

   public int getCachedFlowingColor(@Nullable BlockAndTintGetter world, @Nullable BlockPos pos) {
      if (this.needsColorRefresh) {
         this.cacheColors(world, pos);
      }

      return this.flowingTintCache;
   }

   public int getCachedParticleColor(@Nullable BlockAndTintGetter world, @Nullable BlockPos pos) {
      if (this.needsColorRefresh) {
         this.cacheColors(world, pos);
      }

      return this.particleTintCache;
   }

   public boolean containsFood() {
      return !this.fluidStack.getFoodProvider().isEmpty();
   }

   public void load(CompoundTag compound, Provider registries) {
      CompoundTag fluidTag = compound.getCompound("fluid");
      CompoundTag backCompat = compound.getCompound("FluidHolder");
      if (!backCompat.isEmpty()) {
         fluidTag = backCompat;
      }

      if (!fluidTag.isEmpty()) {
         this.setFluid(SoftFluidStack.load(registries, fluidTag));
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public void load(CompoundTag compound) {
      this.load(compound, Utils.hackyGetRegistryAccess());
   }

   @Deprecated(
      forRemoval = true
   )
   public CompoundTag save(CompoundTag compound) {
      this.save(compound, Utils.hackyGetRegistryAccess());
      return compound;
   }

   public void save(CompoundTag compound, Provider registries) {
      this.setFluid(this.fluidStack);
      Tag tag = this.fluidStack.save(registries);
      compound.put("fluid", tag);
   }

   public boolean tryDrinkUpFluid(Player player, Level world) {
      if (!this.isEmpty() && this.containsFood() && this.fluidStack.getFoodProvider().consume(player, world, this.fluidStack::copyComponentsTo)) {
         this.fluidStack.shrink(1);
         return true;
      } else {
         return false;
      }
   }

   public static int getLiquidCountFromItem(Item i) {
      if (i == Items.GLASS_BOTTLE) {
         return 1;
      } else if (i == Items.BOWL) {
         return 2;
      } else {
         return i == Items.BUCKET ? 4 : 0;
      }
   }

   public static SoftFluidTank create(int var0, HolderGetter<SoftFluid> var1) {
      return SoftFluidTankImpl.create(var0, var1);
   }
}
