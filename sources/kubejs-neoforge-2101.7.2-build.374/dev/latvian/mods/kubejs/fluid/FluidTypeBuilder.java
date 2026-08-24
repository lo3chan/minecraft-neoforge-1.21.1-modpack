package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.block.BlockRenderType;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;
import org.jetbrains.annotations.Nullable;

@ReturnsSelf
public class FluidTypeBuilder extends BuilderBase<FluidType> {
   public transient Properties properties = Properties.create();
   public transient ResourceLocation stillTexture = this.newID("block/", "_still");
   public transient ResourceLocation flowingTexture = this.newID("block/", "_flow");
   public transient ResourceLocation actualStillTexture = this.newID("block/generated/", "_still");
   public transient ResourceLocation actualFlowingTexture = this.newID("block/generated/", "_flow");
   public transient ResourceLocation screenOverlayTexture;
   public transient ResourceLocation blockOverlayTexture;
   public transient KubeColor tint = null;
   public transient BlockRenderType renderType = BlockRenderType.SOLID;

   public FluidTypeBuilder(ResourceLocation id) {
      super(id);
      this.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL);
      this.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
      this.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH);
   }

   public FluidType createObject() {
      return new FluidTypeBuilder.KubeFluidType(this);
   }

   public FluidTypeBuilder stillTexture(ResourceLocation stillTexture) {
      this.stillTexture = stillTexture;
      return this;
   }

   public FluidTypeBuilder flowingTexture(ResourceLocation flowingTexture) {
      this.flowingTexture = flowingTexture;
      return this;
   }

   public FluidTypeBuilder screenOverlayTexture(ResourceLocation screenOverlayTexture) {
      this.screenOverlayTexture = screenOverlayTexture;
      return this;
   }

   public FluidTypeBuilder blockOverlayTexture(ResourceLocation blockOverlayTexture) {
      this.blockOverlayTexture = blockOverlayTexture;
      return this;
   }

   public FluidTypeBuilder tint(KubeColor tint) {
      this.tint = tint;
      return this;
   }

   public FluidTypeBuilder descriptionId(String descriptionId) {
      this.properties.descriptionId(descriptionId);
      return this;
   }

   public FluidTypeBuilder motionScale(double motionScale) {
      this.properties.motionScale(motionScale);
      return this;
   }

   public FluidTypeBuilder canPushEntity(boolean canPushEntity) {
      this.properties.canPushEntity(canPushEntity);
      return this;
   }

   public FluidTypeBuilder canSwim(boolean canSwim) {
      this.properties.canSwim(canSwim);
      return this;
   }

   public FluidTypeBuilder canDrown(boolean canDrown) {
      this.properties.canDrown(canDrown);
      return this;
   }

   public FluidTypeBuilder fallDistanceModifier(float fallDistanceModifier) {
      this.properties.fallDistanceModifier(fallDistanceModifier);
      return this;
   }

   public FluidTypeBuilder canExtinguish(boolean canExtinguish) {
      this.properties.canExtinguish(canExtinguish);
      return this;
   }

   public FluidTypeBuilder canConvertToSource(boolean canConvertToSource) {
      this.properties.canConvertToSource(canConvertToSource);
      return this;
   }

   public FluidTypeBuilder supportsBoating(boolean supportsBoating) {
      this.properties.supportsBoating(supportsBoating);
      return this;
   }

   public FluidTypeBuilder pathType(@Nullable PathType pathType) {
      this.properties.pathType(pathType);
      return this;
   }

   public FluidTypeBuilder adjacentPathType(@Nullable PathType adjacentPathType) {
      this.properties.adjacentPathType(adjacentPathType);
      return this;
   }

   public FluidTypeBuilder sound(SoundAction action, SoundEvent sound) {
      this.properties.sound(action, sound);
      return this;
   }

   public FluidTypeBuilder canHydrate(boolean canHydrate) {
      this.properties.canHydrate(canHydrate);
      return this;
   }

   public FluidTypeBuilder lightLevel(int lightLevel) {
      this.properties.lightLevel(lightLevel);
      return this;
   }

   public FluidTypeBuilder density(int density) {
      this.properties.density(density);
      return this;
   }

   public FluidTypeBuilder temperature(int temperature) {
      this.properties.temperature(temperature);
      return this;
   }

   public FluidTypeBuilder viscosity(int viscosity) {
      this.properties.viscosity(viscosity);
      return this;
   }

   public FluidTypeBuilder rarity(Rarity rarity) {
      this.properties.rarity(rarity);
      return this;
   }

   public FluidTypeBuilder addDripstoneDripping(float chance, ParticleOptions dripParticle, Block cauldron, @Nullable SoundEvent fillSound) {
      this.properties.addDripstoneDripping(chance, dripParticle, cauldron, fillSound);
      return this;
   }

   public FluidTypeBuilder renderType(BlockRenderType renderType) {
      this.renderType = renderType;
      return this;
   }

   public static class KubeFluidType extends FluidType {
      public final FluidTypeBuilder builder;

      public KubeFluidType(FluidTypeBuilder builder) {
         super(builder.properties);
         this.builder = builder;
      }
   }
}
