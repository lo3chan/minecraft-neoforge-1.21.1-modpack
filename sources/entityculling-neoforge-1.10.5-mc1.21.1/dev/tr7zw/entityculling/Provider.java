package dev.tr7zw.entityculling;

import com.logisticscraft.occlusionculling.DataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class Provider implements DataProvider {
   private final Minecraft client = Minecraft.getInstance();
   private ClientLevel world = null;

   @Override
   public boolean prepareChunk(int chunkX, int chunkZ) {
      this.world = this.client.level;
      return this.world != null;
   }

   @Override
   public boolean isOpaqueFullCube(int x, int y, int z) {
      BlockPos pos = new BlockPos(x, y, z);
      BlockState state = this.world.getBlockState(pos);
      return EntityCullingModBase.instance.config.solidLeaves && state.getBlock() instanceof LeavesBlock
         ? true
         : state.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
   }

   @Override
   public void cleanup() {
      this.world = null;
   }
}
