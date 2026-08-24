package at.petrak.hexcasting.common.blocks.akashic;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class BlockAkashicRecord extends Block {
   public BlockAkashicRecord(Properties p_49795_) {
      super(p_49795_);
   }

   @Nullable
   public BlockPos addNewDatum(BlockPos herePos, Level level, HexPattern key, Iota datum) {
      BlockPos clobbereePos = AkashicFloodfiller.floodFillFor(
         herePos,
         level,
         (pos, bs, world) -> world.getBlockEntity(pos) instanceof BlockEntityAkashicBookshelf tilex
            && tilex.getPattern() != null
            && tilex.getPattern().sigsEqual(key)
      );
      if (clobbereePos != null) {
         return null;
      } else {
         BlockPos openPos = AkashicFloodfiller.floodFillFor(
            herePos, level, 0.9F, (pos, bs, world) -> world.getBlockEntity(pos) instanceof BlockEntityAkashicBookshelf tilex && tilex.getPattern() == null, 128
         );
         if (openPos != null) {
            BlockEntityAkashicBookshelf tile = (BlockEntityAkashicBookshelf)level.getBlockEntity(openPos);
            tile.setNewMapping(key, datum);
            return openPos;
         } else {
            return null;
         }
      }
   }

   @Nullable
   public Iota lookupPattern(BlockPos herePos, HexPattern key, ServerLevel slevel) {
      BlockPos foundPos = AkashicFloodfiller.floodFillFor(
         herePos,
         slevel,
         (pos, bs, world) -> world.getBlockEntity(pos) instanceof BlockEntityAkashicBookshelf tilex
            && tilex.getPattern() != null
            && tilex.getPattern().sigsEqual(key)
      );
      if (foundPos == null) {
         return null;
      } else {
         BlockEntityAkashicBookshelf tile = (BlockEntityAkashicBookshelf)slevel.getBlockEntity(foundPos);
         CompoundTag tag = tile.getIotaTag();
         return tag == null ? null : IotaType.deserialize(tag, slevel);
      }
   }
}
