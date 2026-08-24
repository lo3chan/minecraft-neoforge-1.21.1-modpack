package dev.architectury.fluid;

import com.mojang.serialization.Codec;
import dev.architectury.fluid.forge.FluidStackImpl;
import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class FluidStack implements DataComponentHolder {
   private static final FluidStack.FluidStackAdapter<Object> ADAPTER = adapt(FluidStack::getValue, FluidStack::new);
   private static final FluidStack EMPTY = new FluidStack(() -> Fluids.EMPTY, 0L, DataComponentPatch.EMPTY);
   public static final Codec<FluidStack> CODEC = ADAPTER.codec();
   public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> STREAM_CODEC = ADAPTER.streamCodec();
   private final Object value;

   private FluidStack(Supplier<Fluid> fluid, long amount, DataComponentPatch patch) {
      this(ADAPTER.create(fluid, amount, patch));
   }

   private FluidStack(Object value) {
      this.value = Objects.requireNonNull(value);
   }

   private Object getValue() {
      return this.value;
   }

   @ExpectPlatform
   @Transformed
   private static FluidStack.FluidStackAdapter<Object> adapt(Function<FluidStack, Object> toValue, Function<Object, FluidStack> fromValue) {
      return FluidStackImpl.adapt(toValue, fromValue);
   }

   public static FluidStack empty() {
      return EMPTY;
   }

   public static FluidStack create(Fluid fluid, long amount, DataComponentPatch patch) {
      return fluid != Fluids.EMPTY && amount > 0L ? create((Supplier<Fluid>)(() -> fluid), amount, patch) : empty();
   }

   public static FluidStack create(Fluid fluid, long amount) {
      return create(fluid, amount, DataComponentPatch.EMPTY);
   }

   public static FluidStack create(Supplier<Fluid> fluid, long amount, DataComponentPatch patch) {
      return amount <= 0L ? empty() : new FluidStack(fluid, amount, patch);
   }

   public static FluidStack create(Supplier<Fluid> fluid, long amount) {
      return create(fluid, amount, DataComponentPatch.EMPTY);
   }

   public static FluidStack create(Holder<Fluid> fluid, long amount, DataComponentPatch patch) {
      return create((Fluid)fluid.value(), amount, patch);
   }

   public static FluidStack create(Holder<Fluid> fluid, long amount) {
      return create((Fluid)fluid.value(), amount, DataComponentPatch.EMPTY);
   }

   public static FluidStack create(FluidStack stack, long amount) {
      return create(stack.getRawFluidSupplier(), amount, stack.getPatch());
   }

   public static long bucketAmount() {
      return FluidStackHooks.bucketAmount();
   }

   public Fluid getFluid() {
      return this.isEmpty() ? Fluids.EMPTY : this.getRawFluid();
   }

   @Nullable
   public Fluid getRawFluid() {
      return ADAPTER.getFluid(this.value);
   }

   public Supplier<Fluid> getRawFluidSupplier() {
      return ADAPTER.getRawFluidSupplier(this.value);
   }

   public boolean isEmpty() {
      return this.getRawFluid() == Fluids.EMPTY || ADAPTER.getAmount(this.value) <= 0L;
   }

   public long getAmount() {
      return this.isEmpty() ? 0L : ADAPTER.getAmount(this.value);
   }

   public void setAmount(long amount) {
      ADAPTER.setAmount(this.value, amount);
   }

   public void grow(long amount) {
      this.setAmount(this.getAmount() + amount);
   }

   public void shrink(long amount) {
      this.setAmount(this.getAmount() - amount);
   }

   public DataComponentPatch getPatch() {
      return ADAPTER.getPatch(this.value);
   }

   public PatchedDataComponentMap getComponents() {
      return ADAPTER.getComponents(this.value);
   }

   public void applyComponents(DataComponentPatch patch) {
      ADAPTER.applyComponents(this.value, patch);
   }

   public void applyComponents(DataComponentMap patch) {
      ADAPTER.applyComponents(this.value, patch);
   }

   @Nullable
   public <T> T set(DataComponentType<? super T> type, @Nullable T component) {
      return ADAPTER.set(this.value, type, component);
   }

   @Nullable
   public <T> T remove(DataComponentType<? extends T> type) {
      return ADAPTER.remove(this.value, type);
   }

   @Nullable
   public <T> T update(DataComponentType<T> type, T component, UnaryOperator<T> updater) {
      return ADAPTER.update(this.value, type, component, updater);
   }

   @Nullable
   public <T, U> T update(DataComponentType<T> type, T component, U updateContext, BiFunction<T, U, T> updater) {
      return ADAPTER.update(this.value, type, component, updateContext, updater);
   }

   public Component getName() {
      return FluidStackHooks.getName(this);
   }

   public String getTranslationKey() {
      return FluidStackHooks.getTranslationKey(this);
   }

   public FluidStack copy() {
      return new FluidStack(ADAPTER.copy(this.value));
   }

   @Override
   public int hashCode() {
      return ADAPTER.hashCode(this.value);
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof FluidStack) ? false : this.isFluidStackEqual((FluidStack)o);
   }

   public boolean isFluidStackEqual(FluidStack other) {
      return this.getFluid() == other.getFluid() && this.getAmount() == other.getAmount() && this.isComponentEqual(other);
   }

   public boolean isFluidEqual(FluidStack other) {
      return this.getFluid() == other.getFluid();
   }

   public boolean isComponentEqual(FluidStack other) {
      DataComponentPatch patch = this.getPatch();
      DataComponentPatch otherPatch = other.getPatch();
      return Objects.equals(patch, otherPatch);
   }

   public static FluidStack read(RegistryFriendlyByteBuf buf) {
      return FluidStackHooks.read(buf);
   }

   public static Optional<FluidStack> read(Provider provider, Tag tag) {
      return FluidStackHooks.read(provider, tag);
   }

   public void write(RegistryFriendlyByteBuf buf) {
      FluidStackHooks.write(this, buf);
   }

   public Tag write(Provider provider, Tag tag) {
      return FluidStackHooks.write(provider, this, tag);
   }

   public FluidStack copyWithAmount(long amount) {
      return this.isEmpty() ? this : new FluidStack(this.getRawFluidSupplier(), amount, this.getPatch());
   }

   @Internal
   public static void init() {
   }

   @Internal
   public interface FluidStackAdapter<T> {
      T create(Supplier<Fluid> var1, long var2, @Nullable DataComponentPatch var4);

      Supplier<Fluid> getRawFluidSupplier(T var1);

      Fluid getFluid(T var1);

      long getAmount(T var1);

      void setAmount(T var1, long var2);

      DataComponentPatch getPatch(T var1);

      PatchedDataComponentMap getComponents(T var1);

      void applyComponents(T var1, DataComponentPatch var2);

      void applyComponents(T var1, DataComponentMap var2);

      @Nullable
      <D> D set(T var1, DataComponentType<? super D> var2, @Nullable D var3);

      @Nullable
      <D> D remove(T var1, DataComponentType<? extends D> var2);

      @Nullable
      <D> D update(T var1, DataComponentType<D> var2, D var3, UnaryOperator<D> var4);

      @Nullable
      <D, U> D update(T var1, DataComponentType<D> var2, D var3, U var4, BiFunction<D, U, D> var5);

      T copy(T var1);

      int hashCode(T var1);

      Codec<FluidStack> codec();

      StreamCodec<RegistryFriendlyByteBuf, FluidStack> streamCodec();
   }
}
