package net.mehvahdjukaar.amendments.common.tile;

import net.mehvahdjukaar.amendments.Amendments;
import net.mehvahdjukaar.amendments.common.PendulumAnimation;
import net.mehvahdjukaar.amendments.common.SwingAnimation;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class HangingSignTileExtension {
   @Nullable
   private ModBlockProperties.PostType leftAttachment = null;
   @Nullable
   private ModBlockProperties.PostType rightAttachment = null;
   private final boolean isCeiling;
   private boolean canSwing = true;
   private SwingAnimation animation;
   private ItemStack frontItem = ItemStack.EMPTY;
   private ItemStack backItem = ItemStack.EMPTY;

   public HangingSignTileExtension(BlockState state) {
      this.isCeiling = state.getBlock() instanceof CeilingHangingSignBlock;
   }

   public void clientTick(Level level, BlockPos pos, BlockState state) {
      SwingAnimation animation = this.getClientAnimation();
      if (!this.canSwing) {
         animation.reset();
      } else {
         animation.tick(level, pos, state);
      }
   }

   private Vector3f getRotationAxis(BlockState state) {
      return state.hasProperty(WallHangingSignBlock.FACING)
         ? ((Direction)state.getValue(WallHangingSignBlock.FACING)).getClockWise().step()
         : new Vector3f(0.0F, 0.0F, 1.0F)
            .rotateY(0.017453292F * (90.0F + RotationSegment.convertToDegrees((Integer)state.getValue(CeilingHangingSignBlock.ROTATION))));
   }

   public ModBlockProperties.PostType getRightAttachment() {
      return this.rightAttachment;
   }

   public ModBlockProperties.PostType getLeftAttachment() {
      return this.leftAttachment;
   }

   public void saveAdditional(CompoundTag tag, Provider registries) {
      if (!this.isCeiling) {
         if (this.leftAttachment != null) {
            tag.putByte("left_attachment", (byte)this.leftAttachment.ordinal());
         }

         if (this.rightAttachment != null) {
            tag.putByte("right_attachment", (byte)this.rightAttachment.ordinal());
         }
      }

      if (!this.canSwing) {
         tag.putBoolean("can_swing", false);
      }

      if (!this.frontItem.isEmpty()) {
         tag.put("front_item", this.frontItem.save(registries));
      }

      if (!this.backItem.isEmpty()) {
         tag.put("back_item", this.backItem.save(registries));
      }
   }

   public void load(CompoundTag tag, Provider registries) {
      if (!this.isCeiling) {
         if (tag.contains("left_attachment")) {
            this.leftAttachment = ModBlockProperties.PostType.values()[tag.getByte("left_attachment")];
         }

         if (tag.contains("right_attachment")) {
            this.rightAttachment = ModBlockProperties.PostType.values()[tag.getByte("right_attachment")];
         }
      }

      if (tag.contains("can_swing")) {
         this.canSwing = tag.getBoolean("can_swing");
      } else {
         this.canSwing = true;
      }

      if (tag.contains("front_item")) {
         this.setFrontItem(ItemStack.parseOptional(registries, tag.getCompound("front_item")));
      } else {
         this.setFrontItem(ItemStack.EMPTY);
      }

      if (tag.contains("back_item")) {
         this.setBackItem(ItemStack.parseOptional(registries, tag.getCompound("back_item")));
      } else {
         this.setBackItem(ItemStack.EMPTY);
      }
   }

   public void updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      if (!this.isCeiling) {
         Direction selfFacing = (Direction)state.getValue(WallHangingSignBlock.FACING);
         if (direction == selfFacing.getClockWise()) {
            this.rightAttachment = ModBlockProperties.PostType.get(neighborState, true);
            if (level instanceof Level l) {
               l.sendBlockUpdated(pos, state, state, 2);
            }
         } else if (direction == selfFacing.getCounterClockWise()) {
            this.leftAttachment = ModBlockProperties.PostType.get(neighborState, true);
            if (level instanceof Level l) {
               l.sendBlockUpdated(pos, state, state, 2);
            }
         }
      }

      if (direction == Direction.DOWN) {
         this.updateCanSwing(state, neighborState, level, pos);
      }
   }

   private void updateCanSwing(BlockState state, BlockState neighborState, LevelAccessor level, BlockPos pos) {
      this.canSwing = this.isCeiling ? !(Boolean)state.getValue(CeilingHangingSignBlock.ATTACHED) : !Amendments.canConnectDown(neighborState, level, pos);
   }

   public void updateAttachments(Level level, BlockPos pos, BlockState state) {
      if (!this.isCeiling) {
         Direction selfFacing = (Direction)state.getValue(WallHangingSignBlock.FACING);
         this.rightAttachment = ModBlockProperties.PostType.get(level.getBlockState(pos.relative(selfFacing.getClockWise())), true);
         this.leftAttachment = ModBlockProperties.PostType.get(level.getBlockState(pos.relative(selfFacing.getCounterClockWise())), true);
      }

      BlockState below = level.getBlockState(pos.below());
      this.updateCanSwing(state, below, level, pos);
   }

   public boolean canSwing() {
      return this.canSwing;
   }

   public void setFrontItem(ItemStack frontItem) {
      this.frontItem = frontItem;
   }

   public ItemStack getFrontItem() {
      return this.frontItem;
   }

   public void setBackItem(ItemStack backItem) {
      this.backItem = backItem;
   }

   public ItemStack getBackItem() {
      return this.backItem;
   }

   public SwingAnimation getClientAnimation() {
      if (!ClientConfigs.SWINGING_SIGNS.get()) {
         return SwingAnimation.EMPTY;
      } else {
         if (this.animation == null) {
            this.animation = new PendulumAnimation(ClientConfigs.HANGING_SIGN_CONFIG, this::getRotationAxis);
         }

         return this.animation;
      }
   }
}
