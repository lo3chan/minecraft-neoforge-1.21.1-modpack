package net.blay09.mods.balm.neoforge.client.rendering;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.balm.common.NamespaceResolver;
import net.blay09.mods.balm.common.StaticNamespaceResolver;
import net.blay09.mods.balm.neoforge.ModBusEventRegisters;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item;

public record NeoForgeBalmRenderers(NamespaceResolver namespaceResolver) implements BalmRenderers {
   @Override
   public ModelLayerLocation registerModel(ResourceLocation location, String layer, Supplier<LayerDefinition> layerDefinition) {
      ModelLayerLocation modelLayerLocation = new ModelLayerLocation(location, layer);
      this.getActiveRegistrations().layerDefinitions.put(modelLayerLocation, layerDefinition);
      return modelLayerLocation;
   }

   @Override
   public <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> type, EntityRendererProvider<? super T> provider) {
      this.getActiveRegistrations().entityRenderers.add(Pair.of(type::get, provider));
   }

   @Override
   public <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<? super T> provider) {
      this.getActiveRegistrations().blockEntityRenderers.add(Pair.of(type::get, provider));
   }

   @Override
   public void registerBlockColorHandler(BlockColor color, Supplier<Block[]> blocks) {
      this.getActiveRegistrations().blockColors.add(new NeoForgeBalmRenderers.ColorRegistration<>(color, blocks));
   }

   @Override
   public void registerItemColorHandler(ItemColor color, Supplier<ItemLike[]> items) {
      this.getActiveRegistrations().itemColors.add(new NeoForgeBalmRenderers.ColorRegistration<>(color, items));
   }

   @Override
   public void setBlockRenderType(Supplier<Block> block, RenderType renderType) {
      this.getActiveRegistrations().blockRenderTypes.add(new NeoForgeBalmRenderers.BlockRenderTypeRegistration(block, renderType));
   }

   @Override
   public <T extends ParticleOptions> void registerParticleProvider(Supplier<ParticleType<T>> particleType, Function<SpriteSet, ParticleProvider<T>> factory) {
      this.getActiveRegistrations().particleProviderFactories.add(new NeoForgeBalmRenderers.ParticleProviderFactoryRegistration<>(particleType, factory));
   }

   @Override
   public <T extends ParticleOptions> void registerParticleProvider(Supplier<ParticleType<T>> particleType, ParticleProvider<T> provider) {
      this.getActiveRegistrations().particleProviders.add(new NeoForgeBalmRenderers.ParticleProviderRegistration<>(particleType, provider));
   }

   @Override
   public BalmRenderers scoped(String modId) {
      return new NeoForgeBalmRenderers(new StaticNamespaceResolver(modId));
   }

   private NeoForgeBalmRenderers.Registrations getActiveRegistrations() {
      return ModBusEventRegisters.getRegistrations(this.namespaceResolver.getDefaultNamespace(), NeoForgeBalmRenderers.Registrations.class);
   }

   public record BlockRenderTypeRegistration(Supplier<Block> blockSupplier, RenderType renderType) {
   }

   public record ColorRegistration<THandler, TObject>(THandler color, Supplier<TObject[]> objects) {
   }

   public record ParticleProviderFactoryRegistration<T extends ParticleOptions>(
      Supplier<ParticleType<T>> particleType, Function<SpriteSet, ParticleProvider<T>> value
   ) {
   }

   public record ParticleProviderRegistration<T extends ParticleOptions>(Supplier<ParticleType<T>> particleType, ParticleProvider<T> value) {
   }

   public static class Registrations {
      public final Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions = new HashMap<>();
      public final List<Pair<Supplier<BlockEntityType<?>>, BlockEntityRendererProvider<BlockEntity>>> blockEntityRenderers = new ArrayList<>();
      public final List<Pair<Supplier<EntityType<?>>, EntityRendererProvider<Entity>>> entityRenderers = new ArrayList<>();
      public final List<NeoForgeBalmRenderers.ColorRegistration<BlockColor, Block>> blockColors = new ArrayList<>();
      public final List<NeoForgeBalmRenderers.ColorRegistration<ItemColor, ItemLike>> itemColors = new ArrayList<>();
      public final List<NeoForgeBalmRenderers.ParticleProviderFactoryRegistration<?>> particleProviderFactories = new ArrayList<>();
      public final List<NeoForgeBalmRenderers.ParticleProviderRegistration<?>> particleProviders = new ArrayList<>();
      public final List<NeoForgeBalmRenderers.BlockRenderTypeRegistration> blockRenderTypes = new ArrayList<>();

      @SubscribeEvent
      public void setupClient(FMLClientSetupEvent event) {
         event.enqueueWork(
            () -> this.blockRenderTypes
               .forEach(blockRenderType -> ItemBlockRenderTypes.setRenderLayer(blockRenderType.blockSupplier.get(), blockRenderType.renderType()))
         );
      }

      @SubscribeEvent
      public void initRenderers(RegisterRenderers event) {
         for (Pair<Supplier<BlockEntityType<?>>, BlockEntityRendererProvider<BlockEntity>> entry : this.blockEntityRenderers) {
            event.registerBlockEntityRenderer((BlockEntityType)((Supplier)entry.getFirst()).get(), (BlockEntityRendererProvider)entry.getSecond());
         }

         for (Pair<Supplier<EntityType<?>>, EntityRendererProvider<Entity>> entry : this.entityRenderers) {
            event.registerEntityRenderer((EntityType)((Supplier)entry.getFirst()).get(), (EntityRendererProvider)entry.getSecond());
         }
      }

      @SubscribeEvent
      public void initLayerDefinitions(RegisterLayerDefinitions event) {
         for (Entry<ModelLayerLocation, Supplier<LayerDefinition>> entry : this.layerDefinitions.entrySet()) {
            event.registerLayerDefinition(entry.getKey(), entry.getValue());
         }
      }

      @SubscribeEvent
      public void initBlockColors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block event) {
         for (NeoForgeBalmRenderers.ColorRegistration<BlockColor, Block> blockColor : this.blockColors) {
            event.register(blockColor.color(), blockColor.objects().get());
         }
      }

      @SubscribeEvent
      public void initItemColors(Item event) {
         for (NeoForgeBalmRenderers.ColorRegistration<ItemColor, ItemLike> itemColor : this.itemColors) {
            event.register(itemColor.color(), itemColor.objects().get());
         }
      }

      @SubscribeEvent
      public void initParticleProviders(RegisterParticleProvidersEvent event) {
         for (NeoForgeBalmRenderers.ParticleProviderFactoryRegistration<?> factory : this.particleProviderFactories) {
            this.registerParticleProviderFactory(event, factory);
         }

         for (NeoForgeBalmRenderers.ParticleProviderRegistration<?> provider : this.particleProviders) {
            this.registerParticleProvider(event, provider);
         }
      }

      private <T extends ParticleOptions> void registerParticleProviderFactory(
         RegisterParticleProvidersEvent event, NeoForgeBalmRenderers.ParticleProviderFactoryRegistration<T> registration
      ) {
         event.registerSpriteSet(registration.particleType.get(), spriteSet -> registration.value().apply(spriteSet));
      }

      private <T extends ParticleOptions> void registerParticleProvider(
         RegisterParticleProvidersEvent event, NeoForgeBalmRenderers.ParticleProviderRegistration<T> registration
      ) {
         event.registerSpriteSet(registration.particleType.get(), spriteSet -> registration.value());
      }
   }
}
