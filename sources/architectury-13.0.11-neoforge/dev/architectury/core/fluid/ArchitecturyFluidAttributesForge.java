package dev.architectury.core.fluid;

import com.google.common.base.MoreObjects;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;
import org.jetbrains.annotations.Nullable;

class ArchitecturyFluidAttributesForge extends FluidType {
   private final ArchitecturyFluidAttributes attributes;
   private final String defaultTranslationKey;

   public ArchitecturyFluidAttributesForge(Properties builder, Fluid fluid, ArchitecturyFluidAttributes attributes) {
      super(addArchIntoBuilder(builder, attributes));
      this.attributes = attributes;
      this.defaultTranslationKey = Util.makeDescriptionId("fluid", BuiltInRegistries.FLUID.getKey(fluid));
   }

   private static Properties addArchIntoBuilder(Properties builder, ArchitecturyFluidAttributes attributes) {
      builder.lightLevel(attributes.getLuminosity())
         .density(attributes.getDensity())
         .temperature(attributes.getTemperature())
         .rarity(attributes.getRarity())
         .canConvertToSource(attributes.canConvertToSource())
         .viscosity(attributes.getViscosity());
      return builder;
   }

   public ItemStack getBucket(FluidStack stack) {
      Item item = this.attributes.getBucketItem();
      return item == null ? super.getBucket(stack) : new ItemStack(item);
   }

   public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
      consumer.accept(new IClientFluidTypeExtensions() {
         public int getTintColor() {
            return ArchitecturyFluidAttributesForge.this.attributes.getColor();
         }

         public ResourceLocation getStillTexture() {
            return ArchitecturyFluidAttributesForge.this.attributes.getSourceTexture();
         }

         public ResourceLocation getFlowingTexture() {
            return ArchitecturyFluidAttributesForge.this.attributes.getFlowingTexture();
         }

         @Nullable
         public ResourceLocation getOverlayTexture() {
            return ArchitecturyFluidAttributesForge.this.attributes.getOverlayTexture();
         }

         public ResourceLocation getStillTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            return ArchitecturyFluidAttributesForge.this.attributes.getSourceTexture(state, getter, pos);
         }

         public ResourceLocation getFlowingTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            return ArchitecturyFluidAttributesForge.this.attributes.getFlowingTexture(state, getter, pos);
         }

         @Nullable
         public ResourceLocation getOverlayTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            return ArchitecturyFluidAttributesForge.this.attributes.getOverlayTexture(state, getter, pos);
         }

         public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            return ArchitecturyFluidAttributesForge.this.attributes.getColor(state, getter, pos);
         }

         public int getTintColor(FluidStack stack) {
            return ArchitecturyFluidAttributesForge.this.attributes.getColor(ArchitecturyFluidAttributesForge.this.convertSafe(stack));
         }

         public ResourceLocation getStillTexture(FluidStack stack) {
            return ArchitecturyFluidAttributesForge.this.attributes.getSourceTexture(ArchitecturyFluidAttributesForge.this.convertSafe(stack));
         }

         public ResourceLocation getFlowingTexture(FluidStack stack) {
            return ArchitecturyFluidAttributesForge.this.attributes.getFlowingTexture(ArchitecturyFluidAttributesForge.this.convertSafe(stack));
         }

         @Nullable
         public ResourceLocation getOverlayTexture(FluidStack stack) {
            return ArchitecturyFluidAttributesForge.this.attributes.getOverlayTexture(ArchitecturyFluidAttributesForge.this.convertSafe(stack));
         }
      });
   }

   public int getLightLevel(FluidStack stack) {
      return this.attributes.getLuminosity(this.convertSafe(stack));
   }

   public int getLightLevel(FluidState state, BlockAndTintGetter level, BlockPos pos) {
      return this.attributes.getLuminosity(this.convertSafe(state), level, pos);
   }

   public int getDensity(FluidStack stack) {
      return this.attributes.getDensity(this.convertSafe(stack));
   }

   public int getDensity(FluidState state, BlockAndTintGetter level, BlockPos pos) {
      return this.attributes.getDensity(this.convertSafe(state), level, pos);
   }

   public int getTemperature(FluidStack stack) {
      return this.attributes.getTemperature(this.convertSafe(stack));
   }

   public int getTemperature(FluidState state, BlockAndTintGetter level, BlockPos pos) {
      return this.attributes.getTemperature(this.convertSafe(state), level, pos);
   }

   public int getViscosity(FluidStack stack) {
      return this.attributes.getViscosity(this.convertSafe(stack));
   }

   public int getViscosity(FluidState state, BlockAndTintGetter level, BlockPos pos) {
      return this.attributes.getViscosity(this.convertSafe(state), level, pos);
   }

   public Rarity getRarity() {
      return this.attributes.getRarity();
   }

   public Rarity getRarity(FluidStack stack) {
      return this.attributes.getRarity(this.convertSafe(stack));
   }

   public Component getDescription() {
      return this.attributes.getName();
   }

   public Component getDescription(FluidStack stack) {
      return this.attributes.getName(this.convertSafe(stack));
   }

   public String getDescriptionId() {
      return (String)MoreObjects.firstNonNull(this.attributes.getTranslationKey(), this.defaultTranslationKey);
   }

   public String getDescriptionId(FluidStack stack) {
      return (String)MoreObjects.firstNonNull(this.attributes.getTranslationKey(this.convertSafe(stack)), this.defaultTranslationKey);
   }

   @Nullable
   public SoundEvent getSound(SoundAction action) {
      return this.getSound((FluidStack)null, action);
   }

   @Nullable
   public SoundEvent getSound(@Nullable FluidStack stack, SoundAction action) {
      dev.architectury.fluid.FluidStack archStack = this.convertSafe(stack);
      if (SoundEvents.BUCKET_FILL.equals(action)) {
         return this.attributes.getFillSound(archStack);
      } else {
         return SoundEvents.BUCKET_EMPTY.equals(action) ? this.attributes.getEmptySound(archStack) : null;
      }
   }

   @Nullable
   public SoundEvent getSound(@Nullable Player player, BlockGetter getter, BlockPos pos, SoundAction action) {
      if (getter instanceof BlockAndTintGetter level) {
         if (SoundEvents.BUCKET_FILL.equals(action)) {
            return this.attributes.getFillSound(null, level, pos);
         }

         if (SoundEvents.BUCKET_EMPTY.equals(action)) {
            return this.attributes.getEmptySound(null, level, pos);
         }
      }

      return this.getSound((FluidStack)null, action);
   }

   public boolean canConvertToSource(FluidStack stack) {
      return this.attributes.canConvertToSource();
   }

   public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
      return this.attributes.canConvertToSource();
   }

   @Nullable
   public dev.architectury.fluid.FluidStack convertSafe(@Nullable FluidStack stack) {
      return stack == null ? null : FluidStackHooksForge.fromForge(stack);
   }

   @Nullable
   public dev.architectury.fluid.FluidStack convertSafe(@Nullable FluidState state) {
      return state == null ? null : dev.architectury.fluid.FluidStack.create(state.getType(), dev.architectury.fluid.FluidStack.bucketAmount());
   }
}
