package traben.entity_model_features.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFException;
import traben.entity_texture_features.utils.ETFUtils2;

public class EMFModel_ID implements Comparable<EMFModel_ID> {
   public String namespace = "minecraft";
   @NotNull
   private String fileName;
   @NotNull
   private String mapId;
   private final List<EMFModel_ID.FallbackModel> fallBackModels;

   public EMFModel_ID(String both) {
      this(both, both);
   }

   public EMFModel_ID(String both, String mapId) {
      this(both, mapId, new ArrayList<>());
   }

   private EMFModel_ID(@NotNull String both, @NotNull String mapId, List<EMFModel_ID.FallbackModel> fallBackModels) {
      this.fileName = both;
      this.mapId = mapId;
      this.fallBackModels = fallBackModels;
   }

   public EMFModel_ID setFileName(String fileName) throws EMFException {
      if (fileName.contains(":")) {
         String[] split = fileName.split(":");
         if (split.length != 2) {
            ETFUtils2.logError("Invalid file name: " + fileName);
            throw new EMFException("Invalid model file name: " + fileName);
         }

         this.namespace = split[0];
         this.fileName = split[1];
      } else {
         this.fileName = fileName;
      }

      return this;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         EMFModel_ID that = (EMFModel_ID)o;
         return this.fileName.equals(that.fileName) && Objects.equals(this.mapId, that.mapId);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.fileName, this.mapId);
   }

   @Override
   public String toString() {
      return this.fileName;
   }

   public EMFModel_ID setBoth(String both) {
      this.fileName = both;
      this.mapId = both;
      return this;
   }

   public boolean areBothSame() {
      return this.fileName.equals(this.mapId);
   }

   public EMFModel_ID setBoth(String fileName, String mapId) {
      this.fileName = fileName;
      this.mapId = mapId;
      return this;
   }

   public String getfileName() {
      return this.fileName;
   }

   public boolean hasFallbackModels() {
      return !this.fallBackModels.isEmpty();
   }

   public void forEachFallback(Consumer<EMFModel_ID> action) {
      EMFModel_ID fallbackModel = this;

      while (fallbackModel.hasFallbackModels()) {
         fallbackModel = fallbackModel.getNextFallbackModel();
         if (fallbackModel == null) {
            return;
         }

         action.accept(fallbackModel);
      }
   }

   @Nullable
   public EMFModel_ID getNextFallbackModel() {
      if (this.hasFallbackModels()) {
         EMFModel_ID.FallbackModel next = this.fallBackModels.get(0);
         EMFModel_ID second = new EMFModel_ID(next.fileName, this.mapId, this.fallBackModels.subList(1, this.fallBackModels.size()));
         second.namespace = next.namespace == null ? this.namespace : next.namespace;
         return second;
      } else {
         return null;
      }
   }

   public EMFModel_ID addFallbackModel(String namespace, String fileName) throws EMFException {
      return this.addFallbackModel(namespace, fileName, false);
   }

   public EMFModel_ID addFallbackModel(String namespace, String fileName, boolean first) throws EMFException {
      if (fileName.contains(":")) {
         String[] split = fileName.split(":");
         if (split.length != 2) {
            throw new EMFException("Invalid fallback model file name: " + fileName);
         }

         namespace = split[0];
         fileName = split[1];
      }

      if (first) {
         this.fallBackModels.add(0, new EMFModel_ID.FallbackModel(namespace, fileName));
      } else {
         this.fallBackModels.add(new EMFModel_ID.FallbackModel(namespace, fileName));
      }

      return this;
   }

   public EMFModel_ID pushNewMainModelAddingOldAsFallback(String fileName) throws EMFException {
      this.addFallbackModel(this.namespace, this.fileName, true);
      this.fileName = fileName;
      return this;
   }

   public EMFModel_ID pushNewMainModelAndMapIdAddingOldAsFallback(String both) throws EMFException {
      return this.pushNewMainModelAndMapIdAddingOldAsFallback(both, both);
   }

   public EMFModel_ID pushNewMainModelAndMapIdAddingOldAsFallback(String fileName, String mapId) throws EMFException {
      this.addFallbackModel(this.namespace, this.fileName, true);
      this.fileName = fileName;
      this.mapId = mapId;
      return this;
   }

   public EMFModel_ID addFallbackModel(String fileName) throws EMFException {
      return this.addFallbackModel(this.namespace, fileName);
   }

   public EMFModel_ID setMapIdAndAddFallbackModel(String both) throws EMFException {
      return this.setMapIdAndAddFallbackModel(both, both);
   }

   public EMFModel_ID setMapIdAndAddFallbackModel(@NotNull String mapId, String fileName) throws EMFException {
      this.mapId = mapId;
      if (fileName.contains(":")) {
         String[] split = fileName.split(":");
         if (split.length == 2) {
            this.addFallbackModel(split[0], split[1]);
         } else {
            this.addFallbackModel(fileName);
         }
      } else {
         this.addFallbackModel(fileName);
      }

      return this;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public void finishAndPrepAutomatedFallbacks() throws EMFException {
      if (this.getfileName().contains(":")) {
         String[] split = this.fileName.split(":");
         if (split.length == 2) {
            this.assertNamespaceAndCreateDeprecatedModdedFileName(split[0], split[1]);
         }
      }

      if (!"minecraft".equals(this.namespace)) {
         this.assertNamespaceAndCreateDeprecatedModdedFileName(this.namespace, this.fileName);
      }

      if (this.fileName.endsWith("_armor")) {
         if (this.fileName.endsWith("_baby_inner_armor")) {
            this.addFallbackModel("baby_inner_armor");
         } else if (this.fileName.endsWith("_baby_outer_armor")) {
            this.addFallbackModel("baby_outer_armor");
         }

         if (this.fileName.endsWith("_inner_armor")) {
            this.addFallbackModel("inner_armor");
         } else if (this.fileName.endsWith("_outer_armor")) {
            this.addFallbackModel("outer_armor");
         }
      }

      this.fallBackModels.removeIf(next -> next.fileName.isBlank());
   }

   private void assertNamespaceAndCreateDeprecatedModdedFileName(String namespace, String fileName) throws EMFException {
      this.namespace = namespace;
      this.fileName = fileName;
      this.addFallbackModel("minecraft", "modded/" + namespace + "/" + fileName);
   }

   public String getMapId() {
      return this.getNamespace().equals("minecraft") ? this.mapId : this.getNamespace() + ":" + this.mapId;
   }

   public int compareTo(@NotNull EMFModel_ID o) {
      return this.getfileName().compareTo(o.getfileName());
   }

   public String getDisplayFileName() {
      return this.fileName.startsWith("modded/") ? this.fileName + ".jem" : "assets/" + this.namespace + "/optifine/cem/" + this.fileName + ".jem";
   }

   private record FallbackModel(String namespace, String fileName) {
   }
}
