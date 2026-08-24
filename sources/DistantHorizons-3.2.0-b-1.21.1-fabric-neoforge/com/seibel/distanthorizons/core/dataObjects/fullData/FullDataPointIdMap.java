package com.seibel.distanthorizons.core.dataObjects.fullData;

import com.seibel.distanthorizons.core.dataObjects.BlockBiomeWrapperPair;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.objects.DataCorruptedException;
import com.seibel.distanthorizons.core.util.objects.dataStreams.DhDataInputStream;
import com.seibel.distanthorizons.core.util.objects.dataStreams.DhDataOutputStream;
import com.seibel.distanthorizons.core.util.objects.pooling.StringPool;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public class FullDataPointIdMap {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IWrapperFactory WRAPPER_FACTORY = SingletonInjector.INSTANCE.get(IWrapperFactory.class);
   public static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("IdMap");
   private static final boolean RUN_SERIALIZATION_DUPLICATE_VALIDATION = false;
   public static final String BLOCK_STATE_SEPARATOR_STRING = "_DH-BSW_";
   private long pos;
   private final ArrayList<BlockBiomeWrapperPair> blockBiomePairList = new ArrayList<>();
   private final ConcurrentHashMap<BlockBiomeWrapperPair, Integer> idMap = new ConcurrentHashMap<>();
   private int cachedHashCode = 0;

   public FullDataPointIdMap(long pos) {
      this.pos = pos;
   }

   public IBiomeWrapper getBiomeWrapper(int id) throws IndexOutOfBoundsException {
      return this.getEntry(id).biome;
   }

   public IBlockStateWrapper getBlockStateWrapper(int id) throws IndexOutOfBoundsException {
      return this.getEntry(id).blockState;
   }

   private BlockBiomeWrapperPair getEntry(int id) throws IndexOutOfBoundsException {
      try {
         return this.blockBiomePairList.get(id);
      } catch (IndexOutOfBoundsException var4) {
         throw new IndexOutOfBoundsException(
            "FullData ID Map out of sync for pos: "
               + DhSectionPos.toString(this.pos)
               + ". ID: ["
               + id
               + "] greater than the number of known ID's: ["
               + this.blockBiomePairList.size()
               + "]."
         );
      }
   }

   public int getMaxValidId() {
      return this.blockBiomePairList.size() - 1;
   }

   public int size() {
      return this.blockBiomePairList.size();
   }

   public boolean isEmpty() {
      return this.blockBiomePairList.isEmpty();
   }

   public long getPos() {
      return this.pos;
   }

   public int addIfNotPresentAndGetId(IBiomeWrapper biome, IBlockStateWrapper blockState) {
      return this.addIfNotPresentAndGetId(BlockBiomeWrapperPair.get(blockState, biome));
   }

   private int addIfNotPresentAndGetId(BlockBiomeWrapperPair pair) {
      Integer nullableId = this.idMap.get(pair);
      return nullableId != null ? nullableId : this.idMap.compute(pair, (newPair, currentId) -> {
         if (currentId != null) {
            return (Integer)currentId;
         } else {
            currentId = this.blockBiomePairList.size();
            this.blockBiomePairList.add(newPair);
            this.cachedHashCode = 0;
            return currentId;
         }
      });
   }

   public void addAll(FullDataPointIdMap inputMap) {
      ArrayList<BlockBiomeWrapperPair> pairsToMerge = inputMap.blockBiomePairList;

      for (int i = 0; i < pairsToMerge.size(); i++) {
         BlockBiomeWrapperPair pair = pairsToMerge.get(i);
         this.add(pair);
      }
   }

   private void add(BlockBiomeWrapperPair pair) {
      int id = this.blockBiomePairList.size();
      this.blockBiomePairList.add(pair);
      this.idMap.put(pair, id);
      this.cachedHashCode = 0;
   }

   public int[] mergeAndReturnRemappedEntityIds(FullDataPointIdMap inputMap) {
      ArrayList<BlockBiomeWrapperPair> entriesToMerge = inputMap.blockBiomePairList;
      int[] remappedPairIds = new int[entriesToMerge.size()];

      for (int i = 0; i < entriesToMerge.size(); i++) {
         BlockBiomeWrapperPair entity = entriesToMerge.get(i);
         int id = this.addIfNotPresentAndGetId(entity);
         remappedPairIds[i] = id;
      }

      return remappedPairIds;
   }

   public void clear(long pos) {
      this.pos = pos;
      this.blockBiomePairList.clear();
      this.idMap.clear();
      this.cachedHashCode = 0;
   }

   public void serialize(DhDataOutputStream outputStream) throws IOException {
      outputStream.writeInt(this.blockBiomePairList.size());
      new HashMap();

      for (BlockBiomeWrapperPair pair : this.blockBiomePairList) {
         String entryString = pair.serialize();
         outputStream.writeUTF(entryString);
      }
   }

   public static void deserialize(@NotNull FullDataPointIdMap map, DhDataInputStream inputStream, long pos, ILevelWrapper levelWrapper) throws IOException, InterruptedException, DataCorruptedException {
      int entityCount = inputStream.readInt();
      if (entityCount < 0) {
         throw new DataCorruptedException(
            "FullDataPointIdMap deserialize entry count should have a number greater than or equal to 0, returned value [" + entityCount + "]."
         );
      } else {
         map.clear(pos);
         new HashMap();
         PhantomArrayListCheckout checkout = ARRAY_LIST_POOL.checkoutCharArrays(3);

         try {
            for (int i = 0; i < entityCount; i++) {
               if (Thread.interrupted()) {
                  throw new InterruptedException("[" + FullDataPointIdMap.class.getSimpleName() + "] deserializing interrupted.");
               }

               int length = inputStream.readUnsignedShort();
               CharArrayList fullCharList = checkout.getCharArray(0, length);
               CharArrayList biomeCharList = checkout.getCharArray(1, length);
               CharArrayList blockCharList = checkout.getCharArray(2, length);

               for (int stringIndex = 0; stringIndex < length; stringIndex++) {
                  byte b = inputStream.readByte();
                  char c = (char)(b & 255);
                  fullCharList.set(stringIndex, c);
               }

               splitCharArray(fullCharList, biomeCharList, blockCharList);
               String biomeString = StringPool.INSTANCE.getPooledString(biomeCharList);
               IBiomeWrapper biome = WRAPPER_FACTORY.deserializeBiomeWrapperOrGetDefault(biomeString, levelWrapper);
               String blockStateString = StringPool.INSTANCE.getPooledString(blockCharList);
               IBlockStateWrapper blockState = WRAPPER_FACTORY.deserializeBlockStateWrapperOrGetDefault(blockStateString, levelWrapper);
               BlockBiomeWrapperPair newPair = BlockBiomeWrapperPair.get(blockState, biome);
               map.blockBiomePairList.add(newPair);
            }
         } catch (Throwable var19) {
            if (checkout != null) {
               try {
                  checkout.close();
               } catch (Throwable var18) {
                  var19.addSuppressed(var18);
               }
            }

            throw var19;
         }

         if (checkout != null) {
            checkout.close();
         }

         if (map.size() != entityCount) {
            LodUtil.assertNotReach(
               "ID maps failed to deserialize for pos: ["
                  + DhSectionPos.toString(pos)
                  + "], incorrect entity count. Expected count ["
                  + entityCount
                  + "], actual count ["
                  + map.size()
                  + "]"
            );
         }
      }
   }

   private static void splitCharArray(CharArrayList input, CharArrayList biomeString, CharArrayList blockString) throws DataCorruptedException {
      boolean separatorFound = false;
      int foundStartIndex = -1;
      int separatorIndex = 0;

      for (int inputIndex = 0; inputIndex < input.size(); inputIndex++) {
         char ch = input.getChar(inputIndex);
         if (ch == "_DH-BSW_".charAt(separatorIndex)) {
            if (!separatorFound) {
               foundStartIndex = inputIndex;
            }

            separatorFound = true;
            separatorIndex++;
         } else {
            separatorFound = false;
            foundStartIndex = -1;
            separatorIndex = 0;
         }

         if (separatorFound && separatorIndex == "_DH-BSW_".length()) {
            break;
         }
      }

      if (foundStartIndex == -1) {
         throw new DataCorruptedException("Failed to deserialize BiomeBlockStateEntry [" + input.toString() + "], unable to find separator.");
      } else {
         biomeString.clear();

         for (int i = 0; i < foundStartIndex; i++) {
            biomeString.push(input.getChar(i));
         }

         blockString.clear();

         for (int i = foundStartIndex + "_DH-BSW_".length(); i < input.size(); i++) {
            blockString.push(input.getChar(i));
         }
      }
   }

   @Override
   public String toString() {
      return DhSectionPos.toString(this.pos) + " size: " + this.blockBiomePairList.size();
   }

   @Override
   public boolean equals(Object other) {
      return other == this;
   }

   @Override
   public int hashCode() {
      if (this.cachedHashCode == 0) {
         this.generateHashCode();
      }

      return this.cachedHashCode;
   }

   private void generateHashCode() {
      int result = DhSectionPos.hashCode(this.pos);

      for (int i = 0; i < this.blockBiomePairList.size(); i++) {
         result = 31 * result + this.blockBiomePairList.hashCode();
      }

      this.cachedHashCode = result;
   }
}
