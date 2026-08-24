package traben.entity_model_features.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFManager;

public class EMFDirectoryHandler {
   public final String namespace;
   public final String rawFileName;
   public final boolean isSubFolder;
   private final int packIndex;
   private final EMFDirectoryHandler.EMFDirectory actualDirectory;
   private final String suffixAndFileType;

   private EMFDirectoryHandler(String namespace, String modelFileName, String suffixAndFileType, boolean printing) {
      this.namespace = namespace;
      this.rawFileName = modelFileName;
      this.suffixAndFileType = suffixAndFileType;
      ResourceManager resources = Minecraft.getInstance().getResourceManager();
      Resource emfDirResource = this.getResourceOrNull(resources, EMFDirectoryHandler.EMFDirectory.EMF, printing);
      Resource emfSubDirResource = this.getResourceOrNull(resources, EMFDirectoryHandler.EMFDirectory.EMF_SUB, printing);
      Resource optifineDirResource = this.getResourceOrNull(resources, EMFDirectoryHandler.EMFDirectory.OPTIFINE, printing);
      Resource optifineSubDirResource = this.getResourceOrNull(resources, EMFDirectoryHandler.EMFDirectory.OPTIFINE_SUB, printing);
      if (emfDirResource == null && emfSubDirResource == null && optifineDirResource == null && optifineSubDirResource == null) {
         this.actualDirectory = null;
         this.packIndex = -1;
         this.isSubFolder = false;
      } else {
         String emfPack = this.getPackId(emfDirResource);
         String emfSubPack = this.getPackId(emfSubDirResource);
         String optifinePack = this.getPackId(optifineDirResource);
         String optifineSubPack = this.getPackId(optifineSubDirResource);
         ArrayList<String> packOrder = EMFManager.getInstance().getResourcePackList();
         int emfDirIndex = this.getPackIndex(emfPack, packOrder);
         int emfSubDirIndex = this.getPackIndex(emfSubPack, packOrder);
         int optifineDirIndex = this.getPackIndex(optifinePack, packOrder);
         int optifineSubDirIndex = this.getPackIndex(optifineSubPack, packOrder);
         int emfHighest = Math.max(emfDirIndex, emfSubDirIndex);
         int optifineHighest = Math.max(optifineDirIndex, optifineSubDirIndex);
         if (printing) {
            EMFUtils.log(" >>>> pack order indices: " + emfDirIndex + ", " + emfSubDirIndex + ", " + optifineDirIndex + ", " + optifineSubDirIndex);
         }

         if (emfHighest >= optifineHighest) {
            if (emfDirIndex <= emfSubDirIndex) {
               this.actualDirectory = EMFDirectoryHandler.EMFDirectory.EMF_SUB;
               this.packIndex = emfSubDirIndex;
               this.isSubFolder = true;
            } else {
               this.actualDirectory = EMFDirectoryHandler.EMFDirectory.EMF;
               this.packIndex = emfDirIndex;
               this.isSubFolder = false;
            }
         } else if (optifineDirIndex <= optifineSubDirIndex) {
            this.actualDirectory = EMFDirectoryHandler.EMFDirectory.OPTIFINE_SUB;
            this.packIndex = optifineSubDirIndex;
            this.isSubFolder = true;
         } else {
            this.actualDirectory = EMFDirectoryHandler.EMFDirectory.OPTIFINE;
            this.packIndex = optifineDirIndex;
            this.isSubFolder = false;
         }

         if (printing) {
            EMFUtils.log(" >> Final valid directory after checking: " + this.actualDirectory.getAsDirectory(namespace, modelFileName) + suffixAndFileType);
         }
      }
   }

   public static EMFDirectoryHandler basic(String filename) {
      return new EMFDirectoryHandler("minecraft", filename, ".jem", false);
   }

   @Nullable
   public static EMFDirectoryHandler getDirectoryManagerOrNull(
      boolean printing, @NotNull String namespace, @NotNull String modelFileName, @NotNull String suffixAndFileType
   ) {
      try {
         EMFDirectoryHandler directoryManager = new EMFDirectoryHandler(namespace, modelFileName, suffixAndFileType, printing);
         if (directoryManager.foundModel()) {
            return directoryManager;
         }
      } catch (Exception var5) {
         if (printing) {
            EMFUtils.log(
               " >> Exception when searching for: "
                  + EMFDirectoryHandler.EMFDirectory.OPTIFINE.getAsDirectory(namespace, modelFileName)
                  + suffixAndFileType
                  + ". "
                  + var5.getMessage()
            );
         }
      }

      if (printing) {
         EMFUtils.log(
            " >> Failed to find any files for: " + EMFDirectoryHandler.EMFDirectory.OPTIFINE.getAsDirectory(namespace, modelFileName) + suffixAndFileType
         );
      }

      return null;
   }

