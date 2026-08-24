package net.mehvahdjukaar.moonlight.api.platform;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.client.CoreShaderContainer;
import net.mehvahdjukaar.moonlight.api.client.ItemRenderExtension;
import net.mehvahdjukaar.moonlight.api.client.ItemStackRenderer;
import net.mehvahdjukaar.moonlight.api.client.model.CustomBakedModel;
import net.mehvahdjukaar.moonlight.api.client.model.CustomModelLoader;
import net.mehvahdjukaar.moonlight.api.item.IItemDecoratorRenderer;
import net.mehvahdjukaar.moonlight.api.platform.platform.ClientHelperImpl;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.core.ClientConfigs;
import net.mehvahdjukaar.moonlight.core.client.config.ModsTilesScreen;
import net.mehvahdjukaar.moonlight.core.client.config.MoonlightConfigSelectScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientHelper {
   private static final Cache<ResourceLocation, Material> CACHED_MATERIALS = CacheBuilder.newBuilder().expireAfterAccess(2L, TimeUnit.MINUTES).build();

   public static Player getLocalPlayer() {
      LocalPlayer player = Minecraft.getInstance().player;
      return (Player)player;
   }

   public static Level getLocalLevel() {
      ClientLevel level = Minecraft.getInstance().level;
      return (Level)level;
   }

   public static void registerRenderType(Block block, RenderType type) {
      registerRenderType(block, type);
   }

   @Nullable
   public static Screen getMoonlightConfigScreen(String modId, Screen parent, @Nullable ResourceLocation background) {
      return MoonlightConfigSelectScreen.create(modId, parent, background);
   }

   @Nullable
   public static Screen getModsListScreen(@Nullable Screen parent, @Nullable ResourceLocation background) {
      return !ClientConfigs.CUSTOM_CONFIG_SCREEN.get() ? null : new ModsTilesScreen(parent, background);
   }

   public static void registerOptionalTexturePack(ResourceLocation folderName, boolean defaultEnabled) {
      registerOptionalTexturePack(folderName, Component.literal(TextHelper.getReadableName(folderName.getPath())), defaultEnabled);
   }

   public static Material getBlockMaterial(ResourceLocation bockTexture) {
      try {
         return (Material)CACHED_MATERIALS.get(bockTexture, () -> new Material(TextureAtlas.LOCATION_BLOCKS, bockTexture));
      } catch (ExecutionException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static void addMenuScreensRegistration(Consumer<ClientHelper.MenuScreenEvent> var0) {
      ClientHelperImpl.addMenuScreensRegistration(var0);
   }

   public static void addClientSetup(Runnable var0) {
      ClientHelperImpl.addClientSetup(var0);
   }

   public static void addClientSetupAsync(Runnable var0) {
      ClientHelperImpl.addClientSetupAsync(var0);
   }

   public static void addClientLoginCallback(Runnable var0) {
      ClientHelperImpl.addClientLoginCallback(var0);
   }

   public static void registerRenderType(Block var0, RenderType... var1) {
      ClientHelperImpl.registerRenderType(var0, var1);
   }

   public static void registerFluidRenderType(Fluid var0, RenderType var1) {
      ClientHelperImpl.registerFluidRenderType(var0, var1);
   }

   public static void addClientReloadListener(Supplier<PreparableReloadListener> var0, ResourceLocation var1) {
      ClientHelperImpl.addClientReloadListener(var0, var1);
   }

   public static void addParticleRegistration(Consumer<ClientHelper.ParticleEvent> var0) {
      ClientHelperImpl.addParticleRegistration(var0);
   }

   public static void addShaderRegistration(Consumer<ClientHelper.ShaderEvent> var0) {
      ClientHelperImpl.addShaderRegistration(var0);
   }

   public static void addItemRenderersRegistration(Consumer<ClientHelper.ItemRendererEvent> var0) {
      ClientHelperImpl.addItemRenderersRegistration(var0);
   }

   public static void addItemDecoratorsRegistration(Consumer<ClientHelper.ItemDecoratorEvent> var0) {
      ClientHelperImpl.addItemDecoratorsRegistration(var0);
   }

   public static void addEntityRenderersRegistration(Consumer<ClientHelper.EntityRendererEvent> var0) {
      ClientHelperImpl.addEntityRenderersRegistration(var0);
   }

   public static void addBlockEntityRenderersRegistration(Consumer<ClientHelper.BlockEntityRendererEvent> var0) {
      ClientHelperImpl.addBlockEntityRenderersRegistration(var0);
   }

   public static void addBlockColorsRegistration(Consumer<ClientHelper.BlockColorEvent> var0) {
      ClientHelperImpl.addBlockColorsRegistration(var0);
   }

   public static void addItemColorsRegistration(Consumer<ClientHelper.ItemColorEvent> var0) {
      ClientHelperImpl.addItemColorsRegistration(var0);
   }

   public static void addModelLayerRegistration(Consumer<ClientHelper.ModelLayerEvent> var0) {
      ClientHelperImpl.addModelLayerRegistration(var0);
   }

   /** @deprecated */
   public static void addSpecialModelRegistration(Consumer<ClientHelper.SpecialModelEvent> var0) {
      ClientHelperImpl.addSpecialModelRegistration(var0);
   }

   public static void addModelLoaderRegistration(Consumer<ClientHelper.ModelLoaderEvent> var0) {
      ClientHelperImpl.addModelLoaderRegistration(var0);
   }

   public static BakedModel getModel(ModelManager var0, ModelResourceLocation var1) {
      return ClientHelperImpl.getModel(var0, var1);
   }

   public static void addTooltipComponentRegistration(Consumer<ClientHelper.TooltipComponentEvent> var0) {
      ClientHelperImpl.addTooltipComponentRegistration(var0);
   }

   public static void addKeyBindRegistration(Consumer<ClientHelper.KeyBindEvent> var0) {
      ClientHelperImpl.addKeyBindRegistration(var0);
   }

   public static int getPixelRGBA(TextureAtlasSprite var0, int var1, int var2, int var3) {
      return ClientHelperImpl.getPixelRGBA(var0, var1, var2, var3);
   }

   public static BlockModel parseBlockModel(JsonElement var0) {
      return ClientHelperImpl.parseBlockModel(var0);
   }

   public static Path getModIcon(String var0) {
      return ClientHelperImpl.getModIcon(var0);
   }

   public static Screen getModConfigScreen(String var0, Screen var1) {
      return ClientHelperImpl.getModConfigScreen(var0, var1);
   }

   public static boolean hasModConfigScreen(String var0) {
      return ClientHelperImpl.hasModConfigScreen(var0);
   }

   public static Screen getNativeForeignConfigScreen(String var0, Screen var1, ResourceLocation var2) {
      return ClientHelperImpl.getNativeForeignConfigScreen(var0, var1, var2);
   }

   public static boolean hasNativeForeignConfig(String var0) {
      return ClientHelperImpl.hasNativeForeignConfig(var0);
   }

   public static boolean hasOnlyGenericConfigScreen(String var0) {
      return ClientHelperImpl.hasOnlyGenericConfigScreen(var0);
   }

   public static boolean hasHiddenPerWorldConfig(String var0) {
      return ClientHelperImpl.hasHiddenPerWorldConfig(var0);
   }

   public static void registerOptionalTexturePack(ResourceLocation var0, Component var1, boolean var2) {
      ClientHelperImpl.registerOptionalTexturePack(var0, var1, var2);
   }

   public interface BlockColorEvent {
      void register(BlockColor var1, Block... var2);

      int getColor(BlockState var1, BlockAndTintGetter var2, BlockPos var3, int var4);
   }

   @FunctionalInterface
   public interface BlockEntityRendererEvent {
      <E extends BlockEntity> void register(BlockEntityType<? extends E> var1, BlockEntityRendererProvider<E> var2);
   }

   @FunctionalInterface
   public interface EntityRendererEvent {
      <E extends Entity> void register(EntityType<? extends E> var1, EntityRendererProvider<E> var2);
   }

   public interface ItemColorEvent {
      void register(ItemColor var1, ItemLike... var2);

      int getColor(ItemStack var1, int var2);
   }

   @FunctionalInterface
   public interface ItemDecoratorEvent {
      void register(ItemLike var1, IItemDecoratorRenderer var2);
   }

   @FunctionalInterface
   public interface ItemRendererEvent {
      default void register(ItemLike item, final ItemStackRenderer renderer) {
         this.register(item, new ItemRenderExtension() {
            @Nullable
            @Override
            public ItemStackRenderer getItemRenderer() {
               return renderer;
            }
         });
      }

      void register(ItemLike var1, ItemRenderExtension var2);
   }

   @FunctionalInterface
   public interface KeyBindEvent {
      void register(KeyMapping var1);
   }

   @FunctionalInterface
   public interface MenuScreenEvent {
      <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> var1, ScreenConstructor<M, U> var2);
   }

   @FunctionalInterface
   public interface ModelLayerEvent {
      void register(ModelLayerLocation var1, Supplier<LayerDefinition> var2);
   }

   @FunctionalInterface
   public interface ModelLoaderEvent {
      void register(ResourceLocation var1, CustomModelLoader var2);

      default void register(ResourceLocation id, Supplier<CustomBakedModel> bakedModelFactory) {
         this.register(id, (json, context) -> (modelBaker, spriteGetter, transform) -> bakedModelFactory.get());
      }

      default void register(ResourceLocation id, BiFunction<ModelState, Function<Material, TextureAtlasSprite>, CustomBakedModel> bakedModelFactory) {
         this.register(id, (json, context) -> (modelBaker, spriteGetter, transform) -> bakedModelFactory.apply(transform, spriteGetter));
      }
   }

   @FunctionalInterface
   public interface ParticleEvent {
      <P extends ParticleType<T>, T extends ParticleOptions> void register(P var1, ClientHelper.ParticleFactory<T> var2);
   }

   @FunctionalInterface
   public interface ParticleFactory<T extends ParticleOptions> {
      @NotNull
      ParticleProvider<T> create(SpriteSet var1);
   }

   public interface ShaderEvent {
      void register(ResourceLocation var1, VertexFormat var2, Consumer<ShaderInstance> var3);

      default void register(ResourceLocation id, VertexFormat vertexFormat, CoreShaderContainer container) {
         this.register(id, vertexFormat, container::assign);
      }
   }

   public interface SpecialModelEvent {
      void register(ModelResourceLocation var1);

      void register(ResourceLocation var1);
   }

   @FunctionalInterface
   public interface TooltipComponentEvent {
      <T extends TooltipComponent> void register(Class<T> var1, Function<? super T, ? extends ClientTooltipComponent> var2);
   }
}
