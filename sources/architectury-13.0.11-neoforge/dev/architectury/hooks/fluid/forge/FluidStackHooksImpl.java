package dev.architectury.hooks.fluid.forge;

import com.mojang.logging.LogUtils;
import dev.architectury.fluid.FluidStack;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class FluidStackHooksImpl {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static Component getName(FluidStack stack) {
      return stack.getFluid().getFluidType().getDescription(FluidStackHooksForge.toForge(stack));
   }

   public static String getTranslationKey(FluidStack stack) {
      return stack.getFluid().getFluidType().getDescriptionId(FluidStackHooksForge.toForge(stack));
   }

   public static FluidStack read(RegistryFriendlyByteBuf buf) {
      return (FluidStack)FluidStack.STREAM_CODEC.decode(buf);
   }

   public static void write(FluidStack stack, RegistryFriendlyByteBuf buf) {
      FluidStack.STREAM_CODEC.encode(buf, stack);
   }

   public static Optional<FluidStack> read(Provider provider, Tag tag) {
      return FluidStack.CODEC
         .parse(provider.createSerializationContext(NbtOps.INSTANCE), tag)
         .resultOrPartial(string -> LOGGER.error("Tried to load invalid fluid stack: '{}'", string));
   }

   public static FluidStack readOptional(Provider provider, CompoundTag tag) {
      return tag.isEmpty() ? FluidStack.empty() : read(provider, tag).orElse(FluidStack.empty());
   }

   public static Tag write(Provider provider, FluidStack stack, Tag tag) {
      return (Tag)FluidStack.CODEC.encode(stack, provider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow(IllegalStateException::new);
   }

   public static long bucketAmount() {
      return 1000L;
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   public static TextureAtlasSprite getStillTexture(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, FluidState state) {
      if (state.getType() == Fluids.EMPTY) {
         return null;
      } else {
         ResourceLocation texture = IClientFluidTypeExtensions.of(state).getStillTexture(state, level, pos);
         return (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   public static TextureAtlasSprite getStillTexture(FluidStack stack) {
      if (stack.getFluid() == Fluids.EMPTY) {
         return null;
      } else {
         ResourceLocation texture = IClientFluidTypeExtensions.of(stack.getFluid()).getStillTexture(FluidStackHooksForge.toForge(stack));
         return (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   public static TextureAtlasSprite getStillTexture(Fluid fluid) {
      if (fluid == Fluids.EMPTY) {
         return null;
      } else {
         ResourceLocation texture = IClientFluidTypeExtensions.of(fluid).getStillTexture();
         return (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   public static TextureAtlasSprite getFlowingTexture(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, FluidState state) {
      if (state.getType() == Fluids.EMPTY) {
         return null;
      } else {
         ResourceLocation texture = IClientFluidTypeExtensions.of(state).getFlowingTexture(state, level, pos);
         return (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   public static TextureAtlasSprite getFlowingTexture(FluidStack stack) {
      if (stack.getFluid() == Fluids.EMPTY) {
         return null;
      } else {
         ResourceLocation texture = IClientFluidTypeExtensions.of(stack.getFluid()).getFlowingTexture(FluidStackHooksForge.toForge(stack));
         return (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   public static TextureAtlasSprite getFlowingTexture(Fluid fluid) {
      if (fluid == Fluids.EMPTY) {
         return null;
      } else {
         ResourceLocation texture = IClientFluidTypeExtensions.of(fluid).getFlowingTexture();
         return (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static int getColor(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, FluidState state) {
      return state.getType() == Fluids.EMPTY ? -1 : IClientFluidTypeExtensions.of(state).getTintColor(state, level, pos);
   }

   @OnlyIn(Dist.CLIENT)
   public static int getColor(FluidStack stack) {
      return stack.getFluid() == Fluids.EMPTY ? -1 : IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(FluidStackHooksForge.toForge(stack));
   }

   @OnlyIn(Dist.CLIENT)
   public static int getColor(Fluid fluid) {
      return fluid == Fluids.EMPTY ? -1 : IClientFluidTypeExtensions.of(fluid).getTintColor();
   }

   public static int getLuminosity(FluidStack fluid, @Nullable Level level, @Nullable BlockPos pos) {
      return fluid.getFluid().getFluidType().getLightLevel(FluidStackHooksForge.toForge(fluid));
   }

   @Deprecated(
      forRemoval = true
   )
   public static int getLuminosity(Fluid fluid, @Nullable Level level, @Nullable BlockPos pos) {
      if (level != null && pos != null) {
         FluidState state = level.getFluidState(pos);
         return fluid.getFluidType().getLightLevel(state, level, pos);
      } else {
         return fluid.getFluidType().getLightLevel();
      }
   }

   public static int getTemperature(FluidStack fluid, @Nullable Level level, @Nullable BlockPos pos) {
      return fluid.getFluid().getFluidType().getTemperature(FluidStackHooksForge.toForge(fluid));
   }

   public static int getTemperature(Fluid fluid, @Nullable Level level, @Nullable BlockPos pos) {
      if (level != null && pos != null) {
         FluidState state = level.getFluidState(pos);
         return fluid.getFluidType().getTemperature(state, level, pos);
      } else {
         return fluid.getFluidType().getTemperature();
      }
   }

   public static int getViscosity(FluidStack fluid, @Nullable Level level, @Nullable BlockPos pos) {
      return fluid.getFluid().getFluidType().getViscosity(FluidStackHooksForge.toForge(fluid));
   }

   public static int getViscosity(Fluid fluid, @Nullable Level level, @Nullable BlockPos pos) {
      if (level != null && pos != null) {
         FluidState state = level.getFluidState(pos);
         return fluid.getFluidType().getViscosity(state, level, pos);
      } else {
         return fluid.getFluidType().getViscosity();
      }
   }
}
