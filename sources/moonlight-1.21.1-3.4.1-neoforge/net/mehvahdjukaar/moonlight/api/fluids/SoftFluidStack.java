package net.mehvahdjukaar.moonlight.api.fluids;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.platform.SoftFluidStackImpl;
import net.mehvahdjukaar.moonlight.api.misc.HolderRef;
import net.mehvahdjukaar.moonlight.api.misc.HolderReference;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.PotionBottleType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.api.util.codec.CodecUtils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.fluid.SoftFluidInternal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.DataComponentPatch.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoftFluidStack implements DataComponentHolder {
   public static final Codec<SoftFluidStack> CODEC = RecordCodecBuilder.create(
      i -> i.group(
            SoftFluid.REFERENCE_CODEC.fieldOf("id").forGetter(SoftFluidStack::getHolder),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("count", 1).forGetter(SoftFluidStack::getCount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(stack -> stack.components.asPatch())
         )
         .apply(i, SoftFluidStack::of)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, SoftFluidStack> STREAM_CODEC = StreamCodec.composite(
      SoftFluid.STREAM_CODEC,
      SoftFluidStack::getHolder,
      ByteBufCodecs.VAR_INT,
      SoftFluidStack::getCount,
      DataComponentPatch.STREAM_CODEC,
      s -> s.components.asPatch(),
      SoftFluidStack::of
   );
   private final Holder<SoftFluid> fluidHolder;
   private final SoftFluid fluid;
   private int count;
   @NotNull
   private final PatchedDataComponentMap components;
   private boolean isEmptyCache;
   private final Holder<SoftFluid> myEmptyFluid;

   protected SoftFluidStack(Holder<SoftFluid> fluid, int count, DataComponentPatch components) {
      this.fluidHolder = fluid;
      if (components == null) {
         Moonlight.LOGGER.error("Some mod passed a null components, fix me");
         components = DataComponentPatch.EMPTY;
      }

      this.fluid = (SoftFluid)this.fluidHolder.value();
      this.components = PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, Objects.requireNonNull(components, "component map cant be null"));
      this.count = count;
      this.updateEmpty();
      this.myEmptyFluid = this.haxFindEmpty(this.fluidHolder);
   }

   private Holder<SoftFluid> haxFindEmpty(Holder<SoftFluid> fluidHolder) {
      RegistryLookup<SoftFluid> ra = Utils.hackyFindRegistryOf(fluidHolder, SoftFluidRegistry.KEY);
      return MLBuiltinSoftFluids.EMPTY.lookup(ra);
   }

   public static SoftFluidStack of(Holder<SoftFluid> fluid, int count) {
      return of(fluid, count, DataComponentPatch.EMPTY);
   }

   public static SoftFluidStack of(Holder<SoftFluid> fluid) {
      return of(fluid, 1);
   }

   public static SoftFluidStack bucket(Holder<SoftFluid> fluid) {
      return of(fluid, SoftFluid.BUCKET_COUNT);
   }

   public static SoftFluidStack bowl(Holder<SoftFluid> fluid) {
      return of(fluid, SoftFluid.BOWL_COUNT);
   }

   public static SoftFluidStack bottle(Holder<SoftFluid> fluid) {
      return of(fluid, SoftFluid.BOTTLE_COUNT);
   }

   @Deprecated(
      forRemoval = true
   )
   public static SoftFluidStack fromFluid(Fluid fluid, int amount) {
      return fromFluid(fluid, amount, DataComponentPatch.EMPTY);
   }

   public static SoftFluidStack fromFluid(Fluid fluid, int amount, @NotNull Provider reg) {
      return fromFluid(fluid, amount, DataComponentPatch.EMPTY, reg);
   }

   @Deprecated(
      forRemoval = true
   )
   @NotNull
   public static SoftFluidStack fromFluid(Fluid fluid, int amount, @NotNull DataComponentPatch component) {
      RegistryAccess reg = Utils.hackyGetRegistryAccess();
      return fromFluid(fluid, amount, component, reg);
   }

   @NotNull
   public static SoftFluidStack fromFluid(Fluid fluid, int amount, @NotNull DataComponentPatch component, Provider reg) {
      Holder<SoftFluid> f = SoftFluidInternal.fromVanillaFluid(fluid, reg);
      return f == null ? empty(reg) : of(f, amount, component);
   }

   @Deprecated(
      forRemoval = true
   )
   @NotNull
   public static SoftFluidStack fromFluid(FluidState fluid) {
      return fromFluid(fluid, Utils.hackyGetRegistryAccess());
   }

   @NotNull
   public static SoftFluidStack fromFluid(FluidState fluid, Provider reg) {
      return fluid.is(FluidTags.WATER)
         ? fromFluid(fluid.getType(), 3, DataComponentPatch.EMPTY, reg)
         : fromFluid(fluid.getType(), SoftFluid.BUCKET_COUNT, DataComponentPatch.EMPTY, reg);
   }

   @Deprecated(
      forRemoval = true
   )
   public static SoftFluidStack empty() {
      return of(SoftFluidRegistry.hackyGetEmpty(), 0);
   }

   public static SoftFluidStack empty(Provider lookupProvider) {
      return of(SoftFluidRegistry.getEmpty(lookupProvider), 0);
   }

   public static SoftFluidStack empty(HolderGetter<SoftFluid> reg) {
      return of(SoftFluidRegistry.getEmpty(reg), 0);
   }

   public Component getDisplayName() {
      if (MLBuiltinSoftFluids.POTION.is(this.fluidHolder)) {
         PotionBottleType bottle = PotionBottleType.getOrDefault(this);
         return bottle.getTranslatedName();
      } else {
         return this.fluid().getTranslatedName();
      }
   }

   public static void assertCanSerialize(Provider lookupProvider) {
      CodecUtils.assertHasRegistry(lookupProvider, SoftFluidRegistry.KEY);
   }

   public Tag save(Provider lookupProvider) {
      assertCanSerialize(lookupProvider);
      DataResult<Tag> a = CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this);
      if (a.isSuccess()) {
         return (Tag)a.getOrThrow();
      } else {
         Moonlight.LOGGER.error("Failed to encode fluid stack. HOW??, {}", a.error().get());
         if (PlatHelper.isDev()) {
            a.getOrThrow();
         }

         return new CompoundTag();
      }
   }

   public static SoftFluidStack load(Provider lookupProvider, Tag tag) {
      assertCanSerialize(lookupProvider);
      return (SoftFluidStack)CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
   }

   public boolean is(HolderRef<SoftFluid> fluid) {
      return fluid.is(this.fluidHolder);
   }

   public boolean is(HolderReference<SoftFluid> fluid) {
      return fluid.is(this.fluidHolder);
   }

   public boolean is(TagKey<SoftFluid> tag) {
      return this.getHolder().is(tag);
   }

   public boolean is(ResourceKey<SoftFluid> location) {
      return this.getHolder().is(location);
   }

   @Deprecated(
      forRemoval = true
   )
   public boolean is(SoftFluid fluid) {
      return this.fluid() == fluid;
   }

   public boolean is(Holder<SoftFluid> fluid) {
      return fluid == this.fluidHolder || fluid.is(this.fluidKey());
   }

   @Deprecated(
      forRemoval = true
   )
   private Holder<SoftFluid> getFluid() {
      return this.isEmptyCache ? this.myEmptyFluid : this.fluidHolder;
   }

   public final Holder<SoftFluid> getHolder() {
      return this.isEmptyCache ? this.myEmptyFluid : this.fluidHolder;
   }

   public final SoftFluid fluid() {
      return this.isEmptyCache ? (SoftFluid)this.myEmptyFluid.value() : this.fluid;
   }

   public final ResourceKey<SoftFluid> fluidKey() {
      return (ResourceKey<SoftFluid>)this.getHolder().unwrapKey().get();
   }

   public boolean isEmpty() {
      return this.isEmptyCache;
   }

   protected void updateEmpty() {
      this.isEmptyCache = this.count <= 0 || MLBuiltinSoftFluids.EMPTY.is(this.fluidHolder);
   }

   public int getCount() {
      return this.isEmptyCache ? 0 : this.count;
   }

   public void setCount(int count) {
      if (MLBuiltinSoftFluids.EMPTY.is(this.fluidHolder)) {
         if (PlatHelper.isDev()) {
            throw new AssertionError();
         }
      } else {
         this.count = count;
         this.updateEmpty();
      }
   }

   public void grow(int amount) {
      this.setCount(this.count + amount);
   }

   public void shrink(int amount) {
      this.setCount(this.count - amount);
   }

   public void consume(int amount, @Nullable LivingEntity entity) {
      if (entity == null || !entity.hasInfiniteMaterials()) {
         this.shrink(amount);
      }
   }

   public SoftFluidStack copy() {
      return of(this.getHolder(), this.count, this.components.copy().asPatch());
   }

   public SoftFluidStack copyWithCount(int count) {
      SoftFluidStack stack = this.copy();
      if (!stack.isEmpty()) {
         stack.setCount(count);
      }

      return stack;
   }

   public SoftFluidStack split(int amount) {
      int i = Math.min(amount, this.getCount());
      SoftFluidStack stack = this.copyWithCount(i);
      if (!this.isEmpty()) {
         this.shrink(i);
      }

      return stack;
   }

   public boolean isSameFluidSameComponents(SoftFluidStack other) {
      return !this.is(other.getHolder()) ? false : this.isEmpty() && other.isEmpty() || Objects.equals(this.components, other.components);
   }

   public static int hashFluidAndComponents(@Nullable SoftFluidStack stack) {
      if (stack != null) {
         int i = 31 + stack.getHolder().hashCode();
         return 31 * i + stack.getComponents().hashCode();
      } else {
         return 0;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof SoftFluidStack that)
            ? false
            : this.count == that.count
               && Objects.equals(this.fluidHolder.unwrapKey(), that.fluidHolder.unwrapKey())
               && Objects.equals(this.components, that.components);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.fluidHolder.unwrapKey(), this.count, this.components);
   }

   @Override
   public String toString() {
      return this.getCount() + " " + this.getHolder();
   }

   @Deprecated(
      forRemoval = true
   )
   public static Pair<SoftFluidStack, FluidContainerList.Category> fromItem(ItemStack itemStack) {
      return fromItem(itemStack, Utils.hackyGetRegistryAccess());
   }

   @Nullable
   public static Pair<SoftFluidStack, FluidContainerList.Category> fromItem(ItemStack itemStack, Provider reg) {
      Item filledContainer = itemStack.getItem();
      Holder<SoftFluid> fluid = SoftFluidInternal.fromVanillaItem(filledContainer, reg);
      if (fluid != null && !MLBuiltinSoftFluids.EMPTY.is(fluid)) {
         Optional<FluidContainerList.Category> category = ((SoftFluid)fluid.value()).getContainerList().getCategoryFromFilled(filledContainer);
         if (category.isPresent()) {
            int count = category.get().getCapacity();
            Builder fluidComponents = DataComponentPatch.builder();
            PotionContents potion = (PotionContents)itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (potion.is(Potions.WATER)) {
               fluid = MLBuiltinSoftFluids.WATER.getHolder(reg);
            } else if (potion.hasEffects()) {
               PotionBottleType bottleType = PotionBottleType.getOrDefault(filledContainer);
               fluidComponents.set(MoonlightRegistry.BOTTLE_TYPE.get(), bottleType);
            }

            SoftFluidStack sfStack = of(fluid, count, fluidComponents.build());
            copyComponentsTo(itemStack, sfStack, ((SoftFluid)fluid.value()).getPreservedComponents());
            return Pair.of(sfStack, category.get());
         }
      }

      return null;
   }

   public Pair<ItemStack, FluidContainerList.Category> splitToItem(ItemStack emptyContainer) {
      Pair<ItemStack, FluidContainerList.Category> r = this.toItem(emptyContainer);
      if (r != null) {
         this.shrink(((FluidContainerList.Category)r.getSecond()).getCapacity());
      }

      return r;
   }

   @Deprecated(
      forRemoval = true
   )
   public Pair<ItemStack, FluidContainerList.Category> toItem(ItemStack emptyContainer, boolean dontModifyStack) {
      Pair<ItemStack, FluidContainerList.Category> r = this.toItem(emptyContainer);
      if (r != null && !dontModifyStack) {
         this.shrink(((FluidContainerList.Category)r.getSecond()).getCapacity());
      }

      return r;
   }

   @Nullable
   public Pair<ItemStack, FluidContainerList.Category> toItem(ItemStack emptyContainer) {
      Optional<FluidContainerList.Category> opt = this.fluid().getContainerList().getCategoryFromEmpty(emptyContainer.getItem());
      if (opt.isPresent()) {
         FluidContainerList.Category category = opt.get();
         ItemStack[] filledStacks = this.createFilledStacks(category, true);
         if (filledStacks.length != 0) {
            return Pair.of(filledStacks[0], category);
         }
      }

      return null;
   }

   public Multimap<FluidContainerList.Category, ItemStack> toAllPossibleFilledItems() {
      Multimap<FluidContainerList.Category, ItemStack> result = ArrayListMultimap.create();

      for (FluidContainerList.Category category : this.fluid().getContainerList()) {
         for (ItemStack filled : this.createFilledStacks(category, false)) {
            result.put(category, filled);
         }
      }

      return result;
   }

   private ItemStack[] createFilledStacks(FluidContainerList.Category category, boolean onlyFirst) {
      int shrinkAmount = category.getCapacity();
      if (shrinkAmount > this.getCount()) {
         return new ItemStack[0];
      } else {
         List<ItemStack> results = new ArrayList<>();

         for (ItemLike item : category.getFilledItems()) {
            ItemStack filledStack = new ItemStack(item);
            if (category.getEmptyContainer() == Items.GLASS_BOTTLE && this.is(MLBuiltinSoftFluids.POTION)) {
               PotionBottleType type = PotionBottleType.getOrDefault(this);
               filledStack = type.getDefaultItem();
            }

            if (category.getEmptyContainer() == Items.GLASS_BOTTLE && this.is(MLBuiltinSoftFluids.WATER)) {
               filledStack = PotionContents.createItemStack(Items.POTION, Potions.WATER);
            }

            this.copyComponentsTo(filledStack);
            results.add(filledStack);
            if (onlyFirst) {
               break;
            }
         }

         return results.toArray(new ItemStack[0]);
      }
   }

   public void copyComponentsTo(DataComponentHolder to) {
      copyComponentsTo(this, to, this.fluid.getPreservedComponents());
   }

   protected static void copyComponentsTo(DataComponentHolder from, DataComponentHolder to, HolderSet<DataComponentType<?>> types) {
      for (Holder<DataComponentType<?>> h : types) {
         DataComponentType<?> type = (DataComponentType<?>)h.value();
         copyComponentTo(from, to, type);
      }
   }

   private static <A> void copyComponentTo(DataComponentHolder from, DataComponentHolder to, DataComponentType<A> comp) {
      A componentValue = (A)from.get(comp);
      if (componentValue != null) {
         if (to instanceof ItemStack is) {
            is.set(comp, componentValue);
         } else if (to instanceof SoftFluidStack sf) {
            sf.set(comp, componentValue);
         } else {
            PlatHelper.setComponent(to, comp, componentValue);
         }
      }
   }

   public FluidContainerList getContainerList() {
      return this.fluid().getContainerList();
   }

   public FoodProvider getFoodProvider() {
      return this.fluid().getFoodProvider();
   }

   public boolean isEquivalent(Holder<Fluid> fluid) {
      return this.isEquivalent(fluid, DataComponentPatch.EMPTY);
   }

   public boolean isEquivalent(Holder<Fluid> fluid, DataComponentPatch componentPatch) {
      return this.fluid().isEquivalent(fluid) && Objects.equals(this.components.asPatch(), componentPatch);
   }

   public Holder<Fluid> getVanillaFluid() {
      return this.fluid().getVanillaFluid();
   }

   public int getStillColor(@Nullable BlockAndTintGetter world, @Nullable BlockPos pos) {
      SoftFluid fluid = this.fluid();
      SoftFluid.TintMethod method = fluid.getTintMethod();
      if (method == SoftFluid.TintMethod.NO_TINT) {
         return -1;
      } else {
         int specialColor = SoftFluidColors.getSpecialColor(this, world, pos);
         return specialColor != 0 ? specialColor : fluid.getTintColor();
      }
   }

   public int getFlowingColor(@Nullable BlockAndTintGetter world, @Nullable BlockPos pos) {
      SoftFluid.TintMethod method = this.fluid().getTintMethod();
      return method == SoftFluid.TintMethod.FLOWING ? this.getParticleColor(world, pos) : this.getStillColor(world, pos);
   }

   public int getParticleColor(@Nullable BlockAndTintGetter world, @Nullable BlockPos pos) {
      int tintColor = this.getStillColor(world, pos);
      return tintColor == -1 ? this.fluid().getAverageTextureTintColor() : tintColor;
   }

   @NotNull
   public PatchedDataComponentMap getComponents() {
      return this.components;
   }

   @Nullable
   public <T> T set(DataComponentType<? super T> type, @Nullable T component) {
      return (T)this.components.set(type, component);
   }

   public static SoftFluidStack of(Holder<SoftFluid> var0, int var1, DataComponentPatch var2) {
      return SoftFluidStackImpl.of(var0, var1, var2);
   }
}
