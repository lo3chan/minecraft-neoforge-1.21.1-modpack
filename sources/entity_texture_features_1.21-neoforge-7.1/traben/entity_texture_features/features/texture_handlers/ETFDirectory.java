package traben.entity_texture_features.features.texture_handlers;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

public enum ETFDirectory {
   DOES_NOT_EXIST(null),
   ETF(new String[]{"textures", "etf/random"}),
   OLD_OPTIFINE(new String[]{"textures/entity", "optifine/mob"}),
   OPTIFINE(new String[]{"textures", "optifine/random"}),
   VANILLA(null);

   private final String[] replaceStrings;

   private ETFDirectory(String[] replaceStrings) {
      this.replaceStrings = replaceStrings;
   }

   public static HashMap<ResourceLocation, ETFDirectory> getCache() {
      return ETFManager.getInstance().ETF_DIRECTORY_CACHE;
   }

   @Nullable
   public static ResourceLocation getDirectoryVersionOf(@Nullable ResourceLocation vanillaIdentifier) {
      if (vanillaIdentifier == null) {
         return null;
      } else {
         ETFDirectory directory = getDirectoryOf(vanillaIdentifier);

         return switch (directory) {
            case DOES_NOT_EXIST -> null;
            case VANILLA -> vanillaIdentifier;
            default -> getIdentifierAsDirectory(vanillaIdentifier, directory);
         };
      }
   }

   @NotNull
   public static ETFDirectory getDirectoryOf(@NotNull ResourceLocation vanillaIdentifier) {
      HashMap<ResourceLocation, ETFDirectory> cache = getCache();
      ETFDirectory value = cache.get(vanillaIdentifier);
      if (value == null) {
         value = findDirectoryOf(vanillaIdentifier);
         cache.put(vanillaIdentifier, value);
      }

      return value;
   }

   @NotNull
   private static ETFDirectory findDirectoryOf(ResourceLocation vanillaIdentifier) {
      String path = vanillaIdentifier.getPath();
      ResourceManager resources = Minecraft.getInstance().getResourceManager();
      if (path.contains("etf/random/entity") && resources.getResource(vanillaIdentifier).isPresent()) {
         return ETF;
      } else if (path.contains("optifine/random/entity") && resources.getResource(vanillaIdentifier).isPresent()) {
         return OPTIFINE;
      } else if (path.contains("optifine/mob") && resources.getResource(vanillaIdentifier).isPresent()) {
         return OLD_OPTIFINE;
      } else {
         ArrayList<ETFDirectory> foundDirectories = new ArrayList<>();
         if (resources.getResource(getIdentifierAsDirectory(vanillaIdentifier, VANILLA)).isPresent()) {
            foundDirectories.add(VANILLA);
         }

         if (resources.getResource(getIdentifierAsDirectory(vanillaIdentifier, OLD_OPTIFINE)).isPresent()) {
            foundDirectories.add(OLD_OPTIFINE);
         }

         if (resources.getResource(getIdentifierAsDirectory(vanillaIdentifier, OPTIFINE)).isPresent()) {
            foundDirectories.add(OPTIFINE);
         }

         if (resources.getResource(getIdentifierAsDirectory(vanillaIdentifier, ETF)).isPresent()) {
            foundDirectories.add(ETF);
         }

         if (foundDirectories.isEmpty()) {
            return DOES_NOT_EXIST;
         } else if (foundDirectories.size() == 1) {
            return foundDirectories.get(0);
         } else {
            HashMap<String, ETFDirectory> resourcePackNames = new HashMap<>();

            for (ETFDirectory directory : foundDirectories) {
               resources.getResource(getIdentifierAsDirectory(vanillaIdentifier, directory))
                  .ifPresent(value -> resourcePackNames.put(value.sourcePackId(), directory));
            }

            String returnedPack = ETFUtils2.returnNameOfHighestPackFromTheseMultiple(resourcePackNames.keySet().toArray(new String[0]));
            return returnedPack != null ? resourcePackNames.get(returnedPack) : VANILLA;
         }
      }
   }

   @NotNull
   public static ResourceLocation getIdentifierAsDirectory(ResourceLocation identifier, ETFDirectory directory) {
      return directory.doesReplace()
         ? ETFUtils2.res(identifier.getNamespace(), identifier.getPath().replace(directory.replaceStrings[0], directory.replaceStrings[1]))
         : identifier;
   }

   public boolean doesReplace() {
      return this.replaceStrings != null;
   }
}
