package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.BlockBiomeWrapperPair;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPosMutable;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDhTintGetter_neoforge implements BlockAndTintGetter {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final ConcurrentHashMap<String, Holder<Biome>> BIOME_BY_RESOURCE_STRING = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<BlockBiomeWrapperPair, Integer> COLOR_BY_BLOCK_BIOME_PAIR = new ConcurrentHashMap<>();
   protected BiomeWrapper_neoforge biomeWrapper;
   protected BlockStateWrapper_neoforge blockStateWrapper;
   protected FullDataSourceV2 fullDataSource;
   protected int smoothingRadiusInBlocks;
   protected IClientLevelWrapper clientLevelWrapper;

   public void update(
      BiomeWrapper_neoforge biomeWrapper, BlockStateWrapper_neoforge blockStateWrapper, FullDataSourceV2 fullDataSource, IClientLevelWrapper clientLevelWrapper
   ) {
      this.biomeWrapper = biomeWrapper;
      this.blockStateWrapper = blockStateWrapper;
      this.fullDataSource = fullDataSource;
      this.clientLevelWrapper = clientLevelWrapper;
      this.smoothingRadiusInBlocks = Config.Client.Advanced.Graphics.Quality.lodBiomeBlending.get();
   }

   public int getBlockTint(@NotNull BlockPos blockPos, @NotNull ColorResolver colorResolver) {
      DhBlockPosMutable mutableBlockPos = new DhBlockPosMutable(blockPos.getX(), blockPos.getY(), blockPos.getZ());
      return this.tryGetBlockTint(mutableBlockPos, colorResolver);
   }

   public int tryGetBlockTint(DhBlockPosMutable mutableBlockPos) {
      return this.tryGetBlockTint(mutableBlockPos, null);
   }

   private int tryGetBlockTint(DhBlockPosMutable mutableBlockPos, @Nullable ColorResolver colorResolver) {
      byte dataSourceDetailLevel = DhSectionPos.getDetailLevel(this.fullDataSource.getPos());
      dataSourceDetailLevel = (byte)(dataSourceDetailLevel - 6);
      int dataSourceLodWidthInBlocks = DhSectionPos.getDetailLevelWidthInBlocks(dataSourceDetailLevel);
      if (this.smoothingRadiusInBlocks != 0 && dataSourceLodWidthInBlocks <= this.smoothingRadiusInBlocks) {
         int dataPointCount = 0;
         int rollingRed = 0;
         int rollingGreen = 0;
         int rollingBlue = 0;
         int xMin = mutableBlockPos.getX() - this.smoothingRadiusInBlocks;
         int xMax = mutableBlockPos.getX() + this.smoothingRadiusInBlocks + 1;
         int zMin = mutableBlockPos.getZ() - this.smoothingRadiusInBlocks;
         int zMax = mutableBlockPos.getZ() + this.smoothingRadiusInBlocks + 1;
         int levelMinY = this.clientLevelWrapper.getMinHeight();

         for (int x = xMin; x < xMax; x++) {
            for (int z = zMin; z < zMax; z++) {
               mutableBlockPos.setX(x);
               mutableBlockPos.setZ(z);
               long dataPoint = this.fullDataSource.getDataPointAtBlockPos(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ(), levelMinY);
               if (dataPoint != 0L) {
                  int id = FullDataPointUtil.getId(dataPoint);
                  BiomeWrapper_neoforge biomeWrapper = (BiomeWrapper_neoforge)this.fullDataSource.mapping.getBiomeWrapper(id);
                  int color = this.tryGetClientBiomeColor(colorResolver, biomeWrapper);
                  if (color == -1) {
                     return -1;
                  }

                  rollingRed += ColorUtil.getRed(color);
                  rollingGreen += ColorUtil.getGreen(color);
                  rollingBlue += ColorUtil.getBlue(color);
                  dataPointCount++;
               }
            }
         }

         return dataPointCount == 0
            ? this.tryGetClientBiomeColor(colorResolver, this.biomeWrapper)
            : ColorUtil.argbToInt(255, rollingRed / dataPointCount, rollingGreen / dataPointCount, rollingBlue / dataPointCount);
      } else {
         return this.tryGetClientBiomeColor(colorResolver, this.biomeWrapper);
      }
   }

   private int tryGetClientBiomeColor(@Nullable ColorResolver colorResolver, BiomeWrapper_neoforge biomeWrapper) {
      BlockBiomeWrapperPair pair = BlockBiomeWrapperPair.get(this.blockStateWrapper, biomeWrapper);
      Integer cachedColor = COLOR_BY_BLOCK_BIOME_PAIR.get(pair);
      if (cachedColor != null) {
         return cachedColor;
      } else if (colorResolver == null) {
         return -1;
      } else {
         int color = colorResolver.getColor(unwrapClientBiome(biomeWrapper), 0.0, 0.0);
         COLOR_BY_BLOCK_BIOME_PAIR.put(pair, color);
         return color;
      }
   }

   protected static Biome unwrapClientBiome(BiomeWrapper_neoforge biomeWrapper) {
      String biomeString = biomeWrapper.getSerialString();
      if (biomeString == null || biomeString.isEmpty() || biomeString.equals("EMPTY")) {
         biomeString = "minecraft:plains";
      }

      return unwrapBiome(getClientBiome(biomeString));
   }

   protected static Biome unwrapBiome(Holder<Biome> biome) {
      return (Biome)biome.value();
   }

   private static Holder<Biome> getClientBiome(String biomeResourceString) {
      Holder<Biome> biome = BIOME_BY_RESOURCE_STRING.get(biomeResourceString);
      return biome != null ? biome : BIOME_BY_RESOURCE_STRING.compute(biomeResourceString, (resourceString, existingBiome) -> {
         if (existingBiome != null) {
            return existingBiome;
         } else {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel == null) {
               throw new IllegalStateException("Attempted to get client biome when no client level was loaded.");
            } else {
               BiomeWrapper$BiomeDeserializeResult_neoforge result;
               try {
                  result = BiomeWrapper_neoforge.deserializeBiome(resourceString, clientLevel.registryAccess());
               } catch (Exception var7) {
                  LOGGER.warn("Unable to deserialize client biome [" + resourceString + "], using fallback...");

                  try {
                     result = BiomeWrapper_neoforge.deserializeBiome("minecraft:plains", clientLevel.registryAccess());
                  } catch (IOException var6) {
                     LOGGER.error("Unable to deserialize fallback client biome [minecraft:plains], returning NULL.");
                     return null;
                  }
               }

               if (result.success) {
                  existingBiome = result.biome;
               }

               return existingBiome;
            }
         }
      });
   }

   public static void setStaticColor(BlockStateWrapper_neoforge blockStateWrapper, BiomeWrapper_neoforge biomeWrapper, Integer colorInt) {
      BlockBiomeWrapperPair pair = BlockBiomeWrapperPair.get(blockStateWrapper, biomeWrapper);
      COLOR_BY_BLOCK_BIOME_PAIR.put(pair, colorInt);
   }

   public static void clear() {
      COLOR_BY_BLOCK_BIOME_PAIR.clear();
   }
}
