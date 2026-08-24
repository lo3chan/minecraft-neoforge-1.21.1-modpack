package dev.architectury.hooks.fluid;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksImpl;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import java.util.Optional;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class FluidStackHooks {
   private FluidStackHooks() {
   }

   @ExpectPlatform
   @Transformed
   public static Component getName(FluidStack stack) {
      return FluidStackHooksImpl.getName(stack);
   }

   @ExpectPlatform
   @Transformed
   public static String getTranslationKey(FluidStack stack) {
      return FluidStackHooksImpl.getTranslationKey(stack);
   }

   @ExpectPlatform
   @Transformed
   public static FluidStack read(RegistryFriendlyByteBuf buf) {
      return FluidStackHooksImpl.read(buf);
   }

   @ExpectPlatform
   @Transformed
   public static void write(FluidStack stack, RegistryFriendlyByteBuf buf) {
      FluidStackHooksImpl.write(stack, buf);
   }

   @ExpectPlatform
   @Transformed
   public static Optional<FluidStack> read(Provider provider, Tag tag) {
      return FluidStackHooksImpl.read(provider, tag);
   }

   @ExpectPlatform
   @Transformed
   public static FluidStack readOptional(Provider provider, CompoundTag tag) {
      return FluidStackHooksImpl.readOptional(provider, tag);
   }

   @ExpectPlatform
   @Transformed
   public static Tag write(Provider provider, FluidStack stack, Tag tag) {
      return FluidStackHooksImpl.write(provider, stack, tag);
   }

   @ExpectPlatform
   @Transformed
   public static long bucketAmount() {
      return FluidStackHooksImpl.bucketAmount();
   }

   @OnlyIn(Dist.CLIENT)
   @ExpectPlatform
   @Nullable
   @Transformed
   public static TextureAtlasSprite getStillTexture(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, FluidState state) {
      return FluidStackHooksImpl.getStillTexture(level, pos, state);
   }

   @OnlyIn(Dist.CLIENT)
   @ExpectPlatform
   @Nullable
   @Transformed
   public static TextureAtlasSprite getStillTexture(FluidStack stack) {
      return FluidStackHooksImpl.getStillTexture(stack);
   }

   @OnlyIn(Dist.CLIENT)
   @ExpectPlatform
   @Nullable
   @Transformed
   public static TextureAtlasSprite getStillTexture(Fluid fluid) {
      return FluidStackHooksImpl.getStillTexture(fluid);
   }

   @OnlyIn(Dist.CLIENT)
   @ExpectPlatform
   @Nullable
   @Transformed
   public static TextureAtlasSprite getFlowingTexture(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, FluidState state) {
      return FluidStackHooksImpl.getFlowingTexture(level, pos, state);
   }

   @OnlyIn(Dist.CLIENT)
   @ExpectPlatform
   @Nullable
   @Transformed
   public static TextureAtlasSprite getFlowingTexture(FluidStack stack) {
      return FluidStackHooksImpl.getFlowingTexture(stack);
   }

   @OnlyIn(Dist.CLIENT)
   @ExpectPlatform
   @Nullable
   @Transformed
   public static TextureAtlasSprite getFlowingTexture(Fluid fluid) {
      return FluidStackHooksImpl.getFlowingTexture(fluid);
   }

   @OnlyIn(Dist.CLIENT)
   @ExpectPlatform
   @Transformed
   public static int getColor(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, FluidState state) {
      return FluidStackHooksImpl.getColor(level, pos, state);
   }

   @OnlyIn(Dist.CLIENT)
   @ExpectPlatform
   @Transformed
   public static int getColor(FluidStack stack) {
      return FluidStackHooksImpl.getColor(stack);
   }

   @OnlyIn(Dist.CLIENT)
   @ExpectPlatform
   @Transformed
   public static int getColor(Fluid fluid) {
      return FluidStackHooksImpl.getColor(fluid);
   }

   @ExpectPlatform
   @Transformed
   public static int getLuminosity(FluidStack fluid, @Nullable Level level, @Nullable BlockPos pos) {
      return FluidStackHooksImpl.getLuminosity(fluid, level, pos);
   }

   @ExpectPlatform
   @Transformed
   public static int getLuminosity(Fluid fluid, @Nullable Level level, @Nullable BlockPos pos) {
      return FluidStackHooksImpl.getLuminosity(fluid, level, pos);
   }

   @ExpectPlatform
   @Transformed
   public static int getTemperature(FluidStack fluid, @Nullable Level level, @Nullable BlockPos pos) {
      return FluidStackHooksImpl.getTemperature(fluid, level, pos);
   }

   @ExpectPlatform
   @Transformed
   public static int getTemperature(Fluid fluid, @Nullable Level level, @Nullable BlockPos pos) {
      return FluidStackHooksImpl.getTemperature(fluid, level, pos);
   }

   @ExpectPlatform
   @Transformed
   public static int getViscosity(FluidStack fluid, @Nullable Level level, @Nullable BlockPos pos) {
      return FluidStackHooksImpl.getViscosity(fluid, level, pos);
   }

   @ExpectPlatform
   @Transformed
   public static int getViscosity(Fluid fluid, @Nullable Level level, @Nullable BlockPos pos) {
      return FluidStackHooksImpl.getViscosity(fluid, level, pos);
   }
}
