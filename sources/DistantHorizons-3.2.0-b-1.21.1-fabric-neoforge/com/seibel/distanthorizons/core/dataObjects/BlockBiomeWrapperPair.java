package com.seibel.distanthorizons.core.dataObjects;

import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import java.util.concurrent.ConcurrentHashMap;

public class BlockBiomeWrapperPair {
   private static final IWrapperFactory WRAPPER_FACTORY = SingletonInjector.INSTANCE.get(IWrapperFactory.class);
   private static final ConcurrentHashMap<IBlockStateWrapper, ConcurrentHashMap<IBiomeWrapper, BlockBiomeWrapperPair>> CACHED_PAIR_BY_BIOME_BY_BLOCK = new ConcurrentHashMap<>();
   public final IBiomeWrapper biome;
   public final IBlockStateWrapper blockState;
   private int hashCode = 0;
   private boolean hashGenerated = false;
   private String serialString = null;

   public static BlockBiomeWrapperPair get(IBlockStateWrapper blockState, IBiomeWrapper biome) {
      ConcurrentHashMap<IBiomeWrapper, BlockBiomeWrapperPair> pairByBiomeWrapper = CACHED_PAIR_BY_BIOME_BY_BLOCK.get(blockState);
      if (pairByBiomeWrapper != null) {
         BlockBiomeWrapperPair pair = pairByBiomeWrapper.get(biome);
         if (pair != null) {
            return pair;
         }
      }

      return CACHED_PAIR_BY_BIOME_BY_BLOCK.computeIfAbsent(blockState, newBlockState -> new ConcurrentHashMap<>())
         .computeIfAbsent(biome, newBiome -> new BlockBiomeWrapperPair(biome, blockState));
   }

   private BlockBiomeWrapperPair(IBiomeWrapper biome, IBlockStateWrapper blockState) {
      this.biome = biome;
      this.blockState = blockState;
   }

   @Override
   public int hashCode() {
      if (!this.hashGenerated) {
         this.hashCode = generateHashCode(this);
         this.hashGenerated = true;
      }

      return this.hashCode;
   }

   private static int generateHashCode(BlockBiomeWrapperPair pair) {
      return generateHashCode(pair.biome, pair.blockState);
   }

   private static int generateHashCode(IBiomeWrapper biome, IBlockStateWrapper blockState) {
      int prime = 31;
      int result = 1;
      result = 31 * result + (biome == null ? 0 : biome.hashCode());
      return 31 * result + (blockState == null ? 0 : blockState.hashCode());
   }

   @Override
   public boolean equals(Object otherObj) {
      if (otherObj == this) {
         return true;
      } else if (!(otherObj instanceof BlockBiomeWrapperPair)) {
         return false;
      } else {
         BlockBiomeWrapperPair other = (BlockBiomeWrapperPair)otherObj;
         return other.biome.getSerialString().equals(this.biome.getSerialString())
            && other.blockState.getSerialString().equals(this.blockState.getSerialString());
      }
   }

   @Override
   public String toString() {
      return this.serialize();
   }

   public String serialize() {
      if (this.serialString == null) {
         this.serialString = this.biome.getSerialString() + "_DH-BSW_" + this.blockState.getSerialString();
      }

      return this.serialString;
   }
}
