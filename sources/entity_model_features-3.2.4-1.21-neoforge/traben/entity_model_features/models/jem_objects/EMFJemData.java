package traben.entity_model_features.models.jem_objects;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFException;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.EMFModelMappings;
import traben.entity_model_features.models.EMFModel_ID;
import traben.entity_model_features.utils.EMFDirectoryHandler;
import traben.entity_model_features.utils.EMFResourceCaching;
import traben.entity_model_features.utils.EMFUtils;

public class EMFJemData {
   private final transient LinkedHashMap<String, List<LinkedHashMap<String, String>>> allTopLevelAnimationsByVanillaPartName = new LinkedHashMap<>();
   public String texture = "";
   public int[] textureSize = null;
   public double shadow_size = 1.0;
   public LinkedList<EMFPartData> models = new LinkedList<>();
   public transient EMFDirectoryHandler directoryContext = null;
   private transient EMFModel_ID mobModelIDInfo = null;
   private transient ResourceLocation customTexture = null;
   public transient boolean hasAttachments = false;
   private static final Pattern WHITESPACE = Pattern.compile("\\s");
   private static final Pattern THIS = Pattern.compile("(?<=\\W|^)this(?=\\W)");
   private static final Pattern PART = Pattern.compile("(?<=\\W|^)part(?=\\W)");

   public LinkedHashMap<String, List<LinkedHashMap<String, String>>> getAllTopLevelAnimationsByVanillaPartName() {
      return this.allTopLevelAnimationsByVanillaPartName;
   }

   public EMFModel_ID getMobModelIDInfo() {
      return this.mobModelIDInfo;
   }

   public ResourceLocation getCustomTexture() {
      return this.customTexture;
   }

   @Nullable
   public ResourceLocation validateJemTexture(String textureIn) {
      return this.validateJemTexture(textureIn, false);
   }

   @Nullable
   public ResourceLocation validateJemTexture(String textureIn, boolean canRemoveRedundancy) {
      if (textureIn != null && !textureIn.isBlank()) {
         ResourceLocation res = this.validateResourcePathAndExists(textureIn, "png");
         return res == null ? MissingTextureAtlasSprite.getLocation() : res;
      } else {
         return null;
      }
   }

   @Nullable
   public ResourceLocation validateResourcePathAndExists(String pathIn, @Nullable String fileTypeExtension) {
      if (pathIn != null && !pathIn.isBlank()) {
         String pathTest = pathIn.trim();
         if (!pathTest.isBlank()) {
            if (fileTypeExtension != null && !pathTest.endsWith("." + fileTypeExtension)) {
               pathTest = pathTest + "." + fileTypeExtension;
            }

            if (!pathTest.contains(":")) {
               if (!pathTest.contains("/")) {
                  pathTest = this.directoryContext.getRelativeDirectoryLocationNoValidation(pathTest);
               } else if (pathTest.startsWith("./")) {
                  pathTest = this.directoryContext.getRelativeDirectoryLocationNoValidation(pathTest.replaceFirst("\\./", ""));
               } else if (pathTest.startsWith("~/")) {
                  pathTest = "optifine/" + pathTest.replaceFirst("~/", "");
               }
            }

            if (ResourceLocation.tryParse(pathTest) != null) {
               ResourceLocation possibleResource = EMFUtils.res(pathTest);
               if (EMFResourceCaching.resourceExists(Minecraft.getInstance().getResourceManager(), possibleResource)) {
                  return possibleResource;
               }
            } else {
               EMFUtils.logWarn("Invalid resource identifier: " + pathTest + " for " + this.directoryContext.getFileNameWithType());
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public void prepare(EMFDirectoryHandler directoryContext, EMFModel_ID mobModelIDInfo) {
      try {
         this.directoryContext = directoryContext;
         this.mobModelIDInfo = mobModelIDInfo;
         if (this.textureSize == null || this.textureSize.length != 2) {
            this.textureSize = new int[]{64, 32};
            EMFUtils.logWarn("No textureSize provided for: " + directoryContext.getFileNameWithType() + ". Defaulting to 64x32 texture size for model.");
         }

         this.customTexture = this.validateJemTexture(this.texture, true);
         Map<String, String> map = EMFModelMappings.getMapOf(mobModelIDInfo, null);

         for (EMFPartData partData : this.models) {
            if (partData.part != null && map.containsKey(partData.part)) {
               partData.originalPart = partData.part;
               partData.part = map.get(partData.part);
            }
         }

         for (EMFPartData model : this.models) {
            model.prepare(this.textureSize, this);
         }

         if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
            EMFUtils.log("originalModels #= " + this.models.size());
         }

         LinkedHashMap<String, EMFPartData> orderedParts = new LinkedHashMap<>();

         for (EMFPartData partDatax : this.models) {
            String newId = EMFUtils.getIdUnique(orderedParts.keySet(), partDatax.id);
            if (!newId.equals(partDatax.id)) {
               partDatax.id = newId;
            }

            orderedParts.put(partDatax.id, partDatax);
         }

         if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
            EMFUtils.log("orderedParts = " + orderedParts);
         }

         for (EMFPartData part : orderedParts.values()) {
            if (part.animations != null) {
               LinkedList<LinkedHashMap<String, String>> animationsList = new LinkedList<>();

               for (LinkedHashMap<String, String> animation : part.animations) {
                  LinkedHashMap<String, String> processedAnimations = new LinkedHashMap<>();
                  animation.forEach((key, anim) -> {
                     key = processAnimAndKeyString(part, key);
                     anim = processAnimAndKeyString(part, anim);
                     if (!key.isBlank() && !anim.isBlank()) {
                        processedAnimations.put(key, anim);
                     }
                  });
                  if (!processedAnimations.isEmpty()) {
                     animationsList.add(processedAnimations);
                  }
               }

               if (!animationsList.isEmpty()) {
                  this.allTopLevelAnimationsByVanillaPartName.computeIfAbsent(part.part, k -> new LinkedList<>()).addAll(animationsList);
               }
            }
         }

         if (this.shadow_size != 1.0) {
            this.shadow_size = Math.max(this.shadow_size, 0.0);
            LinkedHashMap<String, String> shadowAnim = new LinkedHashMap<>();
            shadowAnim.put("render.shadow_size", String.valueOf(this.shadow_size));
            this.allTopLevelAnimationsByVanillaPartName.computeIfAbsent("root", k -> new LinkedList<>()).add(shadowAnim);
         }
      } catch (Exception var11) {
         String message = "Error preparing jem data, for model [" + mobModelIDInfo.getDisplayFileName() + "]: " + var11.getMessage();
         EMFUtils.logError(message);
         throw (RuntimeException)EMFException.recordException(new RuntimeException(message));
      }
   }

   @NotNull
   private static String processAnimAndKeyString(EMFPartData part, String anim) {
      String result = WHITESPACE.matcher(anim.trim()).replaceAll("");
      result = THIS.matcher(result).replaceAll(part.id);
      return PART.matcher(result).replaceAll(Objects.requireNonNullElse(part.originalPart, part.part));
   }

   @Override
   public String toString() {
      return "EMF_JemData{texture='"
         + this.texture
         + "', textureSize="
         + Arrays.toString(this.textureSize)
         + ", shadow_size="
         + this.shadow_size
         + ", models="
         + this.models.toString()
         + "}";
   }
}