   public String getFileNameWithType() {
      return this.rawFileName + this.suffixAndFileType;
   }

   public String getRelativeDirectoryLocationNoValidation(String fileName) {
      return this.actualDirectory.getAsDirectory(this.namespace, this.rawFileName).replaceFirst(this.rawFileName + "$", fileName);
   }

   public int packIndex() {
      return this.packIndex;
   }

   public boolean validForThisBase(EMFDirectoryHandler propertiesOrSecond) {
      return propertiesOrSecond == null ? false : this.isSubFolder == propertiesOrSecond.isSubFolder && this.packIndex <= propertiesOrSecond.packIndex;
   }

   boolean foundModel() {
      return this.actualDirectory != null && this.packIndex != -1;
   }

   private Resource getResourceOrNull(ResourceManager resources, EMFDirectoryHandler.EMFDirectory directory, boolean printing) {
      try {
         ResourceLocation loc = EMFUtils.res(directory.getAsDirectory(this.namespace, this.rawFileName) + this.suffixAndFileType);
         if (printing) {
            boolean exists = EMFResourceCaching.resourceExists(resources, loc);
            EMFUtils.log(" >>> Checking directory: " + loc + ", exists = " + exists);
            return exists ? (Resource)resources.getResource(loc).orElse(null) : null;
         } else if (!EMFResourceCaching.resourceExists(resources, loc)) {
            return null;
         } else {
            Optional<Resource> res = resources.getResource(loc);
            return res.orElse(null);
         }
      } catch (Exception var6) {
         return null;
      }
   }

   private String getPackId(@Nullable Resource resource) {
      return resource == null ? null : resource.sourcePackId();
   }

   private int getPackIndex(@Nullable String pack, List<String> packOrder) {
      return pack == null ? -1 : packOrder.indexOf(pack);
   }

   public String getFinalFileLocation() {
      return this.actualDirectory.getAsDirectory(this.namespace, this.rawFileName) + this.suffixAndFileType;
   }

   public ResourceLocation getRelativeFilePossiblyEMFOverridden(String jpmOrVariantFileNameWithSuffixAndFileType) {
      EMFDirectoryHandler.EMFDirectory over = this.actualDirectory.override();
      EMFDirectoryHandler.EMFDirectory fall = this.actualDirectory.fallback();
      EMFDirectoryHandler.EMFDirectory first = over == null ? this.actualDirectory : over;
      EMFDirectoryHandler.EMFDirectory second = fall == null ? this.actualDirectory : fall;
      ResourceLocation sameDir = EMFUtils.res(
         first.getAsDirectory(this.namespace, this.rawFileName).replaceFirst(this.rawFileName + "$", jpmOrVariantFileNameWithSuffixAndFileType)
      );
      ResourceManager resources = Minecraft.getInstance().getResourceManager();
      return EMFResourceCaching.resourceExists(resources, sameDir)
         ? sameDir
         : EMFUtils.res(second.getAsDirectory(this.namespace, this.rawFileName).replaceFirst(this.rawFileName + "$", jpmOrVariantFileNameWithSuffixAndFileType));
   }

   @Override
   public String toString() {
      return "EMF model, ID = "
         + EMFDirectoryHandler.EMFDirectory.OPTIFINE.getAsDirectory(this.namespace, this.rawFileName)
         + ", actual = "
         + this.actualDirectory.getAsDirectory(this.namespace, this.rawFileName);
   }

   static enum EMFDirectory {
      EMF {
         @Override
         public String getAsDirectory(String namespace, String fileName) {
            return namespace + ":emf/cem/" + fileName;
         }
      },
      EMF_SUB {
         @Override
         public String getAsDirectory(String namespace, String fileName) {
            return namespace + ":emf/cem/" + fileName + "/" + fileName;
         }
      },
      OPTIFINE {
         @Override
         public String getAsDirectory(String namespace, String fileName) {
            return namespace + ":optifine/cem/" + fileName;
         }
      },
      OPTIFINE_SUB {
         @Override
         public String getAsDirectory(String namespace, String fileName) {
            return namespace + ":optifine/cem/" + fileName + "/" + fileName;
         }
      };

      public abstract String getAsDirectory(String var1, String var2);

      @Nullable
      public EMFDirectoryHandler.EMFDirectory fallback() {
         return switch (this) {
            case EMF -> OPTIFINE;
            case EMF_SUB -> OPTIFINE_SUB;
            default -> null;
         };
      }

      @Nullable
      public EMFDirectoryHandler.EMFDirectory override() {
         return switch (this) {
            case OPTIFINE -> EMF;
            case OPTIFINE_SUB -> EMF_SUB;
            default -> null;
         };
      }
   }
}
