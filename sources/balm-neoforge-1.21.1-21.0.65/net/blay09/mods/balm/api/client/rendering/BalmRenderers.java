package net.blay09.mods.balm.api.client.rendering;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface BalmRenderers {
   @Deprecated
   default ModelLayerLocation registerModel(ResourceLocation location, Supplier<LayerDefinition> layerDefinition) {
      return this.registerModel(location, "main", layerDefinition);
   }

   @Deprecated
   ModelLayerLocation registerModel(ResourceLocation var1, String var2, Supplier<LayerDefinition> var3);

   @Deprecated
   default <T extends Entity> void registerEntityRenderer(ResourceLocation identifier, Supplier<EntityType<T>> type, EntityRendererProvider<? super T> provider) {
      this.registerEntityRenderer(type, provider);
   }

   @Deprecated
   default <T extends BlockEntity> void registerBlockEntityRenderer(
      ResourceLocation identifier, Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<? super T> provider
   ) {
      this.registerBlockEntityRenderer(type, provider);
   }

   @Deprecated
   default void registerBlockColorHandler(ResourceLocation identifier, BlockColor color, Supplier<Block[]> blocks) {
      this.registerBlockColorHandler(color, blocks);
   }

   void registerItemColorHandler(ItemColor var1, Supplier<ItemLike[]> var2);

   @Deprecated
   void setBlockRenderType(Supplier<Block> var1, RenderType var2);

   @Deprecated
   default <T extends ParticleOptions> void registerParticleProvider(
      ResourceLocation identifier, Supplier<ParticleType<T>> particleType, Function<SpriteSet, ParticleProvider<T>> factory
   ) {
      this.registerParticleProvider(particleType, factory);
   }

   @Deprecated
   default <T extends ParticleOptions> void registerParticleProvider(
      ResourceLocation identifier, Supplier<ParticleType<T>> particleType, ParticleProvider<T> provider
   ) {
      this.registerParticleProvider(particleType, provider);
   }

   @Deprecated
   <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> var1, EntityRendererProvider<? super T> var2);

   @Deprecated
   <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> var1, BlockEntityRendererProvider<? super T> var2);

   @Deprecated
   void registerBlockColorHandler(BlockColor var1, Supplier<Block[]> var2);

   @Deprecated
   <T extends ParticleOptions> void registerParticleProvider(Supplier<ParticleType<T>> var1, Function<SpriteSet, ParticleProvider<T>> var2);

   @Deprecated
   <T extends ParticleOptions> void registerParticleProvider(Supplier<ParticleType<T>> var1, ParticleProvider<T> var2);

   @Deprecated
   BalmRenderers scoped(String var1);
}
