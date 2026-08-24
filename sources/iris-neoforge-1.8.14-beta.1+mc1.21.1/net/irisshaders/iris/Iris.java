package net.irisshaders.iris;

import com.google.common.base.Throwables;
import com.mojang.blaze3d.platform.GlDebug;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.zip.ZipError;
import java.util.zip.ZipException;
import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializerRegistry;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.config.IrisConfig;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.buffer.ShaderStorageBufferHolder;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.irisshaders.iris.gl.shader.StandardMacros;
import net.irisshaders.iris.gui.debug.DebugLoadFailedGridScreen;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.helpers.OptionalBoolean;
import net.irisshaders.iris.pbr.texture.PBRTextureManager;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.PipelineManager;
import net.irisshaders.iris.pipeline.VanillaRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.platform.IrisPlatformHelpers;
import net.irisshaders.iris.shaderpack.DimensionId;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.discovery.ShaderpackDirectoryManager;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.option.OptionSet;
import net.irisshaders.iris.shaderpack.option.Profile;
import net.irisshaders.iris.shaderpack.option.values.MutableOptionValues;
import net.irisshaders.iris.shaderpack.option.values.OptionValues;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.sodium.EntityToTerrainVertexSerializer;
import net.irisshaders.iris.vertices.sodium.GlyphExtVertexSerializer;
import net.irisshaders.iris.vertices.sodium.IrisEntityToTerrainVertexSerializer;
import net.irisshaders.iris.vertices.sodium.ModelToEntityVertexSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.Util.OS;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Iris {
   public static final String MODID = "iris";
   public static final String MODNAME = "Iris";
   public static final IrisLogging logger = new IrisLogging("Iris");
   private static final Map<String, String> shaderPackOptionQueue = new HashMap<>();
   private static final String backupVersionNumber = "1.21";
   public static NamespacedId lastDimension = null;
   public static boolean testing = false;
   private static Path shaderpacksDirectory;
   private static ShaderpackDirectoryManager shaderpacksDirectoryManager;
   private static ShaderPack currentPack;
   private static String currentPackName;
   private static Optional<Exception> storedError = Optional.empty();
   private static boolean initialized;
   private static PipelineManager pipelineManager;
   private static IrisConfig irisConfig;
   private static FileSystem zipFileSystem;
   private static KeyMapping reloadKeybind;
   private static KeyMapping toggleShadersKeybind;
   private static KeyMapping shaderpackScreenKeybind;
   private static KeyMapping wireframeKeybind;
   private static boolean resetShaderPackOptions = false;
   private static String IRIS_VERSION;
   private static UpdateChecker updateChecker;
   private static boolean fallback;
   private static boolean loadShaderPackWhenPossible;

   public static void onRenderSystemInit() {
      if (!initialized) {
         logger.warn("Iris::onRenderSystemInit was called, but Iris::onEarlyInitialize was not called. Trying to avoid a crash but this is an odd state.");
      } else {
         PBRTextureManager.INSTANCE.init();
         VertexSerializerRegistry.instance()
            .registerSerializer(DefaultVertexFormat.NEW_ENTITY, IrisVertexFormats.TERRAIN, new EntityToTerrainVertexSerializer());
         VertexSerializerRegistry.instance().registerSerializer(IrisVertexFormats.ENTITY, IrisVertexFormats.TERRAIN, new IrisEntityToTerrainVertexSerializer());
         VertexSerializerRegistry.instance()
            .registerSerializer(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, IrisVertexFormats.GLYPH, new GlyphExtVertexSerializer());
         VertexSerializerRegistry.instance().registerSerializer(DefaultVertexFormat.NEW_ENTITY, IrisVertexFormats.ENTITY, new ModelToEntityVertexSerializer());
         if (!IrisPlatformHelpers.getInstance().isModLoaded("distanthorizons")) {
            loadShaderpack();
         }
      }
   }

   public static void duringRenderSystemInit() {
      setDebug(irisConfig.areDebugOptionsEnabled());
   }

   public static void onLoadingComplete() {
      if (!initialized) {
         logger.warn("Iris::onLoadingComplete was called, but Iris::onEarlyInitialize was not called. Trying to avoid a crash but this is an odd state.");
      } else {
         lastDimension = DimensionId.OVERWORLD;
         getPipelineManager().preparePipeline(DimensionId.OVERWORLD);
      }
   }

   public static void handleKeybinds(Minecraft minecraft) {
      if (loadShaderPackWhenPossible) {
         loadShaderPackWhenPossible = false;
         loadShaderpack();
      }

      if (reloadKeybind.consumeClick()) {
         try {
            reload();
            if (minecraft.player != null) {
               minecraft.player.displayClientMessage(Component.translatable("iris.shaders.reloaded"), false);
            }
         } catch (Exception var3) {
            logger.error("Error while reloading Shaders for Iris!", var3);
            if (minecraft.player != null) {
               minecraft.player
                  .displayClientMessage(
                     Component.translatable("iris.shaders.reloaded.failure", new Object[]{Throwables.getRootCause(var3).getMessage()})
                        .withStyle(ChatFormatting.RED),
                     false
                  );
            }
         }
      } else if (toggleShadersKeybind.consumeClick()) {
         try {
            toggleShaders(minecraft, !irisConfig.areShadersEnabled());
         } catch (Exception var2) {
            logger.error("Error while toggling shaders!", var2);
            if (minecraft.player != null) {
               minecraft.player
                  .displayClientMessage(
                     Component.translatable("iris.shaders.toggled.failure", new Object[]{Throwables.getRootCause(var2).getMessage()})
                        .withStyle(ChatFormatting.RED),
                     false
                  );
            }

            setShadersDisabled();
            fallback = true;
         }
      } else if (shaderpackScreenKeybind.consumeClick()) {
         minecraft.setScreen(new ShaderPackScreen(null));
      } else if (wireframeKeybind.consumeClick() && irisConfig.areDebugOptionsEnabled() && minecraft.player != null && !Minecraft.getInstance().isLocalServer()
         )
       {
         minecraft.player.displayClientMessage(Component.literal("No cheating; wireframe only in singleplayer!"), false);
      }
   }

   public static boolean shouldActivateWireframe() {
      return irisConfig.areDebugOptionsEnabled() && wireframeKeybind.isDown();
   }

   public static void toggleShaders(Minecraft minecraft, boolean enabled) throws IOException {
      irisConfig.setShadersEnabled(enabled);
      irisConfig.save();
      reload();
      if (minecraft.player != null) {
         minecraft.player
            .displayClientMessage(
               enabled ? Component.translatable("iris.shaders.toggled", new Object[]{currentPackName}) : Component.translatable("iris.shaders.disabled"), false
            );
      }
   }

   public static void loadShaderpack() {
      if (irisConfig == null) {
         if (!initialized) {
            throw new IllegalStateException("Iris::loadShaderpack was called, but Iris::onInitializeClient wasn't called yet. How did this happen?");
         } else {
            throw new NullPointerException("Iris.irisConfig was null unexpectedly");
         }
      } else if (!irisConfig.areShadersEnabled()) {
         logger.info("Shaders are disabled because enableShaders is set to false in iris.properties");
         setShadersDisabled();
      } else {
         Optional<String> externalName = irisConfig.getShaderPackName();
         if (externalName.isEmpty()) {
            logger.info("Shaders are disabled because no valid shaderpack is selected");
            setShadersDisabled();
         } else {
            if (!loadExternalShaderpack(externalName.get())) {
               logger.warn("Falling back to normal rendering without shaders because the shaderpack could not be loaded");
               setShadersDisabled();
               fallback = true;
            }
         }
      }
   }

   private static boolean loadExternalShaderpack(String name) {
      Path shaderPackRoot;
      Path shaderPackConfigTxt;
      try {
         shaderPackRoot = getShaderpacksDirectory().resolve(name);
         shaderPackConfigTxt = getShaderpacksDirectory().resolve(name + ".txt");
      } catch (InvalidPathException var12) {
         logger.error("Failed to load the shaderpack \"{}\" because it contains invalid characters in its path", name);
         return false;
      }

      if (!isValidShaderpack(shaderPackRoot)) {
         logger.error("Pack \"{}\" is not valid! Can't load it.", name);
         return false;
      } else {
         boolean isZip = false;
         Path shaderPackPath;
         if (!Files.isDirectory(shaderPackRoot) && shaderPackRoot.toString().endsWith(".zip")) {
            Optional<Path> optionalPath;
            try {
               optionalPath = loadExternalZipShaderpack(shaderPackRoot);
            } catch (NoSuchFileException | FileSystemNotFoundException var9) {
               logger.error("Failed to load the shaderpack \"{}\" because it does not exist in your shaderpacks folder!", name);
               return false;
            } catch (ZipException var10) {
               logger.error("The shaderpack \"{}\" appears to be corrupted, please try downloading it again!", name);
               return false;
            } catch (IOException var11) {
               logger.error("Failed to load the shaderpack \"{}\"!", name);
               logger.error("", var11);
               return false;
            }

            if (!optionalPath.isPresent()) {
               logger.error("Could not load the shaderpack \"{}\" because it appears to lack a \"shaders\" directory", name);
               return false;
            }

            shaderPackPath = optionalPath.get();
            isZip = true;
         } else {
            if (!Files.exists(shaderPackRoot)) {
               logger.error("Failed to load the shaderpack \"{}\" because it does not exist!", name);
               return false;
            }

            shaderPackPath = shaderPackRoot.resolve("shaders");
         }

         if (!Files.exists(shaderPackPath)) {
            logger.error("Could not load the shaderpack \"{}\" because it appears to lack a \"shaders\" directory", name);
            return false;
         } else {
            Map<String, String> changedConfigs = tryReadConfigProperties(shaderPackConfigTxt).map(properties -> (Properties)properties).orElse(new HashMap<>());
            changedConfigs.putAll(shaderPackOptionQueue);
            clearShaderPackOptionQueue();
            if (resetShaderPackOptions) {
               changedConfigs.clear();
            }

            resetShaderPackOptions = false;

            try {
               currentPack = new ShaderPack(shaderPackPath, changedConfigs, StandardMacros.createStandardEnvironmentDefines(), isZip);
               MutableOptionValues changedConfigsValues = currentPack.getShaderPackOptions().getOptionValues().mutableCopy();
               Properties configsToSave = new Properties();
               changedConfigsValues.getBooleanValues().forEach((k, v) -> configsToSave.setProperty(k, Boolean.toString(v)));
               changedConfigsValues.getStringValues().forEach(configsToSave::setProperty);
               tryUpdateConfigPropertiesFile(shaderPackConfigTxt, configsToSave);
            } catch (Exception var8) {
               logger.error("Failed to load the shaderpack \"{}\"!", name);
               logger.error("", var8);
               handleException(var8);
               return false;
            }

            fallback = false;
            currentPackName = name;
            logger.info("Using shaderpack: " + name);
            return true;
         }
      }
   }

   private static void handleException(Exception e) {
      if (lastDimension != null && irisConfig.areDebugOptionsEnabled()) {
         Minecraft.getInstance()
            .setScreen(
               new DebugLoadFailedGridScreen(
                  Minecraft.getInstance().screen, Component.literal(e instanceof ShaderCompileException ? "Failed to compile shaders" : "Exception"), e
               )
            );
      } else if (Minecraft.getInstance().player != null) {
         Minecraft.getInstance()
            .player
            .displayClientMessage(
               Component.translatable(e instanceof ShaderCompileException ? "iris.load.failure.shader" : "iris.load.failure.generic")
                  .append(
                     Component.literal("Copy Info")
                        .withStyle(
                           arg -> arg.withUnderlined(true)
                              .withColor(ChatFormatting.BLUE)
                              .withClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, e.getMessage()))
                              .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))
                        )
                  ),
               false
            );
      } else {
         storedError = Optional.of(e);
      }
   }

   private static Optional<Path> loadExternalZipShaderpack(Path shaderpackPath) throws IOException {
      FileSystem zipSystem = FileSystems.newFileSystem(shaderpackPath, Iris.class.getClassLoader());
      zipFileSystem = zipSystem;
      Path root = zipSystem.getRootDirectories().iterator().next();
      Path potentialShaderDir = zipSystem.getPath("shaders");
      if (Files.exists(potentialShaderDir)) {
         return Optional.of(potentialShaderDir);
      } else {
         Optional var5;
         try (Stream<Path> stream = Files.walk(root)) {
            var5 = stream.filter(x$0 -> Files.isDirectory(x$0)).filter(path -> path.endsWith("shaders")).findFirst();
         }

         return var5;
      }
   }

   private static void setShadersDisabled() {
      currentPack = null;
      fallback = false;
      currentPackName = "(off)";
   }

   public static void setDebug(boolean enable) {
      try {
         irisConfig.setDebugEnabled(enable);
         irisConfig.save();
      } catch (IOException var2) {
         logger.fatal("Failed to save config!", var2);
      }

      int success;
      if (enable) {
         success = GLDebug.setupDebugMessageCallback();
      } else {
         GLDebug.reloadDebugState();
         GlDebug.enableDebugCallback(Minecraft.getInstance().options.glDebugVerbosity, false);
         success = 1;
      }

      logger.info("Debug functionality is " + (enable ? "enabled, logging will be more verbose!" : "disabled."));
      if (Minecraft.getInstance().player != null) {
         if (IrisPlatformHelpers.getInstance().useELS()) {
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("iris.shaders.debug.restartNoDebug"), false);
         } else {
            Minecraft.getInstance()
               .player
               .displayClientMessage(
                  Component.translatable(success != 0 ? (enable ? "iris.shaders.debug.enabled" : "iris.shaders.debug.disabled") : "iris.shaders.debug.failure"),
                  false
               );
         }

         if (success == 2 && !IrisPlatformHelpers.getInstance().useELS()) {
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("iris.shaders.debug.restart"), false);
         }
      }
   }

   private static Optional<Properties> tryReadConfigProperties(Path path) {
      Properties properties = new Properties();
      if (Files.exists(path)) {
         try (InputStream is = Files.newInputStream(path)) {
            properties.load(is);
         } catch (IOException var7) {
            return Optional.empty();
         }
      }

      return Optional.of(properties);
   }

   private static void tryUpdateConfigPropertiesFile(Path path, Properties properties) {
      try {
         if (properties.isEmpty()) {
            if (Files.exists(path)) {
               Files.delete(path);
            }

            return;
         }

         try (OutputStream out = Files.newOutputStream(path)) {
            properties.store(out, null);
         }
      } catch (IOException var7) {
      }
   }

   public static boolean isValidToShowPack(Path pack) {
      return Files.isDirectory(pack) || pack.toString().endsWith(".zip");
   }

   public static boolean isValidShaderpack(Path pack) {
      if (Files.isDirectory(pack)) {
         if (pack.equals(getShaderpacksDirectory())) {
            return false;
         } else {
            try {
               boolean var16;
               try (Stream<Path> stream = Files.walk(pack)) {
                  var16 = stream.filter(x$0 -> Files.isDirectory(x$0)).filter(path -> !path.equals(pack)).anyMatch(path -> path.endsWith("shaders"));
               }

               return var16;
            } catch (IOException var10) {
               return false;
            }
         }
      } else {
         if (pack.toString().endsWith(".zip")) {
            try {
               boolean var4;
               try (FileSystem zipSystem = FileSystems.newFileSystem(pack, Iris.class.getClassLoader())) {
                  Path root = zipSystem.getRootDirectories().iterator().next();

                  try (Stream<Path> stream = Files.walk(root)) {
                     var4 = stream.filter(x$0 -> Files.isDirectory(x$0)).anyMatch(path -> path.endsWith("shaders"));
                  }
               }

               return var4;
            } catch (ZipError var13) {
               logger.warn("The ZIP at " + pack + " is corrupt");
            } catch (IOException var14) {
            }
         }

         return false;
      }
   }

   public static Map<String, String> getShaderPackOptionQueue() {
      return shaderPackOptionQueue;
   }

   public static void queueShaderPackOptionsFromProfile(Profile profile) {
      getShaderPackOptionQueue().putAll(profile.optionValues);
   }

   public static void queueShaderPackOptionsFromProperties(Properties properties) {
      queueDefaultShaderPackOptionValues();
      properties.stringPropertyNames().forEach(key -> getShaderPackOptionQueue().put(key, properties.getProperty(key)));
   }

   public static void queueDefaultShaderPackOptionValues() {
      clearShaderPackOptionQueue();
      getCurrentPack().ifPresent(pack -> {
         OptionSet options = pack.getShaderPackOptions().getOptionSet();
         OptionValues values = pack.getShaderPackOptions().getOptionValues();
         options.getStringOptions().forEach((key, mOpt) -> {
            if (values.getStringValue(key).isPresent()) {
               getShaderPackOptionQueue().put(key, mOpt.getOption().getDefaultValue());
            }
         });
         options.getBooleanOptions().forEach((key, mOpt) -> {
            if (values.getBooleanValue(key) != OptionalBoolean.DEFAULT) {
               getShaderPackOptionQueue().put(key, Boolean.toString(mOpt.getOption().getDefaultValue()));
            }
         });
      });
   }

   public static void clearShaderPackOptionQueue() {
      getShaderPackOptionQueue().clear();
   }

   public static void resetShaderPackOptionsOnNextReload() {
      resetShaderPackOptions = true;
   }

   public static boolean shouldResetShaderPackOptionsOnNextReload() {
      return resetShaderPackOptions;
   }

   public static void reload() throws IOException {
      irisConfig.initialize();
      destroyEverything();
      loadShaderpack();
      if (Minecraft.getInstance().level != null) {
         getPipelineManager().preparePipeline(getCurrentDimension());
      }
   }

   private static void destroyEverything() {
      currentPack = null;
      getPipelineManager().destroyPipeline();
      if (zipFileSystem != null) {
         try {
            zipFileSystem.close();
         } catch (NoSuchFileException var1) {
            logger.warn("Failed to close the shaderpack zip when reloading because it was deleted, proceeding anyways.");
         } catch (IOException var2) {
            logger.error("Failed to close zip file system?", var2);
         }
      }
   }

   public static NamespacedId getCurrentDimension() {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         NamespacedId dimensionId = new NamespacedId(level.dimension().location().getNamespace(), level.dimension().location().getPath());
         ShaderPack pack = getCurrentPack().orElse(null);
         if (pack != null && pack.getDimensionMap().containsKey(dimensionId)) {
            return dimensionId;
         } else {
            ResourceLocation effects = level.dimensionType().effectsLocation();
            if (Level.END.location().equals(effects)) {
               return DimensionId.END;
            } else {
               return Level.NETHER.location().equals(effects) ? DimensionId.NETHER : dimensionId;
            }
         }
      } else {
         return lastDimension;
      }
   }

   private static WorldRenderingPipeline createPipeline(NamespacedId dimensionId) {
      if (currentPack == null) {
         return new VanillaRenderingPipeline();
      } else {
         ProgramSet programs = currentPack.getProgramSet(dimensionId);

         try {
            return new IrisRenderingPipeline(programs);
         } catch (Exception var3) {
            handleException(var3);
            ShaderStorageBufferHolder.forceDeleteBuffers();
            logger.error("Failed to create shader rendering pipeline, disabling shaders!", var3);
            fallback = true;
            return new VanillaRenderingPipeline();
         }
      }
   }

   @NotNull
   public static PipelineManager getPipelineManager() {
      if (pipelineManager == null) {
         pipelineManager = new PipelineManager(Iris::createPipeline);
      }

      return pipelineManager;
   }

   public static Optional<Exception> getStoredError() {
      Optional<Exception> stored = storedError;
      storedError = Optional.empty();
      return stored;
   }

   @NotNull
   public static Optional<ShaderPack> getCurrentPack() {
      return Optional.ofNullable(currentPack);
   }

   public static String getCurrentPackName() {
      return currentPackName;
   }

   public static IrisConfig getIrisConfig() {
      return irisConfig;
   }

   public static UpdateChecker getUpdateChecker() {
      return updateChecker;
   }

   public static boolean isFallback() {
      return fallback;
   }

   public static String getVersion() {
      return IRIS_VERSION == null ? "Version info unknown!" : IRIS_VERSION;
   }

   public static String getFormattedVersion() {
      String version = getVersion();
      ChatFormatting color;
      if (IrisPlatformHelpers.getInstance().isDevelopmentEnvironment()) {
         color = ChatFormatting.GOLD;
         version = version + " (Development Environment)";
      } else if (version.endsWith("-dirty") || version.contains("unknown") || version.endsWith("-nogit")) {
         color = ChatFormatting.RED;
      } else if (version.contains("+rev.")) {
         color = ChatFormatting.LIGHT_PURPLE;
      } else {
         color = ChatFormatting.GREEN;
      }

      return color + version;
   }

   public static String getReleaseTarget() {
      SharedConstants.tryDetectVersion();
      return SharedConstants.getCurrentVersion().isStable() ? SharedConstants.getCurrentVersion().getName() : "1.21";
   }

   public static String getBackupVersionNumber() {
      return "1.21";
   }

   public static Path getShaderpacksDirectory() {
      if (shaderpacksDirectory == null) {
         shaderpacksDirectory = IrisPlatformHelpers.getInstance().getGameDir().resolve("shaderpacks");
      }

      return shaderpacksDirectory;
   }

   public static ShaderpackDirectoryManager getShaderpacksDirectoryManager() {
      if (shaderpacksDirectoryManager == null) {
         shaderpacksDirectoryManager = new ShaderpackDirectoryManager(getShaderpacksDirectory());
      }

      return shaderpacksDirectoryManager;
   }

   public static boolean loadedIncompatiblePack() {
      return DHCompat.lastPackIncompatible();
   }

   public static boolean isPackInUseQuick() {
      return getPipelineManager().getPipelineNullable() instanceof IrisRenderingPipeline;
   }

   public static void loadShaderpackWhenPossible() {
      loadShaderPackWhenPossible = true;
   }

   public static String getVersionSimple() {
      return getVersion().split("\\+")[0];
   }

   public void onEarlyInitialize() {
      IRIS_VERSION = IrisPlatformHelpers.getInstance().getVersion();
      updateChecker = new UpdateChecker(IRIS_VERSION);
      reloadKeybind = IrisPlatformHelpers.getInstance().registerKeyBinding(new KeyMapping("iris.keybind.reload", Type.KEYSYM, 82, "iris.keybinds"));
      toggleShadersKeybind = IrisPlatformHelpers.getInstance()
         .registerKeyBinding(new KeyMapping("iris.keybind.toggleShaders", Type.KEYSYM, 75, "iris.keybinds"));
      shaderpackScreenKeybind = IrisPlatformHelpers.getInstance()
         .registerKeyBinding(new KeyMapping("iris.keybind.shaderPackSelection", Type.KEYSYM, 79, "iris.keybinds"));
      wireframeKeybind = IrisPlatformHelpers.getInstance()
         .registerKeyBinding(new KeyMapping("iris.keybind.wireframe", Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "iris.keybinds"));
      DHCompat.run();

      try {
         if (!Files.exists(getShaderpacksDirectory())) {
            Files.createDirectories(getShaderpacksDirectory());
         }
      } catch (IOException var3) {
         logger.warn("Failed to create the shaderpacks directory!");
         logger.warn("", var3);
      }

      irisConfig = new IrisConfig(
         IrisPlatformHelpers.getInstance().getConfigDir().resolve("iris.properties"),
         IrisPlatformHelpers.getInstance().getConfigDir().resolve("iris-excluded.json")
      );

      try {
         irisConfig.initialize();
      } catch (IOException var2) {
         logger.error("Failed to initialize Iris configuration, default values will be used instead");
         logger.error("", var2);
      }

      updateChecker.checkForUpdates(irisConfig);
      initialized = true;
   }

   static {
      if (IrisPlatformHelpers.getInstance().isDevelopmentEnvironment() && System.getProperty("user.name").contains("ims") && Util.getPlatform() == OS.LINUX) {
      }
   }
}
