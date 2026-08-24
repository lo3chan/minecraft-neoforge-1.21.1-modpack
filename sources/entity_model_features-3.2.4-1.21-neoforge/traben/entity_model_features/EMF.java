package traben.entity_model_features;

import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.animation.state.EMFEntityRenderStateViaReference;
import traben.entity_model_features.propeties.EntityVariableBooleanProperty;
import traben.entity_model_features.propeties.EntityVariableFloatProperty;
import traben.entity_model_features.propeties.GlobalVariableBooleanProperty;
import traben.entity_model_features.propeties.GlobalVariableFloatProperty;
import traben.entity_model_features.propeties.ModelRuleIndexProperty;
import traben.entity_model_features.propeties.ModelSuffixProperty;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.entity_texture_features.config.ETFConfigWarning.Simple;
import traben.entity_texture_features.features.property_reading.properties.RandomProperties.RandomPropertyFactory;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.tconfig.TConfigHandler;

public class EMF {
   public static final int EYES_FEATURE_LIGHT_VALUE = 15728881;
   public static final String MOD_ID = "entity_model_features";
   public static boolean forgeHadLoadingError = false;
   public static boolean testedForge = !ETF.isForge();
   public static boolean tempDisableModelModifications = false;
   public static boolean isLoadingPhase = true;
   private static TConfigHandler<EMFConfig> configHandler = null;

   public static TConfigHandler<EMFConfig> config() {
      if (configHandler == null) {
         configHandler = new TConfigHandler(EMFConfig::new, "entity_model_features", "EMF_load");
         ETF.registerConfigHandler(configHandler);
      }

      return configHandler;
   }

   public static void init() {
      configHandler = new TConfigHandler(EMFConfig::new, "entity_model_features", "EMF");
      ETF.registerConfigHandler(configHandler);
      ETFEntityRenderState.setEtfRenderStateConstructor("for EMF", etf -> new EMFEntityRenderStateViaReference((EMFEntity)etf));
      LogManager.getLogger().info("Loading Entity Model Features, " + randomQuip());
      EMFManager.getInstance();
      ETFApi.registerCustomRandomPropertyFactory(
         "entity_model_features",
         new RandomPropertyFactory[]{
            RandomPropertyFactory.of("modelRule", "entity_model_features.rule_property", ModelRuleIndexProperty::getPropertyOrNull),
            RandomPropertyFactory.of("modelSuffix", "entity_model_features.suffix_property", ModelSuffixProperty::getPropertyOrNull),
            RandomPropertyFactory.of("var", "entity_model_features.var_property", EntityVariableFloatProperty::getPropertyOrNull),
            RandomPropertyFactory.of("varb", "entity_model_features.varb_property", EntityVariableBooleanProperty::getPropertyOrNull),
            RandomPropertyFactory.of("global_var", "entity_model_features.global_var_property", GlobalVariableFloatProperty::getPropertyOrNull),
            RandomPropertyFactory.of("global_varb", "entity_model_features.global_varb_property", GlobalVariableBooleanProperty::getPropertyOrNull)
         }
      );
      ETFConfigWarnings.registerConfigWarning(
         new ETFConfigWarning[]{
            new Simple(
               "ebe_emf",
               () -> EMFManager.getInstance().wasEBEModified(),
               "entity_model_features.config.ebe_warn.1",
               "entity_model_features.config.ebe_warn.2",
               null
            ) {
               public String getSubTitle() {
                  Set<String> ebe = EMFManager.getInstance().EBE_JEMS_FOUND_LAST;
                  double timer = System.currentTimeMillis() % 9000.0;
                  if (ebe.isEmpty() || timer < 3000.0) {
                     return super.getSubTitle();
                  } else {
                     return timer < 6000.0
                        ? Component.translatable("entity_model_features.config.ebe_warn.3").getString() + ebe
                        : Component.translatable("entity_model_features.config.ebe_warn.4").getString();
                  }
               }
            }
         }
      );
      ETFConfigWarnings.registerConfigWarning(
         new ETFConfigWarning[]{
            new Simple(
               "emf_load",
               () -> !EMFManager.getInstance().loadingExceptions.isEmpty(),
               "entity_model_features.config.load_warn.1",
               "entity_model_features.config.load_warn.2",
               null
            )
         }
      );
   }

   public static Screen getConfigScreen(Screen parent) {
      return getConfigScreen(null, parent);
   }

   public static Screen getConfigScreen(Minecraft ignored, Screen parent) {
      return ETF.getConfigScreen(parent);
   }

   public static boolean testForForgeLoadingError() {
      if (!testedForge) {
         testedForge = true;

         try {
            EMFManager.getInstance();
         } catch (IncompatibleClassChangeError var1) {
            if (!var1.getMessage().contains("cannot inherit from final class")) {
               throw var1;
            }

            forgeHadLoadingError = true;
            EMFUtils.logError(
               "EMF has crashed due to a (probably) unrelated forge dependency error,\n EMF has been disabled so the true culprit will be sent to users after game load:\n"
                  + var1.getMessage()
            );
         }
      }

      return forgeHadLoadingError;
   }

   private static String randomQuip() {
      String[] quips = new String[]{
         "special thanks to Cody, top donator!",
         "your third cousin's, dog's, previous owner's, uncle's, old boss's, fifth favourite mod!",
         "breaking your resource packs since April 1st 2023.",
         "not fit for consumption in Portugal.",
         "one of the mods ever made!",
         ",serutaeF ledoM ytitnE gnidoaL",
         "did you know if you turn off the lights and whisper 'OptiFine' 3 times you will lose 20fps.",
         "now compatible with Minecraft Legends!",
         "now available for Terraria!",
         "OptiFine's weirder younger half-brother that runs around making train models.",
         "(:",
         "0% Opti, 70% Fine.",
         "yes EMF breaks your resource pack, on purpose >:). There are 300 lines of code dedicated just for detecting if it is you specifically and if your favourite resource pack is installed, then EMF breaks it >:)\n/s",
         "we get there when we get there.",
         "the ETA is a lie.",
         "allegedly compatible with the OptiFine format.",
         "now compatible with every mod, except all the ones that aren't...",
         "100% of the time it works 90% of the time!",
         "now moving all models 0.00001 blocks to the left every 4 seconds.",
         "PI = 3.1415927 and you can't convince me otherwise.",
         "90 =" + (float)Math.toRadians(90.0) + "!",
         "making those animations fresh since 1862!",
         "Trabee got the flu!",
         "is this 'EPF' in the room with us now?"
      };
      return quips[new Random().nextInt(quips.length)];
   }
}
