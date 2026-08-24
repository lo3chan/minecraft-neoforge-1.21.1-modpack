package com.seibel.distanthorizons.core.config;

import com.seibel.distanthorizons.api.enums.config.EDhApiBlocksToAvoid;
import com.seibel.distanthorizons.api.enums.config.EDhApiDataCompressionMode;
import com.seibel.distanthorizons.api.enums.config.EDhApiGLErrorHandlingMode;
import com.seibel.distanthorizons.api.enums.config.EDhApiGrassSideRendering;
import com.seibel.distanthorizons.api.enums.config.EDhApiHorizontalQuality;
import com.seibel.distanthorizons.api.enums.config.EDhApiLodShading;
import com.seibel.distanthorizons.api.enums.config.EDhApiLoggerLevel;
import com.seibel.distanthorizons.api.enums.config.EDhApiMaxHorizontalResolution;
import com.seibel.distanthorizons.api.enums.config.EDhApiMcRenderingFadeMode;
import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingEngine;
import com.seibel.distanthorizons.api.enums.config.EDhApiServerFolderNameMode;
import com.seibel.distanthorizons.api.enums.config.EDhApiUpdateBranch;
import com.seibel.distanthorizons.api.enums.config.EDhApiVerticalQuality;
import com.seibel.distanthorizons.api.enums.config.EDhApiWorldCompressionMode;
import com.seibel.distanthorizons.api.enums.config.quickOptions.EDhApiQualityPreset;
import com.seibel.distanthorizons.api.enums.config.quickOptions.EDhApiThreadPreset;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiDebugRendering;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogColorMode;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogFalloff;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogDirection;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogMixMode;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiRendererMode;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiTransparency;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorProgressDisplayLocation;
import com.seibel.distanthorizons.core.config.eventHandlers.IgnoredDimensionCsvHandler;
import com.seibel.distanthorizons.core.config.eventHandlers.QuickRenderToggleConfigEventHandler;
import com.seibel.distanthorizons.core.config.eventHandlers.ReloadLodsConfigEventHandler;
import com.seibel.distanthorizons.core.config.eventHandlers.RenderBlockCacheCsvHandler;
import com.seibel.distanthorizons.core.config.eventHandlers.UnsafeValuesConfigListener;
import com.seibel.distanthorizons.core.config.eventHandlers.WorldCurvatureConfigEventHandler;
import com.seibel.distanthorizons.core.config.eventHandlers.presets.RenderQualityPresetConfigEventHandler;
import com.seibel.distanthorizons.core.config.eventHandlers.presets.ThreadPresetConfigEventHandler;
import com.seibel.distanthorizons.core.config.types.ConfigCategory;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.core.config.types.ConfigUIButton;
import com.seibel.distanthorizons.core.config.types.ConfigUIComment;
import com.seibel.distanthorizons.core.config.types.ConfigUISpacer;
import com.seibel.distanthorizons.core.config.types.ConfigUiLinkedEntry;
import com.seibel.distanthorizons.core.config.types.enums.EConfigCommentTextPosition;
import com.seibel.distanthorizons.core.config.types.enums.EConfigEntryAppearance;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import com.seibel.distanthorizons.coreapi.ModInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Config {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static ConfigCategory client = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.class)).build();
   private static boolean complicatedListenerSetupComplete = false;

   public static void completeDelayedSetup() {
      if (!complicatedListenerSetupComplete) {
         complicatedListenerSetupComplete = true;

         try {
            ThreadPresetConfigEventHandler.INSTANCE.setUiOnlyConfigValues();
            RenderQualityPresetConfigEventHandler.INSTANCE.setUiOnlyConfigValues();
            QuickRenderToggleConfigEventHandler.INSTANCE.setUiOnlyConfigValues();
            IgnoredDimensionCsvHandler.INSTANCE.onConfigValueSet();
         } catch (Exception var1) {
            LOGGER.error("Unexpected exception when running config delayed UI setup. Error: [" + var1.getMessage() + "].", var1);
         }
      }
   }

   private static boolean isRunningInDevEnvironment() {
      IMinecraftSharedWrapper mcShared = SingletonInjector.INSTANCE.get(IMinecraftSharedWrapper.class);
      File installFolder = mcShared.getInstallationDirectory();
      File installParentFolder = installFolder.getParentFile();
      return installParentFolder == null
            || !installParentFolder.getName().equals("run")
            || !installFolder.getName().equals("client") && !installFolder.getName().equals("server")
         ? installFolder.getName().equals("run")
         : true;
   }

   public static class Client {
      public static ConfigEntry<Boolean> quickEnableRendering = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
            .setAppearance(EConfigEntryAppearance.ONLY_IN_GUI))
         .build();
      public static ConfigUiLinkedEntry quickLodChunkRenderDistance = new ConfigUiLinkedEntry(
         Config.Client.Advanced.Graphics.Quality.lodChunkRenderDistanceRadius
      );
      public static ConfigEntry<EDhApiQualityPreset> qualityPresetSetting = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
               .set(EDhApiQualityPreset.MEDIUM))
            .setAppearance(EConfigEntryAppearance.ONLY_IN_GUI))
         .addListener(RenderQualityPresetConfigEventHandler.INSTANCE)
         .build();
      public static ConfigEntry<EDhApiThreadPreset> threadPresetSetting = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("common.threadPreset")
               .set(EDhApiThreadPreset.BALANCED))
            .setAppearance(EConfigEntryAppearance.ONLY_IN_GUI))
         .addListener(ThreadPresetConfigEventHandler.INSTANCE)
         .build();
      public static ConfigUiLinkedEntry quickEnableWorldGenerator = new ConfigUiLinkedEntry(Config.Common.WorldGenerator.enableDistantGeneration);
      public static ConfigUiLinkedEntry quickEnableServerGeneration = new ConfigUiLinkedEntry(Config.Server.enableServerGeneration);
      public static ConfigUiLinkedEntry quickShowWorldGenProgress = new ConfigUiLinkedEntry(Config.Common.WorldGenerator.showGenerationProgress);
      public static ConfigUiLinkedEntry quickLodCloudRendering = new ConfigUiLinkedEntry(Config.Client.Advanced.Graphics.GenericRendering.enableCloudRendering);
      public static ConfigEntry<Boolean> showDhOptionsButtonInMinecraftUi = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
            .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
         .comment("Should Distant Horizon's config button appear in Minecraft's options screen next to the fov slider?")
         .build();
      public static ConfigCategory advanced = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.Advanced.class)).build();

      public static class Advanced {
         public static ConfigUIComment advancedHeader = new ConfigUIComment.Builder().setParentConfigClass(Config.Client.Advanced.class).build();
         public static ConfigCategory graphics = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.Advanced.Graphics.class)).build();
         public static ConfigCategory worldGenerator = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Common.WorldGenerator.class))
            .setDestination("common.worldGenerator")
            .build();
         public static ConfigCategory multiplayer = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.Advanced.Multiplayer.class))
            .build();
         public static ConfigCategory server = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Server.class)).setDestination("server").build();
         public static ConfigCategory lodBuilding = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Common.LodBuilding.class))
            .setDestination("common.lodBuilding")
            .build();
         public static ConfigCategory multiThreading = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Common.MultiThreading.class))
            .setDestination("common.multiThreading")
            .build();
         public static ConfigCategory autoUpdater = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.Advanced.AutoUpdater.class))
            .build();
         public static ConfigCategory logging = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Common.Logging.class))
            .setDestination("common.logging")
            .build();
         public static ConfigCategory debugging = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.Advanced.Debugging.class)).build();

         public static class AutoUpdater {
            public static ConfigUIComment autoUpdaterHeader = new ConfigUIComment.Builder()
               .setParentConfigClass(Config.Client.Advanced.AutoUpdater.class)
               .build();
            public static ConfigEntry<Boolean> enableAutoUpdater = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(!Config.isRunningInDevEnvironment()))
               .comment("Automatically check for updates on game launch? \n")
               .build();
            public static ConfigEntry<Boolean> enableSilentUpdates = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
               .comment(
                  "Should Distant Horizons silently, automatically download and install new versions? \nThis setting is force disabled on dedicated servers for stability reasons. \n"
               )
               .build();
            public static ConfigEntry<EDhApiUpdateBranch> updateBranch = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(EDhApiUpdateBranch.AUTO))
               .comment(
                  "If DH should use the nightly (provided by Gitlab), or stable (provided by Modrinth) build. \nIf ["
                     + EDhApiUpdateBranch.AUTO
                     + "] is selected DH will update to new stable releases if the current jar is a stable jar \nand will update to new nightly builds if the current jar is a nightly jar (IE the version number ends in '-dev')."
               )
               .build();
         }

         public static class Debugging {
            public static ConfigUIComment debuggingHeader = new ConfigUIComment.Builder().setParentConfigClass(Config.Client.Advanced.Debugging.class).build();
            public static ConfigEntry<EDhApiRendererMode> rendererMode = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(EDhApiRendererMode.DEFAULT))
               .comment(
                  "What renderer is active? \n\n"
                     + EDhApiRendererMode.DEFAULT
                     + ": Default lod renderer \n"
                     + EDhApiRendererMode.DEBUG_TRIANGLE
                     + ": Debug testing renderer \n"
                     + EDhApiRendererMode.DISABLED
                     + ": Disable rendering"
               )
               .build();
            public static ConfigEntry<EDhApiDebugRendering> debugRenderingColors = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                  .set(EDhApiDebugRendering.OFF))
               .comment(
                  "Should specialized colors/rendering modes be used? \n\n"
                     + EDhApiDebugRendering.OFF
                     + ": LODs will be drawn with their normal colors. \n"
                     + EDhApiDebugRendering.SHOW_DETAIL
                     + ": LODs' color will be based on their detail level. \n"
                     + EDhApiDebugRendering.SHOW_BLOCK_MATERIAL
                     + ": LODs' color will be based on their material. \n"
                     + EDhApiDebugRendering.SHOW_OVERLAPPING_QUADS
                     + ": LODs will be drawn with total white, but overlapping quads will be drawn with red. \n"
               )
               .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
               .build();
            public static ConfigUISpacer debugRenderingSpacer = new ConfigUISpacer.Builder().build();
            public static ConfigEntry<Boolean> enableWhiteWorld = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
               .comment("Stops vertex colors from being passed. \nUseful for debugging shaders")
               .build();
            public static ConfigEntry<Boolean> lodOnlyMode = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
               .comment(
                  "If enabled this will disable (most) vanilla Minecraft rendering. \n\nNOTE: Do not report any issues when this mode is on! \n   This setting is only for fun and debugging. \n   Mod compatibility is not guaranteed."
               )
               .build();
            public static ConfigEntry<Boolean> renderWireframe = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
               .comment("If enabled the LODs will render as wireframe.")
               .build();
            public static ConfigEntry<Boolean> showOverlappingQuadErrors = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
               .comment("If true overlapping quads will be rendered as bright red for easy identification. \nIf false the quads will be rendered normally. \n")
               .build();
            public static ConfigUISpacer miscSpacer = new ConfigUISpacer.Builder().build();
            public static ConfigEntry<Boolean> enableDebugKeybindings = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
               .comment(
                  "If true several keys can be used to toggle debug states. \nF6 - enable/disable LOD rendering \nF7 - enable/disable LOD only rendering \nF8 - cycle through the different debug rendering modes \n"
               )
               .build();
            public static ConfigEntry<Boolean> logBufferGarbageCollection = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
               .comment("If true OpenGL Buffer garbage collection will be logged \nthis also includes the number of live buffers. \n")
               .build();
            public static ConfigUISpacer unsafeSpacer = new ConfigUISpacer.Builder().build();
            public static ConfigEntry<Boolean> allowUnsafeValues = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .setAppearance(EConfigEntryAppearance.ONLY_IN_GUI))
               .addListener(UnsafeValuesConfigListener.INSTANCE)
               .build();
            public static ConfigUISpacer categorySpacer = new ConfigUISpacer.Builder().build();
            public static ConfigCategory debugWireframe = ((ConfigCategory.Builder)new ConfigCategory.Builder()
                  .set(Config.Client.Advanced.Debugging.DebugWireframe.class))
               .build();
            public static ConfigCategory openGl = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.Advanced.Debugging.OpenGl.class))
               .build();
            public static ConfigCategory columnBuilderDebugging = ((ConfigCategory.Builder)new ConfigCategory.Builder()
                  .set(Config.Client.Advanced.Debugging.ColumnBuilderDebugging.class))
               .build();
            public static ConfigCategory positionFinderDebugging = ((ConfigCategory.Builder)new ConfigCategory.Builder()
                  .set(Config.Client.Advanced.Debugging.PositionFinder.class))
               .build();
            public static ConfigCategory f3Screen = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.Advanced.Debugging.F3Screen.class))
               .build();
            public static ConfigCategory exampleConfigScreen = ((ConfigCategory.Builder)new ConfigCategory.Builder()
                  .set(Config.Client.Advanced.Debugging.ExampleConfigScreen.class))
               .build();

            public static class ColumnBuilderDebugging {
               public static ConfigUIComment columnBuilderDebuggingHeader = new ConfigUIComment.Builder()
                  .setParentConfigClass(Config.Client.Advanced.Debugging.ColumnBuilderDebugging.class)
                  .build();
               public static ConfigEntry<Boolean> columnBuilderDebugEnable = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_GUI))
                  .addListener(ReloadLodsConfigEventHandler.INSTANT_INSTANCE)
                  .build();
               public static ConfigEntry<Integer> columnBuilderDebugDetailLevel = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(6))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_GUI))
                  .addListener(ReloadLodsConfigEventHandler.INSTANT_INSTANCE)
                  .build();
               public static ConfigEntry<Integer> columnBuilderDebugXPos = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(0))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_GUI))
                  .addListener(ReloadLodsConfigEventHandler.INSTANT_INSTANCE)
                  .build();
               public static ConfigEntry<Integer> columnBuilderDebugZPos = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(0))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_GUI))
                  .addListener(ReloadLodsConfigEventHandler.INSTANT_INSTANCE)
                  .build();
               public static ConfigUISpacer subLodSpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<Integer> columnBuilderDebugXRow = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(-1))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_GUI))
                  .addListener(ReloadLodsConfigEventHandler.INSTANT_INSTANCE)
                  .build();
               public static ConfigEntry<Integer> columnBuilderDebugZRow = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(-1))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_GUI))
                  .addListener(ReloadLodsConfigEventHandler.INSTANT_INSTANCE)
                  .build();
               public static ConfigEntry<Integer> columnBuilderDebugColumnIndex = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(-1))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_GUI))
                  .addListener(ReloadLodsConfigEventHandler.INSTANT_INSTANCE)
                  .build();
            }

            public static class DebugWireframe {
               public static ConfigUIComment debugWireframeHeader = new ConfigUIComment.Builder()
                  .setParentConfigClass(Config.Client.Advanced.Debugging.DebugWireframe.class)
                  .build();
               public static ConfigEntry<Boolean> enableRendering = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment(
                     "If enabled, various wireframes for debugging internal functions will be drawn. \n\nNOTE: There WILL be performance hit! \n   Additionally, only stuff that's loaded after you enable this \n   will render their debug wireframes. \n"
                  )
                  .build();
               public static ConfigUISpacer wireframeOptionSpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<Boolean> showWorldGenQueue = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment("Render queued world gen tasks?")
                  .build();
               public static ConfigEntry<Boolean> showNetworkSyncOnLoadQueue = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment("Render queued network sync on load tasks?")
                  .build();
               public static ConfigEntry<Boolean> showRenderSectionStatus = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment("Render LOD section status?")
                  .build();
               public static ConfigEntry<Boolean> showQuadTreeRenderStatus = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment("Render Quad Tree Rendering status?")
                  .build();
               public static ConfigEntry<Boolean> showFullDataUpdateStatus = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment("Render full data update/lock status?")
                  .build();
            }

            public static class ExampleConfigScreen {
               public static ConfigUIComment exampleConfigHeader = new ConfigUIComment.Builder()
                  .setParentConfigClass(Config.Client.Advanced.Debugging.ExampleConfigScreen.class)
                  .build();
               public static ConfigUIComment debugConfigScreenNote = new ConfigUIComment.Builder()
                  .setTextPosition(EConfigCommentTextPosition.CENTER_OF_SCREEN)
                  .build();
               public static ConfigEntry<Boolean> boolTest = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false)).build();
               public static ConfigEntry<Byte> byteTest = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set((byte)8))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
                  .build();
               public static ConfigEntry<Integer> intTest = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(69420)).build();
               public static ConfigEntry<Double> doubleTest = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(420.69)).build();
               public static ConfigEntry<Short> shortTest = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set((short)69))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
                  .build();
               public static ConfigEntry<Long> longTest = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(42069L))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
                  .build();
               public static ConfigEntry<Float> floatTest = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(0.42069F))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
                  .build();
               public static ConfigEntry<String> stringTest = ((ConfigEntry.Builder)new ConfigEntry.Builder().set("Test input box")).build();
               public static ConfigEntry<List<String>> listTest = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set(new ArrayList<>(Arrays.asList("option 1", "option 2", "option 3"))))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
                  .build();
               public static ConfigEntry<Map<String, String>> mapTest = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set(new HashMap()))
                     .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
                  .build();
               public static ConfigUIButton uiButtonTest = new ConfigUIButton(
                  () -> new Thread(Config.Client.Advanced.Debugging.ExampleConfigScreen::onButtonPressed).start()
               );
               public static ConfigCategory categoryTest = ((ConfigCategory.Builder)new ConfigCategory.Builder()
                     .set(Config.Client.Advanced.Debugging.ExampleConfigScreen.CategoryTest.class))
                  .build();
               public static ConfigEntry<Integer> linkableTest = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(420)).build();

               public static void onButtonPressed() {
                  IMinecraftClientWrapper mcClient = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
                  Config.LOGGER.info("Attempting to show tinyfd message box...");
                  mcClient.showDialog("Button pressed!", "UITester dialog", "ok", "info");
                  Config.LOGGER.info("dialog closed");
               }

               public static class CategoryTest {
                  public static ConfigUiLinkedEntry linkableTest = new ConfigUiLinkedEntry(Config.Client.Advanced.Debugging.ExampleConfigScreen.linkableTest);
               }
            }

            public static class F3Screen {
               public static ConfigUIComment f3ScreenHeader = new ConfigUIComment.Builder()
                  .setParentConfigClass(Config.Client.Advanced.Debugging.F3Screen.class)
                  .build();
               public static ConfigEntry<Boolean> showPlayerPos = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment("Shows the player's LOD position.")
                  .build();
               public static ConfigEntry<Integer> playerPosSectionDetailLevel = new ConfigEntry.Builder<Integer>()
                  .setMinDefaultMax(6, 6, 16)
                  .comment(
                     "Defines what internal detail level the player position will be shown as. \nInternal detail level means: 6 = 1x1 block, 7 = 2x2 blocks, etc. \n"
                  )
                  .build();
               public static ConfigEntry<Boolean> showThreadPools = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment("Shows info about each thread pool.")
                  .build();
               public static ConfigEntry<Boolean> showRenderThreadTasks = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment("Shows info about the render thread tasks.")
                  .build();
               public static ConfigEntry<Boolean> showCombinedObjectPools = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment("Shows the combined memory use and array counts for all DH pooled objects.")
                  .build();
               public static ConfigEntry<Boolean> showSeparatedObjectPools = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment("Shows the memory use and array counts for each DH object pool.")
                  .build();
               public static ConfigEntry<Boolean> showQueuedChunkUpdateCount = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment("Shows how many chunks are queued for processing and the max count that can be queued.")
                  .build();
               public static ConfigEntry<Boolean> showLevelStatus = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment("Shows what levels are loaded and world gen/rendering info about those levels.")
                  .build();
               public static ConfigEntry<Boolean> onlyShowRenderingLevels = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment("Only show levels that DH is actively rendering.")
                  .build();
            }

            public static class OpenGl {
               public static ConfigUIComment openGlHeader = new ConfigUIComment.Builder()
                  .setParentConfigClass(Config.Client.Advanced.Debugging.OpenGl.class)
                  .build();
               public static ConfigEntry<Boolean> overrideVanillaGLLogger = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "Defines how OpenGL errors are handled. \n Requires rebooting Minecraft to change. \nWill catch OpenGL errors thrown by other mods. \n"
                  )
                  .build();
               public static ConfigEntry<Boolean> onlyLogGlErrorsOnce = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "If true each Open GL error will only be logged once. \nEnabling this may cause some error logs to be missed. \nDoes nothing if overrideVanillaGLLogger is set to false. \n \nGenerally this can be kept as 'true' to prevent log spam. \nHowever, Please set this to 'false' if a developer needs your log to debug a GL issue. \n"
                  )
                  .build();
               public static ConfigEntry<EDhApiGLErrorHandlingMode> glErrorHandlingMode = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                     .set(ModInfo.IS_DEV_BUILD ? EDhApiGLErrorHandlingMode.LOG : EDhApiGLErrorHandlingMode.IGNORE))
                  .comment(
                     "Defines how OpenGL errors are handled. \nMay incorrectly catch OpenGL errors thrown by other mods. \n\n"
                        + EDhApiGLErrorHandlingMode.IGNORE
                        + ": Do nothing. \n"
                        + EDhApiGLErrorHandlingMode.LOG
                        + ": write an error to the log. \n"
                        + EDhApiGLErrorHandlingMode.LOG_THROW
                        + ": write to the log and throw an exception. \n           Warning: this should only be enabled when debugging the LOD renderer \n           as it may break Minecraft's renderer when an exception is thrown. \n"
                  )
                  .build();
            }

            public static class PositionFinder {
               public static ConfigEntry<Boolean> positionFinderEnable = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false)).build();
               public static ConfigEntry<Integer> positionFinderDetailLevel = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(6)).build();
               public static ConfigEntry<Integer> positionFinderXPos = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(0)).build();
               public static ConfigEntry<Integer> positionFinderZPos = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(0)).build();
               public static ConfigUISpacer positionFinderBlockPosSpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<Integer> positionFinderMinBlockY = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(-64)).build();
               public static ConfigEntry<Integer> positionFinderMaxBlockY = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(125)).build();
               public static ConfigEntry<Float> positionFinderMarginPercent = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(0.0F)).build();
            }
         }

         public static class Graphics {
            public static ConfigUIComment advancedGraphicsHeader = new ConfigUIComment.Builder()
               .setParentConfigClass(Config.Client.Advanced.Graphics.class)
               .build();
            public static ConfigCategory quality = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.Advanced.Graphics.Quality.class))
               .build();
            public static ConfigUISpacer qualitySpacer = new ConfigUISpacer.Builder().build();
            public static ConfigEntry<Boolean> enableSsao = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment("Enable Screen Space Ambient Occlusion")
               .build();
            public static ConfigUISpacer ssaoSpacer = new ConfigUISpacer.Builder().build();
            public static ConfigUiLinkedEntry quickEnableGenericRendering = new ConfigUiLinkedEntry(
               Config.Client.Advanced.Graphics.GenericRendering.enableGenericRendering
            );
            public static ConfigCategory genericRendering = ((ConfigCategory.Builder)new ConfigCategory.Builder()
                  .set(Config.Client.Advanced.Graphics.GenericRendering.class))
               .build();
            public static ConfigUISpacer genericRenderingSpacer = new ConfigUISpacer.Builder().build();
            public static ConfigUiLinkedEntry quickEnableDhFog = new ConfigUiLinkedEntry(Config.Client.Advanced.Graphics.Fog.enableDhFog);
            public static ConfigUiLinkedEntry quickEnableMcFog = new ConfigUiLinkedEntry(Config.Client.Advanced.Graphics.Fog.enableVanillaFog);
            public static ConfigCategory fog = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.Advanced.Graphics.Fog.class)).build();
            public static ConfigUISpacer fogSpacer = new ConfigUISpacer.Builder().build();
            public static ConfigUiLinkedEntry quickEnableTexturedLods = new ConfigUiLinkedEntry(Config.Client.Advanced.Graphics.Texture.enableTexturedLods);
            public static ConfigCategory texture = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.Advanced.Graphics.Texture.class))
               .build();
            public static ConfigUiLinkedEntry quickEnableNoiseTexture = new ConfigUiLinkedEntry(Config.Client.Advanced.Graphics.NoiseTexture.enableNoiseTexture);
            public static ConfigCategory noiseTexture = ((ConfigCategory.Builder)new ConfigCategory.Builder()
                  .set(Config.Client.Advanced.Graphics.NoiseTexture.class))
               .build();
            public static ConfigUISpacer noiseTextureSpacer = new ConfigUISpacer.Builder().build();
            public static ConfigUiLinkedEntry quickEnableCaveCulling = new ConfigUiLinkedEntry(Config.Client.Advanced.Graphics.Culling.enableCaveCulling);
            public static ConfigCategory culling = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Client.Advanced.Graphics.Culling.class))
               .build();
            public static ConfigUISpacer cullingSpacer = new ConfigUISpacer.Builder().build();
            public static ConfigEntry<Boolean> overrideVanillaGraphicsSettings = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment(
                  "If true some vanilla graphics settings will be automatically changed \nduring DH setup to provide a better experience. \n \nIE disabling vanilla clouds (which render on top of DH LODs), \n   and chunk fading (DH already fades MC chunks) \n"
               )
               .build();
            public static ConfigUISpacer overrideVanillaSpacer = new ConfigUISpacer.Builder().build();
            public static ConfigCategory experimental = ((ConfigCategory.Builder)new ConfigCategory.Builder()
                  .set(Config.Client.Advanced.Graphics.Experimental.class))
               .build();

            public static class Culling {
               public static ConfigUIComment cullingHeader = new ConfigUIComment.Builder()
                  .setParentConfigClass(Config.Client.Advanced.Graphics.Culling.class)
                  .build();
               public static ConfigEntry<Float> overdrawPrevention = new ConfigEntry.Builder<Float>()
                  .setMinDefaultMax(-1.0F, -1.0F, 1.0F)
                  .comment(
                     "Determines how far from the camera Distant Horizons will start rendering. \nMeasured as a percentage of the vanilla render distance.\n\n-1 = auto, overdraw will change based on the vanilla render distance.\n\nHigher values will prevent LODs from rendering behind vanilla blocks at a higher distance,\nbut may cause holes in the world. \nHoles are most likely to appear when flying through unloaded terrain. \n\nIncreasing the vanilla render distance increases the effectiveness of this setting."
                  )
                  .build();
               public static ConfigEntry<Boolean> reduceOverdrawWithFastMovement = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "If set to true the overdraw prevention radius will get closer\nto the camera when flying/moving quickly.\n\nThis helps reduce issues where Minecraft can't load or\ngenerate chunks fast enough to keep up with DH.\n"
                  )
                  .build();
               public static ConfigUISpacer speedSpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<Boolean> enableCaveCulling = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "If enabled caves won't be rendered. \n\n Note: for some world types this can cause \n overhangs or walls for floating objects. \n Tweaking the caveCullingHeight, can resolve some \n of those issues. \n"
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigEntry<Integer> caveCullingHeight = new ConfigEntry.Builder<Integer>()
                  .setMinDefaultMax(-4096, 60, 4096)
                  .comment("At what Y value should cave culling start? \nLower this value if you get walls for areas with 0 light.")
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigUISpacer caveCullingSpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<Boolean> disableBeaconDistanceCulling = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "If false all beacons near the camera won't be drawn to prevent vanilla overdraw. \nIf true all beacons will be rendered. \n\nGenerally this should be left as true. It's main purpose is for debugging\nbeacon updating/rendering.\n"
                  )
                  .build();
               public static ConfigEntry<Boolean> disableFrustumCulling = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment(
                     "If true LODs outside the player's camera \naren't drawn, increasing GPU performance. \n\nIf false all LODs are drawn, even those behind \nthe player's camera, decreasing GPU performance. \n\nDisable this if you see LODs disappearing at the corners of your vision."
                  )
                  .build();
               public static ConfigEntry<Boolean> disableShadowPassFrustumCulling = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment(
                     "Identical to the other frustum culling option\nonly used when a shader mod is present using the DH API\nand the shadow pass is being rendered.\n\nDisable this if shadows render incorrectly."
                  )
                  .build();
               public static ConfigUISpacer ignoreCsvStartSpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<String> ignoredRenderBlockCsv = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set("minecraft:barrier,minecraft:structure_void,minecraft:light,minecraft:tripwire,minecraft:brown_mushroom"))
                     .setAppearance(EConfigEntryAppearance.ALL))
                  .addListener(RenderBlockCacheCsvHandler.INSTANCE)
                  .comment(
                     "A comma separated list of block resource locations that won't be rendered by DH. \nAir is always included in this list. \n\nNote:\nIf you see gaps, or holes you may have to change\nworldCompression to ["
                        + EDhApiWorldCompressionMode.MERGE_SAME_BLOCKS
                        + "] and re-generate the LODs.\n"
                  )
                  .build();
               public static ConfigEntry<String> ignoredRenderCaveBlockCsv = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(""))
                     .setAppearance(EConfigEntryAppearance.ALL))
                  .addListener(RenderBlockCacheCsvHandler.INSTANCE)
                  .comment(
                     "A comma separated list of block resource locations that shouldn't be rendered \nif they are in a 0 sky light underground area. \nAir is always included in this list. \n\nDefaults to an empty list since most cave blocks will be automatically ignored due to being: \ntransparent, non-solid, or liquids, but new blocks can be added here if needed.\n"
                  )
                  .build();
               public static ConfigEntry<String> waterSubSurfaceBlockReplacementCsv = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set("minecraft:kelp,minecraft:tall_seagrass,minecraft:seagrass"))
                     .setAppearance(EConfigEntryAppearance.ALL))
                  .addListener(RenderBlockCacheCsvHandler.INSTANCE)
                  .comment("A comma separated list of block resource locations that will be replaced by water \nif they're visible on the water's surface. \n")
                  .build();
               public static ConfigEntry<String> waterSurfaceBlockReplacementCsv = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set("minecraft:lily_pad"))
                     .setAppearance(EConfigEntryAppearance.ALL))
                  .addListener(RenderBlockCacheCsvHandler.INSTANCE)
                  .comment("A comma separated list of block resource locations that will be removed \nwhen on top of water. \n")
                  .build();
               public static ConfigUISpacer blockSpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<EDhApiBlocksToAvoid> blocksToIgnore = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                     .set(EDhApiBlocksToAvoid.NON_COLLIDING))
                  .comment(
                     "Defines what blocks should be rendered as LODs. \n\n"
                        + EDhApiBlocksToAvoid.NONE
                        + ": Include all blocks in the LODs \n"
                        + EDhApiBlocksToAvoid.NON_COLLIDING
                        + ": Only render solid blocks in the LODs (tall grass, torches, etc. will be ignored) \n"
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigEntry<Boolean> tintWithAvoidedBlocks = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "Should the blocks underneath avoided blocks gain the color of the avoided block? \n\nTrue: a red flower will tint the grass below it red. \nFalse: skipped blocks will not change color of surface below them. "
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
            }

            public static class Experimental {
               public static ConfigUIComment experimentalHeader = new ConfigUIComment.Builder()
                  .setParentConfigClass(Config.Client.Advanced.Graphics.Experimental.class)
                  .build();
               public static ConfigEntry<Integer> earthCurveRatio = new ConfigEntry.Builder<Integer>()
                  .setMinDefaultMax(-5000, 0, 5000)
                  .comment(
                     "This is the earth size ratio when applying the curvature shader effect. \nNote: Enabling this feature may cause rendering bugs. \n\n0 = flat/disabled \n1 = 1 to 1 (6,371,000 blocks) \n100 = 1 to 100 (63,710 blocks) \n10000 = 1 to 10000 (637.1 blocks) \n\nNote: Due to current limitations, the min value is [50] \nand the max value is 5000. Any values outside this range \nwill be set to 0 (disabled)."
                  )
                  .addListener(WorldCurvatureConfigEventHandler.INSTANCE)
                  .build();
               public static ConfigEntry<String> ignoredDimensionCsv = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(""))
                  .comment(
                     "A comma separated list of dimension resource locations where DH won't render. \n\nExample: \"minecraft:the_nether,minecraft:the_end\"\n\nNote:\nSome DH settings will be disabled and/or changed to improve \nvisuals when DH rendering is disabled. \n"
                  )
                  .addListener(IgnoredDimensionCsvHandler.INSTANCE)
                  .build();
               public static ConfigEntry<EDhApiRenderingEngine> renderingEngine = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                     .set(EDhApiRenderingEngine.AUTO))
                  .comment(
                     "Requires a restart to change. \n \nOptions: \n"
                        + EDhApiRenderingEngine.AUTO
                        + " - changes based on the most likely API for that MC version \n"
                        + EDhApiRenderingEngine.OPEN_GL
                        + " - The Default for MC 1.21.11 and older (supports Iris shaders) \n"
                        + EDhApiRenderingEngine.BLAZE_3D
                        + " - The Default for MC 26.1.2 and newer (supports Vulkan) \n"
                  )
                  .build();
            }

            public static class Fog {
               private static final Float FOG_RANGE_MIN = 0.0F;
               private static final Float FOG_RANGE_MAX = (float)Math.sqrt(2.0);
               public static ConfigUIComment fogHeader = new ConfigUIComment.Builder().setParentConfigClass(Config.Client.Advanced.Graphics.Fog.class).build();
               public static ConfigEntry<Boolean> enableDhFog = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment("Determines if fog is drawn on DH LODs. \n")
                  .build();
               public static ConfigEntry<EDhApiFogColorMode> colorMode = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                     .set(EDhApiFogColorMode.USE_WORLD_FOG_COLOR))
                  .comment(
                     "What color should fog use? \n\n"
                        + EDhApiFogColorMode.USE_WORLD_FOG_COLOR
                        + ": Use the world's fog color. \n"
                        + EDhApiFogColorMode.USE_SKY_COLOR
                        + ": Use the sky's color."
                  )
                  .build();
               public static ConfigEntry<Boolean> enableVanillaFog = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
                  .comment("Should Minecraft's fog render? \nNote: Other mods may conflict with this setting. \n")
                  .build();
               public static ConfigEntry<Float> farFogStart = new ConfigEntry.Builder<Float>()
                  .setMinDefaultMax(FOG_RANGE_MIN, 0.4F, FOG_RANGE_MAX)
                  .comment(
                     "At what distance should the far fog start? \n\n0.0: Fog starts at the player's position. \n1.0: Fog starts at the closest edge of the vanilla render distance. \n1.414: Fog starts at the corner of the vanilla render distance."
                  )
                  .build();
               public static ConfigEntry<Float> farFogEnd = new ConfigEntry.Builder<Float>()
                  .setMinDefaultMax(FOG_RANGE_MIN, 1.0F, FOG_RANGE_MAX)
                  .comment(
                     "Where should the far fog end? \n\n0.0: Fog ends at player's position.\n1.0: Fog ends at the closest edge of the vanilla render distance. \n1.414: Fog ends at the corner of the vanilla render distance."
                  )
                  .build();
               public static ConfigEntry<Float> farFogMin = new ConfigEntry.Builder<Float>()
                  .setMinDefaultMax(-5.0F, 0.0F, FOG_RANGE_MAX)
                  .comment("What is the minimum fog thickness? \n\n0.0: No fog. \n1.0: Fully opaque fog.")
                  .build();
               public static ConfigEntry<Float> farFogMax = new ConfigEntry.Builder<Float>()
                  .setMinDefaultMax(FOG_RANGE_MIN, 1.0F, 5.0F)
                  .comment("What is the maximum fog thickness? \n\n0.0: No fog. \n1.0: Fully opaque fog.")
                  .build();
               public static ConfigEntry<EDhApiFogFalloff> farFogFalloff = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                     .set(EDhApiFogFalloff.EXPONENTIAL_SQUARED))
                  .comment(
                     "How should the fog thickness should be calculated? \n\n"
                        + EDhApiFogFalloff.LINEAR
                        + ": Linear based on distance (will ignore 'density')\n"
                        + EDhApiFogFalloff.EXPONENTIAL
                        + ": 1/(e^(distance*density)) \n"
                        + EDhApiFogFalloff.EXPONENTIAL_SQUARED
                        + ": 1/(e^((distance*density)^2)"
                  )
                  .build();
               public static ConfigEntry<Float> farFogDensity = new ConfigEntry.Builder<Float>()
                  .setMinDefaultMax(0.01F, 2.5F, 50.0F)
                  .comment("Used in conjunction with the Fog Falloff.")
                  .build();
               public static ConfigCategory heightFog = ((ConfigCategory.Builder)new ConfigCategory.Builder()
                     .set(Config.Client.Advanced.Graphics.Fog.HeightFog.class))
                  .build();

               public static class HeightFog {
                  public static ConfigUIComment heightFogHeader = new ConfigUIComment.Builder()
                     .setParentConfigClass(Config.Client.Advanced.Graphics.Fog.HeightFog.class)
                     .build();
                  public static ConfigEntry<EDhApiHeightFogMixMode> heightFogMixMode = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set(EDhApiHeightFogMixMode.SPHERICAL))
                     .comment(
                        "How should height effect the fog thickness? \nNote: height fog is combined with the other fog settings. \n\n"
                           + EDhApiHeightFogMixMode.SPHERICAL
                           + ": Fog is calculated based on camera distance. \n"
                           + EDhApiHeightFogMixMode.CYLINDRICAL
                           + ": Ignore height, fog is calculated based on horizontal distance. \n\n"
                           + EDhApiHeightFogMixMode.MAX
                           + ": max(heightFog, farFog) \n"
                           + EDhApiHeightFogMixMode.ADDITION
                           + ": heightFog + farFog \n"
                           + EDhApiHeightFogMixMode.MULTIPLY
                           + ": heightFog * farFog \n"
                           + EDhApiHeightFogMixMode.INVERSE_MULTIPLY
                           + ": 1 - (1-heightFog) * (1-farFog) \n"
                           + EDhApiHeightFogMixMode.LIMITED_ADDITION
                           + ": farFog + max(farFog, heightFog) \n"
                           + EDhApiHeightFogMixMode.MULTIPLY_ADDITION
                           + ": farFog + farFog * heightFog \n"
                           + EDhApiHeightFogMixMode.INVERSE_MULTIPLY_ADDITION
                           + ": farFog + 1 - (1-heightFog) * (1-farFog) \n"
                           + EDhApiHeightFogMixMode.AVERAGE
                           + ": farFog*0.5 + heightFog*0.5 \n\n"
                     )
                     .build();
                  public static ConfigEntry<EDhApiHeightFogDirection> heightFogDirection = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set(EDhApiHeightFogDirection.BELOW_SET_HEIGHT))
                     .comment(
                        "Where should the height fog start? \n\n"
                           + EDhApiHeightFogDirection.ABOVE_CAMERA
                           + ": Height fog starts at the camera and goes towards the sky \n"
                           + EDhApiHeightFogDirection.BELOW_CAMERA
                           + ": Height fog starts at the camera and goes towards the void \n"
                           + EDhApiHeightFogDirection.ABOVE_AND_BELOW_CAMERA
                           + ": Height fog starts from the camera to goes towards both the sky and void \n"
                           + EDhApiHeightFogDirection.ABOVE_SET_HEIGHT
                           + ": Height fog starts from a set height and goes towards the sky \n"
                           + EDhApiHeightFogDirection.BELOW_SET_HEIGHT
                           + ": Height fog starts from a set height and goes towards the void \n"
                           + EDhApiHeightFogDirection.ABOVE_AND_BELOW_SET_HEIGHT
                           + ": Height fog starts from a set height and goes towards both the sky and void"
                     )
                     .build();
                  public static ConfigEntry<Float> heightFogBaseHeight = new ConfigEntry.Builder<Float>()
                     .setMinDefaultMax(-3000000.0F, 80.0F, 3000000.0F)
                     .comment("If the height fog is calculated around a set height, what is that height position?")
                     .build();
                  public static ConfigEntry<Float> heightFogStart = new ConfigEntry.Builder<Float>()
                     .setMinDefaultMax(Config.Client.Advanced.Graphics.Fog.FOG_RANGE_MIN, 0.0F, Config.Client.Advanced.Graphics.Fog.FOG_RANGE_MAX)
                     .comment(
                        "Should the start of the height fog be offset? \n\n0.0: Fog start with no offset.\n1.0: Fog start with offset of the entire world's height. (Includes depth)"
                     )
                     .build();
                  public static ConfigEntry<Float> heightFogEnd = new ConfigEntry.Builder<Float>()
                     .setMinDefaultMax(Config.Client.Advanced.Graphics.Fog.FOG_RANGE_MIN, 0.6F, Config.Client.Advanced.Graphics.Fog.FOG_RANGE_MAX)
                     .comment(
                        "Should the end of the height fog be offset? \n\n0.0: Fog end with no offset.\n1.0: Fog end with offset of the entire world's height. (Include depth)"
                     )
                     .build();
                  public static ConfigEntry<Float> heightFogMin = new ConfigEntry.Builder<Float>()
                     .setMinDefaultMax(0.0F, 0.0F, Config.Client.Advanced.Graphics.Fog.FOG_RANGE_MAX)
                     .comment("What is the minimum fog thickness? \n\n0.0: No fog. \n1.0: Fully opaque fog.")
                     .build();
                  public static ConfigEntry<Float> heightFogMax = new ConfigEntry.Builder<Float>()
                     .setMinDefaultMax(Config.Client.Advanced.Graphics.Fog.FOG_RANGE_MIN, 1.0F, 5.0F)
                     .comment("What is the maximum fog thickness? \n\n0.0: No fog. \n1.0: Fully opaque fog.")
                     .build();
                  public static ConfigEntry<EDhApiFogFalloff> heightFogFalloff = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set(EDhApiFogFalloff.EXPONENTIAL_SQUARED))
                     .comment(
                        "How should the height fog thickness should be calculated? \n\n"
                           + EDhApiFogFalloff.LINEAR
                           + ": Linear based on height (will ignore 'density')\n"
                           + EDhApiFogFalloff.EXPONENTIAL
                           + ": 1/(e^(height*density)) \n"
                           + EDhApiFogFalloff.EXPONENTIAL_SQUARED
                           + ": 1/(e^((height*density)^2)"
                     )
                     .build();
                  public static ConfigEntry<Float> heightFogDensity = new ConfigEntry.Builder<Float>()
                     .setMinDefaultMax(0.01F, 20.0F, 50.0F)
                     .comment("What is the height fog's density?")
                     .build();
               }
            }

            public static class GenericRendering {
               public static ConfigUIComment genericRendererHeader = new ConfigUIComment.Builder()
                  .setParentConfigClass(Config.Client.Advanced.Graphics.GenericRendering.class)
                  .build();
               public static ConfigEntry<Boolean> enableGenericRendering = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment("If true non terrain objects will be rendered by DH. \ni.e. beacon beams and clouds. \n")
                  .build();
               public static ConfigEntry<Boolean> enableBeaconRendering = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment("If true LOD beacon beams will be rendered. \n")
                  .build();
               public static ConfigEntry<Integer> beaconRenderHeight = new ConfigEntry.Builder<Integer>()
                  .setMinDefaultMax(1, 6000, 6000000)
                  .comment("Sets the maximum height beacons will render up to. \n\nRequires a world re-load to take affect. \n")
                  .build();
               public static ConfigEntry<Boolean> expandDistantBeacons = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "If true LOD beacon beams will be rendered wider at extreme distances, \nmaking them easier to see. \nIf false all LOD beacon beams will only ever be 1 block wide. \n"
                  )
                  .build();
               public static ConfigEntry<Boolean> enableCloudRendering = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment("If true LOD clouds will be rendered. \n")
                  .build();
               public static ConfigEntry<Boolean> enableMultiLayerClouds = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "False = DH will render a single layer of clouds, like vanilla Minecraft. \nTrue = DH will render 3 layers of clouds at different heights. \n"
                  )
                  .build();
               public static ConfigEntry<String> dimensionEnabledCloudRenderingCsv = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set("minecraft:overworld"))
                     .setAppearance(EConfigEntryAppearance.ALL))
                  .comment(
                     "A comma separated separated list of dimension resource locations where DH clouds will render.\n\nExample: \"minecraft:overworld,minecraft:the_end\"\n\nChanges require a world re-load.\n"
                  )
                  .build();
            }

            public static class NoiseTexture {
               public static ConfigUIComment noiseTextureHeader = new ConfigUIComment.Builder()
                  .setParentConfigClass(Config.Client.Advanced.Graphics.NoiseTexture.class)
                  .build();
               public static ConfigEntry<Boolean> enableNoiseTexture = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment("Should a noise texture be applied to LODs? \n\nThis is done to simulate textures and make the LODs appear more detailed. \n")
                  .build();
               public static ConfigEntry<Integer> noiseSteps = new ConfigEntry.Builder<Integer>()
                  .setMinDefaultMax(1, 4, null)
                  .comment("How many steps of noise should be applied to LODs?")
                  .build();
               public static ConfigEntry<Float> noiseIntensity = new ConfigEntry.Builder<Float>()
                  .setMinDefaultMax(0.0F, 0.05F, 1.0F)
                  .comment("How intense should the noise should be?")
                  .build();
               public static ConfigEntry<Integer> noiseDropoff = new ConfigEntry.Builder<Integer>()
                  .setMinDefaultMax(0, 1024, null)
                  .comment("Defines how far should the noise texture render before it fades away. (in blocks) \nSet to 0 to disable noise from fading away \n")
                  .build();
            }

            public static class Quality {
               public static ConfigUIComment qualityHeader = new ConfigUIComment.Builder()
                  .setParentConfigClass(Config.Client.Advanced.Graphics.Quality.class)
                  .build();
               public static ConfigEntry<Integer> lodChunkRenderDistanceRadius = new ConfigEntry.Builder<Integer>()
                  .setMinDefaultMax(32, 256, 4096)
                  .comment(
                     "The radius of the mod's render distance. (measured in chunks)\n\nNote: this is a best effort number. \nThe actual render distance may be above or below this number \ndepending on your other graphic settings. \n"
                  )
                  .build();
               public static ConfigUISpacer qualityDropoffSpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<EDhApiHorizontalQuality> horizontalQuality = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                     .set(EDhApiHorizontalQuality.MEDIUM))
                  .comment(
                     "This indicates how far apart drops in LOD quality are. \n\nHigher settings will increase the distance between drops \nbut will increase memory and GPU usage."
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigEntry<EDhApiMaxHorizontalResolution> maxHorizontalResolution = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                     .set(EDhApiMaxHorizontalResolution.BLOCK))
                  .comment(
                     "What is the maximum detail LODs can render at? \nHigher settings will increase memory and GPU usage. \n\n"
                        + EDhApiMaxHorizontalResolution.CHUNK
                        + ": render 1 LOD for each Chunk. \n"
                        + EDhApiMaxHorizontalResolution.HALF_CHUNK
                        + ": render 4 LODs for each Chunk. \n"
                        + EDhApiMaxHorizontalResolution.FOUR_BLOCKS
                        + ": render 16 LODs for each Chunk. \n"
                        + EDhApiMaxHorizontalResolution.TWO_BLOCKS
                        + ": render 64 LODs for each Chunk. \n"
                        + EDhApiMaxHorizontalResolution.BLOCK
                        + ": render 256 LODs for each Chunk (width of one block). \n\nFastest: "
                        + EDhApiMaxHorizontalResolution.CHUNK
                        + "\nFanciest: "
                        + EDhApiMaxHorizontalResolution.BLOCK
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigEntry<EDhApiVerticalQuality> verticalQuality = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                     .set(EDhApiVerticalQuality.MEDIUM))
                  .comment(
                     "This indicates how well LODs will represent \noverhangs, caves, floating islands, etc. \nHigher options will make the world more accurate, butwill increase memory and GPU usage. \n\nLowest Quality: "
                        + EDhApiVerticalQuality.HEIGHT_MAP
                        + "\nHighest Quality: "
                        + EDhApiVerticalQuality.PIXEL_ART
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigUISpacer cameraZoomSpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<Boolean> useCameraPositionForQualityDropOff = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "If true DH will try to use the camera position when \ndetermining LOD quality drop-off. \nIf false DH will use the player's position. \n \nEnabling helps free-cam mods render correctly. \nDisabling helps multi-camera mods render correctly (ie Immersive Portals or camera mods). \n"
                  )
                  .build();
               public static ConfigEntry<Boolean> increaseQualityWhenZoomedIn = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "If true LOD quality will increase when the camera is zoomed in, \nIE when using a spyglass or zoom mod. \n\nOnly LODs visible through the camera view are affected. \n\nWhen zoomed in LODs will load to the same detail level \nthey would have if you were close to them. \n"
                  )
                  .build();
               public static ConfigEntry<Integer> maxZoomQualityIncrease = new ConfigEntry.Builder<Integer>()
                  .setMinDefaultMax(1, 4, 6)
                  .comment(
                     "How many detail levels zooming in can increase LOD quality by. \nHigher numbers allow stronger zooms to render crisper terrain, \nbut will increase memory and GPU usage while zoomed in. \n \nA vanilla spyglass needs 4 detail levels to reach its full quality. \n"
                  )
                  .build();
               public static ConfigUISpacer qualitySpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<EDhApiTransparency> transparency = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(EDhApiTransparency.COMPLETE))
                  .comment(
                     "How should LOD transparency be handled. \n\n"
                        + EDhApiTransparency.COMPLETE
                        + ": LODs will render transparent. \n"
                        + EDhApiTransparency.DISABLED
                        + ": LODs will be opaque. \n"
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigEntry<EDhApiLodShading> lodShading = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(EDhApiLodShading.AUTO))
                  .comment(
                     "How should LODs be shaded? \n\n"
                        + EDhApiLodShading.AUTO
                        + ": Uses the same side shading as vanilla Minecraft blocks. \n"
                        + EDhApiLodShading.ENABLED
                        + ": Simulates Minecraft's block shading for LODs. \n              Can be used to force LOD shading when using some shaders. \n"
                        + EDhApiLodShading.DISABLED
                        + ": All LOD sides will be rendered with the same brightness. \n"
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigEntry<EDhApiGrassSideRendering> grassSideRendering = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                     .set(EDhApiGrassSideRendering.FADE_TO_DIRT))
                  .comment(
                     "How should the sides and bottom of grass block LODs render? \n\n"
                        + EDhApiGrassSideRendering.AS_GRASS
                        + ": all sides of dirt LOD's render using the top (green) color. \n"
                        + EDhApiGrassSideRendering.FADE_TO_DIRT
                        + ": sides fade from grass to dirt. \n"
                        + EDhApiGrassSideRendering.AS_DIRT
                        + ": sides render entirely as dirt. \n"
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigUISpacer fadeSpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<Boolean> ditherDhFade = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "If true LODs will fade away as you get closer to them. \nIf false LODs will cut off abruptly at a set distance from the camera. \nThis setting is affected by the vanilla overdraw prevention config. \n"
                  )
                  .build();
               public static ConfigEntry<EDhApiMcRenderingFadeMode> vanillaFadeMode = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                     .set(EDhApiMcRenderingFadeMode.DOUBLE_PASS))
                  .comment(
                     "How should vanilla Minecraft fade into Distant Horizons LODs? \n\n"
                        + EDhApiMcRenderingFadeMode.NONE
                        + ": Fastest, there will be a pronounced border between DH and MC rendering. \n"
                        + EDhApiMcRenderingFadeMode.SINGLE_PASS
                        + ": Fades after MC's transparent pass, opaque blocks underwater won't be faded. \n"
                        + EDhApiMcRenderingFadeMode.DOUBLE_PASS
                        + ": Slowest, fades after both MC's opaque and transparent passes, provides the smoothest transition. \n"
                  )
                  .build();
               public static ConfigEntry<Boolean> dhFadeFarClipPlane = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment("Should DH fade out before reaching the far clip plane? \nThis is helpful to prevent DH clouds from cutting off in the distance. \n")
                  .build();
               public static ConfigEntry<Integer> lodBiomeBlending = new ConfigEntry.Builder<Integer>()
                  .setMinDefaultMax(0, 3, 3)
                  .comment(
                     "This is the same as vanilla Biome Blending settings for Lod area. \n    Note: anything above '0' will slow down LOD loading time. \n\n    '0' equals to Vanilla Biome Blending of '1x1' or 'OFF', \n    '1' equals to Vanilla Biome Blending of '3x3', \n    '2' equals to Vanilla Biome Blending of '5x5'..."
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigUISpacer multiplierSpacer = new ConfigUISpacer.Builder().build();
               public static ConfigEntry<Float> brightnessMultiplier = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(1.0F))
                  .comment("How bright LOD colors are. \n\n0 = black \n1 = normal \n2 = near white")
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigEntry<Float> saturationMultiplier = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(1.0F))
                  .comment("How saturated LOD colors are. \n\n0 = black and white \n1 = normal \n2 = very saturated")
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
            }

            public static class Texture {
               public static ConfigEntry<Boolean> enableTexturedLods = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
                  .comment(
                     "If true nearby and zoomed-in LODs will render with their block's \nactual texture instead of a single flat color. \n \nOnly applies to high detail LODs where the texture is actually visible, \ndistant LODs always use flat colors. \nSome shader packs nay not have an affect. \n"
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigEntry<Integer> maxTexturedLodDetailLevel = new ConfigEntry.Builder<Integer>()
                  .setMinDefaultMax(0, 2, 4)
                  .comment(
                     "The highest detail level number that can render with block textures. \nAt higher detail levels each LOD covers multiple blocks, \nwhich can essentially make textures smaller than a pixel and therefore invisible. \n \nLarger numbers texture more distant LODs \nbut increase memory usage. \n"
                  )
                  .addListener(ReloadLodsConfigEventHandler.DELAYED_INSTANCE)
                  .build();
               public static ConfigEntry<String> blocksDontRenderTextureCsv = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set("minecraft:bamboo"))
                     .setAppearance(EConfigEntryAppearance.ALL))
                  .comment(
                     "A comma separated list of block resource locations \nthat DH won't render textures on. \nPartial matches/incomplete resource locations will also match. \n\nExample: \"minecraft:grass_block,nylium\" \n\nChanges require a restart. \n"
                  )
                  .build();
               public static ConfigEntry<String> blocksDontUseSideTextureCsv = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set("grass_block,mycelium,nylium,dirt_path"))
                     .setAppearance(EConfigEntryAppearance.ALL))
                  .comment(
                     "A comma separated list of block resource locations \nthat DH will render their sides using the bottom texture. \nPartial matches/incomplete resource locations will also match. \n\nExample: \"minecraft:grass_block,nylium\" \n\nChanges require a restart. \n"
                  )
                  .build();
               public static ConfigEntry<String> blockTagsDontUseSideTextureCsv = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set("grass_blocks,nylium"))
                     .setAppearance(EConfigEntryAppearance.ALL))
                  .comment(
                     "A comma separated list of tag names \nthat DH will render their sides using the bottom texture. \n\nExample: \"grass_blocks,nylium\" \n\nChanges require a restart. \n"
                  )
                  .build();
               public static ConfigEntry<String> blocksAlwaysRasterizeTextureCsv = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
                        .set("minecraft:beacon"))
                     .setAppearance(EConfigEntryAppearance.ALL))
                  .comment(
                     "A comma separated list of block resource locations \nthat DH will use rasterization to determine \nthe face textures.\n\nCan be used to fix issues with mods/blocks where\na side uses just part of a modeled block's texture. \n\nExample: \"minecraft:beacon\" \n\nChanges require a restart. \n"
                  )
                  .build();
            }
         }

         public static class Multiplayer {
            public static ConfigUIComment multiplayerHeader = new ConfigUIComment.Builder()
               .setParentConfigClass(Config.Client.Advanced.Multiplayer.class)
               .build();
            public static ConfigEntry<EDhApiServerFolderNameMode> serverFolderNameMode = ((ConfigEntry.Builder)new ConfigEntry.Builder()
                  .set(EDhApiServerFolderNameMode.NAME_ONLY))
               .comment(
                  "How should multiplayer save folders should be named? \n\n"
                     + EDhApiServerFolderNameMode.NAME_ONLY
                     + ": Example: \"Minecraft Server\" \n"
                     + EDhApiServerFolderNameMode.IP_ONLY
                     + ": Example: \"192.168.1.40\" \n"
                     + EDhApiServerFolderNameMode.NAME_IP
                     + ": Example: \"Minecraft Server IP 192.168.1.40\" \n"
                     + EDhApiServerFolderNameMode.NAME_IP_PORT
                     + ": Example: \"Minecraft Server IP 192.168.1.40:25565\""
                     + EDhApiServerFolderNameMode.NAME_IP_PORT_MC_VERSION
                     + ": Example: \"Minecraft Server IP 192.168.1.40:25565 GameVersion 1.16.5\""
               )
               .build();
         }
      }
   }

   public static class Common {
      public static class LodBuilding {
         public static ConfigUIComment lodBuildingHeader = new ConfigUIComment.Builder().setParentConfigClass(Config.Common.LodBuilding.class).build();
         public static ConfigEntry<Boolean> disableUnchangedChunkCheck = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
               .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
            .comment(
               "Enabling this will drastically increase chunk processing time\nand you may need to increase your CPU load to handle it.\n\nNormally DH will attempt to skip creating LODs for chunks it's already seen\nand that haven't changed.\n\nHowever sometimes that logic incorrectly prevents LODs from being updated.\nDisabling this check may fix issues where LODs aren't updated after\nblocks have been changed.\n"
            )
            .build();
         public static ConfigEntry<EDhApiDataCompressionMode> dataCompression = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
                  .set(EDhApiDataCompressionMode.Z_STD_BLOCK))
               .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
            .build();
         public static ConfigEntry<EDhApiWorldCompressionMode> worldCompression = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .set(EDhApiWorldCompressionMode.VISUALLY_EQUAL))
            .comment(
               "How should block data be compressed when creating LOD data? \nThis setting will only affect new or updated LOD data, \nany data already generated when this setting is changed will be\nunaffected until it is modified or re-loaded.\n\n"
                  + EDhApiWorldCompressionMode.MERGE_SAME_BLOCKS
                  + " \nEvery block/biome change is recorded in the database. \nThis is what DH 2.0 and 2.0.1 all used by default and will store a lot of data. \nExpected Compression Ratio: 1.0\n\n"
                  + EDhApiWorldCompressionMode.VISUALLY_EQUAL
                  + " \nOnly visible block/biome changes are recorded in the database. \nHidden blocks (IE ores) are ignored.  \nExpected Compression Ratio: 0.7\n"
            )
            .build();
         public static ConfigCategory experimental = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Common.LodBuilding.Experimental.class))
            .build();

         public static class Experimental {
            public static ConfigUIComment experimentalHeader = new ConfigUIComment.Builder()
               .setParentConfigClass(Config.Common.LodBuilding.Experimental.class)
               .build();
            public static ConfigEntry<Boolean> upsampleLowerDetailLodsToFillHoles = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
               .comment(
                  "When active DH will attempt to fill missing LOD data \nwith any data that is present in the tree, preventing holes when moving \nwhen a N-sized generator (or server) is active. \n\nThis is only used when N-sized world generation is available \nand/or when on a server where [generateOnlyInHighestDetail] is false. \n\nExperimental:\nEnabling this option will increase CPU and harddrive use\nand may cause rendering bugs.\n\n"
               )
               .build();
         }
      }

      public static class Logging {
         public static ConfigUIComment loggingHeader = new ConfigUIComment.Builder().setParentConfigClass(Config.Common.Logging.class).build();
         public static ConfigEntry<EDhApiLoggerLevel> globalFileMaxLevel = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("logging.globalFileMaxLevel")
               .set(EDhApiLoggerLevel.INFO))
            .comment("")
            .build();
         public static ConfigEntry<EDhApiLoggerLevel> globalChatMaxLevel = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("logging.globalChatMaxLevel")
               .set(EDhApiLoggerLevel.ERROR))
            .comment("")
            .build();
         public static ConfigUISpacer globalLoggingSpacer = new ConfigUISpacer.Builder().build();
         public static ConfigEntry<EDhApiLoggerLevel> logWorldGenEventToFile = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("logging.logWorldGenEvent")
               .set(EDhApiLoggerLevel.INFO))
            .comment("If enabled, the mod will log information about the world generation process. \nThis can be useful for debugging.")
            .build();
         public static ConfigEntry<EDhApiLoggerLevel> logWorldGenChunkLoadEventToFile = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("logging.logWorldGenLoadEvent")
               .set(EDhApiLoggerLevel.INFO))
            .comment("If enabled, the mod will log information about the world generation process. \nThis can be useful for debugging.")
            .build();
         public static ConfigEntry<EDhApiLoggerLevel> logRendererEventToFile = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(EDhApiLoggerLevel.INFO))
            .comment(
               "If enabled, the mod will log information about the renderer setup, cleanup, and any issues it may encounter. \nThis can be useful for debugging."
            )
            .build();
         public static ConfigEntry<EDhApiLoggerLevel> logRendererGLEventToFile = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(EDhApiLoggerLevel.INFO))
            .comment("If enabled, the mod will log information about the renderer OpenGL process. \nThis can be useful for debugging.")
            .build();
         public static ConfigEntry<EDhApiLoggerLevel> logRendererGLEventToChat = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(EDhApiLoggerLevel.ERROR))
            .comment("If enabled, the mod will log information about the renderer OpenGL process. \nThis can be useful for debugging.")
            .build();
         public static ConfigEntry<EDhApiLoggerLevel> logNetworkEventToFile = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("logging.logNetworkEvent")
               .set(EDhApiLoggerLevel.INFO))
            .comment("If enabled, the mod will log information about network operations. \nThis can be useful for debugging.")
            .build();
         public static ConfigEntry<EDhApiLoggerLevel> logConnectionConfigChangesToFile = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("logging.logConnectionConfigChanges")
               .set(EDhApiLoggerLevel.WARN))
            .comment("If enabled, config changes sent by the server will be logged. \n")
            .build();
         public static ConfigUISpacer warningSpacer = new ConfigUISpacer.Builder().build();
         public static ConfigCategory warning = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Common.Logging.Warning.class)).build();

         public static class Warning {
            public static ConfigUIComment warningHeader = new ConfigUIComment.Builder().setParentConfigClass(Config.Common.Logging.Warning.class).build();
            public static ConfigEntry<Boolean> showLowMemoryWarningOnStartup = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment("If enabled, a chat message will be displayed if Java doesn't have enough \nmemory allocated to run DH well. \n")
               .build();
            public static ConfigEntry<Boolean> showPoolInsufficientMemoryWarning = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment("If enabled, a chat message will be displayed if DH detects \nthat any pooled objects have been garbage collected. \n")
               .build();
            public static ConfigEntry<Boolean> showHighVanillaRenderDistanceWarning = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment("If enabled, a chat message will be displayed if vanilla MC's \nrender distance is higher than the recommended amount. \n")
               .build();
            public static ConfigEntry<Boolean> showReplayWarningOnStartup = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment("If enabled, a chat message will be displayed when a replay is started \ngiving some basic information about how DH will function. \n")
               .build();
            public static ConfigEntry<Boolean> showUpdateQueueOverloadedChatWarning = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
               .comment("If enabled, a chat message will be displayed when DH has too many chunks \nqueued for updating. \n")
               .build();
            public static ConfigEntry<Boolean> showSlowWorldGenSettingWarnings = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment("If enabled, a chat message will be displayed when DH has too many chunks \nqueued for updating. \n")
               .build();
            public static ConfigEntry<Boolean> showModCompatibilityWarningsOnStartup = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment("If enabled, a chat message will be displayed when a potentially problematic \nmod is installed alongside DH. \n")
               .build();
            public static ConfigEntry<Boolean> showDeprecatedRendererWarningOnStartup = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment("If enabled, a chat message will be displayed when DH is using \na deprecated renderer. \n")
               .build();
            public static ConfigEntry<Boolean> logGarbageCollectorWarning = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment(
                  "If enabled, a message will be logged if the garbage \ncollector Java is currently using is known \nto cause frame stuttering and/or other issues. \n"
               )
               .build();
            public static ConfigEntry<Boolean> showGarbageCollectorWarning = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment(
                  "If enabled, a chat message will be displayed in chat if the garbage \ncollector Java is currently using is known \nto cause frame stuttering and/or other issues. \n"
               )
               .build();
            public static ConfigEntry<Boolean> logExplicitGcDisabledWarning = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment(
                  "If enabled, a message will be logged if explicit garbage collection \nis disabled. \nThis is known to cause out-of-memory issues \nand is better solved with a concurrent garbage collector. \n"
               )
               .build();
            public static ConfigEntry<Boolean> showExplicitGcDisabledWarning = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
               .comment(
                  "If enabled, a message will be displayed in chat if explicit garbage collection \nis disabled. \nThis is known to cause out-of-memory issues \nand is better solved with a concurrent garbage collector. \n"
               )
               .build();
         }
      }

      public static class MultiThreading {
         public static ConfigUIComment multiThreadingHeader = new ConfigUIComment.Builder().setParentConfigClass(Config.Common.MultiThreading.class).build();
         public static final ConfigEntry<Integer> numberOfThreads = new ConfigEntry.Builder<Integer>()
            .setChatCommandName("threading.numberOfThreads")
            .setMinDefaultMax(1, ThreadPresetConfigEventHandler.getDefaultThreadCount(), Runtime.getRuntime().availableProcessors())
            .comment("How many threads should be used by Distant Horizons? \n")
            .build();
         public static final ConfigEntry<Double> threadRunTimeRatio = new ConfigEntry.Builder<Double>()
            .setChatCommandName("threading.threadRunTimeRatio")
            .setMinDefaultMax(0.01, ThreadPresetConfigEventHandler.getDefaultRunTimeRatio(), 1.0)
            .comment(
               "A value between 1.0 and 0.0 that represents the percentage \nof time each thread can run before going idle. \n\nThis can be used to reduce CPU usage if the thread count \nis already set to 1 for the given option, or more finely \ntune CPU performance. \n"
            )
            .build();
         public static final ConfigEntry<Integer> threadPriority = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
            .setMinDefaultMax(1, 5, 10)
            .comment(
               "What Java thread priority should DH's primary thread pools run with? \n\nYou probably don't need to change this unless you are also \nrunning C2ME and are seeing thread starvation in either C2ME or DH. \n"
            )
            .build();
      }

      public static class WorldGenerator {
         public static ConfigUIComment worldGeneratorHeader = new ConfigUIComment.Builder().setParentConfigClass(Config.Common.WorldGenerator.class).build();
         public static ConfigEntry<Boolean> enableDistantGeneration = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("generation.enable")
               .set(true))
            .comment(
               " Should Distant Horizons slowly generate LODs \n outside the vanilla render distance? \nDepending on the generator mode, this will import existing chunks \nand/or generating missing chunks."
            )
            .build();
         public static ConfigEntry<EDhApiDistantGeneratorMode> distantGeneratorMode = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("generation.mode")
               .set(EDhApiDistantGeneratorMode.FEATURES))
            .comment(
               "How detailed should LODs be generated outside the vanilla render distance? \n\n"
                  + EDhApiDistantGeneratorMode.PRE_EXISTING_ONLY
                  + " \nOnly create LOD data for already generated chunks. \n\n\n"
                  + EDhApiDistantGeneratorMode.SURFACE
                  + " \nGenerate the world surface, \nthis does NOT include trees, \nor structures. \n\n"
                  + EDhApiDistantGeneratorMode.FEATURES
                  + " \nGenerate everything except structures. \nWARNING: This may cause world generator bugs or instability when paired with certain world generator mods. \n\n"
                  + EDhApiDistantGeneratorMode.INTERNAL_SERVER
                  + " \nAsk the local server to generate/load each chunk. \nThis is the most compatible and will generate structures correctly, \nbut may cause server/simulation lag. \nNote: unlike other modes this option DOES save generated chunks to \nMinecraft's region files. \n"
            )
            .build();
         public static ConfigUISpacer genProgressSpacer = new ConfigUISpacer.Builder().build();
         public static ConfigEntry<EDhApiDistantGeneratorProgressDisplayLocation> showGenerationProgress = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .set(EDhApiDistantGeneratorProgressDisplayLocation.DISABLED))
            .comment(
               "How should distant generator progress be displayed? \n\n"
                  + EDhApiDistantGeneratorProgressDisplayLocation.OVERLAY
                  + ": may be the same as "
                  + EDhApiDistantGeneratorProgressDisplayLocation.CHAT
                  + " for some Minecraft versions \n"
                  + EDhApiDistantGeneratorProgressDisplayLocation.CHAT
                  + " \n"
                  + EDhApiDistantGeneratorProgressDisplayLocation.LOG
                  + " \n"
                  + EDhApiDistantGeneratorProgressDisplayLocation.DISABLED
                  + " \n"
            )
            .build();
         public static ConfigEntry<Integer> generationProgressDisplayIntervalInSeconds = new ConfigEntry.Builder<Integer>()
            .setChatCommandName("generation.logInterval")
            .setMinDefaultMax(1, 2, 14400)
            .comment("How often should the distant generator progress be displayed? \n")
            .build();
         public static ConfigEntry<Integer> generationProgressDisableMessageDisplayTimeInSeconds = new ConfigEntry.Builder<Integer>()
            .setMinDefaultMax(0, 20, 3600)
            .comment(
               "For how many seconds should instructions for disabling the distant generator progress be displayed? \nSetting this to 0 hides the instructional message so the world gen progress is shown immediately when it starts. \n"
            )
            .build();
         public static ConfigEntry<Boolean> generationProgressIncludeChunksPerSecond = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
            .comment(
               "When logging generation progress also include the rate at which chunks \nare being generated. \nThis can be useful for troubleshooting performance. \n"
            )
            .build();
         public static ConfigEntry<Integer> generationCenterChunkX = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("generation.bounds.centerChunk.x")
               .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
            .setMinDefaultMax(-2147483648, 0, 2147483647)
            .comment("The center X chunk position that the world gen max radius is centered around. \n")
            .build();
         public static ConfigEntry<Integer> generationCenterChunkZ = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("generation.bounds.centerChunk.z")
               .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
            .setMinDefaultMax(-2147483648, 0, 2147483647)
            .comment("The center Z chunk position that the world gen max radius is centered around. \n")
            .build();
         public static ConfigEntry<Integer> generationMaxChunkRadius = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("generation.bounds.radiusInChunks")
               .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
            .setMinDefaultMax(0, 0, 2147483647)
            .comment(
               "The max radius in chunks around the central point where world generation is allowed. \nIf this value is set to 0, generation bounds are disabled and the render distance will be used. \n\nThis should only be set if you have a pre-generated world that has a very limited size. \nSetting this on a normal MC world will prevent the world generator from filling \nout your render distance. \n"
            )
            .build();
      }
   }

   public static class Server {
      public static ConfigEntry<Boolean> sendLevelKeys = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("levelKeys.send")
               .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
            .set(true))
         .comment("Makes the server send level keys for each world.\nDisable this if you use alternative ways to send level keys.\n")
         .build();
      public static ConfigEntry<Integer> serverId = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder().set(new Random().nextInt()))
            .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
         .comment(
            "DO NOT CHANGE UNLESS YOU KNOW WHAT YOU'RE DOING.\nAutogenerated ID used to prevent multiple independent servers from accidentally\nwriting over each other's LODs when the same serverKey is set on both.\n"
         )
         .build();
      public static ConfigEntry<String> serverKey = ((ConfigEntry.Builder)((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("levelKeys.serverKey")
               .setAppearance(EConfigEntryAppearance.ONLY_IN_FILE))
            .set(""))
         .comment(
            "Custom server key used which can be used to always reuse the same LOD data folder,\nfor cases when the server doesn't have a static IP for some reason.\nIf this value is empty, the client itself decides which folder name to use.\nRequires rejoining the server to apply after changing.\n"
         )
         .build();
      public static ConfigEntry<String> levelKeyPrefix = ((ConfigEntry.Builder)new ConfigEntry.Builder().setChatCommandName("levelKeys.prefix").set(""))
         .comment(
            "Prefix of the level keys sent to the clients.\nIf the mod is running behind a proxy, each backend should use a unique value.\nIf this value is empty, level key will be based on the server's seed hash.\n"
         )
         .build();
      public static ConfigEntry<Boolean> enableServerGeneration = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(true))
         .comment(
            "When enabled, Distant Horizons will attempt to download missing LODs from the server.\n\nNote: the server must have Distant Generation enabled for it to work."
         )
         .build();
      public static ConfigEntry<Integer> generationRequestRateLimit = new ConfigEntry.Builder<Integer>()
         .setChatCommandName("generation.requestRateLimit")
         .setMinDefaultMax(1, 20, 100)
         .comment(
            "How many LOD generation requests per second should a client send? \nAlso limits the number of client requests allowed to stay in the server's queue."
         )
         .build();
      public static ConfigEntry<Integer> maxGenerationRequestDistance = new ConfigEntry.Builder<Integer>()
         .setChatCommandName("generation.maxRequestDistance")
         .setMinDefaultMax(256, 4096, 4096)
         .comment("Defines the distance allowed to generate around the player.")
         .build();
      public static ConfigEntry<Boolean> enableRealTimeUpdates = ((ConfigEntry.Builder)new ConfigEntry.Builder()
            .setChatCommandName("realTimeUpdates.enable")
            .set(true))
         .comment("If true, clients will receive real-time LOD updates for chunks outside the client's render distance.")
         .build();
      public static ConfigEntry<Integer> realTimeUpdateDistanceRadiusInChunks = new ConfigEntry.Builder<Integer>()
         .setChatCommandName("realTimeUpdates.playerDistance")
         .setMinDefaultMax(32, 256, 4096)
         .comment("Defines the distance the player will receive updates around.")
         .build();
      public static ConfigEntry<Boolean> synchronizeOnLoad = ((ConfigEntry.Builder)new ConfigEntry.Builder().setChatCommandName("syncOnLoad.enable").set(true))
         .comment("If true, clients will receive updated LODs when joining or loading new LODs. \n")
         .build();
      public static ConfigEntry<Integer> syncOnLoadRateLimit = new ConfigEntry.Builder<Integer>()
         .setChatCommandName("syncOnLoad.rateLimit")
         .setMinDefaultMax(1, 50, 100)
         .comment(
            "How many LOD sync requests per second should a client send? \nAlso limits the amount of player's requests allowed to stay in the server's queue."
         )
         .build();
      public static ConfigEntry<Integer> maxSyncOnLoadRequestDistance = new ConfigEntry.Builder<Integer>()
         .setChatCommandName("syncOnLoad.maxRequestDistance")
         .setMinDefaultMax(256, 4096, 4096)
         .comment(
            "Defines the distance allowed to be synchronized around the player. \nShould be the same or larger than maxGenerationRequestDistance in most cases."
         )
         .build();
      public static ConfigEntry<Integer> playerBandwidthLimit = new ConfigEntry.Builder<Integer>()
         .setChatCommandName("common.playerBandwidthLimit")
         .setMinDefaultMax(0, 500, 1000000)
         .comment("Maximum per-player speed for uploading LODs to the clients, in KB/s.\nValue of 0 disables the limit.")
         .build();
      public static ConfigEntry<Integer> globalBandwidthLimit = new ConfigEntry.Builder<Integer>()
         .setChatCommandName("common.globalBandwidthLimit")
         .setMinDefaultMax(0, 0, 10000000)
         .comment("Maximum global speed for uploading LODs to the clients, in KB/s.\nValue of 0 disables the limit.")
         .build();
      public static ConfigEntry<Boolean> enableAdaptiveTransferSpeed = ((ConfigEntry.Builder)new ConfigEntry.Builder().set(false))
         .comment(
            "Enables adaptive transfer speed based on client performance.\nIf true, DH will automatically adjust transfer rate to minimize connection lag.\nIf false, transfer speed will remain fixed.\n"
         )
         .build();
      public static ConfigCategory experimental = ((ConfigCategory.Builder)new ConfigCategory.Builder().set(Config.Server.Experimental.class)).build();

      public static class Experimental {
         public static ConfigEntry<Boolean> enableNSizedGeneration = ((ConfigEntry.Builder)new ConfigEntry.Builder()
               .setChatCommandName("generation.nSized")
               .set(false))
            .comment(
               "When enabled on the client, this allows loading lower detail levels as needed to speed up terrain generation.\nThis must also be enabled on the server; otherwise, it will have no effect.\nFor better performance when switching LOD detail levels, enabling [upsampleLowerDetailLodsToFillHoles] is recommended.\n"
            )
            .build();
      }
   }
}
