package dev.architectury.fluid.forge;

import com.mojang.serialization.Codec;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import dev.architectury.utils.Amount;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public enum FluidStackImpl implements FluidStack.FluidStackAdapter<net.neoforged.neoforge.fluids.FluidStack> {
   INSTANCE;

   public static Function<FluidStack, Object> toValue;
   public static Function<Object, FluidStack> fromValue;

   public static FluidStack.FluidStackAdapter<Object> adapt(Function<FluidStack, Object> toValue, Function<Object, FluidStack> fromValue) {
      FluidStackImpl.toValue = toValue;
      FluidStackImpl.fromValue = fromValue;
      return INSTANCE;
   }

   public net.neoforged.neoforge.fluids.FluidStack create(Supplier<Fluid> fluid, long amount, @Nullable DataComponentPatch patch) {
      Holder<Fluid> holder = Objects.requireNonNull(fluid).get().builtInRegistryHolder();
      return patch == null
         ? new net.neoforged.neoforge.fluids.FluidStack(holder, Amount.toInt(amount))
         : new net.neoforged.neoforge.fluids.FluidStack(holder, Amount.toInt(amount), patch);
   }

   public Supplier<Fluid> getRawFluidSupplier(net.neoforged.neoforge.fluids.FluidStack object) {
      return () -> (Fluid)object.getFluidHolder().value();
   }

   public Fluid getFluid(net.neoforged.neoforge.fluids.FluidStack object) {
      return object.getFluid();
   }

   public long getAmount(net.neoforged.neoforge.fluids.FluidStack object) {
      return object.getAmount();
   }

   public void setAmount(net.neoforged.neoforge.fluids.FluidStack object, long amount) {
      object.setAmount(Amount.toInt(amount));
   }

   public DataComponentPatch getPatch(net.neoforged.neoforge.fluids.FluidStack value) {
      return value.getComponentsPatch();
   }

   public PatchedDataComponentMap getComponents(net.neoforged.neoforge.fluids.FluidStack value) {
      return value.getComponents();
   }

   public void applyComponents(net.neoforged.neoforge.fluids.FluidStack value, DataComponentPatch patch) {
      value.applyComponents(patch);
   }

   public void applyComponents(net.neoforged.neoforge.fluids.FluidStack value, DataComponentMap patch) {
      value.applyComponents(patch);
   }

   @Nullable
   public <D> D set(net.neoforged.neoforge.fluids.FluidStack value, DataComponentType<? super D> type, @Nullable D component) {
      return (D)value.set(type, component);
   }

   @Nullable
   public <D> D remove(net.neoforged.neoforge.fluids.FluidStack value, DataComponentType<? extends D> type) {
      return (D)value.remove(type);
   }

   @Nullable
   public <D> D update(net.neoforged.neoforge.fluids.FluidStack value, DataComponentType<D> type, D component, UnaryOperator<D> updater) {
      return (D)value.update(type, component, updater);
   }

   @Nullable
   public <D, U> D update(net.neoforged.neoforge.fluids.FluidStack value, DataComponentType<D> type, D component, U updateContext, BiFunction<D, U, D> updater) {
      return (D)value.update(type, component, updateContext, updater);
   }

   public net.neoforged.neoforge.fluids.FluidStack copy(net.neoforged.neoforge.fluids.FluidStack value) {
      return value.copy();
   }

   public int hashCode(net.neoforged.neoforge.fluids.FluidStack value) {
      int code = 1;
      code = 31 * code + value.getFluid().hashCode();
      code = 31 * code + value.getAmount();
      return 31 * code + value.getComponents().hashCode();
   }

   @Override
   public Codec<FluidStack> codec() {
      return net.neoforged.neoforge.fluids.FluidStack.CODEC.xmap(FluidStackHooksForge::fromForge, FluidStackHooksForge::toForge);
   }

   @Override
   public StreamCodec<RegistryFriendlyByteBuf, FluidStack> streamCodec() {
      return net.neoforged.neoforge.fluids.FluidStack.STREAM_CODEC.map(FluidStackHooksForge::fromForge, FluidStackHooksForge::toForge);
   }

   static {
      FluidStack.init();
   }
}
