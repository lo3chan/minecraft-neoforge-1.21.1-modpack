package net.mehvahdjukaar.amendments.common.tile;

import net.mehvahdjukaar.amendments.common.ISwingingTile;
import net.mehvahdjukaar.amendments.common.PendulumAnimation;
import net.mehvahdjukaar.amendments.common.SwingAnimation;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.moonlight.api.block.DynamicRenderedBlockTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public abstract class SwayingBlockTile extends DynamicRenderedBlockTile implements ISwingingTile {
   private SwingAnimation animation;

   protected SwayingBlockTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
      super(tileEntityTypeIn, pos, state);
   }

   public void onFancyChanged(boolean newFancy) {
      if (!newFancy) {
         this.amendments$getAnimation().reset();
      }
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, SwayingBlockTile tile) {
      if (tile.rendersFancy()) {
         tile.amendments$getAnimation().tick(pLevel, pPos, pState);
      }
   }

   public abstract Vector3f getRotationAxis(BlockState var1);

   @Override
   public SwingAnimation amendments$getAnimation() {
      if (this.isNeverFancy()) {
         return SwingAnimation.EMPTY;
      } else {
         if (this.animation == null) {
            this.animation = new PendulumAnimation(ClientConfigs.WALL_LANTERN_CONFIG, this::getRotationAxis);
         }

         return this.animation;
      }
   }
}
