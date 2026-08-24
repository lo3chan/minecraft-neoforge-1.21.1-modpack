package net.mehvahdjukaar.moonlight.api.platform.platform;

import com.google.gson.JsonElement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.client.ItemStackRenderer;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.platform.ForeignConfigBridge;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.platform.MoonlightForge;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.model.ExtendedBlockModelDeserializer;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.IModFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ClientHelperImpl {
   public static void registerRenderType(net.minecraft.world.level.block.Block block, RenderType... types) {
      if (types.length == 1) {
         ItemBlockRenderTypes.setRenderLayer(block, types[0]);
      } else {
         List<RenderType> l = List.of(types);
         ItemBlockRenderTypes.setRenderLayer(block, l::contains);
      }
   }

   public static void registerFluidRenderType(Fluid fluid, RenderType type) {
      ItemBlockRenderTypes.setRenderLayer(fluid, type);
   }

   public static void addParticleRegistration(Consumer<ClientHelper.ParticleEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterParticleProvidersEvent> eventConsumer = event -> eventListener.accept(new ClientHelperImpl.ParticleEventImpl(event));
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addEntityRenderersRegistration(Consumer<ClientHelper.EntityRendererEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterRenderers> eventConsumer = event -> eventListener.accept(event::registerEntityRenderer);
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addBlockEntityRenderersRegistration(Consumer<ClientHelper.BlockEntityRendererEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterRenderers> eventConsumer = event -> eventListener.accept(event::registerBlockEntityRenderer);
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addBlockColorsRegistration(Consumer<ClientHelper.BlockColorEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<Block> eventConsumer = event -> eventListener.accept(new ClientHelper.BlockColorEvent() {
         @Override
         public void register(BlockColor color, net.minecraft.world.level.block.Block... block) {
            event.register(color, block);
         }

         @Override
         public int getColor(BlockState block, BlockAndTintGetter level, BlockPos pos, int tint) {
            return event.getBlockColors().getColor(block, level, pos, tint);
         }
      });
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addItemColorsRegistration(Consumer<ClientHelper.ItemColorEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<Item> eventConsumer = event -> eventListener.accept(new ClientHelper.ItemColorEvent() {
         @Override
         public void register(ItemColor color, ItemLike... items) {
            event.register(color, items);
         }

         @Override
         public int getColor(ItemStack stack, int tint) {
            return event.getItemColors().getColor(stack, tint);
         }
      });
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addClientReloadListener(Supplier<PreparableReloadListener> listener, ResourceLocation location) {
      Moonlight.assertInitPhase();
      Consumer<RegisterClientReloadListenersEvent> eventConsumer = event -> event.registerReloadListener(listener.get());
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addModelLayerRegistration(Consumer<ClientHelper.ModelLayerEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterLayerDefinitions> eventConsumer = event -> eventListener.accept(event::registerLayerDefinition);
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addSpecialModelRegistration(Consumer<ClientHelper.SpecialModelEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterAdditional> eventConsumer = event -> eventListener.accept(new ClientHelper.SpecialModelEvent() {
         @Override
         public void register(ModelResourceLocation modelLocation) {
            event.register(modelLocation);
         }

         @Override
         public void register(ResourceLocation id) {
            event.register(ModelResourceLocation.standalone(id));
         }
      });
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addTooltipComponentRegistration(Consumer<ClientHelper.TooltipComponentEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterClientTooltipComponentFactoriesEvent> eventConsumer = event -> eventListener.accept(event::register);
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addModelLoaderRegistration(Consumer<ClientHelper.ModelLoaderEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterGeometryLoaders> eventConsumer = event -> eventListener.accept((i, l) -> event.register(i, (IGeometryLoader)l));
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addItemDecoratorsRegistration(Consumer<ClientHelper.ItemDecoratorEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterItemDecorationsEvent> eventConsumer = event -> eventListener.accept((i, l) -> {
         IItemDecorator deco = l::render;
         event.register(i, deco);
      });
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addKeyBindRegistration(Consumer<ClientHelper.KeyBindEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterKeyMappingsEvent> eventConsumer = event -> eventListener.accept(event::register);
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static int getPixelRGBA(TextureAtlasSprite sprite, int frameIndex, int x, int y) {
      return sprite.getPixelRGBA(frameIndex, x, y);
   }

   public static BakedModel getModel(ModelManager modelManager, ModelResourceLocation modelLocation) {
      return modelManager.getModel(modelLocation);
   }

   @Nullable
   public static Path getModIcon(String modId) {
      Optional<? extends ModContainer> m = ModList.get().getModContainerById(modId);
      if (m.isEmpty()) {
         return null;
      } else {
         IModInfo mod = m.get().getModInfo();
         IModFile file = mod.getOwningFile().getFile();
         String logo = (String)mod.getLogoFile().orElse(null);
         if (logo != null && file != null) {
            String[] parts = Arrays.stream(logo.split("[/\\\\]")).filter(p -> !p.isBlank()).toArray(String[]::new);
            if (parts.length == 0) {
               return null;
            } else {
               Path logoPath = file.findResource(parts);
               return logoPath != null && Files.exists(logoPath) ? logoPath : null;
            }
         } else {
            return null;
         }
      }
   }

   @Nullable
   public static Screen getModConfigScreen(String modId, Screen parent) {
      return ModList.get()
         .getModContainerById(modId)
         .flatMap(container -> container.getCustomExtension(IConfigScreenFactory.class).map(factory -> factory.createScreen(container, parent)))
         .orElse(null);
   }

   public static boolean hasModConfigScreen(String modId) {
      return ModList.get().getModContainerById(modId).map(container -> container.getCustomExtension(IConfigScreenFactory.class).isPresent()).orElse(false);
   }

   @Nullable
   public static Screen getNativeForeignConfigScreen(String modId, Screen parent, @Nullable ResourceLocation background) {
      return ForeignConfigBridge.createScreen(modId, parent, background);
   }

   public static boolean hasNativeForeignConfig(String modId) {
      return ForeignConfigBridge.hasConfig(modId);
   }

   public static boolean hasOnlyGenericConfigScreen(String modId) {
      return ForeignConfigBridge.hasOnlyGenericScreen(modId);
   }

   @Internal
   public static boolean hasHiddenPerWorldConfig(String modId) {
      return ForeignConfigBridge.hasHiddenPerWorldConfig(modId);
   }

   public static BlockModel parseBlockModel(JsonElement json) {
      return (BlockModel)ExtendedBlockModelDeserializer.INSTANCE.getAdapter(BlockModel.class).fromJsonTree(json);
   }

   public static void addClientSetup(Runnable clientSetup) {
      Moonlight.assertInitPhase();
      Consumer<FMLClientSetupEvent> eventConsumer = event -> event.enqueueWork(clientSetup);
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addClientSetupAsync(Runnable clientSetup) {
      Moonlight.assertInitPhase();
      Consumer<FMLClientSetupEvent> eventConsumer = event -> clientSetup.run();
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void registerOptionalTexturePack(ResourceLocation folderName, Component displayName, boolean defaultEnabled) {
      Moonlight.assertInitPhase();
      RegHelper.registerResourcePack(
         PackType.CLIENT_RESOURCES,
         () -> {
            IModFile file = ModList.get().getModFileById(folderName.getNamespace()).getFile();
            PackLocationInfo locationInfo = new PackLocationInfo(
               folderName.toString(), displayName, defaultEnabled ? PackSource.BUILT_IN : PackSource.FEATURE, Optional.empty()
            );

            try {
               final PathPackResources pack = new PathPackResources(locationInfo, file.findResource(new String[]{"resourcepacks/" + folderName.getPath()}));

               Pack var6;
               try {
                  var6 = Pack.readMetaAndCreate(locationInfo, new ResourcesSupplier() {
                     public PackResources openPrimary(PackLocationInfo location) {
                        return pack;
                     }

                     public PackResources openFull(PackLocationInfo location, Metadata metadata) {
                        return pack;
                     }
                  }, PackType.CLIENT_RESOURCES, new PackSelectionConfig(false, Position.TOP, false));
               } catch (Throwable var9) {
                  try {
                     pack.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }

                  throw var9;
               }

               pack.close();
               return var6;
            } catch (Exception var10) {
               if (!DatagenModLoader.isRunningDataGen()) {
                  Moonlight.LOGGER.error("Failed to load optional texture pack: {}", folderName, var10);
               }

               return null;
            }
         }
      );
   }

   public static void addShaderRegistration(Consumer<ClientHelper.ShaderEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterShadersEvent> eventConsumer = event -> eventListener.accept((id, vertexFormat, setter) -> {
         try {
            ShaderInstance shader = new ShaderInstance(event.getResourceProvider(), id, vertexFormat);
            event.registerShader(shader, setter);
         } catch (Exception var5) {
            throw new RuntimeException("Failed to parse shader: " + id, var5);
         }
      });
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addItemRenderersRegistration(Consumer<ClientHelper.ItemRendererEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterClientExtensionsEvent> eventConsumer = event -> eventListener.accept((item, renderer) -> {
         final ItemStackRenderer rend = renderer.getItemRenderer();
         event.registerItem(new IClientItemExtensions() {
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
               return (BlockEntityWithoutLevelRenderer)(rend != null ? rend : super.getCustomRenderer());
            }

            public void renderHelmetOverlay(ItemStack stack, Player player, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
               renderer.renderHelmetOverlay(stack, player, guiGraphics, deltaTracker);
            }
         }, new net.minecraft.world.item.Item[]{item.asItem()});
      });
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addMenuScreensRegistration(Consumer<ClientHelper.MenuScreenEvent> eventListener) {
      Moonlight.assertInitPhase();
      Consumer<RegisterMenuScreensEvent> eventConsumer = event -> eventListener.accept(event::register);
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static void addClientLoginCallback(Runnable callback) {
      Moonlight.assertInitPhase();
      Consumer<PlayerLoggedInEvent> eventConsumer = event -> {
         if (event.getEntity().level().isClientSide()) {
            callback.run();
         }
      };
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   private record ParticleEventImpl(RegisterParticleProvidersEvent event) implements ClientHelper.ParticleEvent {
      @Override
      public <P extends ParticleType<T>, T extends ParticleOptions> void register(P type, ClientHelper.ParticleFactory<T> provider) {
         this.event.registerSpriteSet(type, provider::create);
      }
   }
}
