package net.cibernet.alchemancy.util;

import java.util.ArrayList;
import net.cibernet.alchemancy.data.save.AlchemancyLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent.Post;

@EventBusSubscriber
public class RedstoneSources {
   @SubscribeEvent
   public static void onLevelTick(Post event) {
      if (event.getLevel() instanceof ServerLevel serverLevel) {
         tick(serverLevel);
      }
   }

   public static void createSourceAt(ServerLevel level, BlockPos pos, int power, int ticks, Direction direction) {
      if (!level.getBlockState(pos).is(BlockTags.AIR)) {
         AlchemancyLevelData data = AlchemancyLevelData.compute(level);
         data.getRedstoneSources().put(pos, new RedstoneSources.RedstoneSource(power, ticks, direction));
         data.setDirty();
         updateBlock(level, pos);
      }
   }

   public static int getSourcePower(ServerLevel level, BlockPos pos) {
      return AlchemancyLevelData.compute(level).getRedstoneSources().getOrDefault(pos, RedstoneSources.RedstoneSource.DEFAULT).power;
   }

   public static RedstoneSources.RedstoneSource getSourceAt(ServerLevel level, BlockPos pos) {
      return AlchemancyLevelData.compute(level).getRedstoneSources().getOrDefault(pos, RedstoneSources.RedstoneSource.DEFAULT);
   }

   public static void tick(ServerLevel level) {
      ArrayList<BlockPos> updates = new ArrayList<>();
      AlchemancyLevelData data = AlchemancyLevelData.compute(level);
      data.getRedstoneSources().entrySet().removeIf(entry -> {
         RedstoneSources.RedstoneSource var10000 = entry.getValue();
         int var3 = var10000.ticks;
         var10000.ticks -= 1;
         if (var3 < 0) {
            entry.getValue().power = 0;
            updates.add(entry.getKey());
            data.setDirty();
            return true;
         } else {
            return false;
         }
      });
      if (!updates.isEmpty()) {
         updates.forEach(pos -> updateBlock(level, pos));
      }
   }

   private static void updateBlock(ServerLevel level, BlockPos pos) {
      level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
      level.neighborChanged(pos, Blocks.AIR, pos.above());
   }

   public static class RedstoneSource {
      public static final RedstoneSources.RedstoneSource DEFAULT = new RedstoneSources.RedstoneSource(0, 0, Direction.UP);
      public int power;
      public int ticks;
      public final Direction direction;

      public RedstoneSource(int power, int ticks, Direction direction) {
         this.power = power;
         this.ticks = ticks;
         this.direction = direction;
      }

      public CompoundTag write() {
         CompoundTag tag = new CompoundTag();
         tag.putInt("power", this.power);
         tag.putInt("ticks", this.ticks);
         tag.putString("direction", this.direction.getName());
         return tag;
      }

      public static RedstoneSources.RedstoneSource read(CompoundTag tag) {
         return new RedstoneSources.RedstoneSource(tag.getInt("power"), tag.getInt("ticks"), Direction.byName(tag.getString("direction")));
      }
   }
}
