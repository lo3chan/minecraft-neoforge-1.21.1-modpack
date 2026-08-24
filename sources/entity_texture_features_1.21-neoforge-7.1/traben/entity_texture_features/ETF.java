package traben.entity_texture_features;

import com.demonwav.mcdev.annotations.Translatable;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;
import traben.entity_texture_features.config.screens.ETFConfigScreenWarnings;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.ETFEntityRenderStateViaReference;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.tconfig.TConfigHandler;

public class ETF {
   public static final String MOD_ID = "entity_texture_features";
   public static final Logger LOGGER = LoggerFactory.getLogger("Entity Texture Features");
   public static final int EMISSIVE_FEATURE_LIGHT_VALUE = 15728882;
   public static TConfigHandler<ETFConfigScreenWarnings.WarningConfig> warningConfigHandler = null;
   public static boolean IRIS_DETECTED = false;
   public static boolean FABRIC_API = false;
   public static ETFEntityRenderState.ETFRenderStateInit etfRenderStateConstructor = it -> new ETFEntityRenderStateViaReference(it);
   public static boolean SKIN_LAYERS_DETECTED = false;
   public static Set<TConfigHandler<?>> configHandlers = null;
   private static TConfigHandler<ETFConfig> CONFIG = null;

   public static TConfigHandler<ETFConfig> config() {
      if (CONFIG == null) {
         CONFIG = new TConfigHandler<>(ETFConfig::new, "entity_texture_features", "ETF_load");
      }

      return CONFIG;
   }

   public static void start() {
      CONFIG = new TConfigHandler<>(ETFConfig::new, "entity_texture_features", "ETF");
      registerConfigHandler(CONFIG);
      SKIN_LAYERS_DETECTED = isThisModLoaded("skinlayers3d");
      IRIS_DETECTED = isThisModLoaded("iris") || isThisModLoaded("oculus");
      FABRIC_API = isThisModLoaded("fabric") || isThisModLoaded("fabric-api");
      LOGGER.info("Loading Entity Texture Features, {}", randomQuip());
      warningConfigHandler = new TConfigHandler<>(ETFConfigScreenWarnings.WarningConfig::new, "etf_warnings.json", "ETF");
      registerConfigHandler(warningConfigHandler);
      ETFUtils2.checkModCompatibility();
      ETFConfigWarnings.registerConfigWarning(
         new ETFConfigWarning.Simple(
            "figura", "figura", "config.entity_texture_features.warn.figura.text.1", "config.entity_texture_features.warn.figura.text.2", () -> {
               CONFIG.getConfig().skinFeaturesEnabled = false;
               CONFIG.saveToFile();
            }
         ),
         new ETFConfigWarning.Simple(
            "enhancedblockentities",
            "enhancedblockentities",
            "config.entity_texture_features.warn.ebe.text.1",
            "config.entity_texture_features.warn.ebe.text.2",
            null
         ),
         new ETFConfigWarning.Simple(
            "quark", "quark", "config.entity_texture_features.warn.quark.text.3", "config.entity_texture_features.warn.quark.text.4", null
         ),
         new ETFConfigWarning.Simple(
            "iris & 3d skin layers",
            () -> IRIS_DETECTED && SKIN_LAYERS_DETECTED,
            "config.entity_texture_features.warn.iris_3d.text.1",
            "config.entity_texture_features.warn.iris_3d.text.2",
            null
         ),
         new ETFConfigWarning.Simple(
            "emf",
            () -> !isThisModLoaded("entity_model_features") && !isThisModLoaded("cem"),
            "config.entity_texture_features.warn.no_emf.text.1",
            "config.entity_texture_features.warn.no_emf.text.2",
            null
         )
      );
   }

   private static String randomQuip() {
      String[] quips = new String[]{
         "also try EMF!",
         "also known as ETF!",
         "not to be confused with CIT, seriously, why does that keep happening?",
         "the worst server plugin one guy on my discord has ever seen!",
         "your third cousin's, dog's, previous owner's, uncle's, old boss's, fourth favourite mod!",
         "Thanks for 10 Million plus downloads!!",
         "why does no one download Solid Mobs :(",
         "breaking your resource packs since 17 Jan 2022.",
         "not fit for consumption in the US.",
         "one of the mods ever made!",
         ",serutaeF erutxeT ytitnE gnidoaL",
         "hello there!",
         "you just lost the game.",
         "did you know if you turn off the lights and whisper 'OptiFine' 3 times you will lose 20fps.",
         "now compatible with Minecraft!",
         "now available for Terraria!",
         "OptiFine's weirder younger half-brother that runs around making train noises.",
         ":)",
         "did you know this mod was made because I missed the glowing drowned textures in the Fresh animations addons.",
         "0% Opti, 100% Fine.",
         "Curse you Perry the Platypus!",
         "Lisa needs braces.",
         "Paranormal ResourcePacktivity.",
         "Has Anyone Really Been Far Even as Decided to Use Even Go Want to do Look More Like?"
      };
      int rand = new Random().nextInt(quips.length);
      return quips[rand];
   }

   public static void registerConfigHandler(TConfigHandler<?> configHandler) {
      if (configHandlers == null) {
         configHandlers = new HashSet<>();
      }

      configHandlers.add(configHandler);
   }

   public static Screen getConfigScreen(Screen parent) {
      try {
         return new ETFConfigScreenMain(parent);
      } catch (Exception var2) {
         return null;
      }
   }

   public static Screen getConfigScreen(Minecraft ignored, Screen parent) {
      return getConfigScreen(parent);
   }

   @Nullable
   public static String getBiomeString(Level world, BlockPos pos) {
      return world != null && pos != null ? world.getBiome(pos).unwrapKey().toString().split(" / ")[1].replaceAll("[^\\da-zA-Z_:-]", "") : null;
   }

   @NotNull
   public static Component getTextFromTranslation(@Translatable(foldMethod = true) String translationKey) {
      return Component.translatable(translationKey);
   }

   public static Path getConfigDirectory() {
      return FMLPaths.GAMEDIR.get().resolve(FMLPaths.CONFIGDIR.get());
   }

   public static boolean isThisModLoaded(String modId) {
      try {
         ModList list = ModList.get();
         if (list != null) {
            return list.isLoaded(modId);
         }

         LoadingModList list2 = LoadingModList.get();
         if (list2 != null) {
            return list2.getModFileById(modId) != null;
         }

         ETFUtils2.logError("Forge ModList checking failed!");
      } catch (Exception var3) {
         ETFUtils2.logError("Forge ModList checking failed, via exception!");
      }

      return false;
   }

   public static List<String> modsLoaded() {
      try {
         ModList list = ModList.get();
         if (list != null) {
            return list.getMods().stream().<String>map(IModInfo::getModId).toList();
         }

         LoadingModList list2 = LoadingModList.get();
         if (list2 != null) {
            return list2.getModFiles().stream().flatMap(it -> it.getMods().stream()).<String>map(IModInfo::getModId).toList();
         }

         ETFUtils2.logError("Forge ModList checking failed!");
      } catch (Exception var2) {
         ETFUtils2.logError("Forge ModList checking failed, via exception!");
      }

      return List.of();
   }

   public static boolean isForge() {
      return !isFabric();
   }

   public static boolean isFabric() {
      return false;
   }
}
