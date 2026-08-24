package org.dimdev.limlib.api.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.limlib.api.fluid.FluidDetails;
import org.dimdev.limlib.api.util.function.TriFunction;

public interface ModClient<T extends IClientSided<?>> {
   void init(T var1);

   String getModId();

   default void initParticles(ModClient.RegularParticleRegister regularParticleRegister, ModClient.SpecialParticleRegister specialParticleRegister) {
   }

   default void initFluids(TriConsumer<FlowingFluid, Fluid, FluidDetails> register) {
   }

   default void initScreens(ModClient.ScreenRegister screenRegister) {
   }

   default void initBlockEntityRenderers(ModClient.BlockEntityRegister register) {
   }

   default void initEntityRenderers(ModClient.EntityRegister register) {
   }

   default void initModelLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
   }

   default void initModels(BiConsumer<ModelResourceLocation, Consumer<ModelLoadingRegistry>> consumer) {
   }

   default void initDimensionEffects(BiConsumer<ResourceLocation, DimensionSpecialEffects> effectsRegister) {
   }

   default void initShaders(TriConsumer<ResourceLocation, VertexFormat, Consumer<ShaderInstance>> shaderRegister) {
   }

   default void delayedInit() {
   }

   public interface BlockEntityRegister {
      <T extends BlockEntity> void register(BlockEntityType<T> var1, BlockEntityRendererProvider<T> var2);
   }

   public interface EntityRegister {
      <T extends Entity> void register(EntityType<T> var1, EntityRendererProvider<T> var2);
   }

   public interface RegularParticleRegister {
      <P extends ParticleOptions> void register(ParticleType<P> var1, Function<SpriteSet, ParticleProvider<P>> var2);
   }

   @FunctionalInterface
   public interface ScreenRegister {
      <U extends AbstractContainerMenu, M extends Screen & MenuAccess<U>> void register(MenuType<U> var1, TriFunction<U, Inventory, Component, M> var2);
   }

   public interface SpecialParticleRegister {
      <P extends ParticleOptions> void register(ParticleType<P> var1, ParticleProvider<P> var2);
   }
}
