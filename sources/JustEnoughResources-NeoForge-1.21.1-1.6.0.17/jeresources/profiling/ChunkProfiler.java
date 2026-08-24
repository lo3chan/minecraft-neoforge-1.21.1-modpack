package jeresources.profiling;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import jeresources.platform.Services;
import jeresources.util.MapKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ChunkProfiler implements Runnable {
   private final ServerLevel level;
   private final ResourceKey<Level> dimensionKey;
   private final ProfilingTimer timer;
   private final ProfilingBlacklist blacklist;
   private final List<ChunkAccess> chunks;
   @NotNull
   private final ProfiledDimensionData dimensionData;
   public static final int CHUNK_SIZE = 16;
   public static final int CHUNK_HEIGHT = 256;

   public ChunkProfiler(
      ServerLevel level,
      ResourceKey<Level> dimensionKey,
      List<ChunkAccess> chunks,
      @NotNull ProfiledDimensionData dimensionData,
      ProfilingTimer timer,
      ProfilingBlacklist blacklist
   ) {
      this.level = level;
      this.dimensionKey = dimensionKey;
      this.chunks = chunks;
      this.dimensionData = dimensionData;
      this.timer = timer;
      this.blacklist = blacklist;
   }

   @Override
   public void run() {
      this.chunks.forEach(this::profileChunk);
   }

   private void profileChunk(ChunkAccess chunk) {
      ResourceKey<Level> worldRegistryKey = this.level.dimension();
      this.timer.startChunk(worldRegistryKey);
      Map<String, Integer[]> temp = new HashMap<>();
      MutableBlockPos blockPos = new MutableBlockPos();
      new BlockHitResult(new Vec3(0.0, 0.0, 0.0), Direction.DOWN, blockPos, true);
      Player player = Minecraft.getInstance().player;
      int maxY = chunk.getHighestSectionPosition();

      for (int y = 0; y < maxY; y++) {
         for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
               blockPos.set(x + chunk.getPos().x * 16, y, z + chunk.getPos().z * 16);
               BlockState blockState = chunk.getBlockState(new BlockPos(x, y, z));
               if (!this.blacklist.contains(blockState)) {
                  String key = MapKeys.getKey(blockState, this.level, blockPos);
                  if (!this.dimensionData.dropsMap.containsKey(key)) {
                     this.dimensionData.dropsMap.put(key, getDrops(this.level, blockPos, blockState));
                  }

                  if (!this.dimensionData.silkTouchMap.containsKey(key)) {
                     Block block = blockState.getBlock();
                     boolean canSilkTouch = Services.PLATFORM.isCorrectToolForBlock(block, blockState, this.level, blockPos, player);
                     this.dimensionData.silkTouchMap.put(key, canSilkTouch);
                  }

                  Integer[] array = temp.get(key);
                  if (array == null) {
                     array = new Integer[256];
                     Arrays.fill(array, Integer.valueOf(0));
                  }

                  Integer var16 = array[y];
                  array[y] = array[y] + 1;
                  temp.put(key, array);
               }
            }
         }
      }

      for (Entry<String, Integer[]> entry : temp.entrySet()) {
         Integer[] array = this.dimensionData.distributionMap.get(entry.getKey());
         if (array == null) {
            array = new Integer[256];
            Arrays.fill(array, Integer.valueOf(0));
         }

         for (int i = 0; i < 256; i++) {
            array[i] = array[i] + entry.getValue()[i];
         }

         this.dimensionData.distributionMap.put(entry.getKey(), array);
      }

      this.timer.endChunk(this.dimensionKey);
   }

   public static Map<String, Map<Integer, Float>> getDrops(ServerLevel level, BlockPos pos, BlockState state) {
      int totalTries = 10000;
      Map<String, Map<Integer, Float>> resultMap = new HashMap<>();

      for (int fortune = 0; fortune <= 3; fortune++) {
         Map<String, Integer> dropsMap = new HashMap<>();

         for (int i = 0; i < 10000; i++) {
            NonNullList<ItemStack> drops = NonNullList.create();
            Block.getDrops(state, level, pos, null);

            for (ItemStack drop : drops) {
               if (drop != null) {
                  String key = MapKeys.getKey(drop);
                  Integer count = dropsMap.get(key);
                  if (count != null) {
                     count = count + drop.getCount();
                  } else {
                     count = drop.getCount();
                  }

                  dropsMap.put(key, count);
               }
            }
         }

         for (Entry<String, Integer> dropEntry : dropsMap.entrySet()) {
            Map<Integer, Float> fortuneMap = resultMap.get(dropEntry.getKey());
            if (fortuneMap == null) {
               fortuneMap = new HashMap<>();
            }

            fortuneMap.put(fortune, dropEntry.getValue().intValue() / 10000.0F);
            resultMap.put(dropEntry.getKey(), fortuneMap);
         }
      }

      return resultMap;
   }
}
