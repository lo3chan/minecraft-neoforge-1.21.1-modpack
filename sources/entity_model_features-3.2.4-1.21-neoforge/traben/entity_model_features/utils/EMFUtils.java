package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.jem_objects.EMFPartData;
import traben.entity_model_features.models.parts.EMFModelPartRoot;

public class EMFUtils {
   private static final String MOD_ID_SHORT = "EMF";
   private static final Logger LOGGER = LoggerFactory.getLogger("EMF");

   @NotNull
   public static ResourceLocation res(String fullPath) {
      return ResourceLocation.parse(fullPath);
   }

   @NotNull
   public static ResourceLocation res(String namespace, String path) {
      return ResourceLocation.fromNamespaceAndPath(namespace, path);
   }

   public static EMFModelPartRoot getArrowOrNull(ModelLayerLocation layer) {
      if (EMF.testForForgeLoadingError()) {
         return null;
      } else {
         MeshDefinition modelData = new MeshDefinition();
         PartDefinition modelPartData = modelData.getRoot();
         ModelPart part = modelPartData.bake(32, 32);
         ModelPart possiblyEMF = EMFManager.getInstance().injectIntoModelRootGetter(layer, part);
         return possiblyEMF instanceof EMFModelPartRoot ? (EMFModelPartRoot)possiblyEMF : null;
      }
   }

   public static void overrideMessage(String originalClass, String overriddenClassFromMod, boolean wasReverted) {
      LOGGER.warn("[EMF]: Entity model [" + originalClass + "] has been overridden by [" + overriddenClassFromMod + "] likely from a mod.");
      if (wasReverted) {
         LOGGER.warn(
            "[EMF]: Prevent model overrides option is enabled! EMF will attempt to revert the new model ["
               + overriddenClassFromMod
               + "] back into the original model ["
               + originalClass
               + "]. THIS MAY HAVE UNINTENDED EFFECTS ON THE OTHER MOD, DISABLE THIS EMF SETTING IF IT CAUSES CRASHES!"
         );
      }
   }

   public static void log(String message) {
      log(message, false, false);
   }

   public static void log(String message, boolean inChat) {
      log(message, inChat, false);
   }

   public static void log(String message, boolean inChat, boolean noPrefix) {
      if (inChat) {
         LocalPlayer plyr = Minecraft.getInstance().player;
         if (plyr != null) {
            plyr.displayClientMessage(Component.nullToEmpty((noPrefix ? "" : "§6[EMF]:§r ") + message), false);
         } else {
            LOGGER.info((noPrefix ? "" : "[EMF]: ") + message);
         }
      } else {
         LOGGER.info((noPrefix ? "" : "[EMF]: ") + message);
      }
   }

   public static void chat(String message) {
      LocalPlayer plyr = Minecraft.getInstance().player;
      if (plyr != null) {
         plyr.displayClientMessage(MutableComponent.create(new LiteralContents(message)), false);
      }
   }

   public static void logWarn(String message) {
      logWarn(message, false);
   }

   public static void logWarn(String message, boolean inChat) {
      if (inChat) {
         LocalPlayer plyr = Minecraft.getInstance().player;
         if (plyr != null) {
            plyr.displayClientMessage(Component.nullToEmpty("§6[EMF]§r: " + message), false);
         } else {
            LOGGER.warn("[EMF]: " + message);
         }
      } else {
         LOGGER.warn("[EMF]: " + message);
      }
   }

   public static void logError(String message) {
      logError(message, false);
   }

   public static void logError(String message, boolean inChat) {
      if (inChat) {
         LocalPlayer plyr = Minecraft.getInstance().player;
         if (plyr != null) {
            plyr.displayClientMessage(Component.nullToEmpty("§6[EMF]§r: " + message), false);
         } else {
            LOGGER.error("[EMF]: " + message);
         }
      } else {
         LOGGER.error("[EMF]: " + message);
      }
   }

   @Nullable
   public static EMFPartData readModelPart(ResourceLocation location) {
      boolean print = ((EMFConfig)EMF.config().getConfig()).logModelCreationData;

      try {
         ResourceManager manager = Minecraft.getInstance().getResourceManager();
         if (!EMFResourceCaching.resourceExists(manager, location)) {
            if (print) {
               log("jpm failed " + location + " does not exist", false);
            }

            return null;
         } else {
            Optional<Resource> res = manager.getResource(location);
            if (res.isEmpty()) {
               if (print) {
                  log("jpm failed " + location + " does not exist", false);
               }

               return null;
            } else {
               Resource jpmResource = res.get();
               Gson gson = new GsonBuilder().setPrettyPrinting().create();

               EMFPartData var7;
               try (BufferedReader reader = new BufferedReader(new InputStreamReader(jpmResource.open()))) {
                  var7 = (EMFPartData)gson.fromJson(reader, EMFPartData.class);
               }

               return var7;
            }
         }
      } catch (Exception var11) {
         log("jpm [" + location.toString() + "] failed " + var11, false);
         if (print) {
            var11.printStackTrace();
         }

         if (print) {
            log("jpm read returned null for " + location, false);
         }

         return null;
      }
   }

   public static String getIdUnique(Set<String> known, String desired) {
      while (known.contains(desired) || desired.isBlank()) {
         desired = desired + "#";
      }

      return desired;
   }
}
