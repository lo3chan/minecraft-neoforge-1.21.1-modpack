package net.mehvahdjukaar.moonlight.api.platform.configs.platform;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.electronwill.nightconfig.core.concurrent.SynchronizedConfig;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigCategory;
import net.mehvahdjukaar.moonlight.api.resources.pack.GlobalCachedStrategy;
import net.mehvahdjukaar.moonlight.core.ClientConfigs;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.client.config.MoonlightConfigScreen;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.IConfigSpec.ILoadedConfig;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class ForgeConfigHolder extends ModConfigHolder {
   private static final Map<ModConfig, ForgeConfigHolder> BY_FORGE_CONFIG = new HashMap<>();
   private final ModConfigSpec spec;
   private final ModConfig modConfig;
   private final List<TrackedConfigValue<?>> trackedValues;
   private final ConfigCategory configRoot;
   private static final Class<?> LOADED_CONFIG_CLASS = (Class<?>)Util.make(() -> {
      try {
         return Class.forName("net.neoforged.fml.config.LoadedConfig");
      } catch (ClassNotFoundException var1) {
         throw new RuntimeException(var1);
      }
   });
   private static final Method SET_CONFIG = ObfuscationReflectionHelper.findMethod(
      ModConfig.class, "setConfig", new Class[]{LOADED_CONFIG_CLASS, Function.class}
   );
   private static final Constructor NEW_LOADED_CONFIG = ObfuscationReflectionHelper.findConstructor(
      LOADED_CONFIG_CLASS, new Class[]{CommentedConfig.class, Path.class, ModConfig.class}
   );
   private static final Method GET_PATH = ObfuscationReflectionHelper.findMethod(LOADED_CONFIG_CLASS, "path", new Class[0]);
   private static final Method LOAD_CONFIG = ObfuscationReflectionHelper.findMethod(
      ConfigTracker.class, "loadConfig", new Class[]{ModConfig.class, Path.class, Function.class}
   );

   public static ForgeConfigHolder getFromForgeConfig(ModConfig config) {
      return BY_FORGE_CONFIG.get(config);
   }

   ForgeConfigHolder(
      ResourceLocation name,
      ModConfigSpec spec,
      ConfigType type,
      @Nullable Runnable onChange,
      List<TrackedConfigValue<?>> trackedValues,
      ConfigCategory configRoot
   ) {
      super(name, "toml", FMLPaths.CONFIGDIR.get(), type, onChange);
      this.spec = spec;
      this.trackedValues = trackedValues;
      this.configRoot = configRoot;
      Type forgeType = this.getConfigType() == ConfigType.CLIENT ? Type.CLIENT : Type.COMMON;
      ModContainer modContainer = (ModContainer)ModList.get().getModContainerById(this.getModId()).orElseThrow();
      this.modConfig = ConfigTracker.INSTANCE.registerConfig(forgeType, spec, modContainer, this.getFileName());
      IEventBus bus = modContainer.getEventBus();
      if (onChange != null || this.isSynced() || !trackedValues.isEmpty()) {
         bus.addListener(this::onConfigChange);
      }

      if (this.isSynced()) {
         NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
         NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedOut);
      }

      BY_FORGE_CONFIG.put(this.modConfig, this);
   }

   @Override
   public Path getFullPath() {
      return FMLPaths.CONFIGDIR.get().resolve(this.getFileName());
   }

   @Override
   public void forceLoad() {
      if (!this.isLoaded()) {
         try {
            LOAD_CONFIG.invoke(ConfigTracker.INSTANCE, this.modConfig, this.getFullPath(), Loading::new);
         } catch (Exception var2) {
            throw new ModConfigHolder.ConfigLoadingException(this, var2);
         }
      }
   }

   public ModConfigSpec getSpec() {
      return this.spec;
   }

   @Nullable
   public ModConfig getModConfig() {
      return this.modConfig;
   }

   @Override
   public boolean isLoaded() {
      return this.spec.isLoaded();
   }

   @Override
   protected void saveToDisk() {
      this.spec.save();
   }

   @Override
   public ConfigCategory getConfigRoot() {
      return this.configRoot;
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   @Override
   public Screen makeScreen(Screen parent, @Nullable ResourceLocation background) {
      if (ClientConfigs.CUSTOM_CONFIG_SCREEN.get()) {
         ConfigCategory root = this.getConfigRoot();
         return root == null ? null : new MoonlightConfigScreen(this, root, parent, background);
      } else {
         return ModList.get()
            .getModContainerById(this.getModId())
            .flatMap(container -> container.getCustomExtension(IConfigScreenFactory.class).map(factory -> factory.createScreen(container, parent)))
            .orElse(null);
      }
   }

   @Internal
   private void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         this.syncConfigsToPlayer(serverPlayer);
      }
   }

   @Internal
   public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
      if (event.getEntity().level().isClientSide) {
         this.onRefresh();
      }
   }

   @Internal
   public void onConfigChange(ModConfigEvent event) {
      if (event.getConfig().getSpec() == this.getSpec()) {
         if (PlatHelper.isDev()) {
            Moonlight.LOGGER.info("Detected config change in {}, from neoforge config event", this.getFileName());
         }

         if (this.isSynced() && PlatHelper.getPhysicalSide().isServer()) {
            Moonlight.LOGGER.info("Sending changed configs to client", this.getFileName());
            this.sendSyncedConfigsToAllPlayers();
         }

         boolean invalidateDynamicPacks = false;

         for (TrackedConfigValue<?> trackedValue : this.trackedValues) {
            invalidateDynamicPacks |= trackedValue.pollChanged() && trackedValue.affectsDynamicPacks();
         }

         if (invalidateDynamicPacks) {
            GlobalCachedStrategy.forceInvalidateState(this.getPackType());
         }

         this.onRefresh();
      }
   }

   @Override
   protected byte[] getConfigFileData() throws IOException {
      byte[] var3;
      try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
         CommentedConfig data = this.modConfig.getLoadedConfig().config();
         TomlFormat.instance().createWriter().write(data, stream);
         var3 = stream.toByteArray();
      }

      return var3;
   }

   @Override
   public void loadFromBytes(InputStream stream, boolean readOnly) {
      if (PlatHelper.isIntegratedServer()) {
         readOnly = false;
      }

      try {
         byte[] b = stream.readAllBytes();
         if (readOnly) {
            ConfigTracker.acceptSyncedConfig(this.modConfig, b);
         } else {
            this.acceptEditableConfigs(this.modConfig, b);
         }
      } catch (Exception var4) {
         Moonlight.LOGGER.warn("Failed to sync config file {}:", this.getFileName(), var4);
      }
   }

   public void acceptEditableConfigs(ModConfig modConfig, byte[] bytes) {
      Moonlight.LOGGER.info("Overriding configs {} with synced configs (editable)", modConfig.getFileName());
      SynchronizedConfig newConfig = new SynchronizedConfig(InMemoryCommentedFormat.defaultInstance(), LinkedHashMap::new);
      newConfig.bulkCommentedUpdate(view -> TomlFormat.instance().createParser().parse(new ByteArrayInputStream(bytes), view, ParsingMode.REPLACE));
      Path path = this.getFullPath();

      try {
         Object loadedConfig = NEW_LOADED_CONFIG.newInstance(newConfig, path, modConfig);
         SET_CONFIG.invoke(modConfig, loadedConfig, Reloading::new);
         ((ILoadedConfig)loadedConfig).save();
      } catch (Exception var6) {
         throw new RuntimeException(var6);
      }
   }
}
