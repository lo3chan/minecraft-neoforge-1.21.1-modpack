package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateDoor;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TileEntityEndPirateDoor extends BlockEntity {
   private float openProgress;
   private float prevOpenProgress;
   private float wiggleProgress;
   private float prevWiggleProgress;
   public int wiggleTime;
   public int ticksExisted;

   public TileEntityEndPirateDoor(BlockPos pos, BlockState state) {
      super(AMTileEntityRegistry.END_PIRATE_DOOR.get(), pos, state);
      if (state.getBlock() instanceof BlockEndPirateDoor && (Boolean)state.getValue(BlockEndPirateDoor.OPEN)) {
         this.openProgress = 1.0F;
         this.prevOpenProgress = 1.0F;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public AABB getRenderBoundingBox() {
      return AMPlatform.encapsulating(this.worldPosition, this.worldPosition.offset(1, 3, 1));
   }

   public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityEndPirateDoor entity) {
      entity.tick();
   }

   public void tick() {
      this.prevOpenProgress = this.openProgress;
      this.prevWiggleProgress = this.wiggleProgress;
      boolean opened = false;
      if (this.getBlockState().getBlock() instanceof BlockEndPirateDoor) {
         opened = (Boolean)this.getBlockState().getValue(BlockEndPirateDoor.OPEN);
      }

      if (opened && this.openProgress == 0.0F || !opened && this.openProgress == 1.0F) {
         this.level.gameEvent(GameEvent.BLOCK_ACTIVATE, this.getBlockPos(), Context.of(this.getBlockState()));
         this.level.playSound((Player)null, this.getBlockPos(), AMSoundRegistry.END_PIRATE_DOOR.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      if (opened && this.openProgress < 1.0F) {
         this.openProgress += 0.25F;
      }

      if (!opened && this.openProgress > 0.0F) {
         this.openProgress -= 0.25F;
      }

      if (this.openProgress >= 1.0F && this.prevOpenProgress < 1.0F || this.openProgress <= 0.0F && this.prevOpenProgress > 0.0F) {
         this.wiggleTime = 5;
      }

      if (this.wiggleTime > 0) {
         this.wiggleTime--;
         if (this.wiggleProgress < 1.0F) {
            this.wiggleProgress += 0.25F;
         }
      } else if (this.wiggleProgress > 0.0F) {
         this.wiggleProgress -= 0.25F;
      }

      this.ticksExisted++;
   }

   public float getOpenProgress(float partialTick) {
      return this.prevOpenProgress + (this.openProgress - this.prevOpenProgress) * partialTick;
   }

   public float getWiggleProgress(float partialTick) {
      return this.prevWiggleProgress + (this.wiggleProgress - this.prevWiggleProgress) * partialTick;
   }
}
