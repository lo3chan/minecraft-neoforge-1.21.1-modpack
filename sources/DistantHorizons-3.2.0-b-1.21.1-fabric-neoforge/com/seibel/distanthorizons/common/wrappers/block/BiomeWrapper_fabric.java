package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_1972;
import net.minecraft.class_2960;
import net.minecraft.class_5455;
import net.minecraft.class_6880;
import net.minecraft.class_7924;
import net.minecraft.class_6880.class_6881;

public class BiomeWrapper_fabric implements IBiomeWrapper {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final ConcurrentMap<class_6880<class_1959>, BiomeWrapper_fabric> WRAPPER_BY_BIOME = new ConcurrentHashMap<>();
   public static final ConcurrentHashMap<String, BiomeWrapper_fabric> WRAPPER_BY_RESOURCE_LOCATION = new ConcurrentHashMap<>();
   public static final String EMPTY_BIOME_STRING = "EMPTY";
   public static final BiomeWrapper_fabric EMPTY_WRAPPER = new BiomeWrapper_fabric(null, null);
   public static final String PLAINS_RESOURCE_LOCATION_STRING = "minecraft:plains";
   private static final HashSet<String> brokenResourceLocationStrings = new HashSet<>();
   private static boolean emptyStringWarningLogged = false;
   private static boolean emptyLevelSerializeFailLogged = false;
   public final class_6880<class_1959> biome;
   private String serialString;
   private final int hashCode;

   public static BiomeWrapper_fabric getBiomeWrapper(class_6880<class_1959> biome, ILevelWrapper levelWrapper) {
      if (biome == null) {
         return EMPTY_WRAPPER;
      } else {
         BiomeWrapper_fabric biomeWrapper = WRAPPER_BY_BIOME.get(biome);
         if (biomeWrapper != null) {
            return biomeWrapper;
         } else {
            BiomeWrapper_fabric newWrapper = new BiomeWrapper_fabric(biome, levelWrapper);
            WRAPPER_BY_BIOME.put(biome, newWrapper);
            return newWrapper;
         }
      }
   }

   private BiomeWrapper_fabric(class_6880<class_1959> biome, ILevelWrapper levelWrapper) {
      this.biome = biome;
      this.serialString = this.serialize(levelWrapper);
      this.hashCode = Objects.hash(this.serialString);
   }

   @Override
   public String getName() {
      return this == EMPTY_WRAPPER ? "EMPTY" : this.biome.method_40230().orElse(class_1972.field_9473).method_41185().toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         BiomeWrapper_fabric that = (BiomeWrapper_fabric)obj;
         return Objects.equals(this.getSerialString(), that.getSerialString());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Override
   public String getSerialString() {
      return this.serialString;
   }

   @Override
   public Object getWrappedMcObject() {
      return this.biome;
   }

   @Override
   public String toString() {
      return this.getSerialString();
   }

   public String serialize(ILevelWrapper levelWrapper) {
      if (this.biome == null) {
         return "EMPTY";
      } else if (levelWrapper == null) {
         if (!emptyLevelSerializeFailLogged) {
            emptyLevelSerializeFailLogged = true;
            LOGGER.warn(
               "Unable to serialize biome: [" + this.biome + "] because the passed in level wrapper is null. Future errors of this type won't be logged."
            );
         }

         return "EMPTY";
      } else {
         class_1937 level = (class_1937)levelWrapper.getWrappedMcObject();
         class_5455 registryAccess = level.method_30349();
         class_2960 resourceLocation = registryAccess.method_30530(class_7924.field_41236).method_10221((class_1959)this.biome.comp_349());
         if (resourceLocation == null) {
            String biomeName = ((class_1959)this.biome.comp_349()).toString();
            LOGGER.warn("unable to serialize: " + biomeName);
            this.serialString = "";
         } else {
            this.serialString = resourceLocation.method_12836() + ":" + resourceLocation.method_12832();
         }

         return this.serialString;
      }
   }

   public static IBiomeWrapper deserialize(String resourceLocationString, ILevelWrapper levelWrapper) throws IOException {
      String finalResourceStateString = resourceLocationString;
      if (resourceLocationString.equals("EMPTY")) {
         if (!emptyStringWarningLogged) {
            emptyStringWarningLogged = true;
            LOGGER.warn(
               "[EMPTY] biome string deserialized. This may mean the level was null when a save was attempted, a file saving error, or a biome saving error. Future errors will not be logged."
            );
         }

         return EMPTY_WRAPPER;
      } else if (resourceLocationString.trim().isEmpty() || resourceLocationString.equals("")) {
         LOGGER.warn("Null biome string deserialized.");
         return EMPTY_WRAPPER;
      } else if (WRAPPER_BY_RESOURCE_LOCATION.containsKey(resourceLocationString)) {
         return WRAPPER_BY_RESOURCE_LOCATION.get(resourceLocationString);
      } else {
         BiomeWrapper_fabric foundWrapper = EMPTY_WRAPPER;

         BiomeWrapper_fabric var7;
         try {
            class_1937 level = (class_1937)levelWrapper.getWrappedMcObject();
            class_5455 registryAccess = level.method_30349();
            BiomeWrapper$BiomeDeserializeResult_fabric deserializeResult = deserializeBiome(resourceLocationString, registryAccess);
            if (deserializeResult.success) {
               return getBiomeWrapper(deserializeResult.biome, levelWrapper);
            }

            if (!brokenResourceLocationStrings.contains(resourceLocationString)) {
               brokenResourceLocationStrings.add(resourceLocationString);
               LOGGER.warn("Unable to deserialize biome from string: [" + resourceLocationString + "]");
            }

            var7 = EMPTY_WRAPPER;
         } catch (Exception var11) {
            throw new IOException("Failed to deserialize the string [" + finalResourceStateString + "] into a BiomeWrapper: " + var11.getMessage(), var11);
         } finally {
            WRAPPER_BY_RESOURCE_LOCATION.putIfAbsent(resourceLocationString, foundWrapper);
         }

         return var7;
      }
   }

   public static BiomeWrapper$BiomeDeserializeResult_fabric deserializeBiome(String resourceLocationString, class_5455 registryAccess) throws IOException {
      int separatorIndex = resourceLocationString.indexOf(":");
      if (separatorIndex == -1) {
         throw new IOException("Unable to parse resource location string: [" + resourceLocationString + "].");
      } else {
         class_2960 resourceLocation;
         try {
            resourceLocation = class_2960.method_60655(
               resourceLocationString.substring(0, separatorIndex), resourceLocationString.substring(separatorIndex + 1)
            );
         } catch (Exception var7) {
            throw new IOException("No Resource Location found for the string: [" + resourceLocationString + "] Error: [" + var7.getMessage() + "].");
         }

         class_1959 unwrappedBiome = (class_1959)registryAccess.method_30530(class_7924.field_41236).method_10223(resourceLocation);
         boolean success = unwrappedBiome != null;
         class_6880<class_1959> biome = new class_6881(unwrappedBiome);
         return new BiomeWrapper$BiomeDeserializeResult_fabric(success, biome);
      }
   }
}
