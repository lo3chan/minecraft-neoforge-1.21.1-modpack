package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchor;
import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchorWinch;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TileEntityEndPirateAnchorWinch extends BlockEntity {
   public float clientRoll;
   public int windCounter = 0;
   private int prevTargetChainLength;
   private int targetChainLength = 0;
   private float prevMaximumChainLength;
   private float chainLength;
   private float prevChainLength;
   private int windTime = 0;
   private int ticksExisted = 0;
   private float windProgress;
   private float prevWindProgress;
   private boolean draggingAnchor;
   private boolean anchorEW;
   private boolean pullingUp;
   private boolean hasPower;
   private int anchorPlaceCooldown = 0;

   public TileEntityEndPirateAnchorWinch(BlockPos pos, BlockState state) {
      super(AMTileEntityRegistry.END_PIRATE_ANCHOR_WINCH.get(), pos, state);
      this.prevTargetChainLength = this.targetChainLength;
   }

   public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityEndPirateAnchorWinch entity) {
      entity.tick();
   }

   private int calcChainLength(boolean goBelowAnchor) {
      BlockPos down = this.getBlockPos().below();

      while (
         this.level != null
            && down.getY() > AMCompat.minBuildHeight(this.level)
            && !this.isAnchorTop(this.level, down)
            && (this.isEmptyBlock(down) || this.isAnchorChain(this.level, down))
      ) {
         down = down.below();
      }

      int i = 0;
      if (this.isAnchorTop(this.level, down) || goBelowAnchor) {
         if (goBelowAnchor) {
            i = this.getBlockPos().getY() - 1 - this.keepMovingBelowAnchor(down.below(2));
         } else {
            i = this.getBlockPos().getY() - 1 - down.getY();
         }
      }

      return this.draggingAnchor ? i - 3 : i;
   }

   private int keepMovingBelowAnchor(BlockPos below) {
      while (below.getY() > AMCompat.minBuildHeight(this.level) && this.isEmptyBlock(below)) {
         below = below.below();
      }

      return below.getY();
   }

   private boolean isEmptyBlock(BlockPos pos) {
      return this.level.isEmptyBlock(pos) || this.isAnchorChain(this.level, pos) || this.level.getBlockState(pos).canBeReplaced();
   }

   private boolean isAnchorChain(Level level, BlockPos pos) {
      return level.getBlockState(pos).getBlock() instanceof BlockEndPirateAnchor
         && level.getBlockState(pos).getValue(BlockEndPirateAnchor.PIECE) == BlockEndPirateAnchor.PieceType.CHAIN;
   }

   private boolean isAnchorTop(Level level, BlockPos pos) {
      return level.getBlockState(pos).getBlock() instanceof BlockEndPirateAnchor
         && level.getBlockState(pos.below(2)).getBlock() instanceof BlockEndPirateAnchor
         && level.getBlockState(pos.below(2)).getValue(BlockEndPirateAnchor.PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR;
   }

   private void tick() {
      this.prevChainLength = this.chainLength;
      this.prevWindProgress = this.windProgress;
      this.prevTargetChainLength = this.targetChainLength;
      this.ticksExisted++;
      boolean powered = false;
      if (this.getBlockState().getBlock() instanceof BlockEndPirateAnchorWinch) {
         powered = (Boolean)this.getBlockState().getValue(BlockEndPirateAnchorWinch.POWERED);
      }

      if (powered && this.pullingUp) {
         this.sendDownChains();
      }

      if (!powered && !this.pullingUp) {
         this.pullUpChains();
      }

      if (this.chainLength < this.targetChainLength) {
         this.chainLength = Math.min(this.chainLength + 0.1F, (float)this.targetChainLength);
      }

      if (this.chainLength > this.targetChainLength) {
         this.chainLength = Math.max(this.chainLength - 0.1F, (float)this.targetChainLength);
      }

      if (Math.abs(this.targetChainLength - this.chainLength) > 0.2F) {
         this.windTime = 5;
      }

      if (this.windTime > 0) {
         this.windCounter++;
         this.windTime--;
         if (this.windProgress < 1.0F) {
            this.windProgress += 0.25F;
         }
      } else {
         this.windCounter = 0;
         if (this.windProgress > 0.0F) {
            this.windProgress -= 0.25F;
         }
      }

      if (this.anchorPlaceCooldown > 0) {
         this.anchorPlaceCooldown--;
      }

      if (this.chainLength != this.targetChainLength && this.isWindingUp() && !this.draggingAnchor) {
         BlockPos down = this.getBlockPos();
         if (this.anchorPlaceCooldown == 0
            && (this.checkAndBreakAnchor(down.below()) || this.checkAndBreakAnchor(down.below(1 + (int)Math.ceil(this.chainLength))))) {
            this.draggingAnchor = true;
         }
      }

      if (this.chainLength == this.targetChainLength && this.draggingAnchor) {
         int offset = this.isWindingUp() ? 0 : this.targetChainLength;
         if (this.anchorPlaceCooldown == 0 && this.tryPlaceAnchor(offset)) {
            this.draggingAnchor = false;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public AABB getRenderBoundingBox() {
      return new AABB(-1.0 / 0.0, -1.0 / 0.0, -1.0 / 0.0, 1.0 / 0.0, 1.0 / 0.0, 1.0 / 0.0);
   }

   public boolean checkAndBreakAnchor(BlockPos down) {
      if (this.level.getBlockState(down).getBlock() instanceof BlockEndPirateAnchor) {
         this.anchorEW = (Boolean)this.level.getBlockState(down).getValue(BlockEndPirateAnchor.EASTORWEST);
         BlockPos actualAnchorPos = down.below(2);
         if (this.level.getBlockState(actualAnchorPos).getBlock() instanceof BlockEndPirateAnchor) {
            BlockEndPirateAnchor.removeAnchor(this.level, actualAnchorPos, this.level.getBlockState(actualAnchorPos));
            this.removeChainBlocks();
            return true;
         }
      }

      return false;
   }

   public boolean tryPlaceAnchor(int offset) {
      BlockPos at = this.getBlockPos().below(3 + offset);
      if (BlockEndPirateAnchor.isClearForPlacement(this.level, at, this.anchorEW)) {
         BlockState anchorState = null;
         this.level.setBlock(at, anchorState, 2);
         BlockEndPirateAnchor.placeAnchor(this.level, at, anchorState);
         this.placeChainBlocks(offset);
         return true;
      } else {
         return false;
      }
   }

   private void placeChainBlocks(int offset) {
      BlockPos at = this.getBlockPos().below(3 + offset);
      BlockPos chainPos = at.above(3);

      while (chainPos.getY() < this.getBlockPos().getY() - 1 && this.isEmptyBlock(chainPos)) {
         chainPos = chainPos.above();
      }
   }

   private void removeChainBlocks() {
      for (BlockPos chainPos = this.getBlockPos().below(1 + (int)Math.ceil(this.chainLength));
         chainPos.getY() < this.getBlockPos().getY();
         chainPos = chainPos.above()
      ) {
         if (this.isAnchorChain(this.level, chainPos)) {
            this.level.setBlock(chainPos, Blocks.AIR.defaultBlockState(), 3);
         }
      }
   }

   public void recalculateChains() {
      if (this.targetChainLength != 0) {
         this.prevMaximumChainLength = this.targetChainLength;
      }

      BlockPos at = this.getBlockPos().below(1);
      if (this.isAnchorTop(this.level, at) && this.anchorPlaceCooldown == 0 && this.checkAndBreakAnchor(at)) {
         this.draggingAnchor = true;
      }

      this.targetChainLength = this.calcChainLength(this.draggingAnchor);
   }

   public void sendDownChains() {
      this.recalculateChains();
      this.pullingUp = false;
   }

   public void pullUpChains() {
      if (this.targetChainLength != 0) {
         this.prevMaximumChainLength = this.targetChainLength;
      }

      this.targetChainLength = 0;
      this.pullingUp = true;
   }

   public void onInteract() {
   }

   public float getChainLengthForRender() {
      return Math.max((float)this.targetChainLength, this.prevMaximumChainLength);
   }

   public float getChainLength(float partialTick) {
      return this.prevChainLength + (this.chainLength - this.prevChainLength) * partialTick;
   }

   public float getWindProgress(float partialTick) {
      return this.prevWindProgress + (this.windProgress - this.prevWindProgress) * partialTick;
   }

   public boolean isAnchorEW() {
      return this.anchorEW;
   }

   public boolean isWinching() {
      return this.windTime > 0;
   }

   public boolean isWindingUp() {
      return this.pullingUp;
   }

   public boolean hasAnchor() {
      return this.draggingAnchor;
   }

   protected void loadAdditional(CompoundTag compound, Provider provider) {
      super.loadAdditional(compound, provider);
      this.pullingUp = AMCompat.getBoolean(compound, "PullingUp");
      this.draggingAnchor = AMCompat.getBoolean(compound, "DraggingAnchor");
      this.anchorEW = AMCompat.getBoolean(compound, "EWAnchor");
      this.prevChainLength = this.chainLength = AMCompat.getFloat(compound, "ChainLength");
      this.targetChainLength = AMCompat.getInt(compound, "TargetChainLength");
   }

   protected void saveAdditional(CompoundTag compound, Provider provider) {
      super.saveAdditional(compound, provider);
      compound.putBoolean("PullingUp", this.pullingUp);
      compound.putBoolean("DraggingAnchor", this.draggingAnchor);
      compound.putBoolean("EWAnchor", this.anchorEW);
      compound.putFloat("ChainLength", this.chainLength);
      compound.putInt("TargetChainLength", this.targetChainLength);
   }
}
