package at.petrak.hexcasting.common.blocks.akashic;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.client.render.HexPatternPoints;
import at.petrak.hexcasting.common.lib.HexBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityAkashicBookshelf extends HexBlockEntity {
   public static final String TAG_PATTERN = "pattern";
   public static final String TAG_IOTA = "iota";
   public static final String TAG_DUMMY = "dummy";
   private HexPattern pattern = null;
   private CompoundTag iotaTag = null;
   public HexPatternPoints points;

   public BlockEntityAkashicBookshelf(BlockPos pWorldPosition, BlockState pBlockState) {
      super(HexBlockEntities.AKASHIC_BOOKSHELF_TILE, pWorldPosition, pBlockState);
   }

   @Nullable
   public HexPattern getPattern() {
      return this.pattern;
   }

   @Nullable
   public CompoundTag getIotaTag() {
      return this.iotaTag;
   }

   public void setNewMapping(HexPattern pattern, Iota iota) {
      boolean previouslyEmpty = this.pattern == null;
      this.pattern = pattern;
      this.iotaTag = IotaType.serialize(iota);
      if (previouslyEmpty) {
         BlockState oldBs = this.getBlockState();
         BlockState newBs = (BlockState)oldBs.setValue(BlockAkashicBookshelf.HAS_BOOKS, true);
         this.level.setBlock(this.getBlockPos(), newBs, 3);
         this.level.sendBlockUpdated(this.getBlockPos(), oldBs, newBs, 3);
      } else {
         this.setChanged();
      }
   }

   public void clearIota() {
      boolean previouslyEmpty = this.pattern == null;
      this.pattern = null;
      this.iotaTag = null;
      if (!previouslyEmpty) {
         BlockState oldBs = this.getBlockState();
         BlockState newBs = (BlockState)oldBs.setValue(BlockAkashicBookshelf.HAS_BOOKS, false);
         this.level.setBlock(this.getBlockPos(), newBs, 3);
         this.level.sendBlockUpdated(this.getBlockPos(), oldBs, newBs, 3);
      } else {
         this.setChanged();
      }
   }

   @Override
   protected void saveModData(CompoundTag compoundTag) {
      if (this.pattern != null && this.iotaTag != null) {
         compoundTag.put("pattern", this.pattern.serializeToNBT());
         compoundTag.put("iota", this.iotaTag);
      } else {
         compoundTag.putBoolean("dummy", false);
      }
   }

   @Override
   protected void loadModData(CompoundTag tag) {
      if (tag.contains("pattern") && tag.contains("iota")) {
         this.pattern = HexPattern.fromNBT(tag.getCompound("pattern"));
         this.iotaTag = tag.getCompound("iota");
      } else if (tag.contains("dummy")) {
         this.pattern = null;
         this.iotaTag = null;
      }
   }
}
