package org.dimdev.limlib.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidType;
import org.apache.commons.lang3.tuple.Pair;
import org.dimdev.limlib.NeoforgeResourceLoader;
import org.dimdev.limlib.api.client.IClientSided;
import org.dimdev.limlib.api.client.ModClient;
import org.dimdev.limlib.api.client.ModelLoadingRegistry;
import org.dimdev.limlib.api.fluid.FluidDetails;
import org.dimdev.limlib.api.util.function.TriFunction;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoForgeClientSided<V extends NeoForgeClientSided<V, T>, T extends ModClient<? super V>> implements IClientSided<V> {
   private final T client;
   private List<NeoForgeClientSided.ModelLoadingRegistration> modelLoadingOverrides = new ArrayList<>();
   private final List<Runnable> loginRunnables = new ArrayList<>();
   private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
   private final List<Pair<ResourceLocation, Consumer<ResourceManager>>> loaders = new ArrayList<>();

   public NeoForgeClientSided(IEventBus bus, ModContainer container, T client) {
      this.client = client;
      client.init(this.self());
      client.initModels((id, consumer) -> this.modelLoadingOverrides.add(new NeoForgeClientSided.ModelLoadingRegistration(id, consumer)));
      bus.addListener(event -> client.initParticles(new ModClient.RegularParticleRegister() {
         @Override
         public <P extends ParticleOptions> void register(ParticleType<P> particleType, Function<SpriteSet, ParticleProvider<P>> provider) {
            event.registerSpriteSet(particleType, provider::apply);
         }
      }, event::registerSpecial));
      bus.addListener(
         event -> client.initScreens(
            new ModClient.ScreenRegister() {
               @Override
               public <U extends AbstractContainerMenu, M extends Screen & MenuAccess<U>> void register(
                  MenuType<U> menuType, TriFunction<U, Inventory, Component, M> factory
               ) {
                  event.register(menuType, factory::apply);
               }
            }
         )
      );
      bus.addListener(
         event -> client.initFluids(
            (flowing, fluid, details) -> event.registerFluidType(new NeoForgeClientSided.FluidExtension(details), new FluidType[]{fluid.getFluidType()})
         )
      );
      bus.addListener(event -> {
         client.initEntityRenderers(event::registerEntityRenderer);
         client.initBlockEntityRenderers(event::registerBlockEntityRenderer);
      });
      bus.addListener(event -> client.initModelLayers(event::registerLayerDefinition));
      bus.addListener(event -> this.modelLoadingOverrides.stream().map(NeoForgeClientSided.ModelLoadingRegistration::replacementModel).forEach(event::register));
      bus.addListener(event -> {
         Map<ModelResourceLocation, BakedModel> bakedModels = event.getModelBakery().getBakedTopLevelModels();
         Map<ModelResourceLocation, BakedModel> models = event.getModels();

         for (NeoForgeClientSided.ModelLoadingRegistration registration : this.modelLoadingOverrides) {
            ModelLoadingOverride override = registration.resolve();
            BakedModel replacementModel = bakedModels.get(override.replacementModel());
            if (replacementModel == null) {
               this.LOGGER.error("Missing replacement model {} at bake time", override.replacementModel());
            } else {
               override.resolvedTargets().forEach(location -> models.put(location, replacementModel));
            }
         }
      });
      bus.addListener(this::addReloaders);
      bus.addListener(event -> client.initDimensionEffects(event::register));
      bus.addListener(event -> event.enqueueWork(client::delayedInit));
      bus.addListener(event -> {
         ResourceProvider provider = event.getResourceProvider();
         client.initShaders((id, vertexFormat, consumer) -> {
            try {
               event.registerShader(new ShaderInstance(provider, id, vertexFormat), consumer);
            } catch (IOException var6) {
               throw new RuntimeException(var6);
            }
         });
      });
      NeoForge.EVENT_BUS.addListener(event -> this.loginRunnables.forEach(Runnable::run));
   }

   @Override
   public void register(RenderType type, Block... blocks) {
      for (Block block : blocks) {
         ItemBlockRenderTypes.setRenderLayer(block, type);
      }
   }

   @Override
   public void onClientPlayerJoin(Runnable listener) {
      this.loginRunnables.add(listener);
   }

   @Override
   public void registerClientLoader(String name, Consumer<ResourceManager> consumer) {
      this.loaders.add(Pair.of(ResourceLocation.fromNamespaceAndPath(this.client.getModId(), name), consumer));
   }

   public void addReloaders(RegisterClientReloadListenersEvent event) {
      this.loaders
         .forEach(
            pair -> event.registerReloadListener(
               new NeoforgeResourceLoader.Client((ResourceLocation)pair.getLeft(), (Consumer<ResourceManager>)pair.getValue())
            )
         );
   }

   public record FluidExtension(ResourceLocation flowing, ResourceLocation still, ResourceLocation overlay) implements IClientFluidTypeExtensions {
      public FluidExtension(FluidDetails attributes) {
         this(attributes.flowing(), attributes.still(), attributes.overlay());
      }

      public ResourceLocation getFlowingTexture() {
         return this.flowing;
      }

      @Nullable
      public ResourceLocation getOverlayTexture() {
         return this.overlay;
      }

      public ResourceLocation getStillTexture() {
         return this.still;
      }
   }

   private record ModelLoadingRegistration(ModelResourceLocation replacementModel, Consumer<ModelLoadingRegistry> registration) {
      private ModelLoadingOverride resolve() {
         return ModelLoadingOverride.create(this.replacementModel, this.registration);
      }
   }
}
