package net.mehvahdjukaar.amendments.common.tile;

import net.mehvahdjukaar.amendments.common.block.HangingFlowerPotBlock;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.block.MimicBlockTile;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class HangingFlowerPotBlockTile extends MimicBlockTile {
   public HangingFlowerPotBlockTile(BlockPos pos, BlockState state) {
      super(ModRegistry.HANGING_FLOWER_POT_TILE.get(), pos, state);
   }

   public boolean setHeldBlock(BlockState state) {
      super.setHeldBlock(state);
      if (this.level instanceof ServerLevel) {
         int newLight = ForgeHelper.getLightEmission(this.getHeldBlock(), this.level, this.worldPosition);
         this.level.setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue(HangingFlowerPotBlock.LIGHT_LEVEL, newLight), 3);
      }

      return true;
   }

   public AABB getRenderBoundingBox() {
      return new AABB(this.worldPosition);
   }
}
