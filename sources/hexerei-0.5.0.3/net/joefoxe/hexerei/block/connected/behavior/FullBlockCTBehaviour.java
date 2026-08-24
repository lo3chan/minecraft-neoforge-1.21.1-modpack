package net.joefoxe.hexerei.block.connected.behavior;

import net.joefoxe.hexerei.block.connected.BlockConnectivity;
import net.joefoxe.hexerei.block.connected.CTSpriteShiftEntry;
import net.joefoxe.hexerei.block.connected.ConnectedTextureBehaviour;
import net.joefoxe.hexerei.util.ClientProxy;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FullBlockCTBehaviour extends ConnectedTextureBehaviour.Base {
   private CTSpriteShiftEntry shift;

   public FullBlockCTBehaviour(CTSpriteShiftEntry shift) {
      this.shift = shift;
   }

   @Override
   public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
      if (this.isBeingBlocked(state, reader, pos, otherPos, face)) {
         return false;
      } else {
         BlockConnectivity cc = ClientProxy.BLOCK_CONNECTIVITY;
         BlockConnectivity.Entry entry = cc.get(state);
         BlockConnectivity.Entry otherEntry = cc.get(other);
         if (entry == null || otherEntry == null) {
            return false;
         } else {
            return entry.isSideValid(state, face) && otherEntry.isSideValid(other, face)
               ? entry.getCTSpriteShiftEntry() == otherEntry.getCTSpriteShiftEntry()
               : false;
         }
      }
   }

   @Override
   public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
      return this.shift;
   }
}
