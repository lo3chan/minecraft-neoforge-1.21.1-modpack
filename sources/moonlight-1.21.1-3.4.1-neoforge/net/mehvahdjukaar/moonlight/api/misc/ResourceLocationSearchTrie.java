package net.mehvahdjukaar.moonlight.api.misc;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ResourceLocationSearchTrie extends PathSearchTrie<ResourceLocation> {
   public void insert(ResourceLocation objectToAdd) {
      super.insert(getResPath(objectToAdd), objectToAdd);
   }

   public void insertPath(String fullPath) {
      super.insert(fullPath, fromPath(fullPath));
   }

   @NotNull
   public static String getResPath(ResourceLocation objectToAdd) {
      String path = objectToAdd.getNamespace() + "/" + objectToAdd.getPath();
      return getFolderPath(path);
   }

   private static String getFolderPath(String path) {
      int lastIndex = path.lastIndexOf(47);
      return lastIndex == -1 ? "" : path.substring(0, lastIndex);
   }

   @NotNull
   private static ResourceLocation fromPath(@NotNull String folderPath) {
      int firstSlash = folderPath.indexOf(47);
      if (firstSlash == -1) {
         return ResourceLocation.fromNamespaceAndPath(folderPath, "");
      } else {
         String namespace = folderPath.substring(0, firstSlash);
         String path = folderPath.substring(firstSlash + 1);
         return ResourceLocation.fromNamespaceAndPath(namespace, path);
      }
   }

   public boolean remove(ResourceLocation object) {
      return super.remove(getResPath(object));
   }
}
