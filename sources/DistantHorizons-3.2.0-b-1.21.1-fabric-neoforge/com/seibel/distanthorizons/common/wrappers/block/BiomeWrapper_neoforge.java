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
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Direct;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class BiomeWrapper_neoforge implements IBiomeWrapper {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final ConcurrentMap<Holder<Biome>, BiomeWrapper_neoforge> WRAPPER_BY_BIOME = new ConcurrentHashMap<>();
   public static final ConcurrentHashMap<String, BiomeWrapper_neoforge> WRAPPER_BY_RESOURCE_LOCATION = new ConcurrentHashMap<>();
   public static final String EMPTY_BIOME_STRING = "EMPTY";
   public static final BiomeWrapper_neoforge EMPTY_WRAPPER = new BiomeWrapper_neoforge(null, null);
   public static final String PLAINS_RESOURCE_LOCATION_STRING = "minecraft:plains";
   private static final HashSet<String> brokenResourceLocationStrings = new HashSet<>();
   private static boolean emptyStringWarningLogged = false;
   private static boolean emptyLevelSerializeFailLogged = false;
   public final Holder<Biome> biome;
   private String serialString;
   private final int hashCode;

   public static BiomeWrapper_neoforge getBiomeWrapper(Holder<Biome> biome, ILevelWrapper levelWrapper) {
      if (biome == null) {
         return EMPTY_WRAPPER;
      } else {
         BiomeWrapper_neoforge biomeWrapper = WRAPPER_BY_BIOME.get(biome);
         if (biomeWrapper != null) {
            return biomeWrapper;
         } else {
            BiomeWrapper_neoforge newWrapper = new BiomeWrapper_neoforge(biome, levelWrapper);
            WRAPPER_BY_BIOME.put(biome, newWrapper);
            return newWrapper;
         }
      }
   }

   private BiomeWrapper_neoforge(Holder<Biome> biome, ILevelWrapper levelWrapper) {
      this.biome = biome;
      this.serialString = this.serialize(levelWrapper);
      this.hashCode = Objects.hash(this.serialString);
   }

   @Override
   public String getName() {
      return this == EMPTY_WRAPPER ? "EMPTY" : this.biome.unwrapKey().orElse(Biomes.THE_VOID).registry().toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         BiomeWrapper_neoforge that = (BiomeWrapper_neoforge)obj;
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
         Level level = (Level)levelWrapper.getWrappedMcObject();
         RegistryAccess registryAccess = level.registryAccess();
         ResourceLocation resourceLocation = registryAccess.registryOrThrow(Registries.BIOME).getKey((Biome)this.biome.value());
         if (resourceLocation == null) {
            String biomeName = ((Biome)this.biome.value()).toString();
            LOGGER.warn("unable to serialize: " + biomeName);
            this.serialString = "";
         } else {
            this.serialString = resourceLocation.getNamespace() + ":" + resourceLocation.getPath();
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
         BiomeWrapper_neoforge foundWrapper = EMPTY_WRAPPER;

         BiomeWrapper_neoforge var7;
         try {
            Level level = (Level)levelWrapper.getWrappedMcObject();
            RegistryAccess registryAccess = level.registryAccess();
            BiomeWrapper$BiomeDeserializeResult_neoforge deserializeResult = deserializeBiome(resourceLocationString, registryAccess);
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

   public static BiomeWrapper$BiomeDeserializeResult_neoforge deserializeBiome(String resourceLocationString, RegistryAccess registryAccess) throws IOException {
      int separatorIndex = resourceLocationString.indexOf(":");
      if (separatorIndex == -1) {
         throw new IOException("Unable to parse resource location string: [" + resourceLocationString + "].");
      } else {
         ResourceLocation resourceLocation;
         try {
            resourceLocation = ResourceLocation.fromNamespaceAndPath(
               resourceLocationString.substring(0, separatorIndex), resourceLocationString.substring(separatorIndex + 1)
            );
         } catch (Exception var7) {
            throw new IOException("No Resource Location found for the string: [" + resourceLocationString + "] Error: [" + var7.getMessage() + "].");
         }

         Biome unwrappedBiome = (Biome)registryAccess.registryOrThrow(Registries.BIOME).get(resourceLocation);
         boolean success = unwrappedBiome != null;
         Holder<Biome> biome = new Direct(unwrappedBiome);
         return new BiomeWrapper$BiomeDeserializeResult_neoforge(success, biome);
      }
   }
}
